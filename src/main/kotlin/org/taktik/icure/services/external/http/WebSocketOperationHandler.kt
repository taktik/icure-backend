/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */
package org.taktik.icure.services.external.http

import java.io.Serializable
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.services.external.http.websocket.Operation
import org.taktik.icure.services.external.http.websocket.WebSocketOperation
import org.taktik.icure.services.external.http.websocket.WebSocketParam
import org.taktik.icure.services.external.rest.v1.wscontrollers.KmehrWsController
import reactor.core.publisher.Mono

@Component
class WebSocketOperationHandler(private val kmehrWsController: KmehrWsController, val objectMapper: ObjectMapper, val sessionLogic: AsyncSessionLogic) : WebSocketHandler {
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
		return session.receive().doOnNext { wsm -> wsm.retain() }.flatMap { wsm ->
			(
				if (operation == null) {
					val jsonParameters = objectMapper.readTree(wsm.payloadAsText).get("parameters")
					val path = session.handshakeInfo.uri.path.replaceFirst("^" + (prefix?.toRegex() ?: ""), "").replaceFirst(";.+?=.*".toRegex(), "")
					val invocation = methods[path]
					operation = try {
						invocation!!.operationClass.getConstructor(WebSocketSession::class.java, ObjectMapper::class.java).newInstance(session, objectMapper)
					} catch (e: Exception) {
						log.error("WS error", e)
						throw IllegalStateException(e)
					}

					try {
						val parameters = invocation.method.parameters.map { p: Parameter ->
							val paramAnnotation = p.getAnnotation(WebSocketParam::class.java)
							if (paramAnnotation == null) operation else objectMapper.treeToValue(jsonParameters.get(paramAnnotation.value), p.type)
						}.toTypedArray()

						try {
							(invocation.method.invoke(invocation.bean, *parameters) as Mono<*>)
						} catch (e: Exception) {
							log.error("Cannot call WS invocation", e)
							Mono.empty<Void>()
						}
					} catch (e: Exception) {
						log.error("WS error", e)
						throw IllegalArgumentException(e)
					}
				} else {
					//wsm.payloadAsText works for binary or text messages
					if (wsm.type == WebSocketMessage.Type.TEXT) {
						operation!!.handle<Serializable>(wsm.payloadAsText)
					} else {
						val payloadAsText = wsm.payloadAsText
						operation!!.handle<Serializable>(payloadAsText)
					}
					Mono.empty<Void>()
				}
				).also { wsm.release() }
		}.last().map { null }
	}

	inner class WebSocketInvocation(val operationClass: Class<out Operation?>, val bean: Any?, val method: Method)
}
