package org.taktik.icure.entities.embed

import java.io.Serializable
import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.taktik.icure.asynclogic.objectstorage.DataAttachmentLoadingContext
import org.taktik.icure.utils.toByteArray

/**
 * Represent an attachment holding some additional data for an entity, currently only used for documents.
 * At least one of [couchDbAttachmentId] or [objectStoreAttachmentId] is always not null.
 * @property couchDbAttachmentId if the attachment is stored as a couchdb attachment this holds the id of the attachment, else null.
 * @property objectStoreAttachmentId if the attachment is stored with the object storage service this holds the id of the attachment, else null.
 * @property utis [Uniform Type Identifiers](https://developer.apple.com/library/archive/documentation/FileManagement/Conceptual/understanding_utis/understand_utis_conc/understand_utis_conc.html#//apple_ref/doc/uid/TP40001319-CH202-CHDHIJDE) for the data attachment.
 * This is stored as a list in order to allow specifying a priority, but each uti must be unique.
 * @property loadingContext allows loading of the attachment content.
 */
data class DataAttachment(
	val couchDbAttachmentId: String?,
	val objectStoreAttachmentId: String?,
	val utis: List<String>,
	@JsonIgnore val loadingContext: DataAttachmentLoadingContext? = null
) : Serializable {
	init {
		require(couchDbAttachmentId != null || objectStoreAttachmentId != null) {
			"Must specify the id of at least one storage place for the attachment"
		}
		require(utis.distinct().size == utis.size) {
			val duplicates = utis.groupingBy { it }.eachCount().filter { it.value > 1 }.toList()
			"There are duplicate utis: $duplicates"
		}
	}

	@JsonIgnore private var cachedBytes: Deferred<ByteArray>? = null

	/**
	 * Get the content of the attachment as a data flow.
	 */
	fun contentFlow(): Flow<DataBuffer> {
		checkNotNull(loadingContext) {
			"""
				Can't load the content of this attachment: loading context is not set.
				Only "loaded" entities have the attachment context automatically initialized.
			""".trimIndent()
		}
		return with(loadingContext) { loadFlow() }
	}

	/**
	 * Get the content of the attachment as a byte array.
	 * This should generally be avoided as attachments may be big.
	 * The bytes are loaded on first invocation of the method, and retrieved from cache in following invocations.
	 * Note that the loaded bytes are shared by all users of the data attachment, therefore you should avoid changing the array directly.
	 */
	@Deprecated("Avoid using content bytes when possible, prefer using content flow.")
	suspend fun contentBytes(): ByteArray = coroutineScope {
		val getBytesTask = cachedBytes ?: (
			async(start = CoroutineStart.LAZY) {
				contentFlow().toByteArray(true)
			}.also { cachedBytes = it }
		)
		kotlin.runCatching {
			getBytesTask.await()
		}.onFailure {
			if (cachedBytes === getBytesTask) cachedBytes = null
		}.getOrThrow()
	}
}
