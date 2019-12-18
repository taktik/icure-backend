/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.taktik.icure.services.external.rest.v1.dto.filter

import org.taktik.icure.dto.filter.Filter
import org.taktik.icure.entities.base.Identifiable
import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismSupport
import org.taktik.icure.services.external.rest.v1.dto.filter.code.CodeByRegionTypeLabelLanguageFilter
import org.taktik.icure.services.external.rest.v1.dto.filter.contact.ContactByHcPartyPatientTagCodeDateFilter
import org.taktik.icure.services.external.rest.v1.dto.filter.contact.ContactByHcPartyTagCodeDateFilter
import org.taktik.icure.services.external.rest.v1.dto.filter.contact.ContactByServiceIdsFilter
import org.taktik.icure.services.external.rest.v1.dto.filter.healthelement.HealthElementByHcPartyTagCodeFilter
import org.taktik.icure.services.external.rest.v1.dto.filter.invoice.InvoiceByHcPartyCodeDateFilter
import org.taktik.icure.services.external.rest.v1.dto.filter.patient.*
import org.taktik.icure.services.external.rest.v1.dto.filter.service.ServiceByHcPartyTagCodeDateFilter
import java.io.Serializable

@JsonPolymorphismSupport(Filters.UnionFilter::class, Filters.IntersectionFilter::class, Filters.ComplementFilter::class, Filters.ConstantFilter::class,
        CodeByRegionTypeLabelLanguageFilter::class, PatientByHcPartyFilter::class, PatientByHcPartyDateOfBirthFilter::class,
        PatientByHcPartyDateOfBirthBetweenFilter::class, PatientByHcPartyAndSsinFilter::class, PatientByHcPartyNameContainsFuzzyFilter::class,
        PatientByHcPartyAndExternalIdFilter::class, PatientByIdsFilter::class, PatientByHcPartyNameFilter::class, PatientByHcPartyAndSsinsFilter::class,
        PatientByHcPartyNameContainsFuzzyFilter::class, PatientByHcPartyAndExternalIdFilter::class, PatientByIdsFilter::class,
        PatientByHcPartyAndActiveFilter::class, PatientByHcPartyGenderEducationProfession::class, ContactByHcPartyTagCodeDateFilter::class,
        ContactByHcPartyPatientTagCodeDateFilter::class, ContactByServiceIdsFilter::class, ServiceByHcPartyTagCodeDateFilter::class,
        InvoiceByHcPartyCodeDateFilter::class, HealthElementByHcPartyTagCodeFilter::class)
abstract class FilterDto<O : Identifiable<String>> : Filter<String, O>, Serializable {
	var desc: String? = null

    abstract fun matches(item: O): Boolean
    override fun applyTo(items: List<O>): List<O> {
        return items.filter { item -> this.matches(item) }
    }

    override fun applyTo(items: Set<O>): Set<O> {
        return items.filter { item -> this.matches(item) }.toSet()
    }
}
