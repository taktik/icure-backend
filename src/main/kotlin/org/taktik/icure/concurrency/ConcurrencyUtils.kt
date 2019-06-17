package org.taktik.icure.concurrency

import com.hazelcast.core.ILock
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory

/**
    Executes the given [block] every [intervalMillis] milliseconds forever,
    making sure the [block] is executed on one replica only.
    This is done by trying to acquire the given distributed [lock] and executing the [block]
    only if we acquired the [lock].
    If there is any exception thrown by [block], it is caught and logged, but the
    loop continues.
 */
suspend fun doPeriodicallyOnOneReplicaForever(lock: ILock, intervalMillis: Long, delayAfterErrorMillis: Long, block: suspend () -> Unit) {
    val log = LoggerFactory.getLogger(lock.name)
    while (true) {
        try {
            log.debug("Trying to acquire lock")
            if (lock.tryLock()) {
                try {
                    log.info("Captured lock")
                    while (true) {
                        block()
                        // Keep the lock forever until there is an error
                        delay(intervalMillis)
                    }
                } finally {
                    lock.forceUnlock()
                }
            } else {
                log.debug("Failed to acquire lock, retrying in $delayAfterErrorMillis ms")
                // Wait a bit then try to acquire lock
                delay(delayAfterErrorMillis)
            }
        } catch (e: Throwable) {
            log.warn("Uncaught exception", e)
            delay(delayAfterErrorMillis)
        }
    }
}