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

package org.taktik.icure.services.external.http.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.logic.SessionLogic.SessionContext;
import org.taktik.icure.services.external.http.WebSocketServlet;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Executor;

public class WebSocket extends WebSocketAdapter {
	private SessionContext sessionContext;
	private String prefix;
	private Gson gsonMapper;
	private Map<String, WebSocketServlet.WebSocketInvocation> operations;
	private Operation operation;
	private SessionLogic sessionLogic;
	private Executor executor;

	public WebSocket(SessionContext sessionContext, String prefix, Gson gsonMapper, SessionLogic sessionLogic, Executor executor, Map<String, WebSocketServlet.WebSocketInvocation> operations) {
		this.sessionContext = sessionContext;
		this.prefix = prefix;
		this.gsonMapper = gsonMapper;
		this.sessionLogic = sessionLogic;
		this.executor = executor;
		this.operations = operations;
	}

	@Override
	public void onWebSocketConnect(Session sess) {
		super.onWebSocketConnect(sess);
	}

	@Override
	public void onWebSocketText(String message) {
		if (operation == null) {
			JsonParser parser = new JsonParser();
			JsonObject parameters = parser.parse(message).getAsJsonObject().get("parameters").getAsJsonObject();

			String path = getSession().getUpgradeRequest().getRequestURI().getPath().replaceFirst("^" + prefix, "").replaceFirst(";jsessionid=.*", "");
			WebSocketServlet.WebSocketInvocation invocation = operations.get(path);
			try {
				operation = invocation.getOperationClass().getConstructor(WebSocket.class, Gson.class).newInstance(this, gsonMapper);
			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
				throw new IllegalStateException(e);
			}

			executor.execute(() -> {
				try {
					sessionLogic.doInSessionContext(sessionContext, () -> {
						try {
							invocation.getMethod().invoke(invocation.getBean(), Arrays.stream(invocation.getMethod().getParameters()).map(p -> {
								WebSocketParam paramAnnotation = p.getAnnotation(WebSocketParam.class);
								return paramAnnotation == null ? operation : gsonMapper.fromJson(parameters.get(paramAnnotation.value()), p.getType());
							}).toArray(java.lang.Object[]::new));
						} catch (IllegalAccessException | InvocationTargetException e) {
							throw new IllegalArgumentException(e);
						}

						return null;
					});
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});

		} else {
			operation.handle(message);
		}
	}

	@Override
	public void onWebSocketBinary(byte[] payload, int offset, int len) {
        operation.handle(new String(payload, offset, len, StandardCharsets.UTF_8));
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		super.onWebSocketClose(statusCode, reason);
	}

	@Override
	public void onWebSocketError(Throwable cause) {
		super.onWebSocketError(cause);
		cause.printStackTrace(System.err);
	}
}
