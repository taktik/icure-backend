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
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import org.springframework.web.reactive.socket.WebSocketSession
import org.taktik.icure.services.external.api.AsyncDecrypt
import java.io.IOException
import java.io.Serializable
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

class KmehrFileOperation(webSocket: WebSocketSession, gsonMapper: Gson) : BinaryOperation(webSocket, gsonMapper), AsyncDecrypt {
    private val decodingSessions: MutableMap<String?, DecodingSession<*>> = HashMap()
    @Throws(IOException::class)
    override fun <K : Serializable?> decrypt(encrypted: List<K>, clazz: Class<K>): Future<List<K>> {
        val message: Message<*> = Message("decrypt", clazz.simpleName, UUID.randomUUID().toString(), encrypted)
        val future = CompletableFuture<List<K>>()
        val decodingSession = DecodingSession(future, clazz)
        decodingSessions[message.uuid] = decodingSession
        webSocket.textMessage(gsonMapper.toJson(message))
        return future
    }

    override fun <K: Serializable>handle(message: String?) {
        val parser = JsonParser()
        val dto = parser.parse(message).asJsonObject
        if (dto["command"].asString == "decryptResponse") {
            val decodingSession = decodingSessions[dto["uuid"].asString] as DecodingSession<K>
            decodingSession.future.complete(
                    dto["body"].asJsonArray.map { e ->
                        try {
                            gsonMapper.fromJson<K>(e.asJsonObject, decodingSession.clazz)
                        } catch (ee: JsonSyntaxException) {
                            null
                        }
            }.filterNotNull())
        }
    }

    private inner class DecodingSession<K : Serializable?> internal constructor(var future: CompletableFuture<List<K>>, var clazz: Class<K>)
}
