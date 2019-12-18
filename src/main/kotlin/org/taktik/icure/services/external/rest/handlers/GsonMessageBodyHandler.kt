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
package org.taktik.icure.services.external.rest.handlers

import com.google.gson.*
import org.apache.commons.codec.binary.Base64
import org.taktik.icure.dto.filter.Filter
import org.taktik.icure.dto.gui.Editor
import org.taktik.icure.dto.gui.type.Data
import org.taktik.icure.services.external.rest.v1.dto.PaginatedDocumentKeyIdPair
import org.taktik.icure.services.external.rest.v1.dto.filter.FilterDto
import org.taktik.icure.services.external.rest.v1.dto.filter.predicate.Predicate
import java.io.*
import java.lang.reflect.Type
import javax.ws.rs.Consumes
import javax.ws.rs.Produces
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.ext.MessageBodyReader
import javax.ws.rs.ext.MessageBodyWriter
import javax.ws.rs.ext.Provider

//Used by Jersey instead of jackson
@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class GsonMessageBodyHandler : MessageBodyWriter<Any?>, MessageBodyReader<Any> {
    var gson: Gson? = null
        get() {
            if (field == null) {
                val gsonBuilder = GsonBuilder()
                gsonBuilder
                        .registerTypeAdapter(PaginatedDocumentKeyIdPair::class.java, JsonDeserializer<PaginatedDocumentKeyIdPair<*>> { json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext ->
                            val obj = context.deserialize<Map<String, Any>>(json, MutableMap::class.java)
                            PaginatedDocumentKeyIdPair(obj["startKey"] as List<String>?, obj["startKeyDocId"] as String?) //TODO check what happens when key is string
                        })
                        .registerTypeAdapter(Predicate::class.java, DiscriminatedTypeAdapter(Predicate::class.java))
                        .registerTypeHierarchyAdapter(ByteArray::class.java, ByteArrayToBase64TypeAdapter())
                        .registerTypeAdapter(Filter::class.java, DiscriminatedTypeAdapter(Filter::class.java))
                        .registerTypeAdapter(Editor::class.java, DiscriminatedTypeAdapter(Editor::class.java))
                        .registerTypeAdapter(Data::class.java, DiscriminatedTypeAdapter(Data::class.java))
                        .registerTypeAdapter(FilterDto::class.java, DiscriminatedTypeAdapter(FilterDto::class.java))
                        .registerTypeAdapter(org.taktik.icure.services.external.rest.v1.dto.gui.type.Data::class.java, DiscriminatedTypeAdapter(org.taktik.icure.services.external.rest.v1.dto.gui.type.Data::class.java))
                        .registerTypeAdapter(org.taktik.icure.services.external.rest.v1.dto.gui.Editor::class.java, DiscriminatedTypeAdapter(org.taktik.icure.services.external.rest.v1.dto.gui.Editor::class.java))
                        .registerTypeAdapter(Double::class.java, JsonSerializer { src: Double?, _: Type?, _: JsonSerializationContext? -> if (src == null) null else JsonPrimitive(if (src.isNaN()) 0.0 else if (src.isInfinite()) if (src > 0) Double.MAX_VALUE else Double.MIN_VALUE else src) })
                        .registerTypeAdapter(Boolean::class.java, JsonDeserializer { json: JsonElement, _: Type?, _: JsonDeserializationContext? -> if ((json as JsonPrimitive).isBoolean) json.getAsBoolean() else if (json.isString) json.getAsString() == "true" else json.getAsInt() != 0 } as JsonDeserializer<Boolean>)
                field = gsonBuilder.create()
            }
            return field
        }
        private set

    override fun isReadable(type: Class<*>?, genericType: Type,
                            annotations: Array<Annotation>, mediaType: MediaType): Boolean {
        return true
    }

    @Throws(IOException::class)
    override fun readFrom(type: Class<Any>, genericType: Type, annotations: Array<Annotation>, mediaType: MediaType, httpHeaders: MultivaluedMap<String, String>, entityStream: InputStream): Any {
        InputStreamReader(entityStream, UTF_8).use { streamReader ->
            val jsonType: Type = if (type == genericType) {
                type
            } else {
                genericType
            }
            return gson!!.fromJson(streamReader, jsonType)
        }
    }

    override fun isWriteable(type: Class<*>?, genericType: Type, annotations: Array<Annotation>, mediaType: MediaType): Boolean {
        return true
    }

    override fun getSize(`object`: Any?, type: Class<*>?, genericType: Type, annotations: Array<Annotation>, mediaType: MediaType): Long {
        return -1
    }

    @Throws(IOException::class, WebApplicationException::class)
    override fun writeTo(`object`: Any?, type: Class<*>, genericType: Type, annotations: Array<Annotation>, mediaType: MediaType, httpHeaders: MultivaluedMap<String, Any>, entityStream: OutputStream) {
        OutputStreamWriter(entityStream, UTF_8).use { writer ->
            val jsonType: Type
            jsonType = if (type == genericType) {
                type
            } else {
                genericType
            }
            gson!!.toJson(`object`, jsonType, writer)
        }
    }

    private inner class ByteArrayToBase64TypeAdapter : JsonSerializer<ByteArray?>, JsonDeserializer<ByteArray> {
        private val b64 = Base64()
        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ByteArray {
            return if (json.isJsonArray) {
                val asJsonArray = json.asJsonArray
                val res = ByteArray(asJsonArray.size())
                var i = 0
                val bi: Iterator<JsonElement> = asJsonArray.iterator()
                while (bi.hasNext()) {
                    res[i++] = bi.next().asByte
                }
                res
            } else {
                b64.decode(json.asString)
            }
        }

        override fun serialize(src: ByteArray?, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(b64.encodeToString(src))
        }
    }

    companion object {
        private const val UTF_8 = "UTF-8"
    }
}
