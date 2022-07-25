package org.taktik.icure.asynclogic.objectstorage.impl

import java.security.GeneralSecurityException
import java.security.KeyException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.stereotype.Service
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.asynclogic.objectstorage.DataAttachmentLoader
import org.taktik.icure.asynclogic.objectstorage.DocumentDataAttachmentLoader
import org.taktik.icure.asynclogic.objectstorage.DocumentObjectStorage
import org.taktik.icure.asynclogic.objectstorage.DocumentObjectStorageMigration
import org.taktik.icure.asynclogic.objectstorage.IcureObjectStorage
import org.taktik.icure.asynclogic.objectstorage.IcureObjectStorageMigration
import org.taktik.icure.asynclogic.objectstorage.contentBytesOfNullable
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.base.HasDataAttachments
import org.taktik.icure.entities.embed.DataAttachment
import org.taktik.icure.properties.ObjectStorageProperties
import org.taktik.icure.security.CryptoUtils
import org.taktik.icure.security.CryptoUtils.isValidAesKey
import org.taktik.icure.security.CryptoUtils.keyFromHexString

class DataAttachmentLoaderImpl<T : HasDataAttachments<T>>(
	private val dao: GenericDAO<T>,
	private val icureObjectStorage: IcureObjectStorage<T>,
	private val icureObjectStorageMigration: IcureObjectStorageMigration<T>,
	private val objectStorageProperties: ObjectStorageProperties
): DataAttachmentLoader<T> {
	override fun contentFlowOf(
		target: T,
		retrieveAttachment: T.() -> DataAttachment
	): Flow<DataBuffer> = target.retrieveAttachment().let { attachment ->
		attachment.contentFlowFromCacheOrLoad { doLoadFlow(target, attachment) }
	}

	override suspend fun contentBytesOf(
		target: T,
		retrieveAttachment: T.() -> DataAttachment
	): ByteArray = target.retrieveAttachment().let { attachment ->
		attachment.contentBytesFromCacheOrLoad { doLoadFlow(target, attachment) }
	}

	private fun doLoadFlow(target: T, attachment: DataAttachment): Flow<DataBuffer> =
		attachment.objectStoreAttachmentId?.let {
			icureObjectStorage.readAttachment(target, it)
		} ?: attachment.couchDbAttachmentId!!.let { attachmentId ->
			if (icureObjectStorageMigration.isMigrating(target, attachmentId)) {
				icureObjectStorage.tryReadCachedAttachment(target, attachmentId) ?: loadCouchDbAttachment(target, attachmentId)
			} else {
				if (shouldMigrate(target, attachmentId)) icureObjectStorageMigration.scheduleMigrateAttachment(target, attachmentId)
				loadCouchDbAttachment(target, attachmentId)
			}
		}

	private fun loadCouchDbAttachment(target: T, attachmentId: String) =
		dao.getAttachment(target.id, attachmentId).map { DefaultDataBufferFactory.sharedInstance.wrap(it) }

	private fun shouldMigrate(target: T, attachmentId: String) =
		// TODO maybe we want to have a bigger size limit for migration, to limit the amount of migration task actually executed
		objectStorageProperties.backlogToObjectStorage
			&& target.attachments?.get(attachmentId)?.let { it.contentLength >= objectStorageProperties.sizeLimit } == true
}

@Service
class DocumentDataAttachmentLoaderImpl(
	dao: DocumentDAO,
	objectStorage: DocumentObjectStorage,
	objectStorageMigration: DocumentObjectStorageMigration,
	objectStorageProperties: ObjectStorageProperties
) : DocumentDataAttachmentLoader, DataAttachmentLoader<Document> by DataAttachmentLoaderImpl(
	dao,
	objectStorage,
	objectStorageMigration,
	objectStorageProperties
) {
	override suspend fun decryptAttachment(document: Document?, enckeys: String?, retrieveAttachment: Document.() -> DataAttachment?): ByteArray? =
		decryptAttachment(document, if (enckeys.isNullOrBlank()) emptyList() else enckeys.split(','), retrieveAttachment)

	override suspend fun decryptAttachment(document: Document?, enckeys: List<String>, retrieveAttachment: Document.() -> DataAttachment?): ByteArray? =
		contentBytesOfNullable(document, retrieveAttachment)?.let { content ->
			enckeys.asSequence()
				.filter { sfk -> sfk.keyFromHexString().isValidAesKey() }
				.mapNotNull { sfk ->
					try {
						CryptoUtils.decryptAES(content, sfk.keyFromHexString())
					} catch (_: GeneralSecurityException) {
						null
					} catch (_: KeyException) {
						null
					} catch (_: IllegalArgumentException) {
						null
					}
				}
				.firstOrNull()
				?: content
		}
}
