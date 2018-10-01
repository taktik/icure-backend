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

package org.taktik.icure.services.external.rest.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.apache.commons.codec.binary.Base64;
import org.taktik.icure.services.external.rest.v1.dto.PaginatedDocumentKeyIdPair;
import org.taktik.icure.services.external.rest.v1.dto.filter.predicate.Predicate;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static java.lang.Double.MAX_VALUE;
import static java.lang.Double.MIN_VALUE;

//Used by Jersey instead of jackson
@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class GsonMessageBodyHandler implements MessageBodyWriter<Object>,
		MessageBodyReader<Object> {

	private static final String UTF_8 = "UTF-8";

	private Gson gson;

	public Gson getGson() {
		if (gson == null) {
			final GsonBuilder gsonBuilder = new GsonBuilder();

			gsonBuilder
					.registerTypeAdapter(PaginatedDocumentKeyIdPair.class, (JsonDeserializer<PaginatedDocumentKeyIdPair>) (json, typeOfT, context) -> {
						Map<String, Object> obj = context.deserialize(json, Map.class);
						return new PaginatedDocumentKeyIdPair<>((List<String>) obj.get("startKey"), (String) obj.get("startKeyDocId"));
					})
					.registerTypeAdapter(Predicate.class, new DiscriminatedTypeAdapter<>(Predicate.class))
					.registerTypeHierarchyAdapter(byte[].class, new ByteArrayToBase64TypeAdapter())
					.registerTypeAdapter(org.taktik.icure.dto.filter.Filter.class, new DiscriminatedTypeAdapter<>(org.taktik.icure.dto.filter.Filter.class))
					.registerTypeAdapter(org.taktik.icure.dto.gui.Editor.class, new DiscriminatedTypeAdapter<>(org.taktik.icure.dto.gui.Editor.class))
					.registerTypeAdapter(org.taktik.icure.dto.gui.type.Data.class, new DiscriminatedTypeAdapter<>(org.taktik.icure.dto.gui.type.Data.class))
					.registerTypeAdapter(org.taktik.icure.services.external.rest.v1.dto.filter.Filter.class, new DiscriminatedTypeAdapter<>(org.taktik.icure.services.external.rest.v1.dto.filter.Filter.class))
					.registerTypeAdapter(org.taktik.icure.services.external.rest.v1.dto.gui.type.Data.class, new DiscriminatedTypeAdapter<>(org.taktik.icure.services.external.rest.v1.dto.gui.type.Data.class))
					.registerTypeAdapter(org.taktik.icure.services.external.rest.v1.dto.gui.Editor.class, new DiscriminatedTypeAdapter<>(org.taktik.icure.services.external.rest.v1.dto.gui.Editor.class))
					.registerTypeAdapter(Double.class, (JsonSerializer<Double>) (src, typeOfSrc, context) -> src == null ? null : new JsonPrimitive(src.isNaN() ? 0d : src.isInfinite() ? (src > 0 ? MAX_VALUE : MIN_VALUE) : src))
					.registerTypeAdapter(Boolean.class, (JsonDeserializer<Boolean>) (json, typeOfSrc, context) -> ((JsonPrimitive)json).isBoolean() ? json.getAsBoolean() : ((JsonPrimitive)json).isString() ? json.getAsString().equals("true") : json.getAsInt() != 0);
			gson = gsonBuilder.create();
		}
		return gson;
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType,
							  java.lang.annotation.Annotation[] annotations, MediaType mediaType) {
		return true;
	}

	@Override
	public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
		try (InputStreamReader streamReader = new InputStreamReader(entityStream, UTF_8)) {
			Type jsonType;
			if (type.equals(genericType)) {
				jsonType = type;
			} else {
				jsonType = genericType;
			}
			return getGson().fromJson(streamReader, jsonType);
		}
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return true;
	}

	@Override
	public long getSize(Object object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	@Override
	public void writeTo(Object object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
		try (OutputStreamWriter writer = new OutputStreamWriter(entityStream, UTF_8)) {
			Type jsonType;
			if (type.equals(genericType)) {
				jsonType = type;
			} else {
				jsonType = genericType;
			}
			getGson().toJson(object, jsonType, writer);
		}
	}

	private class ByteArrayToBase64TypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
		private Base64 b64 = new Base64();

		public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			return b64.decode(json.getAsString());
		}

		public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(b64.encodeToString(src));
		}
	}

}
