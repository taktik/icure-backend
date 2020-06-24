package org.taktik.couchdb.parser.adapter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.io.IOException
import java.util.*

internal class SortedSetAdapter<T>(private val elementAdapter: JsonAdapter<T>) : JsonAdapter<SortedSet<T>?>() {
    @Throws(IOException::class)
    override fun fromJson(reader: JsonReader): SortedSet<T>? {
        val result: SortedSet<T>? = TreeSet<T>()
        reader.beginArray()
        while (reader.hasNext()) {
            result?.add(elementAdapter.fromJson(reader))
        }
        reader.endArray()
        return result
    }

    @Throws(IOException::class)
    override fun toJson(writer: JsonWriter, set: SortedSet<T>?) {
        writer.beginArray()
        set?.let {
            for (element in set) {
                elementAdapter.toJson(writer, element)
            }
        }
        writer.endArray()
    }
}
