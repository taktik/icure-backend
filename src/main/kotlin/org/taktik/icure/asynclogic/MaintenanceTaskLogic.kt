/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asyncdao.MaintenanceTaskDAO
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.MaintenanceTask
import org.taktik.icure.entities.embed.Identifier

interface MaintenanceTaskLogic : EntityPersister<MaintenanceTask, String> {
	fun listMaintenanceTasksByHcPartyAndIdentifier(healthcarePartyId: String, identifiers: List<Identifier>): Flow<String>
	fun listMaintenanceTasksByHcPartyAndType(healthcarePartyId: String, type: String, startDate: Long, endDate: Long): Flow<String>
	fun listMaintenanceTasksAfterDate(date: Long): Flow<String>

	fun getGenericDAO(): MaintenanceTaskDAO
	fun filterMaintenanceTasks(filter: FilterChain<MaintenanceTask>, limit: Int, startDocumentId: String?): Flow<ViewQueryResultEvent>
}
