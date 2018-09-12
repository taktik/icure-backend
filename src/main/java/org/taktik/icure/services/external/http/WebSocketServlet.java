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

package org.taktik.icure.services.external.http;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.ws.rs.Path;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.services.external.http.websocket.Operation;
import org.taktik.icure.services.external.http.websocket.WebSocket;
import org.taktik.icure.services.external.http.websocket.WebSocketOperation;
import org.taktik.icure.services.external.rest.v1.wsfacade.KmehrWsFacade;

@Component
public class WebSocketServlet extends org.eclipse.jetty.websocket.servlet.WebSocketServlet {
	public static final int MAX_MESSAGE_SIZE = 4 * 1024 * 1024;
	private KmehrWsFacade kmehrWsFacade;
	private Gson gsonMapper;
	private String prefix;
	private SessionLogic sessionLogic;
	private TaskExecutor wsExecutor;

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.getPolicy().setMaxTextMessageSize(MAX_MESSAGE_SIZE);
		factory.getPolicy().setMaxTextMessageBufferSize(MAX_MESSAGE_SIZE);
		factory.getPolicy().setMaxBinaryMessageSize(MAX_MESSAGE_SIZE);
		factory.getPolicy().setMaxBinaryMessageBufferSize(MAX_MESSAGE_SIZE);

		Map<String,WebSocketInvocation> methods = new HashMap<>();
		scanBeanMethods(this.kmehrWsFacade, methods);
		factory.setCreator((req, resp) ->
				new WebSocket(sessionLogic.getCurrentSessionContext(), prefix, gsonMapper, sessionLogic, wsExecutor, methods));
	}

	private void scanBeanMethods(Object bean, Map<String, WebSocketInvocation> methods) {
		Class clazz = bean.getClass();
		Path annotation = (Path) clazz.getAnnotation(Path.class);

		if (annotation!=null) {
			String basePath = annotation.value();
			Arrays.stream(clazz.getMethods()).filter(m -> m.getAnnotation(WebSocketOperation.class) != null).forEach(m ->
					methods.put((basePath + "/" + m.getAnnotation(Path.class).value()).replaceAll("//", "/"), new WebSocketInvocation(m.getAnnotation(WebSocketOperation.class).adapterClass(), bean, m))
			);
		}
	}

	public class WebSocketInvocation {
		private Class<? extends Operation> operationClass;
		private Object bean;
		private Method method;

		public WebSocketInvocation(Class<? extends Operation> operationClass, Object bean, Method method) {
			this.operationClass = operationClass;
			this.bean = bean;
			this.method = method;
		}

		public Class<? extends Operation> getOperationClass() {
			return operationClass;
		}

		public Object getBean() {
			return bean;
		}

		public Method getMethod() {
			return method;
		}
	}

	@Autowired
	public void setKmehrWsFacade(KmehrWsFacade kmehrWsFacade) {
		this.kmehrWsFacade = kmehrWsFacade;
	}

	@Autowired
	public void setGsonMapper(Gson gsonMapper) {
		this.gsonMapper = gsonMapper;
	}

	@Autowired
	public void setSessionLogic(SessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@Autowired
	public void setWsExecutor(TaskExecutor wsExecutor) {
		this.wsExecutor = wsExecutor;
	}

	public String getPrefix() {
		return prefix;
	}
}
