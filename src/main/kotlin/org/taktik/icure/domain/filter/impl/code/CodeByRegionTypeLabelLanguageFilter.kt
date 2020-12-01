/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.domain.filter.impl.code

import com.github.pozo.KotlinBuilder
import org.taktik.icure.db.StringUtils.sanitizeString
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.base.Code
import java.util.*

@KotlinBuilder
data class CodeByRegionTypeLabelLanguageFilter(
        override val desc: String? = null,
        override val region: String? = null,
        override val type: String? = null,
        override val language: String? = null,
        override val label: String? = null
) : AbstractFilter<Code>, org.taktik.icure.domain.filter.code.CodeByRegionTypeLabelLanguageFilter {

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is CodeByRegionTypeLabelLanguageFilter) return false
        val that = o
        return region == that.region &&
                type == that.type &&
                language == that.language &&
                label == that.label
    }

    override fun hashCode(): Int {
        return Objects.hash(region, type, language, label)
    }

    override fun matches(item: Code): Boolean {
        val ss = sanitizeString(label)
        return ((region == null || item.regions.contains(region))
                && (type == null || type == type)
                && if (language == null) item.label.values.stream().anyMatch { l: String? -> Optional.ofNullable(l).map { s: String? -> sanitizeString(s)!!.contains(ss!!) }.orElse(false) } else Optional.ofNullable(item.label[language!!]).map { s: String? -> sanitizeString(s)!!.contains(ss!!) }.orElse(false))
    }
}
