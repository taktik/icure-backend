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
package org.taktik.icure.domain.filter.impl.user


import com.github.pozo.KotlinBuilder
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.domain.filter.Filters
import org.taktik.icure.entities.User
import java.util.Locale

@OptIn(ExperimentalStdlibApi::class)
@KotlinBuilder
data class UserByNameEmailPhoneFilter(
        override val searchString: String,
        override val desc: String? = null
) : AbstractFilter<User>, org.taktik.icure.domain.filter.user.UserByNameEmailPhoneFilter {
    override fun matches(item: User) = searchString.lowercase().let { ss ->
        listOfNotNull(item.name?.lowercase(),
                item.login?.lowercase(),
                item.email?.lowercase(),
                item.mobilePhone?.lowercase(),
                item.status?.toString()?.lowercase()).any { it.startsWith(ss) }
    }
}
