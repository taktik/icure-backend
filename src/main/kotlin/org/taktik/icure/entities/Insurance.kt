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
import org.ektorp.Attachment
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Insurance(
        @JsonProperty("_id") override val id: String,
        @JsonProperty("_rev") override val rev: String? = null,
        @JsonProperty("deleted") override val deletionDate: Long? = null,

        val name: Map<String, String> = mapOf(),
        val privateInsurance: Boolean = false,
        val hospitalisationInsurance: Boolean = false,
        val ambulatoryInsurance: Boolean = false,
        val code: String? = null,
        val agreementNumber: String? = null,
        val parent: String? = null, //ID of the parent
        val address: Address = Address(),

        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = null,
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = null,
        @JsonProperty("_conflicts") override val conflicts: List<String>? = null,
        @JsonProperty("rev_history") override val revHistory: Map<String, String>? = null

) : StoredDocument {
    companion object : DynamicInitializer<Insurance>

    fun merge(other: Insurance) = Insurance(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: Insurance) = super.solveConflictsWith(other) + mapOf(
            "privateInsurance" to (this.privateInsurance),
            "hospitalisationInsurance" to (this.hospitalisationInsurance),
            "ambulatoryInsurance" to (this.ambulatoryInsurance),
            "code" to (this.code ?: other.code),
            "agreementNumber" to (this.agreementNumber ?: other.agreementNumber),
            "parent" to (this.parent ?: other.parent),
            "address" to (this.address.merge(other.address)),
            "name" to (other.name + this.name)
    )

    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}
