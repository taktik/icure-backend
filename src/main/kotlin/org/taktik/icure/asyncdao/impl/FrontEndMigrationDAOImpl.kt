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
import kotlinx.coroutines.flow.map
import org.taktik.couchdb.annotation.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.FrontEndMigrationDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.FrontEndMigration
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.createQuery

@Repository("frontEndMigrationDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.FrontEndMigration' && !doc.deleted) emit( null, doc._id )}")
class FrontEndMigrationDAOImpl(couchDbProperties: CouchDbProperties,
                               @Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericDAOImpl<FrontEndMigration>(couchDbProperties, FrontEndMigration::class.java, couchDbDispatcher, idGenerator), FrontEndMigrationDAO {

    @View(name = "by_userid_name", map = "function(doc) {\n" +
            "            if (doc.java_type == 'org.taktik.icure.entities.FrontEndMigration' && !doc.deleted && doc.name && doc.userId) {\n" +
            "            emit([doc.userId, doc.name],doc._id);\n" +
            "}\n" +
            "}")
    override fun getByUserIdName(userId: String, name: String?): Flow<FrontEndMigration> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val viewQuery = if (name == null) {
            // This is a range query
            val startKey = ComplexKey.of(userId)
            val endKey = ComplexKey.of(userId, ComplexKey.emptyObject())

            createQuery<FrontEndMigration>("by_userid_name").startKey(startKey).endKey(endKey).includeDocs(true)
        } else {
            createQuery<FrontEndMigration>("by_userid_name").key(ComplexKey.of(userId, name)).includeDocs(true)
        }
        return client.queryViewIncludeDocs<ComplexKey, String, FrontEndMigration>(viewQuery).map { it.doc }
    }

}
