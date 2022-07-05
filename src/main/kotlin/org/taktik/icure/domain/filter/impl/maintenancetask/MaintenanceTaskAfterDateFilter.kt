/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.domain.filter.impl.maintenancetask

import com.github.pozo.KotlinBuilder
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.domain.filter.maintenancetask.MaintenanceTaskAfterDateFilter
import org.taktik.icure.entities.MaintenanceTask

@KotlinBuilder
data class MaintenanceTaskAfterDateFilter(
	override val desc: String? = null,
	override val date: Long
	): AbstractFilter<MaintenanceTask>, MaintenanceTaskAfterDateFilter {
	override fun matches(item: MaintenanceTask): Boolean {
		return ((item.created ?: 0) > date)
	}
}
