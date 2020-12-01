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
