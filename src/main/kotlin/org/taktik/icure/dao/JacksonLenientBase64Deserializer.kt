package org.taktik.icure.dao

import java.util.Base64
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

class JacksonLenientBase64Deserializer : JsonDeserializer<ByteArray>() {
	override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ByteArray? {
		return if (p.currentToken()?.isScalarValue == true) {
			p.valueAsString?.let { Base64.getDecoder().decode(it) }
		} else null
	}
}
