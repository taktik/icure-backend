package org.taktik.icure.utils

tailrec fun <K>retry(trials: Int, closure: () -> K): K {
    try {
        return closure()
    } catch(e: Exception) {
        if (trials < 1) {
            throw e
        }
    }
    return retry(trials - 1, closure)
}

tailrec suspend fun <K>suspendRetry(trials: Int, closure: suspend () -> K): K {
    try {
        return closure()
    } catch(e: Exception) {
        if (trials < 1) {
            throw e
        }
    }
    return suspendRetry(trials - 1, closure)
}
