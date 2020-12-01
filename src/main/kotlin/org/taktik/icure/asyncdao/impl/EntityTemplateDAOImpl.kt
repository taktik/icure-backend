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

import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.queryView
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.EntityTemplateDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.db.StringUtils
import org.taktik.icure.entities.EntityTemplate
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.spring.asynccache.AsyncCacheManager
import org.taktik.icure.utils.createQuery
import org.taktik.icure.utils.distinctById
import java.net.URI

@Repository("entityTemplateDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.EntityTemplate' && !doc.deleted) emit( null, doc._id )}")
class EntityTemplateDAOImpl(couchDbProperties: CouchDbProperties,
                            @Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator, @Qualifier("asyncCacheManager") asyncCacheManager: AsyncCacheManager) : CachedDAOImpl<EntityTemplate>(EntityTemplate::class.java, couchDbProperties, couchDbDispatcher, idGenerator, asyncCacheManager), EntityTemplateDAO {

    @View(name = "by_user_type_descr", map = "classpath:js/entitytemplate/By_user_type_descr.js")
    override suspend fun getByUserIdTypeDescr(userId: String, type: String, searchString: String?, includeEntities: Boolean?): List<EntityTemplate> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val descr = if (searchString != null) StringUtils.sanitizeString(searchString) else null
        val viewQuery = createQuery<EntityTemplate>("by_user_type_descr").startKey(ComplexKey.of(userId, type, descr)).endKey(ComplexKey.of(userId, type, (descr
                ?: "") + "\ufff0")).includeDocs(includeEntities ?: false)

        val result = if (viewQuery.isIncludeDocs) client.queryViewIncludeDocs<ComplexKey, EntityTemplate, EntityTemplate>(viewQuery) else client.queryView<ComplexKey, EntityTemplate>(viewQuery)
        return result.mapNotNull { it.value }.distinctById().toList().sortedWith(compareBy({ it.userId }, { it.entityType }, { it.descr }, { it.id }))
    }

    @View(name = "by_type_descr", map = "classpath:js/entitytemplate/By_type_descr.js")
    override suspend fun getByTypeDescr(type: String, searchString: String?, includeEntities: Boolean?): List<EntityTemplate> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val descr = if (searchString != null) StringUtils.sanitizeString(searchString) else null
        val viewQuery = createQuery<EntityTemplate>("by_type_descr").startKey(ComplexKey.of(type, descr)).endKey(ComplexKey.of(type, (descr
                ?: "") + "\ufff0")).includeDocs(includeEntities ?: false)

        val result = if (viewQuery.isIncludeDocs) client.queryViewIncludeDocs<ComplexKey, EntityTemplate, EntityTemplate>(viewQuery) else client.queryView<ComplexKey, EntityTemplate>(viewQuery)
        return result.mapNotNull { it.value }.distinctById().toList().sortedWith(compareBy({ it.userId }, { it.entityType }, { it.descr }, { it.id }))
    }

}
