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
package org.taktik.icure.services.external.rest.v2.dto.embed


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.v2.dto.base.CodeStubDto
import org.taktik.icure.services.external.rest.v2.dto.base.ICureDocumentDto
import org.taktik.icure.services.external.rest.v2.dto.base.NamedDto

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class PlanOfActionTemplateDto(
        override val id: String,
        override val created: Long? = null,
        override val modified: Long? = null,
        override val author: String? = null,
        override val responsible: String? = null,
        override val medicalLocationId: String? = null,
        override val tags: Set<CodeStubDto> = setOf(),
        override val codes: Set<CodeStubDto> = setOf(),
        override val endOfLife: Long? = null,

        //Usually one of the following is used (either valueDate or openingDate and closingDate)
        override val name: String? = null,
        val descr: String? = null,
        val note: String? = null,
        val relevant: Boolean = true,
        val status: Int = 0, //bit 0: active/inactive, bit 1: relevant/irrelevant, bit 2 : present/absent, ex: 0 = active,relevant and present
        var forms: List<FormSkeletonDto> = listOf()
) : ICureDocumentDto<String>, NamedDto
