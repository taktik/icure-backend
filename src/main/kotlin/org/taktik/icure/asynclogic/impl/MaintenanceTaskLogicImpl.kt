/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import org.springframework.stereotype.Service
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.ViewRowWithDoc
import org.taktik.icure.asyncdao.MaintenanceTaskDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.MaintenanceTaskLogic
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.MaintenanceTask
import org.taktik.icure.entities.embed.Identifier

@Service
class MaintenanceTaskLogicImpl(
	private val maintenanceTaskDAO: MaintenanceTaskDAO,
	sessionLogic: AsyncSessionLogic,
	private val filters: Filters,
) : GenericLogicImpl<MaintenanceTask, MaintenanceTaskDAO>(sessionLogic), MaintenanceTaskLogic {

	override fun listMaintenanceTasksByHcPartyAndIdentifier(healthcarePartyId: String, identifiers: List<Identifier>): Flow<String> = flow {
		emitAll(maintenanceTaskDAO.listMaintenanceTasksByHcPartyAndIdentifier(healthcarePartyId, identifiers))
	}

	override fun listMaintenanceTasksByHcPartyAndType(healthcarePartyId: String, type: String, startDate: Long?, endDate: Long?): Flow<String> = flow {
		emitAll(maintenanceTaskDAO.listMaintenanceTasksByHcPartyAndType(healthcarePartyId, type, startDate, endDate))
	}

	override fun listMaintenanceTasksAfterDate(date: Long): Flow<String> = flow {
		emitAll(maintenanceTaskDAO.listMaintenanceTasksAfterDate(date))
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	override fun filterMaintenanceTasks(filter: FilterChain<MaintenanceTask>, limit: Int, startDocumentId: String?): Flow<ViewQueryResultEvent> =
		flow {
			val ids = filters.resolve(filter.filter)

			val sortedIds = if (startDocumentId != null) { // Sub-set starting from startDocId to the end (including last element)
				ids.dropWhile { it != startDocumentId }
			} else {
				ids
			}
			val selectedIds = sortedIds.take(limit + 1) // Fetching one more maintenanceTask for the start key of the next page
			emitAll(
				getGenericDAO().getEntities(selectedIds)
					.map { ViewRowWithDoc(it.id, it.id, null, it) }
			)
		}

	override fun getGenericDAO(): MaintenanceTaskDAO {
		return maintenanceTaskDAO
	}

	override fun createEntities(entities: Collection<MaintenanceTask>) = flow {
		emitAll( super.createEntities(entities.map { fix(it) }) )
	}
}
