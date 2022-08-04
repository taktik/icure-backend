package org.taktik.icure.asynclogic.impl.filter.user

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.user.UsersByPatientIdFilter
import org.taktik.icure.entities.User

@Service
class UsersByPatientIdFilter(private val userLogic: UserLogic) : Filter<String, User, UsersByPatientIdFilter> {

	override fun resolve(filter: UsersByPatientIdFilter, context: Filters): Flow<String> {
		return userLogic.findByPatientId(filter.patientId)
	}
}
