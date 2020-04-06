/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v1.controllers.extra

import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Operation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactor.mono
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.ClassificationLogic
import org.taktik.icure.entities.Classification
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.services.external.rest.v1.dto.ClassificationDto
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/classification")
@Tag(name = "classification")
class ClassificationController(private val mapper: MapperFacade,
                               private val classificationLogic: ClassificationLogic) {

    @Operation(summary = "Create a classification with the current user", description = "Returns an instance of created classification Template.")
    @PostMapping
    fun createClassification(@RequestBody c: ClassificationDto) = mono {
        val element = classificationLogic.createClassification(mapper.map(c, Classification::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Classification creation failed.")

        mapper.map(element, ClassificationDto::class.java)
    }

    @Operation(summary = "Get a classification Template")
    @GetMapping("/{classificationId}")
    fun getClassification(@PathVariable classificationId: String) = mono {
        val element = classificationLogic.getClassification(classificationId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Getting classification failed. Possible reasons: no such classification exists, or server error. Please try again or read the server log.")

        mapper.map(element, ClassificationDto::class.java)
    }

    @Operation(summary = "Get a list of classifications", description = "Ids are seperated by a coma")
    @GetMapping("/byIds/{ids}")
    fun getClassificationByHcPartyId(@PathVariable ids: String): Flux<ClassificationDto> {
        val elements = classificationLogic.getClassificationByIds(ids.split(','))

        return elements.map { mapper.map(it, ClassificationDto::class.java) }.injectReactorContext()
    }

    @Operation(summary = "List classification Templates found By Healthcare Party and secret foreign keyelementIds.", description = "Keys hast to delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys")
    fun findByHCPartyPatientSecretFKeys(@RequestParam hcPartyId: String, @RequestParam secretFKeys: String): Flux<ClassificationDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }
        val elementList = classificationLogic.findByHCPartySecretPatientKeys(hcPartyId, secretPatientKeys)

        return elementList.map { mapper.map(it, ClassificationDto::class.java) }.injectReactorContext()
    }

    @Operation(summary = "Delete classification Templates.", description = "Response is a set containing the ID's of deleted classification Templates.")
    @DeleteMapping("/{classificationIds}")
    fun deleteClassifications(@PathVariable classificationIds: String): Flux<DocIdentifier> {
        val ids = classificationIds.split(',')
        if (ids.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.")
        }

        return classificationLogic.deleteClassifications(ids.toSet()).injectReactorContext()
    }

    @Operation(summary = "Modify a classification Template", description = "Returns the modified classification Template.")
    @PutMapping
    fun modifyClassification(@RequestBody classificationDto: ClassificationDto) = mono {
        classificationLogic.modifyClassification(mapper.map(classificationDto, Classification::class.java))
        val modifiedClassification = classificationLogic.getClassification(classificationDto.id)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Classification modification failed.")

        mapper.map(modifiedClassification, ClassificationDto::class.java)
    }


    @Operation(summary = "Delegates a classification to a healthcare party", description = "It delegates a classification to a healthcare party (By current healthcare party). Returns the element with new delegations.")
    @PostMapping("/{classificationId}/delegate")
    fun newDelegations(@PathVariable classificationId: String, @RequestBody ds: List<DelegationDto>) = mono {
        classificationLogic.addDelegations(classificationId, ds.map { mapper.map(it, Delegation::class.java) })
        val classificationWithDelegation = classificationLogic.getClassification(classificationId)

        val succeed = classificationWithDelegation != null && classificationWithDelegation.delegations != null && classificationWithDelegation.delegations.isNotEmpty()
        if (succeed) {
            mapper.map(classificationWithDelegation, ClassificationDto::class.java)
        } else {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Delegation creation for classification failed.")
        }
    }

    @Operation(summary = "Update delegations in classification", description = "Keys must be delimited by coma")
    @PostMapping("/delegations")
    fun setClassificationsDelegations(@RequestBody stubs: List<IcureStubDto>) = flow {
        val classifications = classificationLogic.getClassificationByIds(stubs.map { it.id })
        classifications.onEach { classification ->
            stubs.find { it.id == classification.id }?.let { stub ->
                stub.delegations.forEach { (s, delegationDtos) -> classification.delegations[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toMutableSet() }
                stub.encryptionKeys.forEach { (s, delegationDtos) -> classification.encryptionKeys[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toMutableSet() }
                stub.cryptedForeignKeys.forEach { (s, delegationDtos) -> classification.cryptedForeignKeys[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toMutableSet() }
            }
        }
        emitAll(classificationLogic.updateEntities(classifications.toList()))
    }.injectReactorContext()
}
