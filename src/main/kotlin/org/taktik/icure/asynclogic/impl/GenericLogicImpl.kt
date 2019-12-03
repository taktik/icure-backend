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

    override suspend fun createEntities(entities: Collection<E>, createdEntities: MutableCollection<E>): Boolean {
        val dbInstanceUri = sessionLogic.getCurrentSessionContext().map { it.getDbInstanceUri() }.block()!!
        val groupId = sessionLogic.getCurrentSessionContext().map { it.getGroupId() }.block()!!
        return createdEntities.addAll(getGenericDAO().create(dbInstanceUri, groupId, entities))
    }

    override suspend fun updateEntities(entities: Collection<E>): List<E> {
        val dbInstanceUri = sessionLogic.getCurrentSessionContext().map { it.getDbInstanceUri() }.block()!!
        val groupId = sessionLogic.getCurrentSessionContext().map { it.getGroupId() }.block()!!
        return getGenericDAO().save(dbInstanceUri, groupId, entities)
    }

    override suspend fun deleteByIds(identifiers: Collection<String>) {
        val dbInstanceUri = sessionLogic.getCurrentSessionContext().map { it.getDbInstanceUri() }.block()!!
        val groupId = sessionLogic.getCurrentSessionContext().map { it.getGroupId() }.block()!!
        val entities = getGenericDAO().getList(dbInstanceUri, groupId, identifiers).toList()
        getGenericDAO().remove(dbInstanceUri, groupId, entities)
    }

    override suspend fun undeleteByIds(identifiers: Collection<String>) {
        val dbInstanceUri = sessionLogic.getCurrentSessionContext().map { it.getDbInstanceUri() }.block()!!
        val groupId = sessionLogic.getCurrentSessionContext().map { it.getGroupId() }.block()!!
        val entities = getGenericDAO().getList(dbInstanceUri, groupId, identifiers).toList()
        getGenericDAO().unRemove(dbInstanceUri, groupId, entities)
    }

    @ExperimentalCoroutinesApi
    fun <T : Any> injectReactorContext(flow: Flow<T>): Flux<T> {
        return Mono.subscriberContext().flatMapMany { reactorCtx ->
            flow.flowOn(reactorCtx.asCoroutineContext()).asFlux()
        }
    }

    override suspend fun getAllEntities(): Flux<E> {
        return injectReactorContext(
                flow {
                    val dbInstanceUri = sessionLogic.getCurrentSessionContext().map { it.getDbInstanceUri()!! }.awaitSingle()
                    val groupId = sessionLogic.getCurrentSessionContext().map { it.getGroupId()!! }.awaitSingle()
                    getGenericDAO().getAll(dbInstanceUri, groupId).collect {
                        println(it)
                        emit(it)
                    }
                })
    }

    override suspend fun getAllEntityIds(): Flow<String> {
        val dbInstanceUri = sessionLogic.getCurrentSessionContext().map { it.getDbInstanceUri() }.block()!!
        val groupId = sessionLogic.getCurrentSessionContext().map { it.getGroupId() }.block()!!
        return getGenericDAO().getAllIds(dbInstanceUri, groupId)
    }

    override suspend fun hasEntities(): Boolean {
        val dbInstanceUri = sessionLogic.getCurrentSessionContext().map { it.getDbInstanceUri() }.block()!!
        val groupId = sessionLogic.getCurrentSessionContext().map { it.getGroupId() }.block()!!
        return getGenericDAO().hasAny(dbInstanceUri, groupId)
    }

    override suspend fun exists(id: String): Boolean {
        val dbInstanceUri = sessionLogic.getCurrentSessionContext().map { it.getDbInstanceUri() }.block()!!
        val groupId = sessionLogic.getCurrentSessionContext().map { it.getGroupId() }.block()!!
        return getGenericDAO().contains(dbInstanceUri, groupId, id)
    }

    override suspend fun getEntity(id: String): E? {
        val dbInstanceUri = sessionLogic.getCurrentSessionContext().map { it.getDbInstanceUri() }.block()!!
        val groupId = sessionLogic.getCurrentSessionContext().map { it.getGroupId() }.block()!!
        return getGenericDAO().get(dbInstanceUri, groupId, id)
    }

    protected abstract fun getGenericDAO(): D
}
