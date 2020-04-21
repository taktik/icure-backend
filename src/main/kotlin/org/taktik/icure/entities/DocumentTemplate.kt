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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.ektorp.Attachment
import org.taktik.icure.entities.base.*
import org.taktik.icure.entities.embed.DocumentGroup
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import org.taktik.icure.validation.ValidCode

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class DocumentTemplate(
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

        @JsonIgnore val attachment: ByteArray? = null,
        @JsonIgnore var isAttachmentDirty: Boolean = false,
        val mainUti: String? = null,
        val name: String? = null,
        val otherUtis: Set<String> = setOf(),
        val attachmentId: String? = null,
        val version: ReportVersion? = null,
        val owner: String? = null,
        val guid: String? = null,
        val group: DocumentGroup? = null,
        val descr: String? = null,
        val disabled: String? = null,
        val specialty: Code? = null,

        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = null,
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = null,
        @JsonProperty("_conflicts") override val conflicts: List<String>? = null,
        @JsonProperty("rev_history") override val revHistory: Map<String, String>? = null,
        @JsonProperty("java_type") override val _type: String = DocumentTemplate::javaClass.name
) : StoredDocument, ICureDocument {
    companion object : DynamicInitializer<DocumentTemplate>
    fun merge(other: DocumentTemplate) = DocumentTemplate(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: DocumentTemplate) = super<StoredDocument>.solveConflictsWith(other) + super<ICureDocument>.solveConflictsWith(other) + mapOf(
            "mainUti" to (this.mainUti ?:other.mainUti),
            "name" to (this.name ?:other.name),
            "otherUtis" to (other.otherUtis + this.otherUtis),
            "attachmentId" to (this.attachmentId ?:other.attachmentId),
            "version" to (this.version ?:other.version),
            "owner" to (this.owner ?:other.owner),
            "guid" to (this.guid ?:other.guid),
            "group" to (this.group ?:other.group),
            "descr" to (this.descr ?:other.descr),
            "disabled" to (this.disabled ?:other.disabled),
            "specialty" to (this.specialty ?:other.specialty),
            "attachment" to (this.attachment?.let { if(it.size>=other.attachment?.size ?: 0) it else other.attachment} ?: other.attachment )
    )
    override fun withIdRev(id: String?, rev: String): DocumentTemplate =
            if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
}
