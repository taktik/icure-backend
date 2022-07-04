/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.domain.filter.impl.maintenancetask

import com.github.pozo.KotlinBuilder
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.domain.filter.maintenancetask.MaintenanceTaskByHcPartyAndTypeFilter
import org.taktik.icure.entities.MaintenanceTask

@KotlinBuilder
data class MaintenanceTaskByHcPartyAndTypeFilter(
	override val desc: String? = null,
	override val healthcarePartyId: String? = null,
	override val type: String
): AbstractFilter<MaintenanceTask>, MaintenanceTaskByHcPartyAndTypeFilter {
	override fun matches(item: MaintenanceTask): Boolean {
		return (healthcarePartyId == null || item.delegations.keys.contains(healthcarePartyId)) && type == item.taskType
	}
}
