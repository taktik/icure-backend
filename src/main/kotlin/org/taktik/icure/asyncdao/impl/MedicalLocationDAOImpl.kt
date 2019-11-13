package org.taktik.icure.asyncdao.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.MedicalLocationDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.MedicalLocation
import java.net.URI

@Repository("MedicalLocationDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.MedicalLocation' && !doc.deleted) emit( null, doc._id )}")
class MedicalLocationDAOImpl(@Qualifier("patientCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericDAOImpl<MedicalLocation>(MedicalLocation::class.java, couchDbDispatcher, idGenerator), MedicalLocationDAO {
    @View(name = "by_post_code", map = "classpath:js/medicallocation/By_post_code_map.js")
    override fun byPostCode(dbInstanceUrl: URI, groupId: String, postCode: String): Flow<MedicalLocation> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return client.queryViewIncludeDocs<String, String, MedicalLocation>(createQuery("by_post_code").includeDocs(true).key(postCode)).map { it.doc }
    }
}