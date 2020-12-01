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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.CalendarItemTypeDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.CalendarItemType
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.createQuery
import java.net.URI

@Repository("calendarItemTypeDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.CalendarItemType' && !doc.deleted) emit( null, doc._id )}")
class CalendarItemTypeDAOImpl(couchDbProperties: CouchDbProperties,
                              @Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericDAOImpl<CalendarItemType>(couchDbProperties, CalendarItemType::class.java, couchDbDispatcher, idGenerator), CalendarItemTypeDAO {

    @View(name = "all_and_deleted", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.CalendarItemType') emit( doc._id , null )}")
    override fun getAllEntitiesIncludeDelete(): Flow<CalendarItemType> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val viewQuery = createQuery<CalendarItemType>("all_and_deleted").includeDocs(true)

        val result = client.queryViewIncludeDocsNoValue<String, CalendarItemType>(viewQuery).map { it.doc }
        return result.map {
            this.postLoad(it)
        }
    }
}
