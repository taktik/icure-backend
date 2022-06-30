package org.taktik.icure.asynclogic.objectstorage.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.taktik.icure.asynclogic.objectstorage.DataAttachmentLoadingContext
import org.taktik.icure.entities.embed.DataAttachment

class DocumentDataAttachmentLoadingContext(
	private val documentId: String
) : DataAttachmentLoadingContext {
	override fun DataAttachment.loadFlow(): Flow<DataBuffer> {
		TODO("Not yet implemented")
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		other as DocumentDataAttachmentLoadingContext
		if (documentId != other.documentId) return false
		return true
	}

	override fun hashCode(): Int {
		return documentId.hashCode()
	}
}
