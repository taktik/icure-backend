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

package org.taktik.icure.utils

import javax.security.auth.login.LoginException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import org.taktik.icure.asynclogic.AsyncSessionLogic

suspend fun getLoggedHealthCarePartyId(sessionLogic: AsyncSessionLogic): String {
    val user = sessionLogic.getCurrentSessionContext().getUser()
    if (user.healthcarePartyId == null) {
        throw LoginException("You must be logged to perform this action. ")
    }
    return user.healthcarePartyId
}

suspend fun getLoggedDataOwnerId(sessionLogic: AsyncSessionLogic): String {
    val user = sessionLogic.getCurrentSessionContext().getUser()
    return user.healthcarePartyId ?: user.patientId ?: user.deviceId
    ?: throw LoginException("You must be logged to perform this action. ")
}

suspend fun <T>aggregateResults(
        ids: Collection<String>,
        limit: Int,
        supplier: suspend (Collection<String>) -> Flow<T>,
        filter: suspend (T) -> Boolean = { true },
        entities: List<T> = emptyList(),
        startDocumentId: String? = null,
        heuristic: Int = 2,
        ) = aggregateResults(ids, limit, supplier, filter, entities, startDocumentId, 0, heuristic = heuristic).second

tailrec suspend fun <T, A> aggregateResults(
    ids: Collection<String>,
    limit: Int,
    supplier: suspend (Collection<String>) -> Flow<T>,
    filter: suspend (T) -> Boolean = { true },
    entities: List<T> = emptyList(),
    startDocumentId: String? = null,
    filteredOutAccumulator: A,
    filteredOutElementsReducer: suspend (A, T) -> A = { acc, _ -> acc },
    heuristic: Int = 2,
): Pair<A,List<T>> {
    val heuristicLimit = limit * heuristic

    val sortedIds = (startDocumentId?.let {
        ids.dropWhile { id -> it != id }
    } ?: ids)

    var acc = filteredOutAccumulator
    val filteredEntities = entities + supplier(sortedIds.take(heuristicLimit)).filter { el -> filter(el).also { if(!it) { acc = filteredOutElementsReducer(acc, el) } } }.toList()
    val remainingIds = sortedIds.drop(heuristicLimit)

    if (remainingIds.isEmpty() || filteredEntities.count() >= limit) {
        return acc to filteredEntities.take(limit)
    }
    return aggregateResults(
            ids = remainingIds,
            limit = limit,
            supplier = supplier,
            filter = filter,
            entities = filteredEntities,
            filteredOutAccumulator = acc,
            filteredOutElementsReducer = filteredOutElementsReducer,
            heuristic = heuristic
    )
}
