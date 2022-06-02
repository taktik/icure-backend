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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.output.ByteArrayOutputStream
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.commons.uti.UTI
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.entities.Document
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.writeTo

@Repository("documentDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Document' && !doc.deleted) emit( null, doc._id )}")
class DocumentDAOImpl(
	couchDbProperties: CouchDbProperties,
	@Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher,
	idGenerator: IDGenerator
) : GenericDAOImpl<Document>(couchDbProperties, Document::class.java, couchDbDispatcher, idGenerator), DocumentDAO {

	override suspend fun beforeSave(entity: Document) =
		super.beforeSave(entity).let { document ->
			if (document.attachment != null) {
				val newAttachmentId = DigestUtils.sha256Hex(document.attachment)

				if (newAttachmentId != document.attachmentId && document.id != null && document.rev != null && document.attachmentId != null) {
					document.attachments?.containsKey(document.attachmentId)?.takeIf { it }?.let {
						document.copy(
							rev = deleteAttachment(document.id, document.rev!!, document.attachmentId!!),
							attachments = document.attachments - document.attachmentId,
							attachmentId = newAttachmentId,
							isAttachmentDirty = true
						)
					} ?: document.copy(
						attachmentId = newAttachmentId,
						isAttachmentDirty = true
					)
				} else
					document.copy(isAttachmentDirty = true, attachmentId = newAttachmentId)
			} else {
				if (document.attachmentId != null && document.rev != null) {
					document.copy(
						rev = deleteAttachment(document.id, document.rev, document.attachmentId),
						attachmentId = null,
						isAttachmentDirty = false
					)
				} else document
			}
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
					document.copy(
						rev = it,
						isAttachmentDirty = false
					)
				}
			} else document
		}

	override suspend fun postLoad(entity: Document) =
		super.postLoad(entity).let { document ->
			if (document.attachmentId != null) {
				try {
					val attachmentFlow = getAttachment(document.id, document.attachmentId, document.rev)
					document.copy(
						attachment = ByteArrayOutputStream().use {
							attachmentFlow.writeTo(it)
							it.toByteArray()
						}
					)
				} catch (e: IOException) {
					document //Could not load
				}
			} else document
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
