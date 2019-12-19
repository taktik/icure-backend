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
import org.apache.commons.collections4.Predicate
import org.taktik.commons.collections.SortOrder
import org.taktik.couchdb.DocIdentifier
import java.io.Serializable

interface MainLogic {
    suspend fun <E : Serializable?> get(c: Class<E>?, id: String?): E
    suspend fun <E> getEntitiesCount(entityClass: Class<E>?, predicate: Predicate<E>?): Int
    fun <E> getEntities(entityClass: Class<E>?, predicate: Predicate<E>?, offset: Int?, limit: Int?, sortOrders: List<SortOrder<String?>?>?): List<E>?
    fun <E> createEntities(entityClass: Class<E>?, entities: List<E>): Flow<E>
    fun <E> updateEntities(entityClass: Class<E>?, entities: List<E>): Flow<E>
    fun <E, I> deleteEntities(entityClass: Class<E>?, entityIdentifierClass: Class<I>?, entityIdentifiers: Set<I>?): Flow<DocIdentifier>
}
