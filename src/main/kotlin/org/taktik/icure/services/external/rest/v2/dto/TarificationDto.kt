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
import org.taktik.icure.services.external.rest.v2.dto.base.*
import org.taktik.icure.services.external.rest.v2.dto.embed.LetterValueDto
import org.taktik.icure.services.external.rest.v2.dto.embed.PeriodicityDto
import org.taktik.icure.services.external.rest.v2.dto.embed.ValorisationDto

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class TarificationDto(
	override val id: String, // id = type|code|version  => this must be unique
	override val rev: String? = null,
	override val deletionDate: Long? = null,
	override val label: Map<String, String>? = null, //ex: {en: Rheumatic Aortic Stenosis, fr: Sténose rhumatoïde de l'Aorte}
	override val context: String? = null, //ex: When embedded the context where this code is used
	override val type: String? = null, //ex: ICD (type + version + code combination must be unique) (or from tags -> CD-ITEM)
	override val code: String? = null, //ex: I06.2 (or from tags -> healthcareelement). Local codes are encoded as LOCAL:SLLOCALFROMMYSOFT
	override val version: String? = null, //ex: 10. Must be lexicographically searchable

	val author: String? = null,
	val regions: Set<String> = emptySet(), //ex: be,fr
	val periodicity: List<PeriodicityDto> = emptyList(),
	val level: Int? = null, //ex: 0 = System, not to be modified by user, 1 = optional, created or modified by user
	val links: List<String> = emptyList(), //Links towards related codes (corresponds to an approximate link in qualifiedLinks)
	val qualifiedLinks: Map<LinkQualificationDto, List<String>> = emptyMap(), //Links towards related codes
	val flags: Set<CodeFlagDto> = emptySet(), //flags (like female only) for the code
	val searchTerms: Map<String, Set<String>> = emptyMap(), //Extra search terms/ language
	val data: String? = null,
	val appendices: Map<AppendixTypeDto, String> = emptyMap(),
	val disabled: Boolean = false,
	val valorisations: Set<ValorisationDto> = emptySet(),
	val category: Map<String, String> = emptyMap(),
	val consultationCode: Boolean? = null,
	val hasRelatedCode: Boolean? = null,
	val needsPrescriber: Boolean? = null,
	val relatedCodes: Set<String> = emptySet(),
	val nGroup: String? = null,
	val letterValues: List<LetterValueDto> = emptyList()
) : StoredDocumentDto, CodeIdentificationDto<String> {
	override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
	override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)

	override fun normalizeIdentification(): TarificationDto {
		val parts = this.id.split("|").toTypedArray()
		return if (this.type == null || this.code == null || this.version == null) this.copy(
			type = this.type ?: parts[0],
			code = this.code ?: parts[1],
			version = this.version ?: parts[2]
		) else this
	}
}
