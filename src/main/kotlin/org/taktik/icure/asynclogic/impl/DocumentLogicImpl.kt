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
package org.taktik.icure.asynclogic.impl

import java.nio.ByteBuffer
import java.util.UUID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapNotNull
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.couchdb.entity.Option
import org.taktik.couchdb.exception.CouchDbException
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.asynclogic.DocumentLogic
import org.taktik.icure.asynclogic.DocumentLogic.DataAttachmentChange
import org.taktik.icure.asynclogic.objectstorage.IcureObjectStorage
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.embed.DataAttachment
import org.taktik.icure.entities.embed.DeletedAttachment
import org.taktik.icure.exceptions.CreationException
import org.taktik.icure.properties.ObjectStorageProperties
import org.taktik.icure.utils.toByteArray

@ExperimentalCoroutinesApi
@Service
class DocumentLogicImpl(
	private val documentDAO: DocumentDAO,
	sessionLogic: AsyncSessionLogicImpl,
	private val icureObjectStorage: IcureObjectStorage,
	private val objectStorageProperties: ObjectStorageProperties
) : GenericLogicImpl<Document, DocumentDAO>(sessionLogic), DocumentLogic {

	override suspend fun createDocument(document: Document, ownerHealthcarePartyId: String) = fix(document) { fixedDocument ->
		try {
			createEntities(setOf(fixedDocument)).firstOrNull()
		} catch (e: Exception) {
			throw CreationException("Could not create document. ", e)
		}
	}

	override suspend fun getDocument(documentId: String): Document? {
		return documentDAO.get(documentId)
	}

	override suspend fun getDocumentsByExternalUuid(documentId: String): List<Document> {
		return documentDAO.listDocumentsByExternalUuid(documentId)
	}

	override fun getDocuments(documentIds: List<String>) = documentDAO.getEntities(documentIds)

	override fun getAttachment(documentId: String, attachmentId: String): Flow<ByteBuffer> = flow {
		emitAll(documentDAO.readAttachment(documentId, attachmentId, null))
	}

	override suspend fun modifyDocument(updatedDocument: Document, strict: Boolean, currentDocument: Document?): Document? = fix(updatedDocument) { newDoc ->
		val baseline = requireNotNull(currentDocument ?: getDocument(newDoc.id)) {
			"Attempting to modify a non-existing document ${newDoc.id}."
		}
		require(newDoc.rev == baseline.rev) { "Updated document has an older revision ${newDoc.rev} -> ${baseline.rev}" }
		val validatedSecondaryAttachments = (baseline.secondaryAttachments.keys + newDoc.secondaryAttachments.keys).mapNotNull { key ->
			validateModifyDataAttachment(
				newDoc.secondaryAttachments[key],
				baseline.secondaryAttachments[key],
				strict,
				secondaryAttachmentString(key)
			)?.let { key to it }
		}.toMap()
		val validatedMainAttachment = validateModifyDataAttachment(newDoc.mainAttachment, baseline.mainAttachment, strict, MAIN_ATTACHMENT)
		try {
			documentDAO.save(newDoc.copy(secondaryAttachments = validatedSecondaryAttachments).withUpdatedMainAttachment(validatedMainAttachment))
		} catch (e: CouchDbException) {
			// TODO taken from previous implementation of modify document
			logger.warn("Documents of class {} with id {} and rev {} could not be merged", newDoc.javaClass.simpleName, newDoc.id, newDoc.rev)
			throw IllegalStateException(e)
		}
	}

	private fun validateModifyDataAttachment(
		updatedAttachment: DataAttachment?,
		currentAttachment: DataAttachment?,
		strict: Boolean,
		attachmentString: String
	): DataAttachment? =
		if (currentAttachment != null) {
			if (updatedAttachment != null && updatedAttachment hasSameIdsAs currentAttachment) {
				updatedAttachment
			} else {
				require(!strict) {
					"Inconsistency between updated and current for $attachmentString with strict mode enabled: " +
						"expected ${currentAttachment.ids} but got ${updatedAttachment?.ids}"
				}
				updatedAttachment?.withIdsOf(currentAttachment) ?: currentAttachment
			}
		} else {
			require(updatedAttachment == null) {
				"Attachment $attachmentString is in updated document but does not exist in current document"
			}
			null
		}

	override suspend fun updateAttachments(
		currentDocument: Document,
		mainAttachmentChange: DataAttachmentChange?,
		secondaryAttachmentsChanges: Map<String, DataAttachmentChange>
	): Document? {
		// First pre-check all the deletions are valid: avoid pre-storing some elements then cancelling the udpate because of invalid deletion
		validateDeleteChanges(currentDocument, mainAttachmentChange, secondaryAttachmentsChanges)
		// Save now time to have consistent deletion times
		val now = System.currentTimeMillis()
		val (updatedDocument, attachmentTasks) =
			secondaryAttachmentsChanges.toList().fold(
				mainAttachmentChange
					?.let { applyChange(currentDocument, null, it, now) }
					?: (currentDocument to emptyList())
			) { (updatedDocument, allAttachmentTasks), (key, change) ->
				applyChange(updatedDocument, key, change, now).let {
					it.first to (allAttachmentTasks + it.second)
				}
			}
		return try {
			// TODO single query to update couchdb attachments?
			val documentWithDeletedCouchDBAttachments = attachmentTasks
				.filterIsInstance<AttachmentTask.DeleteCouchDb>()
				.fold(updatedDocument) { doc, task -> applyAttachmentTask(doc, task) }
			documentDAO.save(documentWithDeletedCouchDBAttachments)?.let { savedDoc ->
				attachmentTasks
					.filterNot { it is AttachmentTask.DeleteCouchDb }
					.fold(savedDoc) { doc, task -> applyAttachmentTask(doc, task) }
			}
		} catch (e: CouchDbException) {
			// TODO taken from previous implementation of modify document
			logger.warn("Documents of class {} with id {} and rev {} could not be merged", updatedDocument.javaClass.simpleName, updatedDocument.id, updatedDocument.rev)
			throw IllegalStateException(e)
		}
	}

	private fun validateDeleteChanges(
		currentDocument: Document,
		mainAttachmentChange: DataAttachmentChange?,
		secondaryAttachmentsChanges: Map<String, DataAttachmentChange>
	) {
		mainAttachmentChange?.takeIf { it.isDelete }?.also {
			requireNotNull(currentDocument.mainAttachment) { "Can't delete non-existing $MAIN_ATTACHMENT."}
		}
		secondaryAttachmentsChanges.asSequence().filter { it.value.isDelete }.map { it.key }.forEach {
			require(it in currentDocument.secondaryAttachments) { "Can't delete non-existing ${secondaryAttachmentString(it)}" }
		}
	}

	private suspend fun applyChange(
		document: Document,
		key: String?,
		change: DataAttachmentChange,
		deletionTime: Long
	): Pair<Document, List<AttachmentTask>> = when (change) {
		is DataAttachmentChange.CreateOrUpdate -> createOrUpdateAttachment(document, key, change, deletionTime)
		DataAttachmentChange.Delete -> deleteAttachment(document, key, deletionTime)
	}

	private suspend fun createOrUpdateAttachment(
		document: Document,
		key: String?,
		change: DataAttachmentChange.CreateOrUpdate,
		deletionTime: Long
	 ): Pair<Document, List<AttachmentTask>> {
		val (updatedDocument, newUtis, deleteTasks) =
			document.attachmentOf(key)?.let { previousAttachment ->
				val (doc, tasks) = deleteAttachment(document, key, deletionTime)
				Triple(doc, change.utis ?: previousAttachment.utis, tasks)
			} ?: Triple(document, change.utis ?: emptyList(), emptyList())
		val (newAttachment, uploadTask) = createAttachment(updatedDocument, change, newUtis)
		val docWithNewAttachment =
			if (key != null) {
				updatedDocument.copy(secondaryAttachments = updatedDocument.secondaryAttachments + (key to newAttachment))
			} else {
				updatedDocument.withUpdatedMainAttachment(newAttachment)
			}
		return docWithNewAttachment to (deleteTasks + uploadTask)
	}

	private suspend fun createAttachment(
		document: Document,
		change: DataAttachmentChange.CreateOrUpdate,
		utis: List<String>
	): Pair<DataAttachment, AttachmentTask> =
		if (change.size == null || change.size >= objectStorageProperties.sizeLimit)
			tryCreateObjectStorageAttachment(document, change, utis) ?: createCouchDbAttachment(change, utis)
		else
			createCouchDbAttachment(change, utis)

	private suspend fun tryCreateObjectStorageAttachment(
		document: Document,
		change: DataAttachmentChange.CreateOrUpdate,
		utis: List<String>
	): Pair<DataAttachment, AttachmentTask>? {
		val attachmentId = UUID.randomUUID().toString()
		return if (icureObjectStorage.preStore(document.id, attachmentId, change.data))
			DataAttachment(couchDbAttachmentId = null, objectStoreAttachmentId = attachmentId, utis = utis).let {
				it to AttachmentTask.UploadObjectStorage(attachmentId, it.mimeTypeOrDefault)
			}
		else null
	}

	private suspend fun createCouchDbAttachment(
		change: DataAttachmentChange.CreateOrUpdate,
		utis: List<String>
	): Pair<DataAttachment, AttachmentTask> {
		val bytes = change.data.toByteArray(true)
		// TODO are we sure we don't want to unify always to random UUID? Can also avoid saving as bytes
		val attachmentId = DigestUtils.sha256Hex(bytes)
		return DataAttachment(couchDbAttachmentId = attachmentId, objectStoreAttachmentId = null, utis = utis).let {
			it to AttachmentTask.UploadCouchDb(attachmentId, bytes, it.mimeTypeOrDefault)
		}
	}

	private fun deleteAttachment(
		document: Document,
		key: String?,
		deletionTime: Long
	): Pair<Document, List<AttachmentTask>> {
		val attachment = checkNotNull(document.attachmentOf(key)) {
			"Should have already ensured all attachments getting deleted exist ($key)"
		}
		val deletedInfo = DeletedAttachment(attachment.couchDbAttachmentId, attachment.objectStoreAttachmentId, key, deletionTime)
		val updatedDocument =
			if (key != null) {
				document.copy(secondaryAttachments = document.secondaryAttachments - key, deletedAttachments = document.deletedAttachments + deletedInfo)
			} else {
				document.copy(deletedAttachments = document.deletedAttachments + deletedInfo).withUpdatedMainAttachment(null)
			}
		return updatedDocument to listOfNotNull(
			attachment.couchDbAttachmentId?.let { AttachmentTask.DeleteCouchDb(it) },
			attachment.objectStoreAttachmentId?.let { AttachmentTask.DeleteObjectStorage(it) }
		)
	}

	private suspend fun applyAttachmentTask(
		document: Document,
		task: AttachmentTask
	): Document = when (task) {
		is AttachmentTask.DeleteCouchDb -> if (document.attachments?.let { task.attachmentId in it} == true) {
			document.copy(
				rev = documentDAO.deleteAttachment(document.id, document.rev!!, task.attachmentId),
				attachments = document.attachments - task.attachmentId
			)
		} else document
		is AttachmentTask.DeleteObjectStorage ->
			document.also { icureObjectStorage.scheduleDeleteAttachment(it.id, task.attachmentId) }
		is AttachmentTask.UploadCouchDb -> document.copy(
			rev = documentDAO.createAttachment(
				document.id,
				task.attachmentId,
				document.rev!!,
				task.mimeType,
				flowOf(ByteBuffer.wrap(task.data))
			)
		)
		is AttachmentTask.UploadObjectStorage ->
			document.also { icureObjectStorage.scheduleStoreAttachment(it.id, task.attachmentId) }
	}

	override fun listDocumentsByDocumentTypeHCPartySecretMessageKeys(documentTypeCode: String, hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<Document> = flow {
		emitAll(documentDAO.listDocumentsByDocumentTypeHcPartySecretMessageKeys(documentTypeCode, hcPartyId, secretForeignKeys))
	}

	override fun listDocumentsByHCPartySecretMessageKeys(hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<Document> = flow {
		emitAll(documentDAO.listDocumentsByHcPartyAndSecretMessageKeys(hcPartyId, secretForeignKeys))
	}

	override fun listDocumentsWithoutDelegation(limit: Int): Flow<Document> = flow {
		emitAll(documentDAO.listDocumentsWithNoDelegations(limit))
	}

	override fun modifyDocuments(documents: List<Document>): Flow<Document> = flow {
		emitAll(documentDAO.save(documents))
	}

	override fun solveConflicts(ids: List<String>?): Flow<Document> {
		val documentsInConflict = ids?.asFlow()?.mapNotNull { documentDAO.get(it, Option.CONFLICTS) }
			?: documentDAO.listConflicts().mapNotNull { documentDAO.get(it.id, Option.CONFLICTS) }
		return documentsInConflict.mapNotNull {
			documentDAO.get(it.id, Option.CONFLICTS)?.let { document ->
				document.conflicts?.mapNotNull { conflictingRevision -> documentDAO.get(document.id, conflictingRevision) }
					?.fold(document) { kept, conflict -> kept.merge(conflict).also { documentDAO.purge(conflict) } }
					?.let { mergedDocument -> documentDAO.save(mergedDocument) }
			}
		}
	}

	override fun getGenericDAO(): DocumentDAO {
		return documentDAO
	}

	private val DataAttachmentChange.isDelete get() = this === DataAttachmentChange.Delete

	private fun Document.attachmentOf(key: String?): DataAttachment? =
		key?.let { secondaryAttachments[it] } ?: mainAttachment

	private fun secondaryAttachmentString(key: String) = "secondary attachment \"$key\""

	private sealed class AttachmentTask { // TODO change to sealed interface on kotlin 1.5+
		class UploadCouchDb(val attachmentId: String, val data: ByteArray, val mimeType: String) : AttachmentTask()
		class UploadObjectStorage(val attachmentId: String, val mimeType: String) : AttachmentTask()
		class DeleteCouchDb(val attachmentId: String) : AttachmentTask()
		class DeleteObjectStorage(val attachmentId: String) : AttachmentTask()
	}

	companion object {
		private val logger = LoggerFactory.getLogger(DocumentLogicImpl::class.java)
		private const val MAIN_ATTACHMENT = "main attachment"
	}
}
