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
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.taktik.couchdb.annotation.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.AgendaDAO
import org.taktik.couchdb.id.IDGenerator
import org.taktik.icure.entities.Agenda
import org.taktik.icure.properties.CouchDbProperties


@Repository("AgendaDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Agenda' && !doc.deleted) emit( null, doc._id )}")
class AgendaDAOImpl(couchDbProperties: CouchDbProperties,
                    @Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericDAOImpl<Agenda>(couchDbProperties, Agenda::class.java, couchDbDispatcher, idGenerator), AgendaDAO {

    @View(name = "by_user", map = "classpath:js/agenda/by_user.js")
    override fun getAllAgendaForUser(userId: String): Flow<Agenda> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val viewQuery = createQuery(client, "by_user")
                .startKey(userId)
                .endKey(userId)
                .includeDocs(true)

        emitAll(client.queryViewIncludeDocsNoValue<String, Agenda>(viewQuery).map { it.doc })
    }

    @View(name = "readable_by_user", map = "classpath:js/agenda/readable_by_user.js")
    override fun getReadableAgendaForUser(userId: String): Flow<Agenda> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val viewQuery = createQuery(client, "readable_by_user")
                .startKey(userId)
                .endKey(userId)
                .includeDocs(true)

        emitAll(client.queryViewIncludeDocsNoValue<String, Agenda>(viewQuery).map { it.doc })
    }
}
