package org.taktik.icure.asyncdao.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ma.glasnost.orika.MapperFacade
import org.ektorp.ComplexKey
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.FrontEndMigrationDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.FrontEndMigration
import org.taktik.icure.utils.createQuery
import java.net.URI

@Repository("frontEndMigrationDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.FrontEndMigration' && !doc.deleted) emit( null, doc._id )}")
class FrontEndMigrationDAOImpl(@Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator, mapper: MapperFacade) : GenericDAOImpl<FrontEndMigration>(FrontEndMigration::class.java, couchDbDispatcher, idGenerator, mapper), FrontEndMigrationDAO {

    @View(name = "by_userid_name", map = "function(doc) {\n" +
            "            if (doc.java_type == 'org.taktik.icure.entities.FrontEndMigration' && !doc.deleted && doc.name && doc.userId) {\n" +
            "            emit([doc.userId, doc.name],doc._id);\n" +
            "}\n" +
            "}")
    override fun getByUserIdName(dbInstanceUrl: URI, groupId: String, userId: String, name: String?): Flow<FrontEndMigration> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

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
