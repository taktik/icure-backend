package org.taktik.icure.asynclogic.impl.filter

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.taktik.icure.entities.base.Identifiable
import java.io.Serializable

class ConstantFilter<T : Serializable, O : Identifiable<T>> : Filter<T, O, org.taktik.icure.domain.filter.Filters.ConstantFilter<T, O>> {
	override fun resolve(filter: org.taktik.icure.domain.filter.Filters.ConstantFilter<T, O>, context: Filters): Flow<T> {
		return filter.constant.asFlow()
	}
}
