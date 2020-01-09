package org.taktik.icure.utils

fun <K>retry(trials: Int, closure: () -> K): K {
    return try {
        closure()
    } catch(e: Exception) {
        if (trials>1) {
            retry(trials - 1, closure)
        } else {
            throw e
        }
    }
}

suspend fun <K>suspendRetry(trials: Int, closure: suspend () -> K): K {
    return try {
        closure()
    } catch(e: Exception) {
        if (trials>1) {
            suspendRetry(trials - 1, closure)
        } else {
            throw e
        }
    }
}
