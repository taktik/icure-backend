package org.taktik.icure.asynclogic.impl.filter.maintenancetask

import javax.security.auth.login.LoginException
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.MaintenanceTaskLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.maintenancetask.MaintenanceTaskByHcPartyAndIdentifiersFilter
import org.taktik.icure.entities.MaintenanceTask
import org.taktik.icure.utils.getLoggedHealthCarePartyId

@Service
class MaintenanceTaskByHcPartyAndIdentifiersFilter(private val maintenanceTaskLogic: MaintenanceTaskLogic, private val sessionLogic: AsyncSessionLogic) : Filter<String, MaintenanceTask, MaintenanceTaskByHcPartyAndIdentifiersFilter> {

	override fun resolve(
		filter: MaintenanceTaskByHcPartyAndIdentifiersFilter,
		context: Filters
	) = flow {
		try {
			emitAll(maintenanceTaskLogic.listMaintenanceTasksByHcPartyAndIdentifier(filter.healthcarePartyId ?: getLoggedHealthCarePartyId(sessionLogic), filter.identifiers))
		} catch (e: LoginException) {
			throw IllegalArgumentException(e)
		}
	}
}
