/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.handlers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.util.Base64

class JacksonBase64LenientDeserializer : JsonDeserializer<ByteArray>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ByteArray? {
        return if (p.currentToken()?.isScalarValue == true) {
            p.valueAsString?.let { Base64.getDecoder().decode(it) }
        } else null
    }
}
