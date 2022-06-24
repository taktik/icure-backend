package org.taktik.icure.asynclogic.objectstorage.testutils

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpServerErrorException
import org.taktik.icure.asynclogic.objectstorage.ObjectStorageClient
import org.taktik.icure.utils.toByteArray

class FakeObjectStorageClient : ObjectStorageClient {
	var available = true

	val eventsChannel = Channel<ObjectStoreEvent>(UNLIMITED)

	private val documentToAttachments = mutableMapOf<String, MutableMap<String, ByteArray>>()
	private val dataBufferFactory = DefaultDataBufferFactory()

	val attachmentsKeys get() = documentToAttachments.flatMap { (docId, attachments) -> attachments.keys.map { docId to it } }

	override suspend fun upload(documentId: String, attachmentId: String, content: ByteArray): Boolean = if (available) {
		documentToAttachments.computeIfAbsent(documentId) { mutableMapOf() }.putIfAbsent(attachmentId, content)
		eventsChannel.send(ObjectStoreEvent(documentId, attachmentId, ObjectStoreEvent.Type.SUCCESSFUL_UPLOAD))
		true
	} else {
		eventsChannel.send(ObjectStoreEvent(documentId, attachmentId, ObjectStoreEvent.Type.UNSUCCESSFUL_UPLOAD))
		false
	}

	override suspend fun upload(documentId: String, attachmentId: String, content: Flow<DataBuffer>): Boolean =
		upload(documentId, attachmentId, content.toByteArray(true))

	override suspend fun get(documentId: String, attachmentId: String): Flow<DataBuffer> = if (available) {
		documentToAttachments[documentId]?.get(attachmentId)?.let { flowOf(dataBufferFactory.wrap(it)) }
			?: throw IllegalStateException("Document does not exist. Available attachments: $attachmentsKeys")
	} else throw HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE)

	override suspend fun checkAvailable(documentId: String, attachmentId: String): Boolean =
		documentToAttachments[documentId]?.let { attachmentId in it } == true

	override suspend fun delete(documentId: String, attachmentId: String): Boolean = if (available) {
		documentToAttachments[documentId]?.remove(attachmentId)
		eventsChannel.send(ObjectStoreEvent(documentId, attachmentId, ObjectStoreEvent.Type.SUCCESSFUL_DELETE))
		true
	} else {
		eventsChannel.send(ObjectStoreEvent(documentId, attachmentId, ObjectStoreEvent.Type.UNSUCCESSFUL_DELETE))
		false
	}

	data class ObjectStoreEvent(val documentId: String, val attachmentId: String, val type: Type) {
		enum class Type { SUCCESSFUL_UPLOAD, SUCCESSFUL_DELETE, UNSUCCESSFUL_UPLOAD, UNSUCCESSFUL_DELETE }
	}
}
