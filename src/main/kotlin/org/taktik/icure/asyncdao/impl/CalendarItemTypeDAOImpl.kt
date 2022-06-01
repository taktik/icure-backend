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
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.CalendarItemTypeDAO
import org.taktik.icure.entities.CalendarItemType
import org.taktik.icure.properties.CouchDbProperties

@Repository("calendarItemTypeDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.CalendarItemType' && !doc.deleted) emit( null, doc._id )}")
class CalendarItemTypeDAOImpl(
	couchDbProperties: CouchDbProperties,
	@Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher,
	idGenerator: IDGenerator
) : GenericDAOImpl<CalendarItemType>(couchDbProperties, CalendarItemType::class.java, couchDbDispatcher, idGenerator), CalendarItemTypeDAO {

	@View(name = "all_and_deleted", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.CalendarItemType') emit( doc._id , null )}")
	override fun getCalendarItemsWithDeleted(): Flow<CalendarItemType> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		val viewQuery = createQuery(client, "all_and_deleted").includeDocs(true)

		val result = client.queryViewIncludeDocsNoValue<String, CalendarItemType>(viewQuery).map { it.doc }
		emitAll(result.map { this@CalendarItemTypeDAOImpl.postLoad(it) })
	}
}
