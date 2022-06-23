package org.taktik.icure.testutils

import io.kotest.matchers.collections.shouldContainExactly

infix fun ByteArray?.shouldContainExactly(other: ByteArray) =
	this?.toTypedArray() shouldContainExactly other.toTypedArray()
