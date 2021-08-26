/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v2.controllers.extra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.ClassificationLogic
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.services.external.rest.v2.dto.ClassificationDto
import org.taktik.icure.services.external.rest.v2.dto.IcureStubDto
import org.taktik.icure.services.external.rest.v2.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v2.dto.embed.DelegationDto
import org.taktik.icure.services.external.rest.v2.mapper.ClassificationMapper
import org.taktik.icure.services.external.rest.v2.mapper.StubMapper
import org.taktik.icure.services.external.rest.v2.mapper.embed.DelegationMapper
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v2/classification")
@Tag(name = "classification")
class ClassificationController(
        private val classificationLogic: ClassificationLogic,
        private val classificationMapper: ClassificationMapper,
        private val delegationMapper: DelegationMapper,
        private val stubMapper: StubMapper
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "Create a classification with the current user", description = "Returns an instance of created classification Template.")
    @PostMapping
    fun createClassification(@RequestBody c: ClassificationDto) = mono {
        val element = classificationLogic.createClassification(classificationMapper.map(c))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Classification creation failed.")

        classificationMapper.map(element)
    }

    @Operation(summary = "Get a classification Template")
    @GetMapping("/{classificationId}")
    fun getClassification(@PathVariable classificationId: String) = mono {
        val element = classificationLogic.getClassification(classificationId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Getting classification failed. Possible reasons: no such classification exists, or server error. Please try again or read the server log.")

        classificationMapper.map(element)
    }

    @Operation(summary = "Get a list of classifications", description = "Ids are seperated by a coma")
    @GetMapping("/byIds/{ids}")
    fun getClassificationByHcPartyId(@PathVariable ids: String): Flux<ClassificationDto> {
        val elements = classificationLogic.getClassifications(ids.split(','))

        return elements.map { classificationMapper.map(it) }.injectReactorContext()
    }

    @Operation(summary = "List classification Templates found By Healthcare Party and secret foreign keyelementIds.", description = "Keys hast to delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys")
    fun findClassificationsByHCPartyPatientForeignKeys(@RequestParam hcPartyId: String, @RequestParam secretFKeys: String): Flux<ClassificationDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }
        val elementList = classificationLogic.listClassificationsByHCPartyAndSecretPatientKeys(hcPartyId, secretPatientKeys)

        return elementList.map { classificationMapper.map(it) }.injectReactorContext()
    }

    @Operation(summary = "Delete classification Templates.", description = "Response is a set containing the ID's of deleted classification Templates.")
    @PostMapping("/delete/batch")
    fun deleteClassifications(@RequestBody classificationIds: ListOfIdsDto): Flux<DocIdentifier> {
        return classificationIds.ids.takeIf { it.isNotEmpty() }
                ?.let { ids->
                    try {
                        classificationLogic.deleteEntities(HashSet(ids)).injectReactorContext()
                    }
                    catch (e: java.lang.Exception) {
                        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message).also { logger.error(it.message) }
                    }
                }
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.").also { logger.error(it.message) }
    }

    @Operation(summary = "Modify a classification Template", description = "Returns the modified classification Template.")
    @PutMapping
    fun modifyClassification(@RequestBody classificationDto: ClassificationDto) = mono {
        classificationLogic.modifyClassification(classificationMapper.map(classificationDto))
        val modifiedClassification = classificationLogic.getClassification(classificationDto.id)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Classification modification failed.")

        classificationMapper.map(modifiedClassification)
    }


    @Operation(summary = "Delegates a classification to a healthcare party", description = "It delegates a classification to a healthcare party (By current healthcare party). Returns the element with new delegations.")
    @PostMapping("/{classificationId}/delegate")
    fun newClassificationDelegations(@PathVariable classificationId: String, @RequestBody ds: List<DelegationDto>) = mono {
        classificationLogic.addDelegations(classificationId, ds.map { delegationMapper.map(it) })
        val classificationWithDelegation = classificationLogic.getClassification(classificationId)

        val succeed = classificationWithDelegation?.delegations != null && classificationWithDelegation.delegations.isNotEmpty()
        if (succeed) {
            classificationWithDelegation?.let { classificationMapper.map(it) }
        } else {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Delegation creation for classification failed.")
        }
    }

    @Operation(summary = "Update delegations in classification", description = "Keys must be delimited by coma")
    @PostMapping("/delegations")
    fun setClassificationsDelegations(@RequestBody stubs: List<IcureStubDto>) = flow {
        val classifications = classificationLogic.getClassifications(stubs.map { it.id }).map { classification ->
            stubs.find { s -> s.id == classification.id }?.let { stub ->
                classification.copy(
                        delegations = classification.delegations.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.delegations[s]?.map { delegationMapper.map(it) }?.toSet() ?: dels },
                        encryptionKeys = classification.encryptionKeys.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.encryptionKeys[s]?.map { delegationMapper.map(it) }?.toSet() ?: dels },
                        cryptedForeignKeys = classification.cryptedForeignKeys.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.cryptedForeignKeys[s]?.map { delegationMapper.map(it) }?.toSet() ?: dels }
                )
            } ?: classification
        }
        emitAll(classificationLogic.modifyEntities(classifications.toList()).map { stubMapper.mapToStub(it) })
    }.injectReactorContext()
}
