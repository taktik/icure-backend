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
import org.taktik.icure.entities.base.HasDataAttachments
import org.taktik.icure.utils.toByteArray

class FakeObjectStorageClient<T : HasDataAttachments<T>>(override val entityGroupName: String) : ObjectStorageClient<T> {
	var available = true

	val eventsChannel = Channel<ObjectStoreEvent>(UNLIMITED)

	private val entityToAttachments = mutableMapOf<String, MutableMap<String, ByteArray>>()

	val attachmentsKeys get() = entityToAttachments.flatMap { (docId, attachments) -> attachments.keys.map { docId to it } }

	override suspend fun upload(entity: T, attachmentId: String, content: ByteArray): Boolean =
		upload(entity, attachmentId, flowOf(DefaultDataBufferFactory.sharedInstance.wrap(content)))

	override suspend fun upload(entity: T, attachmentId: String, content: Flow<DataBuffer>): Boolean =
		unsafeUpload(entity.id, attachmentId, content)

	override suspend fun unsafeUpload(entityId: String, attachmentId: String, content: Flow<DataBuffer>): Boolean =
		if (available) {
			entityToAttachments.computeIfAbsent(entityId) { mutableMapOf() }.putIfAbsent(attachmentId, content.toByteArray(true))
			eventsChannel.send(ObjectStoreEvent(entityId, attachmentId, ObjectStoreEvent.Type.SUCCESSFUL_UPLOAD))
			true
		} else {
			eventsChannel.send(ObjectStoreEvent(entityId, attachmentId, ObjectStoreEvent.Type.UNSUCCESSFUL_UPLOAD))
			false
		}

	override fun get(entity: T, attachmentId: String): Flow<DataBuffer> =
		if (available) {
			entityToAttachments[entity.id]?.get(attachmentId)?.let { flowOf(DefaultDataBufferFactory.sharedInstance.wrap(it)) }
				?: throw IllegalStateException("Document does not exist. Available attachments: $attachmentsKeys")
		} else throw HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE)

	override suspend fun checkAvailable(entity: T, attachmentId: String): Boolean =
		entityToAttachments[entity.id]?.let { attachmentId in it } == true

	override suspend fun delete(entity: T, attachmentId: String): Boolean =
		unsafeDelete(entity.id, attachmentId)

	override suspend fun unsafeDelete(entityId: String, attachmentId: String): Boolean =
		if (available) {
			entityToAttachments[entityId]?.remove(attachmentId)
			eventsChannel.send(ObjectStoreEvent(entityId, attachmentId, ObjectStoreEvent.Type.SUCCESSFUL_DELETE))
			true
		} else {
			eventsChannel.send(ObjectStoreEvent(entityId, attachmentId, ObjectStoreEvent.Type.UNSUCCESSFUL_DELETE))
			false
		}

	data class ObjectStoreEvent(val documentId: String, val attachmentId: String, val type: Type) {
		enum class Type { SUCCESSFUL_UPLOAD, SUCCESSFUL_DELETE, UNSUCCESSFUL_UPLOAD, UNSUCCESSFUL_DELETE }
	}
}
