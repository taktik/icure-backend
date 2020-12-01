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

package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.EntityPersister
import org.taktik.icure.entities.base.Identifiable
import org.taktik.icure.validation.aspect.Fixer


abstract class GenericLogicImpl<E : Identifiable<String>, D : GenericDAO<E>>(private val sessionLogic: AsyncSessionLogic) : EntityPersister<E, String> {
    private val fixer = Fixer<E>(sessionLogic)
    suspend fun<R> fix(doc: E, next: suspend (doc: E) -> R) : R = next(fixer.fix(doc))
    suspend fun fix(doc: E) : E = fixer.fix(doc)

    override fun createEntities(entities: Collection<E>): Flow<E> = flow {
        emitAll(getGenericDAO().create(entities))
    }

    override fun updateEntities(entities: Collection<E>): Flow<E> = flow {
        emitAll(getGenericDAO().save(entities))
    }

    override fun deleteByIds(identifiers: Collection<String>): Flow<DocIdentifier> = flow {
        val entities = getGenericDAO().getList(identifiers).toList()
        emitAll(getGenericDAO().remove(entities))
    }

    override fun undeleteByIds(identifiers: Collection<String>): Flow<DocIdentifier> = flow {
        val entities = getGenericDAO().getList(identifiers).toList()
        emitAll(getGenericDAO().unRemove(entities))
    }

    override fun getAllEntities(): Flow<E> = flow {
        emitAll(getGenericDAO().getAll())
    }

    override fun getAllEntityIds(): Flow<String> = flow {
        emitAll(getGenericDAO().getAllIds())
    }

    override suspend fun hasEntities(): Boolean {
        return getGenericDAO().hasAny()
    }

    override suspend fun exists(id: String): Boolean {
        return getGenericDAO().contains(id)
    }

    override suspend fun getEntity(id: String): E? {
        return getGenericDAO().get(id)
    }

    protected abstract fun getGenericDAO(): D
}
