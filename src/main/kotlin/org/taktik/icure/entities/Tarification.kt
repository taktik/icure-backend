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
package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import org.taktik.couchdb.entity.Attachment
import org.taktik.icure.entities.base.AppendixType
import org.taktik.icure.entities.base.CodeFlag
import org.taktik.icure.entities.base.CodeIdentification
import org.taktik.icure.entities.base.LinkQualification
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.LetterValue
import org.taktik.icure.entities.embed.Periodicity
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.entities.embed.Valorisation
import org.taktik.icure.entities.utils.MergeUtil.mergeListsDistinct
import org.taktik.icure.entities.utils.MergeUtil.mergeMapsOfSets
import org.taktik.icure.entities.utils.MergeUtil.mergeSets
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Tarification(
	@JsonProperty("_id") override val id: String, // id = type|code|version  => this must be unique
	@JsonProperty("_rev") override val rev: String? = null,
	@JsonProperty("deleted") override val deletionDate: Long? = null,

	override val context: String? = null, //ex: When embedded the context where this code is used
	override val type: String? = null, //ex: ICD (type + version + code combination must be unique) (or from tags -> CD-ITEM)
	override val code: String? = null, //ex: I06.2 (or from tags -> healthcareelement). Local codes are encoded as LOCAL:SLLOCALFROMMYSOFT
	override val version: String? = null, //ex: 10. Must be lexicographically searchable
	override val label: Map<String, String>? = null, //ex: {en: Rheumatic Aortic Stenosis, fr: Sténose rhumatoïde de l'Aorte}

	val author: String? = null,
	val regions: Set<String> = emptySet(), //ex: be,fr
	val periodicity: List<Periodicity> = emptyList(),
	val level: Int? = null, //ex: 0 = System, not to be modified by user, 1 = optional, created or modified by user
	val links: List<String> = emptyList(), //Links towards related codes (corresponds to an approximate link in qualifiedLinks)
	val qualifiedLinks: Map<LinkQualification, List<String>> = emptyMap(), //Links towards related codes
	val flags: Set<CodeFlag> = emptySet(), //flags (like female only) for the code
	val searchTerms: Map<String, Set<String>> = emptyMap(), //Extra search terms/ language
	val data: String? = null,
	val appendices: Map<AppendixType, String> = emptyMap(),
	val disabled: Boolean = false,
	val valorisations: Set<Valorisation> = emptySet(),
	val category: Map<String, String> = emptyMap(),
	val consultationCode: Boolean? = null,
	val hasRelatedCode: Boolean? = null,
	val needsPrescriber: Boolean? = null,
	val relatedCodes: Set<String> = emptySet(),
	val nGroup: String? = null,
	val letterValues: List<LetterValue> = emptyList(),

	@JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = emptyMap(),
	@JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = emptyList(),
	@JsonProperty("_conflicts") override val conflicts: List<String>? = emptyList(),
	@JsonProperty("rev_history") override val revHistory: Map<String, String>? = emptyMap()

) : StoredDocument, CodeIdentification {
	companion object : DynamicInitializer<Tarification> {
		fun from(type: String, code: String, version: String) = Tarification(id = "$type|$code|$version", type = type, code = code, version = version)
	}

	fun merge(other: Tarification) = Tarification(args = this.solveConflictsWith(other))
	fun solveConflictsWith(other: Tarification) = super<StoredDocument>.solveConflictsWith(other) + super<CodeIdentification>.solveConflictsWith(other) + mapOf(
		"author" to (this.author ?: other.author),
		"regions" to (other.regions + this.regions),
		"periodicity" to (other.periodicity + this.periodicity),
		"level" to (this.level ?: other.level),
		"links" to (other.links + this.links),
		"qualifiedLinks" to (other.qualifiedLinks + this.qualifiedLinks),
		"flags" to (other.flags + this.flags),
		"searchTerms" to mergeMapsOfSets(this.searchTerms, other.searchTerms),
		"data" to (this.data ?: other.data),
		"appendices" to (other.appendices + this.appendices),
		"disabled" to (this.disabled),
		"valorisations" to mergeSets(
			this.valorisations, other.valorisations,
			{ a, b -> a.predicate == b.predicate && a.startOfValidity == b.startOfValidity && a.endOfValidity == b.endOfValidity }
		),
		"category" to (other.category + this.category),
		"consultationCode" to (this.consultationCode ?: other.consultationCode),
		"hasRelatedCode" to (this.hasRelatedCode ?: other.hasRelatedCode),
		"needsPrescriber" to (this.needsPrescriber ?: other.needsPrescriber),
		"relatedCodes" to (other.relatedCodes + this.relatedCodes),
		"nGroup" to (this.nGroup ?: other.nGroup),
		"letterValues" to mergeListsDistinct(
			this.letterValues, other.letterValues,
			{ a, b -> a.coefficient == b.coefficient && a.index == b.index && a.letter == b.letter }
		)
	)

	override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
	override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)

	override fun normalizeIdentification(): Tarification {
		val parts = this.id.split("|").toTypedArray()
		return if (this.type == null || this.code == null || this.version == null) this.copy(
			type = this.type ?: parts[0],
			code = this.code ?: parts[1],
			version = this.version ?: parts[2]
		) else this
	}
}
