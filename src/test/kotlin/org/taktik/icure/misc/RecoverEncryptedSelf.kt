package org.taktik.icure.misc

import org.ektorp.Options
import org.ektorp.ViewQuery
import org.ektorp.http.StdHttpClient
import org.ektorp.impl.StdCouchDbInstance

class RecoverEncryptedSelf {
    fun visit(list1: List<*>, list2: List<*>) : Boolean {
        return if (list1.size != list2.size) {
             false
        } else {
            list1.mapIndexed { i, item1 ->
                val item2 = list2[i]
                if (item1 != null && item1 is List<*> && item2 != null && item2 is List<*>) {
                    visit(item1, item2)
                } else if (item1 != null && item1 is Map<*, *> && item2 != null && item2 is Map<*, *>) {
                    visit(item1, item2)
                } else false
            }.any { it }
        }
    }

    fun visit(map1: Map<*, *>, map2: Map<*, *>) : Boolean {
        return map2.map { e ->
            val k = e.key
            val item2 = e.value
            val item1 = map1[k]

            if (k == "encryptedSelf") {
                if (item1 == null && item2 != null && map1["_id"] != null && map1["_id"] == map2["_id"]) {
                    (map1 as HashMap<String, Any>)["encryptedSelf"] = item2; true
                } else false
            } else if (item1 != null && item1 is List<*> && item2 is List<*> && item1.isNotEmpty() && item2.isNotEmpty() && (item1 + item2).all { it is Map<*, *> && it.containsKey("_id") }) {
                visit(item1.sortedBy { (it as Map<*, *>)["_id"] as String }, item2.sortedBy { (it as Map<*, *>)["_id"] as String })
            } else if (item1 != null && item1 is Map<*, *> && item2 is Map<*, *>) {
                visit(item1, item2)
            } else false
        }.any { it }
    }

    fun recover(db: String) {
        val httpClient = StdHttpClient.Builder().socketTimeout(120000).connectionTimeout(120000).url(db).build()
        val dbInstance = StdCouchDbInstance(httpClient);
        // if the second parameter is true, the database will be created if it doesn't exists
        val couchdb = dbInstance.createConnector("icure-healthdata", false)
        listOf("Contact", "HealthElement").forEach {
            couchdb.queryViewForIds(ViewQuery().designDocId("_design/$it").viewName("all")).forEach { id ->
                val revs = couchdb.getRevisions(id)
                val current = couchdb.get(HashMap::class.java, id)
                if (revs.size > 1) {
                    revs.filterIndexed { index, revision -> index>0 }.forEach { revision ->
                        if (revision.status == "available") {
                            val lessRecent = couchdb.get(HashMap::class.java, id, Options().revision(revision.rev))
                            if (visit(current as Map<*, *>, lessRecent as Map<*, *>)) {
                                couchdb.update(current)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    RecoverEncryptedSelf().recover(args[0])
}