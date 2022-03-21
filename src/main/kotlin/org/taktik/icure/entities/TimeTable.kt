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
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.base.Encryptable
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.entities.embed.TimeTableItem
import org.taktik.icure.entities.utils.MergeUtil.mergeListsDistinct
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import org.taktik.icure.validation.ValidCode

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class TimeTable(
        @JsonProperty("_id") override val id: String,
        @JsonProperty("_rev") override val rev: String? = null,
        @field:NotNull(autoFix = AutoFix.NOW) override val created: Long? = null,
        @field:NotNull(autoFix = AutoFix.NOW) override val modified: Long? = null,
        @field:NotNull(autoFix = AutoFix.CURRENTUSERID) override val author: String? = null,
        @field:NotNull(autoFix = AutoFix.CURRENTHCPID) override val responsible: String? = null,
        override val medicalLocationId: String? = null,
        @field:ValidCode(autoFix = AutoFix.NORMALIZECODE) override val tags: Set<CodeStub> = emptySet(),
        @field:ValidCode(autoFix = AutoFix.NORMALIZECODE) override val codes: Set<CodeStub> = emptySet(),
        override val endOfLife: Long? = null,
        @JsonProperty("deleted") override val deletionDate: Long? = null,

        val name: String? = null,
        val agendaId: String? = null,
        @field:NotNull(autoFix = AutoFix.FUZZYNOW) val startTime: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
        @field:NotNull(autoFix = AutoFix.FUZZYNOW) val endTime: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
        val items: List<TimeTableItem> = emptyList(),
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
    companion object : DynamicInitializer<TimeTable>

    fun merge(other: TimeTable) = TimeTable(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: TimeTable) = super<StoredICureDocument>.solveConflictsWith(other) +
            super<Encryptable>.solveConflictsWith(other) + mapOf(
            "name" to (this.name ?: other.name),
            "agendaId" to (this.agendaId ?: other.agendaId),
            "startTime" to (this.startTime ?: other.startTime),
            "endTime" to (this.endTime ?: other.endTime),
            "items" to mergeListsDistinct(this.items, other.items)
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
