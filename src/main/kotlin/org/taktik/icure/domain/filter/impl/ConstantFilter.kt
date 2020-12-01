package org.taktik.icure.domain.filter.impl

import com.github.pozo.KotlinBuilder
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.domain.filter.Filters
import org.taktik.icure.entities.base.Identifiable

@KotlinBuilder
data class ConstantFilter<O : Identifiable<String>>(
        override val desc: String? = null,
        override val constant: Set<String>
) : AbstractFilter<O>, Filters.ConstantFilter<String, O> {
    override fun matches(item: O): Boolean {
        return constant.contains(item.id)
    }
}
