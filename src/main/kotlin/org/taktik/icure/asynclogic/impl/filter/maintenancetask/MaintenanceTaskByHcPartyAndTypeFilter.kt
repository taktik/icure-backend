/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.asynclogic.impl.filter.maintenancetask

import javax.security.auth.login.LoginException
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.MaintenanceTaskLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.maintenancetask.MaintenanceTaskByHcPartyAndTypeFilter
import org.taktik.icure.entities.MaintenanceTask
import org.taktik.icure.utils.getLoggedHealthCarePartyId

@Service
@Profile("app")
class MaintenanceTaskByHcPartyAndTypeFilter(
	private val maintenanceTaskLogic: MaintenanceTaskLogic,
	private val sessionLogic: AsyncSessionLogic
) : Filter<String, MaintenanceTask, MaintenanceTaskByHcPartyAndTypeFilter> {

	override fun resolve(filter: MaintenanceTaskByHcPartyAndTypeFilter, context: Filters) = flow<String> {
		try {
			emitAll(maintenanceTaskLogic.listMaintenanceTasksByHcPartyAndType(filter.healthcarePartyId ?: getLoggedHealthCarePartyId(sessionLogic), filter.type))
		} catch (e: LoginException) {
			throw IllegalArgumentException(e)
		}
	}
}
