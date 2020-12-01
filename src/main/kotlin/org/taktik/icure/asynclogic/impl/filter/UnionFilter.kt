package org.taktik.icure.asynclogic.impl.filter

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import org.taktik.icure.entities.base.Identifiable
import java.io.Serializable

@FlowPreview
class UnionFilter<T : Serializable, O : Identifiable<T>> : Filter<T, O, org.taktik.icure.domain.filter.Filters.UnionFilter<T, O>> {
	override fun resolve(filter: org.taktik.icure.domain.filter.Filters.UnionFilter<T, O>, context: Filters): Flow<T> {
		return filter.filters.asFlow().flatMapConcat { context.resolve(it) }
	}
}
