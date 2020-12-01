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

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.reactor.mono
import org.apache.commons.lang3.StringUtils.isBlank
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.taktik.icure.asynclogic.DocumentLogic
import org.taktik.icure.be.format.logic.MultiFormatLogic
import org.taktik.icure.services.external.rest.v1.dto.ContactDto
import org.taktik.icure.services.external.rest.v1.mapper.ContactMapper
import org.taktik.icure.services.external.rest.v1.mapper.ResultInfoMapper

@RestController
@RequestMapping("/rest/v1/be_result_import")
@Tag(name = "beresultimport")
class ResultImportController(private val multiFormatLogic: MultiFormatLogic,
                             private val documentLogic: DocumentLogic,
                             private val resultInfoMapper: ResultInfoMapper,
                             private val contactMapper: ContactMapper
) {

    @Operation(summary = "Can we handle this document")
    @GetMapping("/canhandle/{id}")
    fun canHandle(@PathVariable id: String,
                  @RequestParam enckeys: String) = mono {
        documentLogic.get(id)?.let { multiFormatLogic.canHandle(it, if (isBlank(enckeys)) listOf() else enckeys.split(',')) }
    }

    @Operation(summary = "Extract general infos from document")
    @GetMapping("/infos/{id}")
    fun getInfos(@PathVariable id: String,
                 @RequestParam language: String,
                 @RequestParam enckeys: String,
                 @RequestParam(required = false) full: Boolean?) = mono {
        val doc = documentLogic.get(id)
        doc?.let {
            multiFormatLogic.getInfos(
                    it,
                    full ?: false,
                    language,
                    if (isBlank(enckeys)) listOf() else enckeys.split(',')
            )?.map { resultInfoMapper.map(it) }
        }
    }

    @Operation(summary = "import document")
    @GetMapping("/import/{documentId}/{hcpId}/{language}")
    fun doImport(@PathVariable documentId: String,
                 @PathVariable hcpId: String,
                 @PathVariable language: String,
                 @RequestParam protocolIds: String,
                 @RequestParam formIds: String,
                 @RequestParam planOfActionId: String,
                 @RequestParam enckeys: String, @RequestBody ctc: ContactDto) = mono {
        documentLogic.get(documentId)?.let {
            multiFormatLogic.doImport(
                    language,
                    it,
                    hcpId,
                    protocolIds.split(','),
                    formIds.split(','),
                    planOfActionId,
                    contactMapper.map(ctc),
                    if (isBlank(enckeys)) listOf() else enckeys.split(',')
            )?.let { contactMapper.map(it) }
        }
    }
}
