package org.taktik.icure.asyncdao.impl

import kotlinx.coroutines.flow.map
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.EntityReferenceDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.EntityReference
import org.taktik.icure.utils.createQuery
import org.taktik.icure.utils.firstOrNull
import java.net.URI



@Repository("entityReferenceDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.EntityReference' && !doc.deleted) emit(doc._id)}")
class EntityReferenceDAOImpl(@Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericDAOImpl<EntityReference>(EntityReference::class.java, couchDbDispatcher, idGenerator), EntityReferenceDAO {

    override suspend fun getLatest(dbInstanceUrl: URI, groupId: String, prefix: String): EntityReference? {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val viewQuery = createQuery<EntityReference>("all").startKey(prefix + "\ufff0").descending(true).includeDocs(true).limit(1)
        val entityReferences = client.queryViewIncludeDocsNoValue<String, EntityReference>(viewQuery).map { it.doc }
        return entityReferences.firstOrNull()
    }
}
