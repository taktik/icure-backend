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
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse
import org.eclipse.jetty.websocket.servlet.WebSocketCreator
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.task.TaskExecutor
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.services.external.http.websocket.Operation
import org.taktik.icure.services.external.http.websocket.WebSocket
import org.taktik.icure.services.external.http.websocket.WebSocketOperation
import org.taktik.icure.services.external.rest.v1.wscontrollers.KmehrWsController
import reactor.core.publisher.Mono
import java.lang.reflect.Method
import java.util.*

@Component
class WebSocketServlet(private val kmehrWsController: KmehrWsController, val gsonMapper: Gson, val sessionLogic: AsyncSessionLogic, val wsExecutor: TaskExecutor) : WebSocketHandler {
    var prefix: String? = null

    fun configure(factory: WebSocketServletFactory) {
        factory.policy.maxTextMessageSize = MAX_MESSAGE_SIZE
        factory.policy.maxTextMessageBufferSize = MAX_MESSAGE_SIZE
        factory.policy.maxBinaryMessageSize = MAX_MESSAGE_SIZE
        factory.policy.maxBinaryMessageBufferSize = MAX_MESSAGE_SIZE
        val methods: MutableMap<String, WebSocketInvocation> = HashMap()
        scanBeanMethods(kmehrWsController, methods)
        factory.creator = WebSocketCreator { req: ServletUpgradeRequest?, resp: ServletUpgradeResponse? -> WebSocket(sessionLogic!!.getCurrentSessionContext(), prefix, gsonMapper, sessionLogic, wsExecutor, methods) }
    }

    private fun scanBeanMethods(bean: Any, methods: MutableMap<String, WebSocketInvocation>) {
        val clazz: Class<*> = bean.javaClass
        val annotation = clazz.getAnnotation(RequestMapping::class.java)
        if (annotation != null && annotation.path.isNotEmpty()) {
            val basePath: String = annotation.path[0]
            clazz.methods.filter { m: Method -> m.getAnnotation(WebSocketOperation::class.java)?.let { wso ->
                    m.getAnnotation(RequestMapping::class.java)?.path?.isNotEmpty()
                } == true
            }.forEach { m: Method ->
                methods[(basePath + "/" + m.getAnnotation(RequestMapping::class.java).path[0]).replace("//".toRegex(), "/")] =
                        WebSocketInvocation(m.getAnnotation(WebSocketOperation::class.java).adapterClass.java, bean, m) }
        }
    }

    override fun handle(session: WebSocketSession): Mono<Void> {
        return null
    }

    inner class WebSocketInvocation(val operationClass: Class<out Operation?>, val bean: Any?, val method: Method)

    companion object {
        const val MAX_MESSAGE_SIZE = 4 * 1024 * 1024
    }
}
