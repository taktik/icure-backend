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
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.EntityReferenceDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.EntityReference
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.createQuery
import org.taktik.icure.utils.firstOrNull


@Repository("entityReferenceDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.EntityReference' && !doc.deleted) emit(doc._id, doc._id)}")
class EntityReferenceDAOImpl(couchDbProperties: CouchDbProperties,
                             @Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericDAOImpl<EntityReference>(couchDbProperties, EntityReference::class.java, couchDbDispatcher, idGenerator), EntityReferenceDAO {

    override suspend fun getLatest(prefix: String): EntityReference? {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val viewQuery = createQuery<EntityReference>("all").startKey(prefix + "\ufff0").descending(true).includeDocs(true).limit(1)
        val entityReferences = client.queryViewIncludeDocsNoValue<String, EntityReference>(viewQuery).map { it.doc }
        return entityReferences.firstOrNull()
    }
}
