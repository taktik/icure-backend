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

import org.taktik.icure.dao.impl.GenericDAOImpl
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.base.StoredICureDocument

import javax.persistence.PersistenceException
import java.util.Objects

/**
 * @author Bernard Paulus - 07/03/2017
 */
class GenericIcureDAOImpl<T : StoredICureDocument>(entityClass: Class<T>, db: CouchDbICureConnector, idGenerator: IDGenerator) : GenericDAOImpl<T>(entityClass, db, idGenerator) {

    override fun <K : Collection<T>> save(newEntity: Boolean?, entities: K): List<T> =
            super.save(entities.filterNotNull().map { it.apply { setTimestamps(this) } })

    override fun save(newEntity: Boolean?, entity: T?): T? {
        if (entity != null) {
            setTimestamps(entity)
        }
        return super.save(newEntity, entity)
    }

    @Throws(PersistenceException::class)
    override fun unremove(entities: Collection<T>) {
        entities.stream().filter(Predicate<T> { Objects.nonNull(it) })
                .forEach(Consumer<T> { setTimestamps(it) })
        super.unremove(entities)
    }

    override fun unremove(entity: T) {
        if (entity != null) {
            setTimestamps(entity)
        }
        super.unremove(entity)
    }

    private fun setTimestamps(entity: StoredICureDocument) {
        val epochMillis = System.currentTimeMillis()
        if (entity.created == null) {
            entity.created = epochMillis
        }
        entity.modified = epochMillis
    }
}
