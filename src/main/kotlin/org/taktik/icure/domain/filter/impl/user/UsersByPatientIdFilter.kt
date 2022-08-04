package org.taktik.icure.domain.filter.impl.user

import com.github.pozo.KotlinBuilder
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.User

@KotlinBuilder
data class UsersByPatientIdFilter(
	override val desc: String?,
	override val patientId: String
) : AbstractFilter<User>, org.taktik.icure.domain.filter.user.UsersByPatientIdFilter {

	override fun matches(item: User): Boolean {
		return item.patientId != null &&
				patientId == item.patientId
	}

}
