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

package org.taktik.icure.services.external.rest.v1.utils

import java.io.Serializable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toCollection
import org.taktik.couchdb.TotalCount
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.ViewRowNoDoc
import org.taktik.couchdb.ViewRowWithDoc
import org.taktik.couchdb.id.Identifiable
import org.taktik.icure.domain.filter.predicate.Predicate
import org.taktik.icure.services.external.rest.v1.dto.PaginatedDocumentKeyIdPair
import org.taktik.icure.services.external.rest.v1.dto.PaginatedList

@Suppress("UNCHECKED_CAST")
// TODO SH MB: handle offsets
suspend fun <U : Identifiable<String>, T : Serializable> Flow<ViewQueryResultEvent>.paginatedList(mapper: (U) -> T, realLimit: Int, predicate: Predicate? = null): PaginatedList<T> {
	var viewRowCount = 0
	var lastProcessedViewRow: ViewRowWithDoc<*, *, *>? = null
	var lastProcessedViewRowNoDoc: ViewRowNoDoc<*, *>? = null

	var totalSize: Int = 0
	var nextKeyPair: PaginatedDocumentKeyIdPair<*>? = null

	val resultRows = mutableListOf<T>()
	this.mapNotNull { viewQueryResultEvent ->
		when (viewQueryResultEvent) {
			is TotalCount -> {
				totalSize = viewQueryResultEvent.total
				null
			}
			is ViewRowWithDoc<*, *, *> -> {
				when {
					viewRowCount == realLimit -> {
						nextKeyPair = PaginatedDocumentKeyIdPair(viewQueryResultEvent.key, viewQueryResultEvent.id) // TODO SH MB: startKey was a List<String> before, now it is a String, ok?
						viewRowCount++
						lastProcessedViewRow?.doc as? U
					}
					viewRowCount < realLimit -> {
						val previous = lastProcessedViewRow
						lastProcessedViewRow = viewQueryResultEvent
						viewRowCount++
						previous?.doc as? U // if this is the first one, the Mono will be empty, so it will be ignored by flatMap
					}
					else -> { // we have more elements than expected, just ignore them
						viewRowCount++
						null
					}
				}?.takeUnless { predicate?.apply(it) == false }
			}
			is ViewRowNoDoc<*, *> -> {
				when {
					viewRowCount == realLimit -> {
						nextKeyPair = PaginatedDocumentKeyIdPair(viewQueryResultEvent.key, viewQueryResultEvent.id)
						viewRowCount++
						lastProcessedViewRowNoDoc?.id as? U
					}
					viewRowCount < realLimit -> {
						val previous = lastProcessedViewRowNoDoc
						lastProcessedViewRowNoDoc = viewQueryResultEvent
						viewRowCount++
						previous?.id as? U // if this is the first one, the Mono will be empty, so it will be ignored by flatMap
					}
					else -> { // we have more elements than expected, just ignore them
						viewRowCount++
						null
					}
				}
			}
			else -> {
				null
			}
		}
	}.map {
		mapper(it)
	}.toCollection(resultRows)

	if (resultRows.size < realLimit) {
		((lastProcessedViewRow?.doc as? U) ?: lastProcessedViewRowNoDoc?.id as U?)?.let { resultRows.add(mapper(it)) }
	}
	return PaginatedList(pageSize = realLimit, totalSize = totalSize, nextKeyPair = nextKeyPair, rows = resultRows)
}

suspend fun Flow<ViewQueryResultEvent>.paginatedListOfIds(realLimit: Int): PaginatedList<String> {
	var viewRowCount = 0
	var lastProcessedViewRow: ViewRowWithDoc<*, *, *>? = null
	var lastProcessedViewRowNoDoc: ViewRowNoDoc<*, *>? = null

	var totalSize: Int = 0
	var nextKeyPair: PaginatedDocumentKeyIdPair<*>? = null

	val resultRows = mutableListOf<String>()
	this.mapNotNull { viewQueryResultEvent ->
		when (viewQueryResultEvent) {
			is TotalCount -> {
				totalSize = viewQueryResultEvent.total
				null
			}
			is ViewRowWithDoc<*, *, *> -> {
				when {
					viewRowCount == realLimit -> {
						nextKeyPair = PaginatedDocumentKeyIdPair(viewQueryResultEvent.key, viewQueryResultEvent.id) // TODO SH MB: startKey was a List<String> before, now it is a String, ok?
						viewRowCount++
						lastProcessedViewRow?.id
					}
					viewRowCount < realLimit -> {
						val previous = lastProcessedViewRow
						lastProcessedViewRow = viewQueryResultEvent
						viewRowCount++
						previous?.id
					}
					else -> { // we have more elements than expected, just ignore them
						viewRowCount++
						null
					}
				}
			}
			is ViewRowNoDoc<*, *> -> {
				when {
					viewRowCount == realLimit -> {
						nextKeyPair = PaginatedDocumentKeyIdPair(viewQueryResultEvent.key, viewQueryResultEvent.id)
						viewRowCount++
						lastProcessedViewRowNoDoc?.id
					}
					viewRowCount < realLimit -> {
						val previous = lastProcessedViewRowNoDoc
						lastProcessedViewRowNoDoc = viewQueryResultEvent
						viewRowCount++
						previous?.id // if this is the first one, the Mono will be empty, so it will be ignored by flatMap
					}
					else -> { // we have more elements than expected, just ignore them
						viewRowCount++
						null
					}
				}
			}
			else -> {
				null
			}
		}
	}.toCollection(resultRows)

	if (resultRows.size < realLimit) {
		((lastProcessedViewRow?.id) ?: lastProcessedViewRowNoDoc?.id)?.let { resultRows.add(it) }
	}
	return PaginatedList(pageSize = realLimit, totalSize = totalSize, nextKeyPair = nextKeyPair, rows = resultRows)
}

suspend fun <T : Serializable> Flow<ViewQueryResultEvent>.paginatedList(realLimit: Int): PaginatedList<T> {
	var viewRowCount = 0
	var lastProcessedViewRow: ViewRowWithDoc<*, *, *>? = null

	var totalSize: Int = 0
	var nextKeyPair: PaginatedDocumentKeyIdPair<*>? = null

	val resultRows = mutableListOf<T>()
	this.mapNotNull { viewQueryResultEvent ->
		when (viewQueryResultEvent) {
			is TotalCount -> {
				totalSize = viewQueryResultEvent.total
				null
			}
			is ViewRowWithDoc<*, *, *> -> {
				when {
					viewRowCount == realLimit -> {
						nextKeyPair = PaginatedDocumentKeyIdPair(viewQueryResultEvent.key, viewQueryResultEvent.id)
						viewRowCount++
						lastProcessedViewRow?.doc as? T
					}
					viewRowCount < realLimit -> {
						val previous = lastProcessedViewRow
						lastProcessedViewRow = viewQueryResultEvent
						viewRowCount++
						previous?.doc as? T
					}
					else -> { // we have more elements than expected, just ignore them
						viewRowCount++
						null
					}
				}
			}
			else -> {
				null
			}
		}
	}.toCollection(resultRows)
	if (resultRows.size < realLimit) {
		(lastProcessedViewRow?.doc as? T)?.let {
			resultRows.add(it)
		}
	}
	return PaginatedList(pageSize = realLimit, totalSize = totalSize, nextKeyPair = nextKeyPair, rows = resultRows)
}
