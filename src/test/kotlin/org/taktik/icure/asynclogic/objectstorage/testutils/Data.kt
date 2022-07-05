package org.taktik.icure.asynclogic.objectstorage.testutils

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.transform
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.taktik.icure.entities.Document

const val document1id = "document1"
const val document2id = "document2"

val document1 = Document(document1id)
val document2 = Document(document2id)

const val attachment1 = "attachment1"
const val attachment2 = "attachment2"

val bytes1 by lazy { byteArrayOf(1, 2, 3) }
val bytes2 by lazy { byteArrayOf(4, 5, 6) }
val bytes3 by lazy { byteArrayOf(7, 8, 9) }
val bytes4 by lazy { byteArrayOf(10, 11, 12) }

val sampleAttachments by lazy {
	listOf(
		Triple(document1, attachment1, bytes1),
		Triple(document1, attachment2, bytes2),
		Triple(document2, attachment1, bytes3),
		Triple(document2, attachment2, bytes4),
	)
}

fun ByteArray.byteSizeDataBufferFlow() =
	toList().asFlow().map { DefaultDataBufferFactory.sharedInstance.wrap(byteArrayOf(it)) }

fun ByteArray.delayedBytesFlow(byteDelay: Long, maxDelays: Int) = flow {
	byteSizeDataBufferFlow().collectIndexed { i, data ->
		if (i in 1..maxDelays) delay(byteDelay)
		emit(data)
	}
}
