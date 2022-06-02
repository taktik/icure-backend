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

import java.time.Instant
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.github.pozo.KotlinBuilder
import org.taktik.couchdb.entity.Attachment
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.base.Encryptable
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.InstantDeserializer
import org.taktik.icure.utils.InstantSerializer
import org.taktik.icure.utils.invoke
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import org.taktik.icure.validation.ValidCode

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class AccessLog(
	@JsonProperty("_id") override val id: String,
	@JsonProperty("_rev") override val rev: String? = null,
	@field:NotNull(autoFix = AutoFix.NOW) override val created: Long? = null,
	@field:NotNull(autoFix = AutoFix.NOW) override val modified: Long? = null,
	@field:NotNull(autoFix = AutoFix.CURRENTUSERID) override val author: String? = null,
	@field:NotNull(autoFix = AutoFix.CURRENTHCPID) override val responsible: String? = null,
	override val medicalLocationId: String? = null,
	@field:ValidCode(autoFix = AutoFix.NORMALIZECODE) override val tags: Set<CodeStub> = emptySet(),
	@field:ValidCode(autoFix = AutoFix.NORMALIZECODE) override val codes: Set<CodeStub> = emptySet(),
	override val endOfLife: Long?,
	@JsonProperty("deleted") override val deletionDate: Long? = null,
	val objectId: String? = null,
	val accessType: String? = null,
	val user: String? = null,
	val detail: String? = null,
	@JsonSerialize(using = InstantSerializer::class, include = JsonSerialize.Inclusion.NON_NULL) @JsonDeserialize(using = InstantDeserializer::class)
	val date: Instant? = null,
	@Deprecated("Use cryptedForeignKeys instead") val patientId: String? = null,
	override val secretForeignKeys: Set<String> = emptySet(),
	override val cryptedForeignKeys: Map<String, Set<Delegation>> = emptyMap(),
	override val delegations: Map<String, Set<Delegation>> = emptyMap(),
	override val encryptionKeys: Map<String, Set<Delegation>> = emptyMap(),
	override val encryptedSelf: String? = null,
	@JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = emptyMap(),
	@JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = emptyList(),
	@JsonProperty("_conflicts") override val conflicts: List<String>? = emptyList(),
	@JsonProperty("rev_history") override val revHistory: Map<String, String>? = emptyMap()

) : StoredICureDocument, Encryptable {
	companion object : DynamicInitializer<AccessLog>

	fun merge(other: AccessLog) = AccessLog(args = this.solveConflictsWith(other))
	fun solveConflictsWith(other: AccessLog) = super<StoredICureDocument>.solveConflictsWith(other) + super<Encryptable>.solveConflictsWith(other) + mapOf(
		"objectId" to (this.objectId ?: other.objectId),
		"accessType" to (this.accessType ?: other.accessType),
		"user" to (this.user ?: other.user),
		"detail" to (this.detail ?: other.detail)
	)

	override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
	override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
	override fun withTimestamps(created: Long?, modified: Long?) =
		when {
			created != null && modified != null -> this.copy(created = created, modified = modified)
			created != null -> this.copy(created = created)
			modified != null -> this.copy(modified = modified)
			else -> this
		}
}
