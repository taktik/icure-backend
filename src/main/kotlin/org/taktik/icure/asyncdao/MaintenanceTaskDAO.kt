/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.entities.MaintenanceTask
import org.taktik.icure.entities.embed.Identifier

interface MaintenanceTaskDAO : GenericDAO<MaintenanceTask> {
	fun listMaintenanceTasksByHcPartyAndIdentifier(healthcarePartyId: String, identifiers: List<Identifier>): Flow<String>
	fun listMaintenanceTasksAfterDate(date: Long): Flow<String>
	fun listMaintenanceTasksByHcPartyAndType(healthcarePartyId: String, type: String, startDate: Long, endDate: Long): Flow<String>
}
