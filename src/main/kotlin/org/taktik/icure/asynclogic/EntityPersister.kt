/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.validation.aspect.Check

interface EntityPersister<E, I> {

    suspend fun createEntities(@Check entities: Collection<E>, createdEntities: MutableCollection<E>): Boolean

    suspend fun updateEntities(@Check entities: Collection<E>): List<E>

    suspend fun deleteByIds(identifiers: Collection<I>)
    //suspend fun deleteByIdsAndRevs(identifiers: Collection<I>, revs: Collection<String>)
    // TODO SH why those 2 lines commented?
    suspend fun undeleteByIds(identifiers: Collection<I>)
    //suspend fun undeleteByIdsAndRevs(identifiers: Collection<I>, revs: Collection<String>)

    suspend fun getAllEntities(): Flow<E>
    suspend fun getAllEntityIds(): Flow<I>

    suspend fun hasEntities(): Boolean

    suspend fun exists(id: I): Boolean

    suspend fun getEntity(id: I): E?
}
