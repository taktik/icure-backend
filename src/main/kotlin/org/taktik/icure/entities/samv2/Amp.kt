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

package org.taktik.icure.entities.samv2

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import org.taktik.couchdb.entity.Attachment
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.entities.samv2.embed.AmpComponent
import org.taktik.icure.entities.samv2.embed.AmpStatus
import org.taktik.icure.entities.samv2.embed.Ampp
import org.taktik.icure.entities.samv2.embed.Company
import org.taktik.icure.entities.samv2.embed.MedicineType
import org.taktik.icure.entities.samv2.embed.SamText
import org.taktik.icure.entities.samv2.stub.VmpStub

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Amp(
        @JsonProperty("_id") override val id: String,
        @JsonProperty("_rev") override val rev: String? = null,
        @JsonProperty("deleted") override val deletionDate: Long? = null,

        val from: Long? = null,
        val to: Long? = null,
        val code: String? = null,
        val vmp: VmpStub? = null,
        val officialName: String? = null,
        val status: AmpStatus? = null,
        val name: SamText? = null,
        val blackTriangle: Boolean = false,
        val medicineType: MedicineType? = null,
        val company: Company? = null,
        val abbreviatedName: SamText? = null,
        val proprietarySuffix: SamText? = null,
        val prescriptionName: SamText? = null,
        val ampps: Set<Ampp> = setOf(),
        val components: Set<AmpComponent> = setOf(),

        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = mapOf(),
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = listOf(),
        @JsonProperty("_conflicts") override val conflicts: List<String>? = listOf(),
        @JsonProperty("rev_history") override val revHistory: Map<String, String>? = mapOf()
) : StoredDocument {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}
