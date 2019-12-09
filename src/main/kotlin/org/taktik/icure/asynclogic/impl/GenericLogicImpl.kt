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

import kotlinx.coroutines.flow.*
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.asynclogic.EntityPersister
import org.taktik.icure.entities.base.Identifiable
import org.taktik.icure.utils.reEmit

abstract class GenericLogicImpl<E : Identifiable<String>, D : GenericDAO<E>>(private val sessionLogic: AsyncSessionLogic) : EntityPersister<E, String> {

    override fun createEntities(entities: Collection<E>): Flow<E> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(getGenericDAO().create(dbInstanceUri, groupId, entities))
    }

    override fun updateEntities(entities: Collection<E>): Flow<E> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(getGenericDAO().save(dbInstanceUri, groupId, entities))
    }

    override fun deleteByIds(identifiers: Collection<String>): Flow<DocIdentifier> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val entities = getGenericDAO().getList(dbInstanceUri, groupId, identifiers).toList()
        emitAll(getGenericDAO().remove(dbInstanceUri, groupId, entities))
    }

    override fun undeleteByIds(identifiers: Collection<String>): Flow<DocIdentifier> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val entities = getGenericDAO().getList(dbInstanceUri, groupId, identifiers).toList()
        emitAll(getGenericDAO().unRemove(dbInstanceUri, groupId, entities))
    }

    override fun getAllEntities(): Flow<E> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(getGenericDAO().getAll(dbInstanceUri, groupId))
    }

    override fun getAllEntityIds(): Flow<String> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(getGenericDAO().getAllIds(dbInstanceUri, groupId))
    }

    override suspend fun hasEntities(): Boolean {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return getGenericDAO().hasAny(dbInstanceUri, groupId)
    }

    override suspend fun exists(id: String): Boolean {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return getGenericDAO().contains(dbInstanceUri, groupId, id)
    }

    override suspend fun getEntity(id: String): E? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return getGenericDAO().get(dbInstanceUri, groupId, id)
    }

    protected abstract fun getGenericDAO(): D
}
