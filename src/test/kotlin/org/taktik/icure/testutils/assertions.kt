package org.taktik.icure.testutils

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldNotContainExactly

infix fun ByteArray?.shouldContainExactly(other: ByteArray) =
	this?.toList() shouldContainExactly other.toList()

infix fun ByteArray?.shouldNotContainExactly(other: ByteArray) =
	this?.toList() shouldNotContainExactly other.toList()
