package org.taktik.icure.asynclogic.objectstorage.testutils

import org.taktik.icure.entities.base.HasDataAttachments

class EntityWithAttachmentInfo<T : HasDataAttachments<T>>(
	val entity: T,
	val contentsByKey: Map<String, ByteArray>
)

fun <T : HasDataAttachments<T>> T.withAttachmentInfos(vararg keyToContent: Pair<String, ByteArray>) =
	EntityWithAttachmentInfo(this, keyToContent.toMap())
