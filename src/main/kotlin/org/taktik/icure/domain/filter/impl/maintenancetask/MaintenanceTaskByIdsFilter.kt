/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.domain.filter.impl.maintenancetask

import com.github.pozo.KotlinBuilder
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.domain.filter.Filters
import org.taktik.icure.entities.MaintenanceTask

@KotlinBuilder
data class MaintenanceTaskByIdsFilter(
	override val desc: String? = null,
	override val ids: Set<String>
): AbstractFilter<MaintenanceTask>, Filters.IdsFilter<String, MaintenanceTask> {
	override fun matches(item: MaintenanceTask) = ids.contains(item.id)
}
