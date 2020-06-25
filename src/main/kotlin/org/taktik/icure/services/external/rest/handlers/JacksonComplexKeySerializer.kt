package org.taktik.icure.services.external.rest.handlers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.ektorp.ComplexKey

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
