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
package org.taktik.icure.services.external.http

import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.services.external.http.websocket.Operation
import org.taktik.icure.services.external.http.websocket.WebSocketOperation
import org.taktik.icure.services.external.http.websocket.WebSocketParam
import org.taktik.icure.services.external.rest.v1.wscontrollers.KmehrWsController
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.io.Serializable
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import java.util.*
import java.util.concurrent.Callable

@Component
class WebSocketOperationHandler(private val kmehrWsController: KmehrWsController, val gsonMapper: Gson, val sessionLogic: AsyncSessionLogic) : WebSocketHandler {
    var prefix: String? = null
    val methods = scanBeanMethods(kmehrWsController)
    private val log = LoggerFactory.getLogger(this.javaClass)

    fun scanBeanMethods(bean: Any): Map<String, WebSocketInvocation> {
        return bean.javaClass.getAnnotation(RestController::class.java)?.let {
            if (it.value.isNotEmpty()) {
                val basePath: String = it.value
                bean.javaClass.methods.filter { m: Method ->
                    m.getAnnotation(WebSocketOperation::class.java)?.let { _ ->
                        m.getAnnotation(RequestMapping::class.java)?.value?.isNotEmpty()
                    } == true
                }.fold(mutableMapOf<String, WebSocketInvocation>()) { methods, m ->
                    methods[(basePath + "/" + m.getAnnotation(RequestMapping::class.java).value[0]).replace("//".toRegex(), "/")] =
                            WebSocketInvocation(m.getAnnotation(WebSocketOperation::class.java).adapterClass.java, bean, m)
                    methods
                }
            } else {
                null
            }
        } ?: mapOf()
    }

    override fun handle(session: WebSocketSession): Mono<Void> {
        var operation: Operation? = null
        return mono {
            session.receive().doOnNext { wsm -> wsm.retain() }.asFlow().collect {
                if (operation == null) {
                    val parameters = JsonParser.parseString(it.payloadAsText).asJsonObject["parameters"].asJsonObject
                    val path = session.handshakeInfo.uri.path.replaceFirst("^" + (prefix?.toRegex() ?: ""), "").replaceFirst(";.+?=.*".toRegex(), "")
                    val invocation = methods[path]
                    operation = try {
                        invocation!!.operationClass.getConstructor(WebSocketSession::class.java, Gson::class.java).newInstance(session, gsonMapper)
                    } catch (e: Exception) {
                        log.error("WS error",e);
                        throw IllegalStateException(e)
                    }

                    try {
                        val parameters = invocation.method.parameters.map { p: Parameter ->
                            val paramAnnotation = p.getAnnotation(WebSocketParam::class.java)
                            if (paramAnnotation == null) operation else gsonMapper.fromJson(parameters[paramAnnotation.value], p.type)
                        }.toTypedArray()
                        val res = invocation.method.invoke(invocation.bean, *parameters) as Mono<*>
                        res.awaitFirstOrNull()
                    } catch (e: Exception) {
                        log.error("WS error",e);
                        throw IllegalArgumentException(e)
                    }
                } else {
                    operation!!.handle<Serializable>(it.payloadAsText)
                }
                it.release()
            }
            null
        }
    }

    inner class WebSocketInvocation(val operationClass: Class<out Operation?>, val bean: Any?, val method: Method)

}
