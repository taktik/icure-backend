package org.taktik.icure.asynclogic.impl.filter

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import org.taktik.icure.entities.base.Identifiable
import java.io.Serializable

class IntersectionFilter<T : Serializable, O : Identifiable<T>> : Filter<T, O, org.taktik.icure.domain.filter.Filters.IntersectionFilter<T, O>> {
	override fun resolve(filter: org.taktik.icure.domain.filter.Filters.IntersectionFilter<T, O>, context: Filters): Flow<T> = flow {
		val filters = filter.filters
		val result = mutableSetOf<T>()
		for (i in filters.indices) {
			if (i == 0) {
				result.addAll(context.resolve(filters[i]).toList())
			} else {
				result.retainAll(context.resolve(filters[i]).toList())
			}
			result.forEach { emit(it) } // TODO SH MB: not reactive... can be optimized?
		}
	}
}
