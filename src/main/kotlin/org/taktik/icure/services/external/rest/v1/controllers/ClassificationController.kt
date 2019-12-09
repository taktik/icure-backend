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

package org.taktik.icure.services.external.rest.v1.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
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

@RestController
@RequestMapping("/rest/v1/classification")
@Api(tags = ["classification"])
class ClassificationController(private val mapper: MapperFacade,
                               private val classificationLogic: ClassificationLogic) {

    @ApiOperation(nickname = "createClassification", value = "Create a classification with the current user", notes = "Returns an instance of created classification Template.")
    @PostMapping
    suspend fun createClassification(@RequestBody c: ClassificationDto): ClassificationDto {
        val element = classificationLogic.createClassification(mapper.map(c, Classification::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Classification creation failed.")

        return mapper.map(element, ClassificationDto::class.java)
    }

    @ApiOperation(nickname = "getClassification", value = "Get a classification Template")
    @GetMapping("/{classificationId}")
    suspend fun getClassification(@PathVariable classificationId: String): ClassificationDto {
        val element = classificationLogic.getClassification(classificationId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Getting classification failed. Possible reasons: no such classification exists, or server error. Please try again or read the server log.")

        return mapper.map(element, ClassificationDto::class.java)
    }

    @ApiOperation(nickname = "getClassificationByHcPartyId", value = "Get a list of classifications", notes = "Ids are seperated by a coma")
    @GetMapping("/byIds/{ids}")
    fun getClassificationByHcPartyId(@PathVariable ids: String): Flow<ClassificationDto> {
        val elements = classificationLogic.getClassificationByIds(ids.split(','))

        return elements.map { mapper.map(it, ClassificationDto::class.java) }
    }

    @ApiOperation(nickname = "findByHCPartyPatientSecretFKeys", value = "List classification Templates found By Healthcare Party and secret foreign keyelementIds.", notes = "Keys hast to delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys")
    fun findByHCPartyPatientSecretFKeys(@RequestParam hcPartyId: String, @RequestParam secretFKeys: String): Flow<ClassificationDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }
        val elementList = classificationLogic.findByHCPartySecretPatientKeys(hcPartyId, secretPatientKeys)

        return elementList.map { mapper.map(it, ClassificationDto::class.java) }
    }

    @ApiOperation(nickname = "deleteClassifications", value = "Delete classification Templates.", notes = "Response is a set containing the ID's of deleted classification Templates.")
    @DeleteMapping("/{classificationIds}")
    fun deleteClassifications(@PathVariable classificationIds: String): Flow<DocIdentifier> {
        val ids = classificationIds.split(',')
        if (ids.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.")
        }

        return classificationLogic.deleteClassifications(ids.toSet())
    }

    @ApiOperation(nickname = "modifyClassification", value = "Modify a classification Template", notes = "Returns the modified classification Template.")
    @PutMapping
    suspend fun modifyClassification(@RequestBody classificationDto: ClassificationDto): ClassificationDto {
        classificationLogic.modifyClassification(mapper.map(classificationDto, Classification::class.java))
        val modifiedClassification = classificationLogic.getClassification(classificationDto.id)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Classification modification failed.")

        return mapper.map(modifiedClassification, ClassificationDto::class.java)
    }


    @ApiOperation(nickname = "newDelegations", value = "Delegates a classification to a healthcare party", notes = "It delegates a classification to a healthcare party (By current healthcare party). Returns the element with new delegations.")
    @PostMapping("/{classificationId}/delegate")
    suspend fun newDelegations(@PathVariable classificationId: String, @RequestBody ds: List<DelegationDto>): ClassificationDto {
        classificationLogic.addDelegations(classificationId, ds.map { mapper.map(it, Delegation::class.java) })
        val classificationWithDelegation = classificationLogic.getClassification(classificationId)

        val succeed = classificationWithDelegation != null && classificationWithDelegation.delegations != null && classificationWithDelegation.delegations.isNotEmpty()
        if (succeed) {
            return mapper.map(classificationWithDelegation, ClassificationDto::class.java)
        } else {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Delegation creation for classification failed.")
        }
    }

    @ApiOperation(nickname = "setClassificationsDelegations", value = "Update delegations in classification", notes = "Keys must be delimited by coma")
    @PostMapping("/delegations")
    suspend fun setClassificationsDelegations(@RequestBody stubs: List<IcureStubDto>) {
        val classifications = classificationLogic.getClassificationByIds(stubs.map { it.id })
        classifications.onEach { classification ->
            stubs.find { it.id == classification.id }?.let { stub ->
                stub.delegations.forEach { (s, delegationDtos) -> classification.delegations[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toSet() }
                stub.encryptionKeys.forEach { (s, delegationDtos) -> classification.encryptionKeys[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toSet() }
                stub.cryptedForeignKeys.forEach { (s, delegationDtos) -> classification.cryptedForeignKeys[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toSet() }
            }
        }
        classificationLogic.updateEntities(classifications.toList())
    }
}
