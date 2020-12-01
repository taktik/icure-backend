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
import com.github.pozo.KotlinBuilder
import org.ektorp.Attachment
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.base.Encryptable
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.DocumentLocation
import org.taktik.icure.entities.embed.DocumentStatus
import org.taktik.icure.entities.embed.DocumentType
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.security.CryptoUtils
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import org.taktik.icure.validation.ValidCode
import java.nio.ByteBuffer
import java.security.GeneralSecurityException
import java.security.KeyException
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Document(
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
        val size: Long? = null,
        val hash: String? = null,
        val openingContactId: String? = null,

        @JsonIgnore val attachment: ByteArray? = null,
        @JsonIgnore var isAttachmentDirty: Boolean = false,
        val documentLocation: DocumentLocation? = null,
        val documentType: DocumentType? = null,
        val documentStatus: DocumentStatus? = null,
        val externalUri: String? = null,
        val mainUti: String? = null,
        val name: String? = null,
        val version: String? = null,
        val otherUtis: Set<String> = setOf(),
        val storedICureDocumentId: String? = null, //The ICureDocument (Form, Contact, ...) that has been used to generate the document
        val externalUuid: String? = null,

        val attachmentId: String? = null,

        val idOpeningContact: String? = null,
        val idClosingContact: String? = null,

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
    companion object : DynamicInitializer<Document>

    fun merge(other: Document) = Document(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: Document) = super<StoredICureDocument>.solveConflictsWith(other) + super<Encryptable>.solveConflictsWith(other) + mapOf(
            "documentLocation" to (this.documentLocation ?: other.documentLocation),
            "documentType" to (this.documentType ?: other.documentType),
            "documentStatus" to (this.documentStatus ?: other.documentStatus),
            "externalUri" to (this.externalUri ?: other.externalUri),
            "mainUti" to (this.mainUti ?: other.mainUti),
            "name" to (this.name ?: other.name),
            "version" to (this.version ?: other.version),
            "idOpeningContact" to (this.idOpeningContact ?: other.idOpeningContact),
            "idClosingContact" to (this.idClosingContact ?: other.idClosingContact),
            "otherUtis" to (other.otherUtis + this.otherUtis),
            "storedICureDocumentId" to (this.storedICureDocumentId ?: other.storedICureDocumentId),
            "externalUuid" to (this.externalUuid ?: other.externalUuid),
            "attachmentId" to (this.attachmentId ?: other.attachmentId),
            "attachment" to (this.attachment?.let { if (it.size >= other.attachment?.size ?: 0) it else other.attachment }
                    ?: other.attachment)
    )

    fun decryptAttachment(enckeys: List<String?>?): ByteArray? {
        if (enckeys?.isNotEmpty() == true) {
            for (sfk in enckeys) {
                val bb = ByteBuffer.wrap(ByteArray(16))
                val uuid = UUID.fromString(sfk)
                bb.putLong(uuid.mostSignificantBits)
                bb.putLong(uuid.leastSignificantBits)
                try {
                    return CryptoUtils.decryptAES(attachment, bb.array())
                } catch (ignored: GeneralSecurityException) {
                } catch (ignored: KeyException) {
                } catch (ignored: IllegalArgumentException) {
                }
            }
        }
        return attachment
    }

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
