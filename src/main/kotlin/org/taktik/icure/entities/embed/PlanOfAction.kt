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
package org.taktik.icure.entities.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.base.ICureDocument
import org.taktik.icure.entities.base.Named
import org.taktik.icure.entities.utils.MergeUtil
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import org.taktik.icure.validation.ValidCode

/**
 * Created by aduchate on 09/07/13, 16:30
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class PlanOfAction(
        @JsonProperty("_id") override val id: String,
        @field:NotNull(autoFix = AutoFix.NOW) override val created: Long? = null,
        @field:NotNull(autoFix = AutoFix.NOW) override val modified: Long? = null,
        @field:NotNull(autoFix = AutoFix.CURRENTUSERID) override val author: String? = null,
        @field:NotNull(autoFix = AutoFix.CURRENTHCPID) override val responsible: String? = null,
        override val medicalLocationId: String? = null,
        @field:ValidCode(autoFix = AutoFix.NORMALIZECODE) override val tags: Set<CodeStub> = setOf(),
        @field:ValidCode(autoFix = AutoFix.NORMALIZECODE) override val codes: Set<CodeStub> = setOf(),
        override val endOfLife: Long? = null,

        //Usually one of the following is used (either valueDate or openingDate and closingDate)
        @field:NotNull(autoFix = AutoFix.FUZZYNOW) val valueDate: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
        @field:NotNull(autoFix = AutoFix.FUZZYNOW) val openingDate: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
        val closingDate: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
        val deadlineDate: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20140101235960.
        override val name: String? = null,
        val descr: String? = null,
        val note: String? = null,
        val relevant: Boolean = true,
        val idOpeningContact: String? = null,
        val idClosingContact: String? = null,
        val status: Int = 0, //bit 0: active/inactive, bit 1: relevant/irrelevant, bit 2 : present/absent, ex: 0 = active,relevant and present

        val documentIds: Set<String> = setOf(),
        val prescriberId: String? = null, //healthcarePartyId
        val numberOfCares: Int? = null,
        val careTeamMemberships: List<CareTeamMembership?> = listOf(),
        override val encryptedSelf: String? = null
) : Encrypted, ICureDocument<String>, Named {
    companion object : DynamicInitializer<PlanOfAction> {
        const val STATUS_PLANNED = 1 shl 0
        const val STATUS_ONGOING = 1 shl 1
        const val STATUS_FINISHED = 1 shl 2
        const val STATUS_PROLONGED = 1 shl 3
        const val STATUS_CANCELED = 1 shl 4
    }

    fun merge(other: PlanOfAction) = PlanOfAction(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: PlanOfAction) = super<Encrypted>.solveConflictsWith(other) + super<ICureDocument>.solveConflictsWith(other) + mapOf(
            "valueDate" to (this.valueDate?.coerceAtMost(other.valueDate ?: Long.MAX_VALUE) ?: other.valueDate),
            "openingDate" to (this.openingDate?.coerceAtMost(other.openingDate ?: Long.MAX_VALUE) ?: other.openingDate),
            "closingDate" to (this.closingDate?.coerceAtLeast(other.closingDate ?: 0L) ?: other.closingDate),
            "deadlineDate" to (this.deadlineDate?.coerceAtMost(other.deadlineDate ?: Long.MAX_VALUE)
                    ?: other.deadlineDate),
            "name" to (this.descr ?: other.descr),
            "descr" to (this.descr ?: other.descr),
            "note" to (this.note ?: other.note),
            "relevant" to (this.relevant ?: other.relevant),
            "idOpeningContact" to (this.idOpeningContact ?: other.idOpeningContact),
            "idClosingContact" to (this.idClosingContact ?: other.idClosingContact),
            "status" to (this.status),

            "documentIds" to (other.documentIds + this.documentIds),
            "prescriberId" to (this.prescriberId ?: other.prescriberId),
            "numberOfCares" to (this.numberOfCares ?: other.numberOfCares),
            "careTeamMemberships" to MergeUtil.mergeListsDistinct(this.careTeamMemberships, other.careTeamMemberships)
    )
    override fun withTimestamps(created: Long?, modified: Long?) =
            when {
                created != null && modified != null -> this.copy(created = created, modified = modified)
                created != null -> this.copy(created = created)
                modified != null -> this.copy(modified = modified)
                else -> this
            }

}
