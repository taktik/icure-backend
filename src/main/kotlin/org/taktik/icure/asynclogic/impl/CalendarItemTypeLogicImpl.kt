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
package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.CalendarItemTypeDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.CalendarItemTypeLogic
import org.taktik.icure.entities.CalendarItemType
import org.taktik.icure.exceptions.DeletionException

@ExperimentalCoroutinesApi
@Service
class CalendarItemTypeLogicImpl(
	private val calendarItemTypeDAO: CalendarItemTypeDAO,
	private val sessionLogic: AsyncSessionLogic
) : GenericLogicImpl<CalendarItemType, CalendarItemTypeDAO>(sessionLogic), CalendarItemTypeLogic {

	override suspend fun createCalendarItemType(calendarItemType: CalendarItemType) = fix(calendarItemType) { calendarItemType ->
		calendarItemTypeDAO.create(calendarItemType)
	}

	override fun deleteCalendarItemTypes(ids: List<String>): Flow<DocIdentifier> {
		try {
			return deleteEntities(ids)
		} catch (e: Exception) {
			throw DeletionException(e.message, e)
		}
	}

	override suspend fun getCalendarItemType(calendarItemTypeId: String): CalendarItemType? {
		return calendarItemTypeDAO.get(calendarItemTypeId)
	}

	override fun getCalendarItemTypes(calendarItemTypeIds: Collection<String>) = flow {
		emitAll(calendarItemTypeDAO.getEntities(calendarItemTypeIds))
	}

	override suspend fun modifyCalendarTypeItem(calendarItemType: CalendarItemType) = fix(calendarItemType) { calendarItemType ->
		calendarItemTypeDAO.save(calendarItemType)
	}

	override fun getAllEntitiesIncludeDelete(): Flow<CalendarItemType> = flow {
		emitAll(calendarItemTypeDAO.getCalendarItemsWithDeleted())
	}

	override fun getGenericDAO(): CalendarItemTypeDAO {
		return calendarItemTypeDAO
	}
}
