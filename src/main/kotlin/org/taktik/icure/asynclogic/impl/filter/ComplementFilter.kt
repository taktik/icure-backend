package org.taktik.icure.asynclogic.impl.filter

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import org.taktik.icure.entities.base.Identifiable
import java.io.Serializable

class ComplementFilter<T : Serializable, O : Identifiable<T>> : Filter<T, O, org.taktik.icure.domain.filter.Filters.ComplementFilter<T, O>> {
	override fun resolve(filter: org.taktik.icure.domain.filter.Filters.ComplementFilter<T, O>, context: Filters): Flow<T> = flow {
		val superFlow: Flow<T> = context.resolve(filter.superSet)
		val subList: List<T> = context.resolve(filter.subSet).toList()
		superFlow.collect {
			if (!subList.contains(it)) emit(it)
		}
	}
}
