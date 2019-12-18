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
package org.taktik.icure.asynclogic.impl

import com.google.gson.*
import org.springframework.stereotype.Service
import org.taktik.icure.dto.filter.Filter
import org.taktik.icure.dto.gui.Editor
import org.taktik.icure.dto.gui.type.Data
import org.taktik.icure.services.external.rest.handlers.DiscriminatedTypeAdapter
import org.taktik.icure.services.external.rest.v1.dto.filter.predicate.Predicate
import java.lang.reflect.Type
import java.util.*

/**
 * Created by emad7105 on 03/03/2015.
 */
@Service
class GsonSerializerFactory {
    val gsonSerializer: Gson
        get() {
            val gsonBuilder = GsonBuilder()
            gsonBuilder.serializeSpecialFloatingPointValues()
                    .registerTypeAdapter(Predicate::class.java, DiscriminatedTypeAdapter(Predicate::class.java))
                    .registerTypeAdapter(Filter::class.java, DiscriminatedTypeAdapter(Filter::class.java))
                    .registerTypeAdapter(Editor::class.java, DiscriminatedTypeAdapter(Editor::class.java))
                    .registerTypeAdapter(Data::class.java, DiscriminatedTypeAdapter(Data::class.java))
                    .registerTypeAdapter(org.taktik.icure.services.external.rest.v1.dto.filter.FilterDto::class.java, DiscriminatedTypeAdapter(org.taktik.icure.services.external.rest.v1.dto.filter.FilterDto::class.java))
                    .registerTypeAdapter(org.taktik.icure.services.external.rest.v1.dto.gui.type.Data::class.java, DiscriminatedTypeAdapter(org.taktik.icure.services.external.rest.v1.dto.gui.type.Data::class.java))
                    .registerTypeAdapter(org.taktik.icure.services.external.rest.v1.dto.gui.Editor::class.java, DiscriminatedTypeAdapter(org.taktik.icure.services.external.rest.v1.dto.gui.Editor::class.java))
                    .registerTypeAdapter(ByteArray::class.java, JsonDeserializer<ByteArray> { json: JsonElement, _: Type?, _: JsonDeserializationContext? ->
                        if (json.isJsonPrimitive && (json as JsonPrimitive).isString) {
                            Base64.getDecoder().decode(json.getAsString())
                        } else if (json.isJsonArray && ((json as JsonArray).size() == 0 || json[0].isJsonPrimitive)) {
                            val res = ByteArray(json.size())
                            var i = 0
                            while (i < json.size()) {
                                res[i] = json[i].asByte
                                i++
                            }
                            res
                        } else {
                            throw IllegalArgumentException("byte[] are expected to be encoded as base64 strings")
                        }
                    })
            return gsonBuilder.create()
        }
}
