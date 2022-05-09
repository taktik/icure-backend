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
import io.swagger.v3.oas.annotations.media.Schema
import org.taktik.icure.services.external.rest.v2.dto.base.*
import org.taktik.icure.services.external.rest.v2.dto.embed.PeriodicityDto

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CodeDto(
	override val id: String, // id = type|code|version  => this must be unique
	override val rev: String? = null,
	override val deletionDate: Long? = null,

	override val context: String? = null, //ex: When embedded the context where this code is used
	override val type: String? = null, //ex: ICD (type + version + code combination must be unique) (or from tags -> CD-ITEM)
	override val code: String? = null, //ex: I06.2 (or from tags -> healthcareelement). Local codes are encoded as LOCAL:SLLOCALFROMMYSOFT
	override val version: String? = null, //ex: 10. Must be lexicographically searchable
	override val label: Map<String, String>? = null, //ex: {en: Rheumatic Aortic Stenosis, fr: Sténose rhumatoïde de l'Aorte}

	val author: String? = null,
	val regions: Set<String> = emptySet(), //ex: be,fr
	val periodicity: Set<PeriodicityDto> = emptySet(),
	val level: Int? = null, //ex: 0 = System, not to be modified by userDto, 1 = optional, created or modified by userDto
	val links: Set<String> = emptySet(), //Links towards related codes (corresponds to an approximate link in qualifiedLinks)
	val qualifiedLinks: Map<LinkQualificationDto, List<String>> = emptyMap(), //Links towards related codes
	val flags: Set<CodeFlagDto> = emptySet(), //flags (like female only) for the code
	val searchTerms: Map<String, Set<String>> = emptyMap(), //Extra search terms/ language
	val data: String? = null,
	val appendices: Map<AppendixTypeDto, String> = emptyMap(),
	@Schema(defaultValue = "false") val disabled: Boolean = false
) : StoredDocumentDto, CodeIdentificationDto<String> {
	companion object {
		fun from(type: String, code: String, version: String) = CodeDto(id = "$type|$code|$version", type = type, code = code, version = version)
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
