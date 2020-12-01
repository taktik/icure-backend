package org.taktik.icure.handlers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.taktik.couchdb.entity.ComplexKey

class JacksonComplexKeySerializer : JsonSerializer<ComplexKey>() {
    override fun serialize(value: ComplexKey, jgen: JsonGenerator, provider: SerializerProvider?) {
        jgen.writeStartArray()
        value.components.forEach {
            when {
                it==null -> jgen.writeNull()
                it::class == Object::class -> {
                    jgen.writeStartObject()
                    jgen.writeEndObject()
                }
                else -> jgen.writeObject(it)
            }
        }
        jgen.writeEndArray()
    }
}
