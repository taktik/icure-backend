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
import org.taktik.icure.asyncdao.PropertyTypeDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.PropertyType
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.spring.asynccache.AsyncCacheManager
import org.taktik.icure.utils.createQuery
import org.taktik.icure.utils.firstOrNull

@Repository("propertyTypeDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.PropertyType' && !doc.deleted) emit( null, doc._id )}")
class PropertyTypeDAOImpl(couchDbProperties: CouchDbProperties,
                          @Qualifier("configCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator, @Qualifier("asyncCacheManager") asyncCacheManager: AsyncCacheManager) : CachedDAOImpl<PropertyType>(PropertyType::class.java, couchDbProperties, couchDbDispatcher, idGenerator, asyncCacheManager), PropertyTypeDAO {

    @View(name = "by_identifier", map = "function(doc) {\n" +
            "            if (doc.java_type == 'org.taktik.icure.entities.PropertyType' && !doc.deleted && doc.identifier) {\n" +
            "            emit(doc.identifier,doc._id);\n" +
            "}\n" +
            "}")
    override suspend fun getByIdentifier(propertyTypeIdentifier: String): PropertyType? {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val wrappedValue = getWrapperFromCache("PID:$propertyTypeIdentifier")
        if (wrappedValue == null) {
            val result = client.queryViewIncludeDocs<String, String, PropertyType>(createQuery<PropertyType>("by_identifier").includeDocs(true).key(propertyTypeIdentifier)).map { it.doc }.firstOrNull()

            if (result?.id != null) {
                putInCache(result.id, result)
            }
            return result
        }
        return wrappedValue.get() as PropertyType
    }


    override suspend fun evictFromCache(entity: PropertyType) {
        super.evictFromCache(entity)
    }

    override suspend fun putInCache(key: String, entity: PropertyType) {
        super.putInCache(key, entity)
    }

}
