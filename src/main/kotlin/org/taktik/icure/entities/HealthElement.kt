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
package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import org.ektorp.Attachment
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.base.Encryptable
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.CareTeamMember
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.Episode
import org.taktik.icure.entities.embed.Laterality
import org.taktik.icure.entities.embed.PlanOfAction
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.entities.utils.MergeUtil.mergeListsDistinct
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import org.taktik.icure.validation.ValidCode
import javax.validation.Valid

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class HealthElement(
        @JsonProperty("_id") override val id: String,
        @JsonProperty("_rev") override val rev: String? = null,
        @field:NotNull(autoFix = AutoFix.NOW) override val created: Long? = null,
        @field:NotNull(autoFix = AutoFix.NOW) override val modified: Long? = null,
        @field:NotNull(autoFix = AutoFix.CURRENTUSERID) override val author: String? = null,
        @field:NotNull(autoFix = AutoFix.CURRENTHCPID) override val responsible: String? = null,
        override val medicalLocationId: String? = null,
        @field:ValidCode(autoFix = AutoFix.NORMALIZECODE) override val tags: Set<CodeStub> = setOf(),
        @field:ValidCode(autoFix = AutoFix.NORMALIZECODE) override val codes: Set<CodeStub> = setOf(),
        override val endOfLife: Long? = null,
        @JsonProperty("deleted") override val deletionDate: Long? = null,

        @field:NotNull(autoFix = AutoFix.UUID) val healthElementId: String? = null, //Several versions of the same health element share the same healthElementId while having different ids
        //Usually one of the following is used (either valueDate or openingDate and closingDate)
        @field:NotNull(autoFix = AutoFix.FUZZYNOW) val valueDate: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
        @field:NotNull(autoFix = AutoFix.FUZZYNOW) val openingDate: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
        val closingDate: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
        val descr: String? = null,
        val note: String? = null,
        val relevant: Boolean = true,
        val idOpeningContact: String? = null,
        val idClosingContact: String? = null,
        val idService: String? = null, //When a service is used to create the healthElement
        val status: Int = 0, //bit 0: active/inactive, bit 1: relevant/irrelevant, bit 2 : present/absent, ex: 0 = active,relevant and present
        val laterality: Laterality? = null,
        @field:Valid val plansOfAction: List<PlanOfAction> = listOf(),
        @field:Valid val episodes: List<Episode> = listOf(),
        val careTeam: List<CareTeamMember> = listOf(),

        override val secretForeignKeys: Set<String> = setOf(),
        override val cryptedForeignKeys: Map<String, Set<Delegation>> = mapOf(),
        override val delegations: Map<String, Set<Delegation>> = mapOf(),
        override val encryptionKeys: Map<String, Set<Delegation>> = mapOf(),
        override val encryptedSelf: String? = null,
        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = null,
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = null,
        @JsonProperty("_conflicts") override val conflicts: List<String>? = null,
        @JsonProperty("rev_history") override val revHistory: Map<String, String>? = null

) : StoredICureDocument, Encryptable {
    companion object : DynamicInitializer<HealthElement>

    fun merge(other: HealthElement) = HealthElement(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: HealthElement) = super<StoredICureDocument>.solveConflictsWith(other) + super<Encryptable>.solveConflictsWith(other) + mapOf(
            "healthElementId" to (this.healthElementId ?: other.healthElementId),
            "valueDate" to (valueDate?.coerceAtMost(other.valueDate ?: Long.MAX_VALUE) ?: other.valueDate),
            "openingDate" to (openingDate?.coerceAtMost(other.openingDate ?: Long.MAX_VALUE) ?: other.openingDate),
            "closingDate" to (closingDate?.coerceAtLeast(other.closingDate ?: 0L) ?: other.closingDate),
            "descr" to (this.descr ?: other.descr),
            "note" to (this.note ?: other.note),
            "relevant" to (this.relevant),
            "idOpeningContact" to (this.idOpeningContact ?: other.idOpeningContact),
            "idClosingContact" to (this.idClosingContact ?: other.idClosingContact),
            "idService" to (this.idService ?: other.idService),
            "status" to (this.status),
            "laterality" to (this.laterality ?: other.laterality),
            "plansOfAction" to mergeListsDistinct(this.plansOfAction, other.plansOfAction, { a, b -> a.id == b.id }, { a, b -> a.merge(b) }),
            "episodes" to mergeListsDistinct(this.episodes, other.episodes, { a, b -> a.id == b.id }, { a, b -> a.merge(b) }),
            "careTeam" to mergeListsDistinct(this.careTeam, other.careTeam, { a, b -> a.id == b.id }, { a, b -> a.merge(b) })
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
