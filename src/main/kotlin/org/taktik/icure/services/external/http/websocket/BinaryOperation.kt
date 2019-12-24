/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.taktik.icure.services.external.http.websocket

import com.google.gson.Gson
import org.apache.commons.logging.LogFactory
import java.io.IOException
import java.nio.ByteBuffer
import java.util.*

abstract class BinaryOperation internal constructor(protected var gsonMapper: Gson, protected var webSocket: WebSocket) : Operation, AsyncProgress {
    @Throws(IOException::class)
    fun binaryResponse(response: ByteBuffer?) {
        webSocket.remote.sendBytes(response)
    }

    @Throws(IOException::class)
    fun errorResponse(e: Exception) {
        val ed: MutableMap<String, String?> = HashMap()
        ed["message"] = e.message
        ed["localizedMessage"] = e.localizedMessage
        log.info("Error in socket " + e.message + ":" + e.localizedMessage + " ", e)
        if (webSocket.remote != null) {
            webSocket.remote.sendString(gsonMapper.toJson(ed))
        }
    }

    @Throws(IOException::class)
    override fun progress(progress: Double) {
        val wrapper = HashMap<String, Double>()
        wrapper["progress"] = progress
        val message: Message<*> = Message("progress", "Map", UUID.randomUUID().toString(), Arrays.asList(wrapper))
        webSocket.remote.sendString(gsonMapper.toJson(message))
    }

    companion object {
        private val log = LogFactory.getLog(BinaryOperation::class.java)
    }

}
