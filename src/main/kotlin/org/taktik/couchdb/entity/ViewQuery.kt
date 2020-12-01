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

package org.taktik.couchdb.entity

import com.fasterxml.jackson.databind.JsonNode
import org.ektorp.impl.StdObjectMapperFactory
import org.ektorp.util.Exceptions
import org.taktik.couchdb.append
import org.taktik.couchdb.param
import org.taktik.couchdb.params
import java.net.URI
import java.util.*

/**
 *
 * @author henrik lundgren
 */
class ViewQuery {
    private val queryParams: MutableMap<String, String>? = TreeMap()
    private var mapper = DEFAULT_MAPPER
    var dbPath: String? = null
        private set
    var designDocId: String? = null
        private set
    var viewName: String? = null
        private set
    var key: Any? = null
        private set
    var keys: List<Any?>? = null
        private set
    var startKey: Any? = null
        private set
    var startDocId: String? = null
        private set
    var endKey: Any? = null
        private set
    var endDocId: String? = null
        private set
    var limit = NOT_SET
        private set
    private var staleOk: String? = null
    var isDescending = false
        private set
    var skip = NOT_SET
        private set
    var isGroup = false
        private set
    var groupLevel = NOT_SET
        private set
    var isReduce = true
        private set
    var isIncludeDocs = false
        private set
    var isInclusiveEnd = true
        private set
    private var ignoreNotFound = false
    var isUpdateSeq = false
        private set
    var isCacheOk = false
        private set
    private var cachedQuery: URI? = null
    private var listName: String? = null
    fun isStaleOk(): Boolean {
        return staleOk != null && ("ok" == staleOk || "update_after" == staleOk)
    }

    fun dbPath(s: String?): ViewQuery {
        reset()
        dbPath = s
        return this
    }

    fun designDocId(s: String?): ViewQuery {
        reset()
        designDocId = s
        return this
    }

    /**
     * Will automatically set the query special _all_docs URI.
     * In this case, setting designDocId will have no effect.
     * @return
     */
    fun allDocs(): ViewQuery {
        reset()
        viewName = ALL_DOCS_VIEW_NAME
        return this
    }

    fun viewName(s: String?): ViewQuery {
        reset()
        viewName = s
        return this
    }

    fun listName(s: String?): ViewQuery {
        reset()
        listName = s
        return this
    }

    /**
     * If set to true, the view query result will be cached and subsequent queries
     * (with cacheOk set) may be served from the cache instead of the db.
     *
     * Note that if the view changes, the cache will be invalidated.
     *
     * @param b
     * @return
     */
    fun cacheOk(b: Boolean): ViewQuery {
        reset()
        isCacheOk = b
        return this
    }

    /**
     * @return the view query for chained calls
     */
    fun key(s: String?): ViewQuery {
        reset()
        key = s
        return this
    }

    /**
     * @return the view query for chained calls
     */
    fun rawKey(s: String): ViewQuery {
        reset()
        key = parseJson(s)
        return this
    }

    private fun parseJson(s: String): JsonNode {
        return try {
            mapper.readTree(s)
        } catch (e: Exception) {
            throw Exceptions.propagate(e)
        }
    }

    /**
     * @return the view query for chained calls
     */
    fun key(i: Int): ViewQuery {
        reset()
        key = i
        return this
    }

    /**
     * @return the view query for chained calls
     */
    fun key(l: Long): ViewQuery {
        reset()
        key = l
        return this
    }

    /**
     * @return the view query for chained calls
     */
    fun key(f: Float): ViewQuery {
        reset()
        key = f
        return this
    }

    /**
     * @return the view query for chained calls
     */
    fun key(d: Double): ViewQuery {
        reset()
        key = d
        return this
    }

    /**
     * @return the view query for chained calls
     */
    fun key(b: Boolean): ViewQuery {
        reset()
        key = b
        return this
    }

    /**
     * @return the view query for chained calls
     */
    fun key(o: Any?): ViewQuery {
        reset()
        key = o
        return this
    }

    /**
     * For multiple-key queries (as of CouchDB 0.9). Keys will be JSON-encoded.
     * @param keyList a list of Object, will be JSON encoded according to each element's type.
     * @return the view query for chained calls
     */
    fun keys(keyList: Collection<*>): ViewQuery {
        reset()
        keys = keyList.toList()
        return this
    }

    /**
     * @return the view query for chained calls
     */
    fun startKey(s: String?): ViewQuery {
        reset()
        startKey = s
        return this
    }

    /**
     * @return the view query for chained calls
     */
    fun rawStartKey(s: String): ViewQuery {
        reset()
        startKey = parseJson(s)
        return this
    }

    /**
     * @return the view query for chained calls
     */
    fun startKey(i: Int): ViewQuery {
        reset()
        startKey = i
        return this
    }

    /**
     * @return the view query for chained calls
     */
    fun startKey(l: Long): ViewQuery {
        reset()
        startKey = l
        return this
    }

    /**
     * @return the view query for chained calls
     */
    fun startKey(f: Float): ViewQuery {
        reset()
        startKey = f
        return this
    }

    /**
     * @return the view query for chained calls
     */
    fun startKey(d: Double): ViewQuery {
        reset()
        startKey = d
        return this
    }

    /**
     * @return the view query for chained calls
     */
    fun startKey(b: Boolean): ViewQuery {
        reset()
        startKey = b
        return this
    }

    /**
     * @return the view query for chained calls
     */
    fun startKey(o: Any?): ViewQuery {
        reset()
        startKey = o
        return this
    }

    fun startDocId(s: String?): ViewQuery {
        reset()
        startDocId = s
        return this
    }

    /**
     * @return the view query for chained calls
     */
    fun endKey(s: String?): ViewQuery {
        reset()
        endKey = s
        return this
    }

    /**
     * @return the view query for chained calls
     */
    fun rawEndKey(s: String): ViewQuery {
        reset()
        endKey = parseJson(s)
        return this
    }

    /**
     * @return the view query for chained calls
     */
    fun endKey(i: Int): ViewQuery {
        reset()
        endKey = i
        return this
    }

    /**
     * @return the view query for chained calls
     */
    fun endKey(l: Long): ViewQuery {
        reset()
        endKey = l
        return this
    }

    /**
     * @return the view query for chained calls
     */
    fun endKey(f: Float): ViewQuery {
        reset()
        endKey = f
        return this
    }

    /**
     * @return the view query for chained calls
     */
    fun endKey(d: Double): ViewQuery {
        reset()
        endKey = d
        return this
    }

    /**
     * @return the view query for chained calls
     */
    fun endKey(b: Boolean): ViewQuery {
        reset()
        endKey = b
        return this
    }

    /**
     * @return the view query for chained calls
     */
    fun endKey(o: Any?): ViewQuery {
        reset()
        endKey = o
        return this
    }

    fun endDocId(s: String?): ViewQuery {
        reset()
        endDocId = s
        return this
    }

    /**
     * limit=0 you don't get any data, but all meta-data for this View. The number of documents in this View for example.
     * @param i the limit
     * @return the view query for chained calls
     */
    fun limit(i: Int): ViewQuery {
        reset()
        limit = i
        return this
    }

    /**
     * The stale option can be used for higher performance at the cost of possibly not seeing the all latest data. If you set the stale option to ok, CouchDB may not perform any refreshing on the view that may be necessary.
     * @param b the staleOk flag
     * @return the view query for chained calls
     */
    fun staleOk(b: Boolean): ViewQuery {
        reset()
        staleOk = if (b) "ok" else null
        return this
    }

    /**
     * Same as staleOk(true) but will also trigger a rebuild of the view index after the results of the view have been retrieved.
     * (since CouchDB 1.1.0)
     * @return
     */
    fun staleOkUpdateAfter(): ViewQuery {
        reset()
        staleOk = "update_after"
        return this
    }

    /**
     * View rows are sorted by the key; specifying descending=true will reverse their order. Note that the descending option is applied before any key filtering, so you may need to swap the values of the startkey and endkey options to get the expected results.
     * @param b the descending flag
     * @return the view query for chained calls
     */
    fun descending(b: Boolean): ViewQuery {
        reset()
        isDescending = b
        return this
    }

    /**
     * The skip option should only be used with small values, as skipping a large range of documents this way is inefficient (it scans the index from the startkey and then skips N elements, but still needs to read all the index values to do that). For efficient paging you'll need to use startkey and limit. If you expect to have multiple documents emit identical keys, you'll need to use startkey_docid in addition to startkey to paginate correctly. The reason is that startkey alone will no longer be sufficient to uniquely identify a row.
     * @param i the skip count
     * @return the view query for chained calls
     */
    fun skip(i: Int): ViewQuery {
        reset()
        skip = i
        return this
    }

    /**
     * The group option controls whether the reduce function reduces to a set of distinct keys or to a single result row.
     * @param b the group flag
     * @return the view query for chained calls
     */
    fun group(b: Boolean): ViewQuery {
        reset()
        isGroup = b
        return this
    }

    fun groupLevel(i: Int): ViewQuery {
        reset()
        groupLevel = i
        return this
    }

    /**
     * If a view contains both a map and reduce function, querying that view will by default return the result of the reduce function. The result of the map function only may be retrieved by passing reduce=false as a query parameter.
     * @param b the reduce flag
     * @return the view query for chained calls
     */
    fun reduce(b: Boolean): ViewQuery {
        reset()
        isReduce = b
        return this
    }

    /**
     * The include_docs option will include the associated document. Although, the user should keep in mind that there is a race condition when using this option. It is possible that between reading the view data and fetching the corresponding document that the document has changed. If you want to alleviate such concerns you should emit an object with a _rev attribute as in emit(key, {"_rev": doc._rev}). This alleviates the race condition but leaves the possiblity that the returned document has been deleted (in which case, it includes the "_deleted": true attribute).
     * @param b the includeDocs flag
     * @return the view query for chained calls
     */
    fun includeDocs(b: Boolean): ViewQuery {
        reset()
        isIncludeDocs = b
        return this
    }

    /**
     * The inclusive_end option controls whether the endkey is included in the result. It defaults to true.
     * @param b the inclusiveEnd flag
     * @return the view query for chained calls
     */
    fun inclusiveEnd(b: Boolean): ViewQuery {
        reset()
        isInclusiveEnd = b
        return this
    }

    /**
     * The update_seq option adds a field to the result indicating the update_seq the view reflects.  It defaults to false.
     * @param b the updateSeq flag
     * @return the view query for chained calls
     */
    fun updateSeq(b: Boolean): ViewQuery {
        reset()
        isUpdateSeq = b
        return this
    }

    fun queryParam(name: String, value: String): ViewQuery {
        queryParams!![name] = value
        return this
    }

    /**
     * Resets internal state so this builder can be used again.
     */
    fun reset() {
        cachedQuery = null
    }

    fun hasMultipleKeys(): Boolean {
        return keys != null
    }

    fun keysAsJson() = keys?.let { keys -> mapper.writeValueAsString(mapper.createObjectNode().apply { putArray("keys").apply { keys.forEach { addPOJO(it) } } }) } ?: "{\"keys\":[]}"
    fun buildQuery(): URI = cachedQuery ?: buildQueryURI().also { cachedQuery = it }

    fun buildQueryURI() = buildViewPath()
                .let { q -> key?.let { q.param("key", jsonEncode(it)) } ?: q }
                .let { q -> startKey?.let { q.param("startkey", jsonEncode(it)) } ?: q }
                .let { q -> endKey?.let { q.param("endkey", jsonEncode(it)) } ?: q }
                .let { q -> startDocId?.let { q.param("startkey_docid", it) } ?: q }
                .let { q -> endDocId?.let { q.param("endkey_docid", it) } ?: q }
                .let { q -> if (hasValue(limit)) q.param("limit", limit.toString()) else q }
                .let { q -> staleOk?.let { q.param("stale", it) } ?: q }
                .let { q -> if (isDescending) q.param("descending", "true") else q }
                .let { q -> if (!isInclusiveEnd) q.param("inclusive_end", "false") else q }
                .let { q -> if (!isReduce) q.param("reduce", "false") else q }
                .let { q -> if (hasValue(skip)) q.param("skip", skip.toString()) else q }
                .let { q -> if (isIncludeDocs) q.param("include_docs", "true") else q }
                .let { q -> if (isGroup) q.param("group", "true") else q }
                .let { q -> if (hasValue(groupLevel)) q.param("group_level", groupLevel.toString()) else q }
                .let { q -> if (queryParams?.isNotEmpty() == true) q.params(queryParams) else q }
                .let { q -> if (isUpdateSeq) q.param("update_seq", "true") else q }

    private fun jsonEncode(key: Any?): String {
        return try {
            mapper.writeValueAsString(key)
        } catch (e: Exception) {
            throw Exceptions.propagate(e)
        }
    }

    private fun buildViewPath(): URI {
        assertHasText(dbPath, "dbPath")
        assertHasText(viewName, "viewName")
        val uri = URI(dbPath!!)
        return if (listName != null) {
            uri.append(designDocId).append("_list").append(listName).append(viewName)
        } else if (ALL_DOCS_VIEW_NAME == viewName) {
            uri.append(viewName)
        } else {
            assertHasText(designDocId, "designDocId")
            uri.append(designDocId).append("_view").append(viewName)
        }
    }

    private fun assertHasText(s: String?, fieldName: String) {
        check(!(s == null || s.length == 0)) { String.format("%s must have a value", fieldName) }
    }

    private fun hasValue(i: Int): Boolean {
        return i != NOT_SET
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = (prime * result
                + if (cachedQuery == null) 0 else cachedQuery.hashCode())
        result = prime * result + if (dbPath == null) 0 else dbPath.hashCode()
        result = prime * result + if (isDescending) 1231 else 1237
        result = (prime * result
                + if (designDocId == null) 0 else designDocId.hashCode())
        result = (prime * result
                + if (endDocId == null) 0 else endDocId.hashCode())
        result = prime * result + if (endKey == null) 0 else endKey.hashCode()
        result = prime * result + if (isGroup) 1231 else 1237
        result = prime * result + groupLevel
        result = prime * result + if (ignoreNotFound) 1231 else 1237
        result = prime * result + if (isIncludeDocs) 1231 else 1237
        result = prime * result + if (isInclusiveEnd) 1231 else 1237
        result = prime * result + if (isUpdateSeq) 1231 else 1237
        result = prime * result + if (key == null) 0 else key.hashCode()
        result = prime * result + limit
        result = (prime * result
                + if (listName == null) 0 else listName.hashCode())
        result = (prime * result
                + (queryParams?.hashCode() ?: 0))
        result = prime * result + if (isReduce) 1231 else 1237
        result = prime * result + skip
        result = prime * result + if (staleOk == null) 0 else staleOk.hashCode()
        result = (prime * result
                + if (startDocId == null) 0 else startDocId.hashCode())
        result = (prime * result
                + if (startKey == null) 0 else startKey.hashCode())
        result = (prime * result
                + if (viewName == null) 0 else viewName.hashCode())
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null) return false
        if (javaClass != obj.javaClass) return false
        val other = obj as ViewQuery
        if (cachedQuery == null) {
            if (other.cachedQuery != null) return false
        } else if (cachedQuery != other.cachedQuery) return false
        if (dbPath == null) {
            if (other.dbPath != null) return false
        } else if (dbPath != other.dbPath) return false
        if (isDescending != other.isDescending) return false
        if (designDocId == null) {
            if (other.designDocId != null) return false
        } else if (designDocId != other.designDocId) return false
        if (endDocId == null) {
            if (other.endDocId != null) return false
        } else if (endDocId != other.endDocId) return false
        if (endKey == null) {
            if (other.endKey != null) return false
        } else if (endKey != other.endKey) return false
        if (isGroup != other.isGroup) return false
        if (groupLevel != other.groupLevel) return false
        if (ignoreNotFound != other.ignoreNotFound) return false
        if (isIncludeDocs != other.isIncludeDocs) return false
        if (isInclusiveEnd != other.isInclusiveEnd) return false
        if (isUpdateSeq != other.isUpdateSeq) return false
        if (key == null) {
            if (other.key != null) return false
        } else if (key != other.key) return false
        if (limit != other.limit) return false
        if (listName == null) {
            if (other.listName != null) return false
        } else if (listName != other.listName) return false
        if (queryParams == null) {
            if (other.queryParams != null) return false
        } else if (queryParams != other.queryParams) return false
        if (isReduce != other.isReduce) return false
        if (skip != other.skip) return false
        if (staleOk == null) {
            if (other.staleOk != null) return false
        } else if (staleOk != other.staleOk) return false
        if (startDocId == null) {
            if (other.startDocId != null) return false
        } else if (startDocId != other.startDocId) return false
        if (startKey == null) {
            if (other.startKey != null) return false
        } else if (startKey != other.startKey) return false
        if (viewName == null) {
            if (other.viewName != null) return false
        } else if (viewName != other.viewName) return false
        return true
    }

    fun setIgnoreNotFound(ignoreNotFound: Boolean) {
        this.ignoreNotFound = ignoreNotFound
    }

    fun isIgnoreNotFound(): Boolean {
        return ignoreNotFound
    }

    override fun toString(): String {
        return buildQuery().toString()
    }

    companion object {
        private val DEFAULT_MAPPER = StdObjectMapperFactory().createObjectMapper()
        private const val ALL_DOCS_VIEW_NAME = "_all_docs"
        private const val NOT_SET = -1
    }
}
