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

package org.taktik.icure.logic.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import org.springframework.stereotype.Service;
import org.taktik.icure.dto.filter.Filter;
import org.taktik.icure.dto.gui.Editor;
import org.taktik.icure.dto.gui.type.Data;
import org.taktik.icure.services.external.rest.handlers.DiscriminatedTypeAdapter;
import org.taktik.icure.services.external.rest.v1.dto.filter.predicate.Predicate;

import java.util.Base64;


/**
 * Created by emad7105 on 03/03/2015.
 */
@Service
public class GsonSerializerFactory {
	public Gson getGsonSerializer() {
		final GsonBuilder gsonBuilder = new GsonBuilder();

		gsonBuilder.serializeSpecialFloatingPointValues()
			.registerTypeAdapter(Predicate.class, new DiscriminatedTypeAdapter<>(Predicate.class))
			.registerTypeAdapter(Filter.class, new DiscriminatedTypeAdapter<>(Filter.class))
			.registerTypeAdapter(Editor.class, new DiscriminatedTypeAdapter<>(Editor.class))
			.registerTypeAdapter(Data.class, new DiscriminatedTypeAdapter<>(Data.class))
			.registerTypeAdapter(org.taktik.icure.services.external.rest.v1.dto.filter.Filter.class, new DiscriminatedTypeAdapter<>(org.taktik.icure.services.external.rest.v1.dto.filter.Filter.class))
			.registerTypeAdapter(org.taktik.icure.services.external.rest.v1.dto.gui.type.Data.class, new DiscriminatedTypeAdapter<>(org.taktik.icure.services.external.rest.v1.dto.gui.type.Data.class))
			.registerTypeAdapter(org.taktik.icure.services.external.rest.v1.dto.gui.Editor.class, new DiscriminatedTypeAdapter<>(org.taktik.icure.services.external.rest.v1.dto.gui.Editor.class))
			.registerTypeAdapter(byte[].class, (JsonDeserializer<byte[]>) (json, typeOfT, context) -> {
				if (json.isJsonPrimitive() && ((JsonPrimitive)json).isString()) {
					return Base64.getDecoder().decode(json.getAsString());
				} else if (json.isJsonArray() && (((JsonArray)json).size() == 0 || ((JsonArray)json).get(0).isJsonPrimitive() )) {
					byte[] res = new byte[((JsonArray)json).size()];
					for (int i=0;i<((JsonArray)json).size();i++) {
						res[i] = ((JsonArray)json).get(i).getAsByte();
					}
					return res;
				}
				throw new IllegalArgumentException("byte[] are expected to be encoded as base64 strings");
			})
		;

		return gsonBuilder.create();
	}
}
