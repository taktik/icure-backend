package org.taktik.icure.domain.filter.impl

import com.github.pozo.KotlinBuilder
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.domain.filter.Filters
import org.taktik.icure.entities.base.Identifiable

@KotlinBuilder
data class IntersectionFilter<O : Identifiable<String>>(
        override val desc: String? = null,
        override val filters: List<AbstractFilter<O>> = listOf()
) : AbstractFilter<O>, Filters.IntersectionFilter<String, O> {
    override fun matches(item: O): Boolean {
        for (f in filters) {
            if (!f.matches(item)) {
                return false
            }
        }
        return true
    }
}
