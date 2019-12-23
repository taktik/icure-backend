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

package org.taktik.icure.services.external.rest.v1.controllers.be

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import ma.glasnost.orika.MapperFacade
import org.apache.commons.lang3.StringUtils.isBlank
import org.springframework.web.bind.annotation.*
import org.taktik.icure.be.format.logic.MultiFormatLogic
import org.taktik.icure.entities.Contact
import org.taktik.icure.asynclogic.DocumentLogic
import org.taktik.icure.services.external.rest.v1.dto.ContactDto
import org.taktik.icure.services.external.rest.v1.dto.ResultInfoDto

@RestController
@RequestMapping("/rest/v1/be_result_import")
@Api(tags = ["be_result_import"])
class ResultImportController(private val multiFormatLogic: MultiFormatLogic,
                             private val documentLogic: DocumentLogic,
                             private val mapper: MapperFacade) {

    @ApiOperation(nickname = "canHandle", value = "Can we handle this document")
    @GetMapping("/canhandle/{id}")
    suspend fun canHandle(@PathVariable id: String,
                  @RequestParam enckeys: String): Boolean? {
        return documentLogic.get(id)?.let { multiFormatLogic.canHandle(it, if (isBlank(enckeys)) null else enckeys.split(',')) }
    }

    @ApiOperation(nickname = "getInfos", value = "Extract general infos from document")
    @GetMapping("/infos/{id}")
    suspend fun getInfos(@PathVariable id: String,
                 @RequestParam(required = false) full: Boolean?,
                 @RequestParam language: String,
                 @RequestParam enckeys: String): List<ResultInfoDto> {
        return multiFormatLogic.getInfos(documentLogic.get(id), full
                ?: false, language, if (isBlank(enckeys)) null else enckeys.split(',')).map { mapper.map(it, ResultInfoDto::class.java) }
    }

    @ApiOperation(nickname = "doImport", value = "import document")
    @GetMapping("/import/{documentId}/{hcpId}/{language}")
    fun doImport(@PathVariable documentId: String,
                 @PathVariable hcpId: String,
                 @PathVariable language: String,
                 @RequestParam protocolIds: String,
                 @RequestParam formIds: String,
                 @RequestParam planOfActionId: String,
                 @RequestParam enckeys: String, ctc: ContactDto): ContactDto {
        return mapper.map(multiFormatLogic.doImport(language, documentLogic.get(documentId), hcpId, protocolIds.split(','),
                formIds.split(','), planOfActionId, mapper.map(ctc, Contact::class.java),
                if (isBlank(enckeys)) null else enckeys.split(',')), ContactDto::class.java)
    }
}
