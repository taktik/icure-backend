package org.taktik.couchdb

/**
 * @author aduchate on 17/02/2017.
 */
data class Change(var seq:String? = null, var id:String? = null, var changes:List<Any>? = null, var doc:Map<String, Any?>? = null, var deleted: Boolean = false) {
    override fun toString(): String {
        return "Change(seq=$seq, id=$id, changes=$changes, doc=$doc, deleted=$deleted)"
    }
}
