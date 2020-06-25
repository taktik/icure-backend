package org.taktik.icure.services.external.rest.handlers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.node.ArrayNode
import org.ektorp.ComplexKey
import java.util.HashMap

class JacksonComplexKeyDeserializer : JsonDeserializer<ComplexKey>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ComplexKey {
        val jsonList = p.readValueAsTree<ArrayNode>()
        return ComplexKey.of(*jsonList.map { it?.let { p.codec.treeToValue(it, Object::class.java) }}.toTypedArray())
    }
}
