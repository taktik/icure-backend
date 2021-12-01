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
import org.taktik.icure.entities.samv2.embed.CommentedClassification
import org.taktik.icure.entities.samv2.embed.SamText
import org.taktik.icure.entities.samv2.embed.VmpComponent
import org.taktik.icure.entities.samv2.embed.Vtm
import org.taktik.icure.entities.samv2.embed.Wada
import org.taktik.icure.entities.samv2.stub.VmpGroupStub

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Vmp(
        @JsonProperty("_id") override val id: String,
        @JsonProperty("_rev") override val rev: String? = null,
        @JsonProperty("deleted") override val deletionDate: Long? = null,

        val from: Long? = null,
        val to: Long? = null,
        val code: String? = null,
        val vmpGroup: VmpGroupStub? = null,
        val name: SamText? = null,
        val abbreviation: SamText? = null,
        val vtm: Vtm? = null,
        val wadas: Set<Wada>? = null,
        val components: Set<VmpComponent>? = null,
        val commentedClassifications: Set<CommentedClassification>? = null,

        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = emptyMap(),
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = emptyList(),
        @JsonProperty("_conflicts") override val conflicts: List<String>? = emptyList(),
        @JsonProperty("rev_history") override val revHistory: Map<String, String>? = emptyMap()

) : StoredDocument {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}
