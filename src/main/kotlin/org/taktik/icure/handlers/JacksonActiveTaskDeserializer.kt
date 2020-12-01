package org.taktik.icure.handlers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.boot.jackson.JsonObjectDeserializer
import org.taktik.couchdb.ActiveTask
import org.taktik.couchdb.DatabaseCompactionTask
import org.taktik.couchdb.Indexer
import org.taktik.couchdb.ViewCompactionTask

class JacksonActiveTaskDeserializer : JsonObjectDeserializer<ActiveTask>() {
    private val discriminator = "type"
    private val subclasses = mapOf(
            "indexer" to Indexer::class.java,
            "replication" to Indexer::class.java,
            "database_compaction" to DatabaseCompactionTask::class.java,
            "view_compaction" to ViewCompactionTask::class.java
    )

    override fun deserializeObject(jsonParser: JsonParser?, context: DeserializationContext?, codec: ObjectCodec, tree: JsonNode): ActiveTask {
        val discr = tree[discriminator].textValue() ?: throw IllegalArgumentException("Missing discriminator $discriminator in object")
        val selectedSubClass = subclasses[discr] ?: throw IllegalArgumentException("Invalid subclass $discr in object")
        return codec.treeToValue(tree, selectedSubClass)
    }
}
