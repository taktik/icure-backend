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

package org.taktik.icure.asyncdao.impl

import kotlinx.coroutines.flow.Flow
import ma.glasnost.orika.MapperFacade
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.base.StoredICureDocument
import java.net.URI

/**
 * @author Antoine Duchâteau
 *
 * Change the behaviour of delete by a soft delete and undelete capabilities
 * Automatically update the modified date
 *
 */
open class GenericIcureDAOImpl<T : StoredICureDocument>(entityClass: Class<T>, couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator, mapper: MapperFacade) : GenericDAOImpl<T>(entityClass, couchDbDispatcher, idGenerator, mapper) {

    override suspend fun save(dbInstanceUrl: URI, groupId: String, newEntity: Boolean?, entity: T): T? =
            super.save(dbInstanceUrl, groupId, newEntity, entity.apply { setTimestamps(this) })

    override suspend fun <K : Collection<T>> save(dbInstanceUrl: URI, groupId: String, newEntity: Boolean?, entities: K): Flow<T> =
            super.save(dbInstanceUrl, groupId, newEntity, entities.map { it.apply { setTimestamps(this) } })

    override suspend fun unRemove(dbInstanceUrl: URI, groupId: String, entity: T) =
            super.unRemove(dbInstanceUrl, groupId, entity.apply { setTimestamps(this) })

    override fun unRemove(dbInstanceUrl: URI, groupId: String, entities: Collection<T>) =
            super.unRemove(dbInstanceUrl, groupId, entities.map { it.apply { setTimestamps(this) } })


    private fun setTimestamps(entity: StoredICureDocument) {
        val epochMillis = System.currentTimeMillis()
        if (entity.created == null) {
            entity.created = epochMillis
        }
        entity.modified = epochMillis
    }
}
