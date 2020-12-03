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

package org.taktik.icure.asyncdao.impl

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.base.ICureDocument
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.properties.CouchDbProperties
import java.net.URI

/**
 * @author Antoine Duchâteau
 *
 * Change the behaviour of delete by a soft delete and undelete capabilities
 * Automatically update the modified date
 *
 */
open class GenericIcureDAOImpl<T : StoredICureDocument>(entityClass: Class<T>, couchDbProperties: CouchDbProperties,
                                                        couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericDAOImpl<T>(couchDbProperties, entityClass, couchDbDispatcher, idGenerator) {
    override suspend fun save(newEntity: Boolean?, entity: T): T? =
            super.save(newEntity, entity.apply { setTimestamps(this) })

    override suspend fun <K : Collection<T>> save(newEntity: Boolean?, entities: K): Flow<T> =
            super.save(newEntity, entities.map { it.apply { setTimestamps(this) } })

    override suspend fun unRemove(entity: T) =
            super.unRemove(entity.apply { setTimestamps(this) })

    override fun unRemove(entities: Collection<T>) =
            super.unRemove(entities.map { it.apply { setTimestamps(this) } })

    private fun setTimestamps(entity: ICureDocument<String>) {
        val epochMillis = System.currentTimeMillis()
        if (entity.created == null) {
            entity.withTimestamps(created = epochMillis, modified = epochMillis)
        }
        entity.withTimestamps(modified = epochMillis)
    }
}
