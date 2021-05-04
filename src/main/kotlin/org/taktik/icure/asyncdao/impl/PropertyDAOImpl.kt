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

import kotlinx.coroutines.flow.map
import org.taktik.couchdb.annotation.View
import org.springframework.beans.factory.annotation.Qualifier

import org.springframework.stereotype.Repository
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.PropertyDAO
import org.taktik.couchdb.id.IDGenerator
import org.taktik.icure.entities.Property
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.spring.asynccache.AsyncCacheManager

import org.taktik.icure.utils.firstOrNull

@Repository("propertyDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Property' && !doc.deleted) emit(null, doc._id )}")
class PropertyDAOImpl(couchDbProperties: CouchDbProperties,
                      @Qualifier("configCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator, @Qualifier("asyncCacheManager") asyncCacheManager: AsyncCacheManager) : CachedDAOImpl<Property>(Property::class.java, couchDbProperties, couchDbDispatcher, idGenerator, asyncCacheManager), PropertyDAO {

    @View(name = "by_identifier", map = "classpath:js/property/By_identifier_Map.js")
    override suspend fun getByIdentifier(propertyIdentifier: String): Property? {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val wrappedValue = getWrapperFromCache("PID:$propertyIdentifier")
        if (wrappedValue == null) {
            val result = client.queryViewIncludeDocs<String, String, Property>(createQuery(client, "by_identifier").includeDocs(true).key(propertyIdentifier)).map { it.doc }.firstOrNull()
            if (result?.id != null) {
                putInCache(result.id, result)
            }
            return result
        }
        return wrappedValue.get() as Property
    }

    override suspend fun evictFromCache(entity: Property) {
        super.evictFromCache(entity)
    }

    override suspend fun putInCache(key: String, entity: Property) {
        super.putInCache(key, entity)
    }

    companion object {
        private val IDENTIFIER = "type.identifier"
    }
}
