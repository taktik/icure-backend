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

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.apache.commons.logging.LogFactory
import org.springframework.web.reactive.socket.WebSocketSession
import org.taktik.icure.be.ehealth.logic.kmehr.v20131001.KmehrExport
import org.taktik.icure.services.external.api.AsyncDecrypt
import reactor.core.publisher.Mono
import java.io.IOException
import java.io.Serializable
import java.time.Duration
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeoutException

class KmehrFileOperation(webSocket: WebSocketSession, objectMapper: ObjectMapper) : BinaryOperation(webSocket, objectMapper), AsyncDecrypt {
    private val log = LogFactory.getLog(KmehrExport::class.java)
    private val decodingSessions: MutableMap<String?, DecodingSession<*>> = HashMap()

    @Throws(IOException::class)
    override suspend fun <K : Serializable?> decrypt(encrypted: List<K>, clazz: Class<K>): List<K> {
        val message: Message<*> = Message("decrypt", clazz.simpleName, UUID.randomUUID().toString(), encrypted)
        val future = CompletableFuture<List<K>>()
        val decodingSession = DecodingSession(future, clazz)
        decodingSessions[message.uuid] = decodingSession
        val jsonMessage = objectMapper.writeValueAsString(message)
        val wsMessage = if (jsonMessage.length > 65000) webSocket.binaryMessage { it.wrap(jsonMessage.toByteArray(Charsets.UTF_8)) } else webSocket.textMessage(objectMapper.writeValueAsString(message))
        webSocket.send(Mono.just(wsMessage)).awaitFirstOrNull()
        return try {
            Mono.fromFuture(future).timeout(Duration.ofSeconds(120)).awaitFirst()
        } catch (toe: TimeoutException) {
            decodingSessions.remove(message.uuid)
            listOf()
        }
    }

    override fun <K : Serializable> handle(message: String?) {
        val dto = try {
            objectMapper.readTree(message)
        } catch(e:Exception) {
            log.error("Cannot parse because of ${e}. Object is: ${message}", e)
            throw(e)
        }
        if (dto["command"].asText() == "decryptResponse") {
            val uuid = dto["uuid"].asText()
            val decodingSession = decodingSessions[uuid] as DecodingSession<K>
            val result = dto["body"].mapNotNull { jsonObject ->
                try {
                    val value = objectMapper.treeToValue<K>(jsonObject, decodingSession.clazz)
                    value
                } catch (ee: Exception) {
                    log.error("Cannot parse because of ${ee}")
                    null
                }
            }
            decodingSession.future.complete(result)
            decodingSessions.remove(uuid)
        }
    }

    private inner class DecodingSession<K : Serializable?> internal constructor(var future: CompletableFuture<List<K>>, var clazz: Class<K>)
}
