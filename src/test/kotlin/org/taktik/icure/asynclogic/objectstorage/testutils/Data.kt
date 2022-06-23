package org.taktik.icure.asynclogic.objectstorage.testutils

const val document1 = "document1"
const val document2 = "document2"
const val attachment1 = "attachment1"
const val attachment2 = "attachment2"
val bytes1 = byteArrayOf(1, 2, 3)
val bytes2 = byteArrayOf(4, 5, 6)
val bytes3 = byteArrayOf(7, 8, 9)
val bytes4 = byteArrayOf(10, 11, 12)

val sampleAttachments = listOf(
	Triple(document1, attachment1, bytes1),
	Triple(document1, attachment2, bytes2),
	Triple(document2, attachment1, bytes3),
	Triple(document2, attachment2, bytes4),
)
