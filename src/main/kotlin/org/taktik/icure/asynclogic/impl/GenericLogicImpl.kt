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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.asynclogic.EntityPersister
import org.taktik.icure.entities.base.Identifiable
import org.taktik.icure.logic.SessionLogic
import java.net.URI

abstract class GenericLogicImpl<E : Identifiable<String>, D : GenericDAO<E>>(private val sessionLogic: SessionLogic): EntityPersister<E, String> {

    override suspend fun createEntities(entities: Collection<E>, createdEntities: MutableCollection<E>): Boolean {
        return createdEntities.addAll(getGenericDAO().create(sessionLogic.currentSessionContext.dbInstanceUri, sessionLogic.currentSessionContext.groupId, entities))
    }

    override suspend fun updateEntities(entities: Collection<E>): List<E> {
        return getGenericDAO().save(sessionLogic.currentSessionContext.dbInstanceUri, sessionLogic.currentSessionContext.groupId, entities)
    }

    override suspend fun deleteByIds(identifiers: Collection<String>) {
        val entities = getGenericDAO().getList(sessionLogic.currentSessionContext.dbInstanceUri, sessionLogic.currentSessionContext.groupId, identifiers).toList()
        getGenericDAO().remove(sessionLogic.currentSessionContext.dbInstanceUri, sessionLogic.currentSessionContext.groupId, entities)
    }

    override suspend fun undeleteByIds(identifiers: Collection<String>) {
        val entities = getGenericDAO().getList(sessionLogic.currentSessionContext.dbInstanceUri, sessionLogic.currentSessionContext.groupId, identifiers).toList()
        getGenericDAO().unRemove(sessionLogic.currentSessionContext.dbInstanceUri, sessionLogic.currentSessionContext.groupId, entities)
    }

    override fun getAllEntities(): Flow<E> {
        return getGenericDAO().getAll(sessionLogic.currentSessionContext.dbInstanceUri, sessionLogic.currentSessionContext.groupId)
    }

    override fun getAllEntityIds(): Flow<String> {
        return getGenericDAO().getAllIds(sessionLogic.currentSessionContext.dbInstanceUri, sessionLogic.currentSessionContext.groupId)
    }

    override suspend fun hasEntities(): Boolean {
        return getGenericDAO().hasAny(sessionLogic.currentSessionContext.dbInstanceUri, sessionLogic.currentSessionContext.groupId)
    }

    override suspend fun exists(id: String): Boolean {
        return getGenericDAO().contains(sessionLogic.currentSessionContext.dbInstanceUri, sessionLogic.currentSessionContext.groupId, id)
    }

    override suspend fun getEntity(id: String): E? {
        return getGenericDAO().get(sessionLogic.currentSessionContext.dbInstanceUri, sessionLogic.currentSessionContext.groupId, id)
    }

    protected abstract fun getGenericDAO(): D
}
