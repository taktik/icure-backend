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
import org.taktik.couchdb.DocIdentifier

interface EntityPersister<E, I> {

    fun createEntities(entities: Collection<E>): Flow<E>

    fun updateEntities(entities: Collection<E>): Flow<E>

    fun deleteByIds(identifiers: Collection<I>): Flow<DocIdentifier>
    fun undeleteByIds(identifiers: Collection<I>): Flow<DocIdentifier>

    fun getAllEntities(): Flow<E>
    fun getAllEntityIds(): Flow<I>

    suspend fun hasEntities(): Boolean

    suspend fun exists(id: I): Boolean

    suspend fun getEntity(id: I): E?
}
