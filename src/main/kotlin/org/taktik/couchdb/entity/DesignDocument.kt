/*
 *  iCure Data Stack. Copyright (c) 2020  aduchate
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import org.taktik.couchdb.CouchDbDocument

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class DesignDocument(
        @JsonProperty("_id") override var id: String,
        @JsonProperty("_rev") override var rev: String? = null,
        @JsonProperty("rev_history") override val revHistory: Map<String, String> = mapOf(),
        val language: String? = null,
        val views: Map<String, View> = mapOf(),
        val lists: Map<String, String> = mapOf(),
        val shows: Map<String, String> = mapOf(),
        val updateHandlers: Map<String, String>? = null,
        val filters: Map<String, String> = mapOf()
) : CouchDbDocument {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)

    fun mergeWith(dd: DesignDocument, forceUpdate: Boolean): Pair<DesignDocument, Boolean> {
        var changed = false
        return (((((
                mergeViews(dd.views, forceUpdate)?.let {
                    changed = true
                    dd.copy(views = it)
                } ?: dd)
                .mergeFunctions(lists, dd.lists, forceUpdate)?.let {
                    changed = true
                    dd.copy(lists = it)
                } ?: dd)
                .mergeFunctions(shows, dd.shows, forceUpdate)?.let {
                    changed = true
                    dd.copy(shows = it)
                } ?: dd)
                .mergeFunctions(filters, dd.filters, forceUpdate)?.let {
                    changed = true
                    dd.copy(filters = it)
                } ?: dd)
                .mergeFunctions(updateHandlers, dd.updateHandlers, forceUpdate)?.let {
                    changed = true
                    dd.copy(updateHandlers = it)
        } ?: dd) to changed
    }

    private fun mergeFunctions(existing: Map<String, String>?, mergeFunctions: Map<String, String>?, updateOnDiff: Boolean) =
            (mergeFunctions ?: mapOf()).entries.fold(null as Map<String, String>?) { res, (name, func) ->
                if (existing == null || !existing.containsKey(name) || (updateOnDiff && existing[name] != func)) (existing ?: mapOf()) + (name to func) else res
            }

    private fun mergeViews(mergeViews: Map<String, View>, updateOnDiff: Boolean) =
            mergeViews.entries.fold(null as Map<String, View>?) { res, (name, view) ->
                if (!views.containsKey(name) || (updateOnDiff && views[name] != view)) views + (name to view) else res
            }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class View(val map: String, val reduce: String? = null)
