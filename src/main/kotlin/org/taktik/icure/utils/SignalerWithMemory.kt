package org.taktik.icure.utils

import kotlinx.coroutines.channels.Channel

/**
 * Allows a coroutine to suspend until a signal is received.
 * Anyone can send a signal to this, in a non-suspending way.
 * When somebody calls awaitSignal:
 *  - If there has been a signal since last time awaitSignal was completed returns immediately
 *  - Otherwise suspend until somebody calls signal
 * If multiple coroutines are waiting each will need a signal to resume.
 */
class SignalerWithMemory {
	// No need to ever close channels, they can be garbage collected without issues. https://stackoverflow.com/questions/43889066/what-closing-a-kotlinx-coroutines-channel-does
	private val channel = Channel<Unit>(capacity = 1)

	/**
	 * Signal one waiting coroutine. If no coroutine is waiting the signal is saved for later, but at most one signal is saved.
	 */
	fun signal() {
		channel.offer(Unit)
	}

	/**
	 * If there is a saved signal return immediately, otherwise suspend until someone signals.
	 * If multiple coroutines are waiting it may need multiple signals before resuming.
	 */
	suspend fun awaitSignal() {
		channel.receive()
	}
}
