/*
 * CopyrightDto (C) 2018 Taktik SA
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
package org.taktik.icure.services.external.rest.v1.dto

import org.taktik.icure.services.external.rest.v1.dto.base.AppendixTypeDto
import org.taktik.icure.services.external.rest.v1.dto.base.CodeFlagDto
import org.taktik.icure.services.external.rest.v1.dto.base.CodeIdentificationDto
import org.taktik.icure.services.external.rest.v1.dto.base.LinkQualificationDto
import org.taktik.icure.services.external.rest.v1.dto.base.StoredDocumentDto
import org.taktik.icure.services.external.rest.v1.dto.embed.PeriodicityDto


data class CodeDto(
        override val id: String,         // id = type|code|version  => this must be unique
        override val rev: String? = null,
        override val deletionDate: Long? = null,

        override val type: String? = null, //ex: ICD (type + version + code combination must be unique) (or from tags -> CD-ITEM)
        override val code: String? = null, //ex: I06.2 (or from tags -> healthcareelement). Local codes are encoded as LOCAL:SLLOCALFROMMYSOFT
        override val version: String? = null, //ex: 10. Must be lexicographically searchable

        val author: String? = null,
        val regions: Set<String> = setOf(), //ex: be,fr
        val periodicity: Set<PeriodicityDto> = setOf(),
        val level: Int? = null, //ex: 0 = System, not to be modified by userDto, 1 = optional, created or modified by userDto
        val label: Map<String, String> = mapOf(), //ex: {en: Rheumatic Aortic Stenosis, fr: Sténose rhumatoïde de l'Aorte}
        val links: Set<String> = setOf(), //Links towards related codes (corresponds to an approximate link in qualifiedLinks)
        val qualifiedLinks: Map<LinkQualificationDto, List<String>> = mapOf(), //Links towards related codes
        val flags: Set<CodeFlagDto> = setOf(), //flags (like female only) for the code
        val searchTerms: Map<String, Set<String>> = mapOf(), //Extra search terms/ language
        val data: String? = null,
        val appendices: Map<AppendixTypeDto, String> = mapOf(),
        val isDisabled: Boolean = false
) : StoredDocumentDto, CodeIdentificationDto {
    companion object {
        fun from(type: String, code: String, version: String) = CodeDto(id = "$type:$code:$version", type = type, code = code, version = version)
    }

    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)

    override fun normalizeIdentification(): CodeDto {
        val parts = this.id.split("|").toTypedArray()
        return if (this.type == null || this.code == null || this.version == null) this.copy(
                type = this.type ?: parts[0],
                code = this.code ?: parts[1],
                version = this.version ?: parts[2]
        ) else this
    }
}
