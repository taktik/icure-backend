package org.taktik.icure.asynclogic.objectstorage

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.base.HasDataAttachments
import org.taktik.icure.entities.embed.DataAttachment

/**
 * Allows loading attachments for an entity of type [T].
 */
interface DataAttachmentLoader<T : HasDataAttachments<T>> {
	/**
	 * Load the content of an attachment as a flow.
	 * This method does not automatically cache the content, but if the content was loaded as byte in
	 * the past the resulting flow will rely on the cached data.
	 * Note that cached data is shared between all users, thus it should not be modified.
	 * @param target the entity for which you want to load an attachment.
	 * @param retrieveAttachment provides the information on the attachment you want to load.
	 * @return the content of the data attachment as a flow.
	 */
	fun contentFlowOf(target: T, retrieveAttachment: T.() -> DataAttachment): Flow<DataBuffer>

	/**
	 * Load the content of an attachment as a byte array, automatically caching it.
	 * Note that cached data is shared between all users, thus it should not be modified.
	 * @param target the entity for which you want to load an attachment.
	 * @param retrieveAttachment provides the information on the attachment you want to load.
	 * @return the content of the data attachment as a byte array.
	 */
	suspend fun contentBytesOf(target: T, retrieveAttachment: T.() -> DataAttachment): ByteArray
}

/**
 * Nullable version of [DataAttachmentLoader.contentFlowOf]
 */
fun <T : HasDataAttachments<T>> DataAttachmentLoader<T>.contentFlowOfNullable(target: T?, retrieveAttachment: T.() -> DataAttachment?): Flow<DataBuffer>? =
	target?.let { t -> t.retrieveAttachment()?.let { contentFlowOf(t) { it } } }

/**
 * Automatically retrieve the attachment by key.
 */
fun <T : HasDataAttachments<T>> DataAttachmentLoader<T>.contentFlowOfNullable(target: T?, key: String): Flow<DataBuffer>? =
	contentFlowOfNullable(target) { dataAttachments[key] }

/**
 * Nullable version of [DataAttachmentLoader.contentBytesOf]
 */
suspend fun <T : HasDataAttachments<T>> DataAttachmentLoader<T>.contentBytesOfNullable(target: T?, retrieveAttachment: T.() -> DataAttachment?): ByteArray? =
	target?.let { t -> t.retrieveAttachment()?.let { contentBytesOf(t) { it } } }

/**
 * Automatically retrieve the attachment by key.
 */
suspend fun <T : HasDataAttachments<T>> DataAttachmentLoader<T>.contentBytesOfNullable(target: T?, key: String): ByteArray? =
	contentBytesOfNullable(target) { dataAttachments[key] }


interface DocumentDataAttachmentLoader: DataAttachmentLoader<Document> {
	suspend fun decryptAttachment(
		document: Document?,
		enckeys: String?,
		retrieveAttachment: Document.() -> DataAttachment?
	): ByteArray?

	suspend fun decryptAttachment(
		document: Document?,
		enckeys: List<String?>?,
		retrieveAttachment: Document.() -> DataAttachment?
	): ByteArray?

	suspend fun decryptMainAttachment(
		document: Document?,
		enckeys: String?
	): ByteArray? =
		decryptAttachment(document, enckeys, Document::mainAttachment)

	suspend fun decryptMainAttachment(
		document: Document?,
		enckeys: List<String?>?
	): ByteArray? =
		decryptAttachment(document, enckeys, Document::mainAttachment)
}
