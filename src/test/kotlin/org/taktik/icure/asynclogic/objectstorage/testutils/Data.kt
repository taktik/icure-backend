package org.taktik.icure.asynclogic.objectstorage.testutils

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.apache.commons.codec.digest.DigestUtils
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

fun ByteArray.modify(amount: Byte) = map { (it + amount).toByte() }.toByteArray()

const val SIZE_LIMIT = 10L
const val MIGRATION_SIZE_LIMIT = 20L
val smallAttachment by lazy { (1 .. SIZE_LIMIT / 2).map { it.toByte() }.toByteArray() }
val bigAttachment by lazy { (1 .. SIZE_LIMIT * 3 / 2).map { it.toByte() }.toByteArray() }
val migrationBigAttachment by lazy { (1 .. MIGRATION_SIZE_LIMIT * 3 / 2).map { it.toByte() }.toByteArray() }

const val jsonUti = "public.json"
const val htmlUti = "public.html"
const val xmlUti = "public.xml"
const val javascriptUti = "com.netscape.javascript-source"
val sampleUtis by lazy { listOf(xmlUti, jsonUti) }

const val key1 = "key1"
const val key2 = "key2"
const val key3 = "key3"
const val key4 = "key4"
