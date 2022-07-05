/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.domain.filter.maintenancetask

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.MaintenanceTask

interface MaintenanceTaskByHcPartyAndTypeFilter : Filter<String, MaintenanceTask> {
	val type: String
	val healthcarePartyId: String?
}
