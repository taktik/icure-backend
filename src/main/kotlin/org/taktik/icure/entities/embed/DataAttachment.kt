package org.taktik.icure.entities.embed

import java.io.Serializable
import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.taktik.commons.uti.UTI
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

	companion object {
		/**
		 * Default mime type for data attachments, if no specific uti is provided.
		 */
		const val DEFAULT_MIME_TYPE = "application/xml"

		private const val NULL_CONTEXT_MESSAGE =
			"Can't load the content of this attachment: loading context is not set. " +
			"Only \"loaded\" entities have the attachment context automatically initialized. "
	}

	val ids: Pair<String?, String?> get() = couchDbAttachmentId to objectStoreAttachmentId

	/**
	 * Get the mime type string for this attachment. If the attachment does not specify a UTI with a valid mime type returns null.
	 */
	val mimeType: String? get() =
		utis.mapNotNull(UTI::get).flatMap { it.mimeTypes ?: emptyList() }.firstOrNull()

	/**
	 * [mimeType] or [DEFAULT_MIME_TYPE].
	 */
	val mimeTypeOrDefault: String get() =
		mimeType ?: DEFAULT_MIME_TYPE

	@JsonIgnore private var cachedBytes: Deferred<ByteArray>? = null

	/**
	 * Get the content of the attachment as a data flow.
	 * If someone previously invoked [contentBytes], or [contentFlow] with [cacheBytes] true the flow is build on top of the cached byte array.
	 * Note that in this case the array us shared by all users of the data attachment, therefore you should avoid changing the array directly.
	 * @param cacheBytes if true caches the attachment content.
	 */
	fun contentFlow(cacheBytes: Boolean): Flow<DataBuffer> {
		checkNotNull(loadingContext) { NULL_CONTEXT_MESSAGE }
		return if (cacheBytes) {
			flow { emit(DefaultDataBufferFactory.sharedInstance.wrap(contentBytes())) }
		} else doLoadFlow(true)
	}

	/**
	 * Get the content of the attachment as a byte array.
	 * This method automatically caches the attachment content.
	 * Note that the loaded bytes are shared by all users of the data attachment, therefore you should avoid changing the array directly.
	 */
	suspend fun contentBytes(): ByteArray = coroutineScope {
		val getBytesTask = cachedBytes ?: (
			async(start = CoroutineStart.LAZY) {
				doLoadFlow(false).toByteArray(true)
			}.also { cachedBytes = it }
		)
		kotlin.runCatching {
			getBytesTask.await()
		}.onFailure {
			if (cachedBytes === getBytesTask) cachedBytes = null
		}.getOrThrow()
	}

	fun doLoadFlow(allowFromCachedBytes: Boolean): Flow<DataBuffer> {
		checkNotNull(loadingContext) { NULL_CONTEXT_MESSAGE }
		return cachedBytes?.takeIf { allowFromCachedBytes }?.let {
			flow { emit(DefaultDataBufferFactory.sharedInstance.wrap(it.await())) }
		} ?: with(loadingContext) { loadFlow() }
	}

	/**
	 * @return if this and other attachment have the same ids (the attachment is the same and is stored in the same place)
	 */
	infix fun hasSameIdsAs(other: DataAttachment) =
		this.ids == other.ids

	/**
	 * @return a copy of this data attachment where the ids are replaced with those of another data attachment
	 */
	fun withIdsOf(other: DataAttachment) = copy(
		couchDbAttachmentId = other.couchDbAttachmentId,
		objectStoreAttachmentId = other.couchDbAttachmentId
	)
}
