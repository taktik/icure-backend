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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.reactor.asFlux
import org.taktik.couchdb.ViewRowWithDoc
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.asynclogic.EntityPersister
import org.taktik.icure.entities.base.Identifiable
import org.taktik.icure.logic.SessionLogic
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URI

abstract class GenericLogicImpl<E : Identifiable<String>, D : GenericDAO<E>>(private val sessionLogic: AsyncSessionLogic): EntityPersister<E, String> {

    override suspend fun createEntities(dbInstanceUri: URI, groupId: String, entities: Collection<E>, createdEntities: MutableCollection<E>): Boolean {
        return createdEntities.addAll(getGenericDAO().create(dbInstanceUri, groupId, entities))
    }

    override suspend fun updateEntities(dbInstanceUri: URI, groupId: String, entities: Collection<E>): List<E> {
        return getGenericDAO().save(dbInstanceUri, groupId, entities)
    }

    override suspend fun deleteByIds(dbInstanceUri: URI, groupId: String, identifiers: Collection<String>) {
        val entities = getGenericDAO().getList(dbInstanceUri, groupId, identifiers).toList()
        getGenericDAO().remove(dbInstanceUri, groupId, entities)
    }

    override suspend fun undeleteByIds(dbInstanceUri: URI, groupId: String, identifiers: Collection<String>) {
        val entities = getGenericDAO().getList(dbInstanceUri, groupId, identifiers).toList()
        getGenericDAO().unRemove(dbInstanceUri, groupId, entities)
    }

    override suspend fun getAllEntities(dbInstanceUri: URI, groupId: String): Flow<E> {
        return getGenericDAO().getAll(dbInstanceUri, groupId)
    }

    override suspend fun getAllEntityIds(dbInstanceUri: URI, groupId: String): Flow<String> {
        return getGenericDAO().getAllIds(dbInstanceUri, groupId)
    }

    override suspend fun hasEntities(dbInstanceUri: URI, groupId: String): Boolean {
        return getGenericDAO().hasAny(dbInstanceUri, groupId)
    }

    override suspend fun exists(dbInstanceUri: URI, groupId: String, id: String): Boolean {
        return getGenericDAO().contains(dbInstanceUri, groupId, id)
    }

    override suspend fun getEntity(dbInstanceUri: URI, groupId: String, id: String): E? {
        return getGenericDAO().get(dbInstanceUri, groupId, id)
    }

    protected abstract fun getGenericDAO(): D
}
