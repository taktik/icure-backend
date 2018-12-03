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

package org.taktik.icure.dto.filter;

import org.taktik.icure.dto.filter.contact.ContactByHcPartyPatientTagCodeDateFilter;
import org.taktik.icure.dto.filter.patient.*;
import org.taktik.icure.dto.filter.service.ServiceByHcPartyTagCodeDateFilter;
import org.taktik.icure.entities.base.Identifiable;
import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismSupport;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@JsonPolymorphismSupport({Filters.UnionFilter.class, Filters.IntersectionFilter.class, Filters.ComplementFilter.class, Filters.ConstantFilter.class,
		PatientByHcPartyFilter.class, PatientByHcPartyDateOfBirthFilter.class, PatientByHcPartyDateOfBirthBetweenFilter.class, PatientByHcPartyAndSsinFilter.class,
		PatientByHcPartyAndSsinsFilter.class,PatientByHcPartyNameContainsFuzzyFilter.class,  PatientByHcPartyAndExternalIdFilter.class, PatientByIdsFilter.class,
		ContactByHcPartyPatientTagCodeDateFilter.class, ServiceByHcPartyTagCodeDateFilter.class, PatientByHcPartyNameFilter.class, PatientByHcPartyAndActiveFilter.class})
public interface Filter<T extends Serializable, O extends Identifiable<T>> {
	List<O> applyTo(List<O> items);
	Set<O> applyTo(Set<O> items);
}
