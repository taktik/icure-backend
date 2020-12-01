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
import org.taktik.icure.asyncdao.RoleDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.Role
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.spring.asynccache.AsyncCacheManager
import org.taktik.icure.utils.createQuery
import org.taktik.icure.utils.firstOrNull
import java.net.URI

@Repository("roleDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Role' && !doc.deleted) emit( null, doc._id )}")
class RoleDAOImpl(couchDbProperties: CouchDbProperties,
                  @Qualifier("configCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator, @Qualifier("asyncCacheManager") asyncCacheManager: AsyncCacheManager) : CachedDAOImpl<Role>(Role::class.java, couchDbProperties, couchDbDispatcher, idGenerator, asyncCacheManager), RoleDAO {

    @View(name = "by_name", map = "function(doc) {\n" +
            "            if (doc.java_type == 'org.taktik.icure.entities.Role' && !doc.deleted && doc.name) {\n" +
            "            emit(doc.name,doc._id);\n" +
            "}\n" +
            "}")
    override suspend fun getByName(name: String): Role? {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        return client.queryViewIncludeDocs<String, String, Role>(createQuery<Role>("by_name").key(name).includeDocs(true)).map { it.doc }.firstOrNull()
    }
}
