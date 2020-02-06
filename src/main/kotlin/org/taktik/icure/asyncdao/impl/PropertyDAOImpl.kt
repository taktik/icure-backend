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

import kotlinx.coroutines.flow.map
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier

import org.springframework.stereotype.Repository
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.PropertyDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.Property
import org.taktik.icure.spring.asynccache.AsyncCacheManager
import org.taktik.icure.utils.createQuery
import org.taktik.icure.utils.firstOrNull
import java.net.URI

@Repository("propertyDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Property' && !doc.deleted) emit(doc._id )}")
class PropertyDAOImpl(@Qualifier("configCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator, @Qualifier("asyncCacheManager") AsyncCacheManager: AsyncCacheManager) : CachedDAOImpl<Property>(Property::class.java, couchDbDispatcher, idGenerator, AsyncCacheManager), PropertyDAO {

    @View(name = "by_identifier", map = "classpath:js/property/By_identifier_Map.js")
    override suspend fun getByIdentifier(dbInstanceUrl: URI, groupId: String, propertyIdentifier: String): Property? {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val wrappedValue = getWrapperFromCache(dbInstanceUrl, groupId, "PID:$propertyIdentifier")
        if (wrappedValue == null) {
            val result = client.queryViewIncludeDocs<String, String, Property>(createQuery<Property>("by_identifier").includeDocs(true).key(propertyIdentifier)).map { it.doc }.firstOrNull()
            if (result?.id != null) {
                putInCache(dbInstanceUrl, groupId, result.id, result)
            }
            return result
        }
        return wrappedValue.get() as Property
    }

    override fun evictFromCache(dbInstanceUrl: URI, groupId: String, entity: Property) {
        super.evictFromCache(dbInstanceUrl, groupId, entity)
    }

    override fun putInCache(dbInstanceUrl: URI, groupId: String, key: String, entity: Property) {
        super.putInCache(dbInstanceUrl, groupId, key, entity)
    }

    companion object {
        private val IDENTIFIER = "type.identifier"
    }
}
