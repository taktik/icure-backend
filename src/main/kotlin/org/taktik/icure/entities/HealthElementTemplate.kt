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
import org.ektorp.Attachment
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.base.ICureDocument
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.PlanOfActionTemplate
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.entities.utils.MergeUtil
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import org.taktik.icure.validation.ValidCode
import javax.validation.Valid

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class HealthElementTemplate(
        @JsonProperty("_id") override val id: String,
        @JsonProperty("_rev") override val rev: String? = null,
        @NotNull(autoFix = AutoFix.NOW) override val created: Long? = null,
        @NotNull(autoFix = AutoFix.NOW) override val modified: Long? = null,
        @NotNull(autoFix = AutoFix.CURRENTUSERID) override val author: String? = null,
        @NotNull(autoFix = AutoFix.CURRENTHCPID) override val responsible: String? = null,
        @ValidCode(autoFix = AutoFix.NORMALIZECODE) override val tags: Set<CodeStub> = setOf(),
        @ValidCode(autoFix = AutoFix.NORMALIZECODE) override val codes: Set<CodeStub> = setOf(),
        override val endOfLife: Long? = null,
        @JsonProperty("deleted") override val deletionDate: Long? = null,
        val descr: String? = null,
        val note: String? = null,
        val status : Int = 0, //bit 0: active/inactive, bit 1: relevant/irrelevant, bit 2 : present/absent, ex: 0 = active,relevant and present
        val isRelevant : Boolean = true,
        val plansOfAction: @Valid List<PlanOfActionTemplate> = listOf(),
        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = null,
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = null,
        @JsonProperty("_conflicts") override val conflicts: List<String>? = null,
        @JsonProperty("rev_history") override val revHistory: Map<String, String>? = null,
        @JsonProperty("java_type") override val _type: String = HealthElementTemplate::javaClass.name
) : StoredDocument, ICureDocument {
    companion object : DynamicInitializer<HealthElementTemplate>
    fun merge(other: HealthElementTemplate) = HealthElementTemplate(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: HealthElementTemplate) = super<StoredDocument>.solveConflictsWith(other) + super<ICureDocument>.solveConflictsWith(other) + mapOf(
            "descr" to (this.descr ?: other.descr),
            "note" to (this.note ?: other.note),
            "isRelevant" to (this.isRelevant),
            "status" to (this.status),
            "plansOfAction" to MergeUtil.mergeListsDistinct(this.plansOfAction, other.plansOfAction, { a, b -> a.id == b.id }, { a, b -> a.merge(b) })
    )
    override fun withIdRev(id: String?, rev: String): HealthElementTemplate =
            if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
}
