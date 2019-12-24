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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.asFlux
import org.apache.commons.logging.LogFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import java.io.IOException
import java.util.*

abstract class BinaryOperation(protected var webSocket: WebSocketSession, protected var gsonMapper: Gson) : Operation, AsyncProgress {
    private val log = LogFactory.getLog(BinaryOperation::class.java)

    @Throws(IOException::class)
    fun binaryResponse(response: Flow<DataBuffer>) {
        webSocket.send(response.map { WebSocketMessage(WebSocketMessage.Type.BINARY, it)}.asFlux())
    }

    @Throws(IOException::class)
    fun errorResponse(e: Exception) {
        val ed: MutableMap<String, String?> = HashMap()
        ed["message"] = e.message
        ed["localizedMessage"] = e.localizedMessage
        log.info("Error in socket " + e.message + ":" + e.localizedMessage + " ", e)
        webSocket.textMessage(gsonMapper.toJson(ed))
    }

    @Throws(IOException::class)
    override fun progress(progress: Double) {
        val wrapper = HashMap<String, Double>()
        wrapper["progress"] = progress
        val message: Message<*> = Message("progress", "Map", UUID.randomUUID().toString(), listOf(wrapper))
        webSocket.textMessage(gsonMapper.toJson(message))
    }

}
