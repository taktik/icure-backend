package org.taktik.icure.services.external.http.websocket

import java.io.IOException

interface AsyncProgress {
    @Throws(IOException::class)
    fun progress(progress: Double)
}
