package org.taktik.icure.asynclogic.objectstorage

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.taktik.icure.entities.embed.DataAttachment

/**
 * Holds the logic and data necessary to load data attachments.
 */
interface DataAttachmentLoadingContext {
	/**
	 * @return the content of the data attachment as a flow.
	 */
	fun DataAttachment.loadFlow(): Flow<DataBuffer>
}
