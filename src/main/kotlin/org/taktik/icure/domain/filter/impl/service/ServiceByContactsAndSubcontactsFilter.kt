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
package org.taktik.icure.domain.filter.impl.service

import com.github.pozo.KotlinBuilder
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.embed.Service

@KotlinBuilder
data class ServiceByContactsAndSubcontactsFilter(
	override val desc: String? = null,
	override val healthcarePartyId: String? = null,
	override val contacts: Set<String>,
	override val subContacts: Set<String>? = null,
	override val startValueDate: Long? = null,
	override val endValueDate: Long? = null
) : AbstractFilter<Service>, org.taktik.icure.domain.filter.service.ServiceByContactsAndSubcontactsFilter {

	override fun matches(item: Service): Boolean {
		return (
			(healthcarePartyId == null || item.delegations.keys.contains(healthcarePartyId)) &&
				contacts!!.contains(item.contactId) &&
				(subContacts == null || subContacts.intersect(item.subContactIds ?: setOf()).isNotEmpty())
			)
	}
}
