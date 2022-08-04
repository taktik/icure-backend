package org.taktik.icure.domain.filter.user

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.User

interface UsersByPatientIdFilter : Filter<String, User> {
	val patientId: String
}
