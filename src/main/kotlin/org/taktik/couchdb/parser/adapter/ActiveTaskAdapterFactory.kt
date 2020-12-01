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
import org.taktik.couchdb.ActiveTask
import org.taktik.couchdb.DatabaseCompactionTask
import org.taktik.couchdb.Indexer
import org.taktik.couchdb.ReplicationTask
import org.taktik.couchdb.UnsupportedTask
import java.lang.reflect.Type

class ActiveTaskAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        return if (Types.getRawType(type) != ActiveTask::class.java || !annotations.isEmpty()) {
            null
        } else {
            object : JsonAdapter<ActiveTask>() {
                val adapters : Map<Type, JsonAdapter<out ActiveTask>> = mapOf(
                        DatabaseCompactionTask::class.java to moshi.adapter(DatabaseCompactionTask::class.java),
                        Indexer::class.java to moshi.adapter(Indexer::class.java),
                        ReplicationTask::class.java to moshi.adapter(ReplicationTask::class.java)
                )

                override fun fromJson(reader: JsonReader): ActiveTask {
                    val jsonMap = reader.readJsonValue() as? Map<String, *>
                    return jsonMap?.let { params ->
                        when {
                            params["type"] == "indexer" -> adapters[Indexer::class.java]?.fromJsonValue(jsonMap)
                            params["type"] == "replication" -> adapters[ReplicationTask::class.java]?.fromJsonValue(jsonMap)
                            else -> adapters[DatabaseCompactionTask::class.java]?.fromJsonValue(jsonMap)
                        }
                    } ?: UnsupportedTask()
                }

                override fun toJson(writer: JsonWriter, value: ActiveTask?) {
                    value?.let { at ->
                        adapters[at.javaClass]?.let {
                            (it as JsonAdapter<ActiveTask>).toJson(writer, at)
                        }
                    } ?: writer.nullValue()
                }
            }
        }
    }
}
