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

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.taktik.net.append
import org.taktik.net.param
import org.taktik.couchdb.util.Exceptions
import java.net.URI

/**
 *
 * @author henrik lundgren
 */
data class ViewQuery(
        val dbPath: String? = null,
        val designDocId: String? = null,
        val viewName: String? = null,
        val listName: String? = null,
        val key: Any? = null,
        val keys: List<Any?>? = null,
        val startKey: Any? = null,
        val startDocId: String? = null,
        val endKey: Any? = null,
        val endDocId: String? = null,
        val limit: Int = NOT_SET,
        val isDescending: Boolean = false,
        val skip: Int = NOT_SET,
        val isGroup: Boolean = false,
        val groupLevel: Int = NOT_SET,
        val isReduce: Boolean = true,
        val isIncludeDocs: Boolean = false,
        val isInclusiveEnd: Boolean = true,
        val isUpdateSeq: Boolean = false,
        val ignoreNotFound: Boolean = false,
        private val staleOk: String? = null
) {
    private var cachedQuery: URI? = null

    fun isStaleOk(): Boolean {
        return staleOk != null && ("ok" == staleOk || "update_after" == staleOk)
    }

    fun dbPath(dbPath: String) = copy(dbPath = dbPath)
    fun designDocId(designDocId: String) = copy(designDocId = designDocId)
    fun allDocs() = viewName(ALL_DOCS_VIEW_NAME)
    fun viewName(viewName: String) = copy(viewName = viewName)
    fun listName(listName: String) = copy(listName = listName)
    fun key(key: String?) = this.copy(key = key)
    fun key(key: Int?) = this.copy(key = key)
    fun key(key: Long?) = this.copy(key = key)
    fun key(key: Double?) = this.copy(key = key)
    fun key(key: Float?) = this.copy(key = key)
    fun key(key: Boolean?) = this.copy(key = key)
    fun key(key: Map<String,*>?) = this.copy(key = key)
    fun key(key: Collection<*>?) = this.copy(key = key)
    fun key(key: ComplexKey) = this.copy(key = key)
    fun keys(keys: Collection<*>?) = this.copy(keys = keys?.toList())
    fun startKey(startKey: Any?) = this.copy(startKey = startKey)
    fun endKey(startKey: Any?) = this.copy(startKey = startKey)
    fun startDocId(startDocId: String?) = this.copy(startDocId = startDocId)
    fun endDocId(endDocId: String?) = this.copy(endDocId = endDocId)

    /**
     * limit=0 you don't get any data, but all meta-data for this View. The number of documents in this View for example.
     * @param i the limit
     * @return the view query for chained calls
     */
    fun limit(limit: Int) = copy(limit = limit)

    /**
     * The stale option can be used for higher performance at the cost of possibly not seeing the all latest data. If you set the stale option to ok, CouchDB may not perform any refreshing on the view that may be necessary.
     * @param b the staleOk flag
     * @return the view query for chained calls
     */
    fun staleOk(staleOk: Boolean) = copy(staleOk = if (staleOk) "ok" else null)

    /**
     * Same as staleOk(true) but will also trigger a rebuild of the view index after the results of the view have been retrieved.
     * (since CouchDB 1.1.0)
     * @return
     */
    fun staleOkUpdateAfter() = copy(staleOk = "update_after")

    /**
     * View rows are sorted by the key; specifying descending=true will reverse their order. Note that the descending option is applied before any key filtering, so you may need to swap the values of the startkey and endkey options to get the expected results.
     * @param descending the descending flag
     * @return the view query for chained calls
     */
    fun descending(descending: Boolean) = copy(isDescending = descending)

    /**
     * The skip option should only be used with small values, as skipping a large range of documents this way is inefficient (it scans the index from the startkey and then skips N elements, but still needs to read all the index values to do that). For efficient paging you'll need to use startkey and limit. If you expect to have multiple documents emit identical keys, you'll need to use startkey_docid in addition to startkey to paginate correctly. The reason is that startkey alone will no longer be sufficient to uniquely identify a row.
     * @param skip the skip count
     * @return the view query for chained calls
     */
    fun skip(skip: Int) = copy(skip = skip)

    /**
     * The group option controls whether the reduce function reduces to a set of distinct keys or to a single result row.
     * @param value the group flag
     * @return the view query for chained calls
     */
    fun group(value: Boolean) = copy(isGroup = value)
    fun groupLevel(level: Int) = copy(groupLevel = level)

    /**
     * If a view contains both a map and reduce function, querying that view will by default return the result of the reduce function. The result of the map function only may be retrieved by passing reduce=false as a query parameter.
     * @param value the reduce flag
     * @return the view query for chained calls
     */
    fun reduce(value: Boolean) = copy(isReduce = value)

    /**
     * The include_docs option will include the associated document. Although, the user should keep in mind that there is a race condition when using this option. It is possible that between reading the view data and fetching the corresponding document that the document has changed. If you want to alleviate such concerns you should emit an object with a _rev attribute as in emit(key, {"_rev": doc._rev}). This alleviates the race condition but leaves the possiblity that the returned document has been deleted (in which case, it includes the "_deleted": true attribute).
     * @param b the includeDocs flag
     * @return the view query for chained calls
     */
    fun includeDocs(value: Boolean) = copy(isIncludeDocs = value)

    /**
     * The inclusive_end option controls whether the endkey is included in the result. It defaults to true.
     * @param value the inclusiveEnd flag
     * @return the view query for chained calls
     */
    fun inclusiveEnd(value: Boolean) = copy(isInclusiveEnd = value)

    /**
     * The update_seq option adds a field to the result indicating the update_seq the view reflects.  It defaults to false.
     * @param value the updateSeq flag
     * @return the view query for chained calls
     */
    fun updateSeq(value: Boolean) = copy(isUpdateSeq = value)

    fun ignoreNotFound(value: Boolean) = copy(ignoreNotFound = value)

    fun hasMultipleKeys() = keys != null
    fun keysAsJson() = keys?.let { keys -> DEFAULT_MAPPER.writeValueAsString(DEFAULT_MAPPER.createObjectNode().apply { putArray("keys").apply { keys.forEach { addPOJO(it) } } }) } ?: "{\"keys\":[]}"
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
                .let { q -> if (isUpdateSeq) q.param("update_seq", "true") else q }

    private fun jsonEncode(key: Any?): String {
        return try {
            DEFAULT_MAPPER.writeValueAsString(key)
        } catch (e: Exception) {
            throw Exceptions.propagate(e)
        }
    }

    private fun buildViewPath(): URI {
        assertHasText(dbPath, "dbPath")
        assertHasText(viewName, "viewName")
        val uri = URI(dbPath!!)
        return when {
            listName != null -> uri.append(designDocId).append("_list").append(listName).append(viewName)
            viewName == ALL_DOCS_VIEW_NAME -> uri.append(ALL_DOCS_VIEW_NAME)
            else -> {
                assertHasText(designDocId, "designDocId")
                uri.append(designDocId).append("_view").append(viewName)
            }
        }
    }

    private fun assertHasText(s: String?, fieldName: String) = check(!s.isNullOrEmpty()) { "$fieldName must have a value" }
    private fun hasValue(i: Int) = i != NOT_SET
    override fun toString(): String = buildQuery().toString()

    companion object {
        private val DEFAULT_MAPPER = ObjectMapper().apply {
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
        private const val ALL_DOCS_VIEW_NAME = "_all_docs"
        private const val NOT_SET = -1
    }
}
