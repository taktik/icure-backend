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

package org.taktik.icure.services.external.rest.v2.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.v2.dto.base.MimeAttachmentDto

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ImportResultDto(
        val patient: PatientDto? = null,
        val hes: List<HealthElementDto> = listOf(),
        val ctcs: List<ContactDto> = listOf(),
        val warnings: List<String> = listOf(),
        val errors: List<String> = listOf(),
        val forms: List<FormDto> = listOf(),
        val hcps: List<HealthcarePartyDto> = listOf(),
        val documents: List<DocumentDto> = listOf(),
        val attachments: Map<String, MimeAttachmentDto> = mapOf()
)
