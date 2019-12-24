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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class BinaryOperation implements Operation, AsyncProgress {
	private static Log log = LogFactory.getLog(BinaryOperation.class);

	protected Gson gsonMapper;
	protected WebSocket webSocket;

	BinaryOperation(Gson gsonMapper, WebSocket webSocket) {
		this.gsonMapper = gsonMapper;
		this.webSocket = webSocket;
	}

	public void binaryResponse(ByteBuffer response) throws IOException {
		webSocket.getRemote().sendBytes(response);
	}

	public void errorResponse(Exception e) throws IOException {
		Map<String,String> ed = new HashMap<>();
		ed.put("message",e.getMessage());
		ed.put("localizedMessage",e.getLocalizedMessage());

		log.info("Error in socket " + e.getMessage() + ":" +e.getLocalizedMessage() + " ", e);

		if (webSocket.getRemote() != null) { webSocket.getRemote().sendString(gsonMapper.toJson(ed)); }
	}

	@Override
	public void progress(Double progress) throws IOException {
		HashMap<String, Double> wrapper = new HashMap<>();
		wrapper.put("progress",progress);
		Message message = new Message<>("progress", "Map", UUID.randomUUID().toString(), Arrays.asList(wrapper));

		webSocket.getRemote().sendString(gsonMapper.toJson(message));
	}

}
