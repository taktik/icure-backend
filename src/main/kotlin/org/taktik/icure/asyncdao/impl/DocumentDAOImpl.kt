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

package org.taktik.icure.asyncdao.impl

import java.io.IOException
import java.nio.ByteBuffer
import javax.annotation.PostConstruct
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.output.ByteArrayOutputStream
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.commons.uti.UTI
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.exception.CouchDbConflictException
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.asynclogic.objectstorage.IcureObjectStorage
import org.taktik.icure.entities.Document
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.properties.ObjectStorageProperties
import org.taktik.icure.utils.toByteArray
import org.taktik.icure.utils.writeTo

@FlowPreview
@ExperimentalCoroutinesApi
@Repository("documentDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Document' && !doc.deleted) emit( null, doc._id )}")
class DocumentDAOImpl(
	couchDbProperties: CouchDbProperties,
	@Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher,
	idGenerator: IDGenerator,
	private val icureObjectStorage: IcureObjectStorage,
	private val objectStorageProperties: ObjectStorageProperties
) : GenericDAOImpl<Document>(couchDbProperties, Document::class.java, couchDbDispatcher, idGenerator), DocumentDAO {
	companion object {
		private val log = LoggerFactory.getLogger(DocumentDAOImpl::class.java)
	}

	override suspend fun beforeSave(entity: Document) =
		super.beforeSave(entity).let { document ->
			if (document.attachment != null) {
				val newAttachmentId = DigestUtils.sha256Hex(document.attachment)
				val oldAttachmentId = document.attachmentId ?: document.objectStoreReference
				if (newAttachmentId != oldAttachmentId) {
					val updatedDocument =
						if (oldAttachmentId != null) {
							deleteStoredAttachment(document)
						} else {
							document
						}
					if (
						document.attachment.size >= objectStorageProperties.sizeLimit
							&& icureObjectStorage.preStore(document.id, newAttachmentId, document.attachment) // If cache can't be used fallback to couchdb attachment
					) {
						updatedDocument.copy(objectStoreReference = newAttachmentId, isAttachmentDirty = true)
					} else {
						updatedDocument.copy(attachmentId = newAttachmentId, isAttachmentDirty = true)
					}
				} else {
					document
				}
			} else if (document.attachmentId != null || document.objectStoreReference != null) { // && document.attachment == null
				deleteStoredAttachment(document)
			} else document
		}

	private suspend fun deleteStoredAttachment(document: Document): Document =
		if (document.rev != null) {
			if (document.objectStoreReference != null) {
				icureObjectStorage.scheduleDeleteAttachment(documentId = document.id, attachmentId = document.objectStoreReference)
			}
			/*
			 * Note: the `attachments` property can be just attachment stubs, and as long as we loaded the document from the database and it has any attachments
			 * its value won't be null. Additionally, since all methods which modify the document first load it we are sure this won't be null if a client requested
			 * the change.
			 */
			if (document.attachmentId != null && document.attachments?.containsKey(document.attachmentId) == true) {
				document.copy(
					rev = deleteAttachment(document.id, document.rev, document.attachmentId),
					attachments = document.attachments - document.attachmentId,
					attachmentId = null,
					objectStoreReference = null,
					isAttachmentDirty = false
				)
			} else {
				document.copy(attachmentId = null, objectStoreReference = null, isAttachmentDirty = false)
			}
		} else {
			document
		}


	override suspend fun afterSave(entity: Document) =
		super.afterSave(entity).let { document ->
			if (document.isAttachmentDirty && document.attachmentId != null && document.rev != null && document.attachment != null) {
				val uti = UTI.get(document.mainUti)
				var mimeType = "application/xml"
				if (uti != null && uti.mimeTypes != null && uti.mimeTypes.size > 0) {
					mimeType = uti.mimeTypes[0]
				}
				createAttachment(document.id, document.attachmentId, document.rev, mimeType, flowOf(ByteBuffer.wrap(document.attachment))).let {
					document.copy(rev = it, isAttachmentDirty = false)
				}
			} else if (document.isAttachmentDirty && document.objectStoreReference != null) {
				icureObjectStorage.scheduleStoreAttachment(documentId = document.id, attachmentId = document.objectStoreReference)
				document.copy(isAttachmentDirty = false)
			} else {
				document
			}
		}

	override suspend fun postLoad(entity: Document) = super.postLoad(entity).let { document ->
		if (document.attachmentId != null) {
			tryLoadMigratingAttachment(document) ?: try {
				val attachment = ByteArrayOutputStream().use {
					getAttachment(document.id, document.attachmentId, document.rev).writeTo(it)
					it.toByteArray()
				}
				val documentWithAttachment = document.copy(attachment = attachment)
				if (
					objectStorageProperties.backlogToObjectStorage
						&& attachment.size >= objectStorageProperties.sizeLimit
						&& !icureObjectStorage.isMigrating(documentId = document.id, attachmentId = document.attachmentId)
						&& icureObjectStorage.preStore(documentId = document.id, attachmentId = document.attachmentId, attachment)
				) {
					startMigration(documentWithAttachment, document.attachmentId)
				} else {
					documentWithAttachment
				}
			} catch (e: IOException) {
				log.warn("Could not access couchdb attachment", e)
				document
			}
		} else if (document.objectStoreReference != null) {
			try {
				icureObjectStorage.readAttachment(document.id, document.objectStoreReference)
					.let { document.copy(attachment = it.toByteArray(true)) }
			} catch (e: IOException) {
				log.warn("Could not access object storage attachment", e)
				document
			}
		} else document
	}

	// Faster attachment loading if we are migrating locally (i.e. it is not someone else who is migrating) an attachment from couchdb to the object storage service
	private suspend fun tryLoadMigratingAttachment(document: Document): Document? =
		document.objectStoreReference?.takeIf {
			// Need to check if migrating, checking if attachment is in the cache is not enough: we may have cached the document then failed to start migration due to conflicts.
			icureObjectStorage.isMigrating(documentId = document.id, attachmentId = it)
		}?.let {
			icureObjectStorage.tryReadCachedAttachment(documentId = document.id, attachmentId = it)
		}?.let {
			document.copy(attachment = it.toByteArray(true))
		}

	// Start migration and return the document updated for migration
	private suspend fun startMigration(document: Document, attachmentId: String): Document = try {
		val migratingDocument =
			document.takeIf { it.objectStoreReference == it.attachmentId } // Someone else has updated the document for migration
				?: document.copy(objectStoreReference = document.attachmentId).also { save(it) } // Mark the document as migrating
		icureObjectStorage.scheduleMigrateAttachment(documentId = document.id, attachmentId = attachmentId, this@DocumentDAOImpl)
		migratingDocument
	} catch (_: CouchDbConflictException) {
		document
	}

	@View(name = "conflicts", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Document' && !doc.deleted && doc._conflicts) emit(doc._id )}")
	override fun listConflicts(): Flow<Document> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val viewQuery = createQuery(client, "conflicts")
			.limit(200)
			.includeDocs(true)

		emitAll(client.queryViewIncludeDocsNoValue<String, Document>(viewQuery).map { it.doc })
	}

	@View(name = "by_hcparty_message", map = "classpath:js/document/By_hcparty_message_map.js")
	override fun listDocumentsByHcPartyAndSecretMessageKeys(hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<Document> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val keys = secretForeignKeys.map { fk -> ComplexKey.of(hcPartyId, fk) }

		val viewQuery = createQuery(client, "by_hcparty_message")
			.keys(keys)
			.includeDocs(true)

		emitAll(client.queryViewIncludeDocs<Array<String>, String, Document>(viewQuery).map { it.doc })
	}

	@View(name = "without_delegations", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Document' && !doc.deleted && (!doc.delegations || Object.keys(doc.delegations).length === 0)) emit(doc._id )}")
	override fun listDocumentsWithNoDelegations(limit: Int): Flow<Document> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val viewQuery = createQuery(client, "without_delegations")
			.limit(limit)
			.includeDocs(true)

		emitAll(client.queryViewIncludeDocsNoValue<String, Document>(viewQuery).map { it.doc })
	}

	@View(name = "by_type_hcparty_message", map = "classpath:js/document/By_document_type_hcparty_message_map.js")
	override fun listDocumentsByDocumentTypeHcPartySecretMessageKeys(documentTypeCode: String, hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<Document> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val keys = secretForeignKeys.map { fk -> ComplexKey.of(documentTypeCode, hcPartyId, fk) }

		val viewQuery = createQuery(client, "by_type_hcparty_message")
			.keys(keys)
			.includeDocs(true)

		emitAll(client.queryViewIncludeDocs<Array<String>, String, Document>(viewQuery).map { it.doc })
	}

	override fun readAttachment(documentId: String, attachmentId: String, rev: String?): Flow<ByteBuffer> {
		return getAttachment(documentId, attachmentId, rev)
	}

	@View(name = "by_externalUuid", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Document' && !doc.deleted && doc.externalUuid) emit( doc.externalUuid, doc._id )}")
	override suspend fun listDocumentsByExternalUuid(externalUuid: String): List<Document> {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val viewQuery = createQuery(client, "by_externalUuid")
			.key(externalUuid)
			.includeDocs(true)

		return client.queryViewIncludeDocs<String, String, Document>(viewQuery).map { it.doc /*postLoad(it.doc)*/ }.toList().sortedByDescending { it.created ?: 0 }
	}
}
