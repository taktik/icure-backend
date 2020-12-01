package org.taktik.couchdb

data class Change<out T>(val seq: String, val id: String, val changes: List<Any>, val doc: T, val deleted: Boolean = false) {
    override fun toString(): String {
        return "Change(seq=$seq, id=$id, changes=$changes, deleted=$deleted)"
    }
}