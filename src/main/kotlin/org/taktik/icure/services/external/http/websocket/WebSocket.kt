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
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketAdapter
import org.taktik.icure.asynclogic.SessionLogic
import org.taktik.icure.services.external.http.WebSocketServlet.WebSocketInvocation
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Parameter
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executor

class WebSocket(// TODO SH does this still work ?
        private val sessionContext: SessionLogic.SessionContext, private val prefix: String, private val gsonMapper: Gson, private val sessionLogic: SessionLogic, private val executor: Executor, private val operations: Map<String, WebSocketInvocation>) : WebSocketAdapter() {
    private var operation: Operation? = null
    override fun onWebSocketConnect(sess: Session) {
        super.onWebSocketConnect(sess)
    }

    override fun onWebSocketText(message: String) {
        if (operation == null) {
            val parser = JsonParser()
            val parameters = parser.parse(message).asJsonObject["parameters"].asJsonObject
            val path = session.upgradeRequest.requestURI.path.replaceFirst("^" + prefix.toRegex(), "").replaceFirst(";jsessionid=.*".toRegex(), "")
            val invocation = operations[path]
            operation = try {
                invocation!!.operationClass.getConstructor(WebSocket::class.java, Gson::class.java).newInstance(this, gsonMapper)
            } catch (e: InstantiationException) {
                throw IllegalStateException(e)
            } catch (e: IllegalAccessException) {
                throw IllegalStateException(e)
            } catch (e: NoSuchMethodException) {
                throw IllegalStateException(e)
            } catch (e: InvocationTargetException) {
                throw IllegalStateException(e)
            }
            executor.execute {
                try {
                    sessionLogic.doInSessionContext(sessionContext, Callable<Any?> {
                        try {
                            invocation.method.invoke(invocation.bean, *Arrays.stream(invocation.method.parameters).map { p: Parameter ->
                                val paramAnnotation = p.getAnnotation(WebSocketParam::class.java)
                                if (paramAnnotation == null) operation else gsonMapper.fromJson(parameters[paramAnnotation.value()], p.type)
                            }.toArray { _Dummy_.__Array__() })
                        } catch (e: IllegalAccessException) {
                            throw IllegalArgumentException(e)
                        } catch (e: InvocationTargetException) {
                            throw IllegalArgumentException(e)
                        }
                        null
                    })
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            }
        } else {
            operation!!.handle(message)
        }
    }

    override fun onWebSocketBinary(payload: ByteArray, offset: Int, len: Int) {
        super.onWebSocketBinary(payload, offset, len)
    }

    override fun onWebSocketClose(statusCode: Int, reason: String) {
        super.onWebSocketClose(statusCode, reason)
    }

    override fun onWebSocketError(cause: Throwable) {
        super.onWebSocketError(cause)
        cause.printStackTrace(System.err)
    }

}
