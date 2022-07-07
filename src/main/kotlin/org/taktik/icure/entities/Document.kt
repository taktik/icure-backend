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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import org.taktik.couchdb.entity.Attachment
import org.taktik.icure.asynclogic.objectstorage.DataAttachmentLoader
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.base.Encryptable
import org.taktik.icure.entities.base.HasDataAttachments
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.DataAttachment
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.DeletedAttachment
import org.taktik.icure.entities.embed.DocumentLocation
import org.taktik.icure.entities.embed.DocumentStatus
import org.taktik.icure.entities.embed.DocumentType
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import org.taktik.icure.validation.ValidCode

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder

/**
 * This entity is a root level object. It represents a Document. It is serialized in JSON and saved in the underlying CouchDB database.
 * A Document conforms to a series of interfaces:
 * - StoredICureDocument
 * - Encryptable
 *
 * @property id The Id of the document. We encourage using either a v4 UUID or a HL7 Id.
 * @property rev The revision of the document in the database, used for conflict management / optimistic locking.
 * @property created The timestamp (unix epoch in ms) of creation of the document, will be filled automatically if missing. Not enforced by the application server.
 * @property modified The date (unix epoch in ms) of the latest modification of the document, will be filled automatically if missing. Not enforced by the application server.
 * @property author The id of the User that has created this document, will be filled automatically if missing. Not enforced by the application server.
 * @property responsible The id of the healthcare party that is responsible for this document, will be filled automatically if missing. Not enforced by the application server.
 * @property medicalLocationId The id of the medical location where the document was created.
 * @property tags Tags that qualify the document as being member of a certain class.
 * @property codes Codes that identify or qualify this particular document.
 * @property endOfLife Soft delete (unix epoch in ms) timestamp of the object.
 * @property deletionDate Hard delete (unix epoch in ms) timestamp of the object. Filled automatically when document is deleted.
 * @property size Size of the document file
 * @property hash Hashed version of the document
 * @property openingContactId Id of the contact during which the document was created
 * @property documentLocation Location of the document
 * @property documentType The type of document, ex: admission, clinical path, document report,invoice, etc.
 * @property documentStatus The status of the development of the document. Ex: Draft, finalized, reviewed, signed, etc.
 * @property externalUri When the document is stored in an external repository, this is the uri of the document in that repository
 * @property name Name of the document
 * @property version The document version
 * @property storedICureDocumentId The ICureDocument (Form, Contact, ...) that has been used to generate the document
 * @property externalUuid A unique external id (from another external source).
 * @property attachmentId Id of the couch db attachment holding the main data attachment of this document. Null if the main data attachment is not stored as a couchdb attachment.
 * @property objectStoreReference Id of the main data attachment of this document in the object storage service. Null if the main data attachment is not stored using the object storage service.
 * @property mainUti The main Uniform Type Identifier of the document main data attachment (https://developer.apple.com/library/archive/documentation/FileManagement/Conceptual/understanding_utis/understand_utis_conc/understand_utis_conc.html#//apple_ref/doc/uid/TP40001319-CH202-CHDHIJDE)
 * @property otherUtis Extra Uniform Type Identifiers for thje document main data attachment.
 * @property delegations The delegations giving access to all connected healthcare information.
 * @property encryptionKeys The patient secret encryption key used to encrypt the secured properties (like note for example), encrypted for separate Crypto Actors.
 * @property encryptedSelf The encrypted fields of this document.
 *
 */
data class Document(
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
	val size: Long? = null,
	val hash: String? = null,
	val openingContactId: String? = null,
	val documentLocation: DocumentLocation? = null,
	val documentType: DocumentType? = null,
	val documentStatus: DocumentStatus? = null,
	val externalUri: String? = null,
	val name: String? = null,
	val version: String? = null,
	val storedICureDocumentId: String? = null, //The ICureDocument (Form, Contact, ...) that has been used to generate the document
	val externalUuid: String? = null,
	val attachmentId: String? = null,
	val objectStoreReference: String? = null,
	val mainUti: String? = null,
	val otherUtis: Set<String> = emptySet(),
	val secondaryAttachments: Map<String, DataAttachment> = emptyMap(),
	override val deletedAttachments: List<DeletedAttachment> = emptyList(),
	override val secretForeignKeys: Set<String> = emptySet(),
	override val cryptedForeignKeys: Map<String, Set<Delegation>> = emptyMap(),
	override val delegations: Map<String, Set<Delegation>> = emptyMap(),
	override val encryptionKeys: Map<String, Set<Delegation>> = emptyMap(),
	override val encryptedSelf: String? = null,

	@JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = emptyMap(),
	@JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = emptyList(),
	@JsonProperty("_conflicts") override val conflicts: List<String>? = emptyList(),
	@JsonProperty("rev_history") override val revHistory: Map<String, String>? = emptyMap()

) : StoredICureDocument, Encryptable, HasDataAttachments<Document> {
	companion object : DynamicInitializer<Document>

	val mainAttachmentKey: String get() = id

	val mainAttachment: DataAttachment? by lazy {
		if (attachmentId != null || objectStoreReference != null)
			DataAttachment(
				attachmentId,
				objectStoreReference,
				listOfNotNull(mainUti) + otherUtis
			)
		else
			null
	}

	override val dataAttachments: Map<String, DataAttachment> by lazy {
		mainAttachment?.let { secondaryAttachments + (mainAttachmentKey to it) } ?: secondaryAttachments
	}

	override fun withUpdatedDataAttachment(key: String, newValue: DataAttachment?): Document =
		if (key == mainAttachmentKey) {
			withUpdatedMainAttachment(newValue)
		} else if (newValue != null) {
			copy(secondaryAttachments = secondaryAttachments + (key to newValue))
		} else {
			copy(secondaryAttachments = secondaryAttachments - key)
		}

	override fun withDataAttachments(newDataAttachments: Map<String, DataAttachment>): Document = this
		.copy(secondaryAttachments = newDataAttachments.filter { it.key != mainAttachmentKey })
		.withUpdatedMainAttachment(newDataAttachments[mainAttachmentKey])

	override fun withDeletedAttachments(newDeletedAttachments: List<DeletedAttachment>): Document =
		copy(deletedAttachments = newDeletedAttachments)

	fun merge(other: Document) = Document(args = this.solveConflictsWith(other))
	// TODO update conflict resolution
	fun solveConflictsWith(other: Document) = super<StoredICureDocument>.solveConflictsWith(other) + super<Encryptable>.solveConflictsWith(other) + mapOf(
		"openingContactId" to (this.openingContactId ?: other.openingContactId),
		"documentLocation" to (this.documentLocation ?: other.documentLocation),
		"documentType" to (this.documentType ?: other.documentType),
		"documentStatus" to (this.documentStatus ?: other.documentStatus),
		"externalUri" to (this.externalUri ?: other.externalUri),
		"name" to (this.name ?: other.name),
		"version" to (this.version ?: other.version),
		"storedICureDocumentId" to (this.storedICureDocumentId ?: other.storedICureDocumentId),
		"externalUuid" to (this.externalUuid ?: other.externalUuid),
		"mainUti" to (this.mainUti ?: other.mainUti),
		"otherUtis" to (other.otherUtis + this.otherUtis),
		// TODO can check _attachments to decide
	)/* + if (this.attachment != null && this.attachment.size >= (other.attachment?.size ?: 0)) {
		mapOf(
			"attachmentId" to this.attachmentId,
			"attachment" to this.attachment
		)
	} else {
		mapOf(
			"attachmentId" to other.attachmentId,
			"attachment" to other.attachment
		)
	}*/

	override fun withIdRev(id: String?, rev: String) =
		if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)

	override fun withDeletionDate(deletionDate: Long?) =
		this.copy(deletionDate = deletionDate)

	override fun withTimestamps(created: Long?, modified: Long?) =
		when {
			created != null && modified != null -> this.copy(created = created, modified = modified)
			created != null -> this.copy(created = created)
			modified != null -> this.copy(modified = modified)
			else -> this
		}

	fun withUpdatedMainAttachment(newMainAttachment: DataAttachment?) =
		this.copy(
			attachmentId = newMainAttachment?.couchDbAttachmentId,
			objectStoreReference = newMainAttachment?.objectStoreAttachmentId,
			mainUti = newMainAttachment?.utis?.firstOrNull(),
			otherUtis = newMainAttachment?.utis?.drop(1)?.toSet() ?: emptySet(),
		)
}
