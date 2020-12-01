package org.taktik.couchdb.parser.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.util.*

internal class Base64Adapter {
    @FromJson
    fun fromJson(string: String?): ByteArray {
        return Base64.getDecoder().decode(string)
    }

    @ToJson
    fun toJson(bytes: ByteArray?): String {
        return Base64.getEncoder().encodeToString(bytes)
    }
}
