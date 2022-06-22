package org.taktik.icure.utils

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeout

class SignalerWithMemoryTest : StringSpec({
	lateinit var signaler: SignalerWithMemory

	beforeEach {
		signaler = SignalerWithMemory()
	}

	fun CoroutineScope.makeJob() = async {
		signaler.awaitSignal()
		0
	}

	suspend fun Deferred<Int>.shouldNotComplete() =
		shouldThrow<TimeoutCancellationException> { withTimeout(20) { await() } }

	suspend fun Deferred<Int>.shouldComplete() =
		withTimeout(20) { await() shouldBe 0 }

	"Await signal should suspend until a signal is received" {
		val job = makeJob()
		job.shouldNotComplete()
		signaler.signal()
		job.shouldComplete()
	}

	"Signals while no one is waiting should be saved" {
		signaler.signal()
		val job = makeJob()
		job.shouldComplete()
	}

	"Signal should be consumed by await" {
		val job1 = makeJob()
		signaler.signal()
		job1.shouldComplete()
		val job2 = makeJob()
		job2.shouldNotComplete()
		signaler.signal()
		job2.shouldComplete()
	}

	"At most one signal should be saved" {
		signaler.signal()
		signaler.signal()
		makeJob().shouldComplete()
		val job = makeJob()
		job.shouldNotComplete()
		signaler.signal()
		job.shouldComplete()
	}
})
