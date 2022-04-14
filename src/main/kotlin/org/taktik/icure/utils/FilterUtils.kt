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
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import org.taktik.icure.asynclogic.AsyncSessionLogic

suspend fun getLoggedHealthCarePartyId(sessionLogic: AsyncSessionLogic): String {
    val user = sessionLogic.getCurrentSessionContext().getUser()
    if (user.healthcarePartyId == null) {
        throw LoginException("You must be logged to perform this action. ")
    }
    return user.healthcarePartyId!!
}

tailrec suspend fun <T> aggregateResults(
    ids: Collection<String>,
    limit: Int,
    supplier: suspend (Collection<String>) -> Flow<T>,
    filter: suspend (T) -> Boolean,
    entities: Flow<T> = emptyFlow(),
    startDocumentId: String? = null,
    heuristic: Int = 2,
): Flow<T> {
    val heuristicLimit = limit * heuristic

    val sortedIds = (startDocumentId?.takeIf { entities.count() == 0 }?.let {
        ids.dropWhile { id -> it != id }
    } ?: ids)

    val filteredEntities =
        flowOf(supplier(sortedIds.take(heuristicLimit)).filter { filter(it) }, entities).flattenConcat()
    val remainingIds = ids.drop(heuristicLimit)

    if (remainingIds.count() == 0 || filteredEntities.count() >= limit) {
        return filteredEntities.take(limit)
    }
    return aggregateResults(remainingIds, limit, supplier, filter, filteredEntities)
}
