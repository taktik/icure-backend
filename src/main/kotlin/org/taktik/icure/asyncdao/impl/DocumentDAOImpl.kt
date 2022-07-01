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

import java.nio.ByteBuffer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.stereotype.Repository
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.asynclogic.objectstorage.DataAttachmentLoadingContext
import org.taktik.icure.asynclogic.objectstorage.IcureObjectStorage
import org.taktik.icure.asynclogic.objectstorage.IcureObjectStorageMigration
import org.taktik.icure.be.ehealth.logic.kmehr.validSsinOrNull
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.embed.DataAttachment
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.properties.ObjectStorageProperties
import org.taktik.icure.utils.toByteArray

@FlowPreview
@ExperimentalCoroutinesApi
@Repository("documentDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Document' && !doc.deleted) emit( null, doc._id )}")
class DocumentDAOImpl(
	couchDbProperties: CouchDbProperties,
	@Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher,
	idGenerator: IDGenerator,
	private val icureObjectStorage: IcureObjectStorage,
	private val icureObjectStorageMigration: IcureObjectStorageMigration,
	private val objectStorageProperties: ObjectStorageProperties
) : GenericDAOImpl<Document>(couchDbProperties, Document::class.java, couchDbDispatcher, idGenerator), DocumentDAO {

	override suspend fun afterSave(entity: Document) = super.afterSave(entity).let(::setLoadingContext)

	override suspend fun postLoad(entity: Document) = super.postLoad(entity).let(::setLoadingContext)

	private fun setLoadingContext(document: Document): Document {
		val loadingContext = AttachmentLoadingContext(this, icureObjectStorage, icureObjectStorageMigration, objectStorageProperties, document.id)
		return (
			document.secondaryAttachments.takeIf { it.isNotEmpty() }?.let {
				document.copy(secondaryAttachments = it.mapValues { (_, v) -> v.copy(loadingContext = loadingContext) })
			} ?: document
		).let { updatedDocument ->
			updatedDocument.mainAttachment?.let {
				updatedDocument.withUpdatedMainAttachment(it.copy(loadingContext = loadingContext))
			} ?: updatedDocument
		}
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

	private class AttachmentLoadingContext(
		private val dao: DocumentDAO,
		private val icureObjectStorage: IcureObjectStorage,
		private val icureObjectStorageMigration: IcureObjectStorageMigration,
		private val objectStorageProperties: ObjectStorageProperties,
		private val documentId: String
	) : DataAttachmentLoadingContext {
		override fun DataAttachment.loadFlow(): Flow<DataBuffer> =
			objectStoreAttachmentId?.let { icureObjectStorage.readAttachment(documentId, it) } ?: couchDbAttachmentId!!.let { attachmentId ->
				if (icureObjectStorageMigration.isMigrating(documentId, attachmentId)) {
					icureObjectStorage.tryReadCachedAttachment(documentId, attachmentId) ?: loadCouchDbAttachment(attachmentId)
				} else if (objectStorageProperties.backlogToObjectStorage) flow {
					val bytes = loadCouchDbAttachment(attachmentId).toByteArray(true)
					if (bytes.size >= objectStorageProperties.sizeLimit && icureObjectStorageMigration.preMigrate(documentId, attachmentId, bytes)) {
						icureObjectStorageMigration.scheduleMigrateAttachment(documentId, attachmentId, dao)
					}
					emit(DefaultDataBufferFactory.sharedInstance.wrap(bytes))
				} else loadCouchDbAttachment(attachmentId)
			}

		private fun loadCouchDbAttachment(attachmentId: String) =
			dao.getAttachment(documentId, attachmentId).map { DefaultDataBufferFactory.sharedInstance.wrap(it) }
	}
}
