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
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.jetty.websocket.api.Session;
import org.taktik.icure.services.external.api.AsyncDecrypt;
import org.taktik.icure.services.external.rest.v1.dto.embed.ServiceDto;

public class KmehrFileOperation extends BinaryOperation implements AsyncDecrypt {
	private Map<String,DecodingSession> decodingSessions = new HashMap<>();

	public KmehrFileOperation(WebSocket webSocket, Gson gsonMapper) {
		super(gsonMapper, webSocket);
	}

	@Override
	public <K extends Serializable> Future<List<K>> decrypt(List<K> encrypted, Class<K> clazz) throws IOException {
		Message message = new Message<>("decrypt", clazz.getSimpleName(), UUID.randomUUID().toString(), encrypted);

		CompletableFuture<List<K>> future = new CompletableFuture<>();
		DecodingSession<K> decodingSession = new DecodingSession<>(future, clazz);
		decodingSessions.put(message.getUuid(), decodingSession);

		webSocket.getRemote().sendString(gsonMapper.toJson(message));

		return future;
	}

	@Override
	public void handle(String message) {
		JsonParser parser = new JsonParser();
		JsonObject dto = parser.parse(message).getAsJsonObject();

		if (dto.get("command").getAsString().equals("decryptResponse")) {
			DecodingSession decodingSession = decodingSessions.get(dto.get("uuid").getAsString());
            if (decodingSession != null) {
                decodingSession.getFuture().complete(StreamSupport.stream(dto.get("body").getAsJsonArray().spliterator(), false).map(e -> {
                    try {
                        return gsonMapper.fromJson(e.getAsJsonObject(), decodingSession.getClazz());
                    } catch (com.google.gson.JsonSyntaxException ee) {
                        return null;
                    }
                }).collect(Collectors.toList()));
            }
		}
	}

	private class DecodingSession<K extends Serializable> {
		CompletableFuture<List<K>> future;
		Class<K> clazz;

		DecodingSession(CompletableFuture<List<K>> future, Class<K> clazz) {
			this.future = future;
			this.clazz = clazz;
		}

		public CompletableFuture<List<K>> getFuture() {
			return future;
		}

		public Class<K> getClazz() {
			return clazz;
		}
	}
}
