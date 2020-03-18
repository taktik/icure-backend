package org.taktik.icure.asyncdao.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ma.glasnost.orika.MapperFacade
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.AgendaDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.Agenda
import org.taktik.icure.utils.createQuery
import java.net.URI

@Repository("AgendaDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Agenda' && !doc.deleted) emit( null, doc._id )}")
class AgendaDAOImpl(@Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator, mapper: MapperFacade) : GenericDAOImpl<Agenda>(Agenda::class.java, couchDbDispatcher, idGenerator, mapper), AgendaDAO {

    @View(name = "by_user", map = "classpath:js/agenda/by_user.js")
    override fun getAllAgendaForUser(dbInstanceUrl: URI, groupId: String, userId: String): Flow<Agenda> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val viewQuery = createQuery<Agenda>("by_user")
                .startKey(userId)
                .endKey(userId)
                .includeDocs(true)

        return client.queryViewIncludeDocsNoValue<String, Agenda>(viewQuery).map { it.doc }
    }

    @View(name = "readable_by_user", map = "classpath:js/agenda/readable_by_user.js")
    override fun getReadableAgendaForUser(dbInstanceUrl: URI, groupId: String, userId: String): Flow<Agenda> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val viewQuery = createQuery<Agenda>("readable_by_user")
                .startKey(userId)
                .endKey(userId)
                .includeDocs(true)

        return client.queryViewIncludeDocsNoValue<String, Agenda>(viewQuery).map { it.doc }
    }
}
