package org.taktik.icure.domain.filter.impl

import com.github.pozo.KotlinBuilder
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.domain.filter.Filters
import org.taktik.icure.entities.base.Identifiable

@KotlinBuilder
data class ComplementFilter<O : Identifiable<String>>(
        override val desc: String? = null,
        override val superSet: AbstractFilter<O>,
        override val subSet: AbstractFilter<O>
) : AbstractFilter<O>, Filters.ComplementFilter<String, O> {
   override fun matches(item: O): Boolean {
        return superSet.matches(item) && !subSet.matches(item)
    }
}
