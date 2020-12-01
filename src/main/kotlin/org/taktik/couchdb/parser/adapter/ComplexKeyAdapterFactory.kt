/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.couchdb.parser.adapter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.taktik.couchdb.entity.ComplexKey
import java.lang.reflect.Type

class ComplexKeyAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        return if (Types.getRawType(type) != ComplexKey::class.java || !annotations.isEmpty()) {
            null
        } else {
            object : JsonAdapter<ComplexKey>() {
                override fun fromJson(reader: JsonReader): ComplexKey {
                    val jsonList = reader.readJsonValue() as? List<*> ?: throw IllegalStateException("Invalid complex key format detected during deserialisation")
                    return ComplexKey.of(*jsonList.map { it?.let { moshi.adapter(it.javaClass).fromJsonValue(it) } }.toTypedArray())
                }

                override fun toJson(writer: JsonWriter, value: ComplexKey?) {
                    value?.let { ck ->
                        writer.beginArray()
                        ck.components.forEach {
                            it?.let { moshi.adapter(it.javaClass).toJson(writer, it) } ?: writer.nullValue()
                        }
                        writer.endArray()
                    } ?: writer.nullValue()
                }
            }
        }
    }
}
