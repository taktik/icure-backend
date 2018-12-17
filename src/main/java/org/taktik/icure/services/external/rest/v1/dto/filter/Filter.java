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

package org.taktik.icure.services.external.rest.v1.dto.filter;

import org.taktik.icure.dto.filter.contact.ContactByHcPartyPatientTagCodeDateFilter;
import org.taktik.icure.entities.base.Identifiable;
import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismSupport;
import org.taktik.icure.services.external.rest.v1.dto.filter.contact.ContactByHcPartyTagCodeDateFilter;
import org.taktik.icure.services.external.rest.v1.dto.filter.contact.ContactByServiceIdsFilter;
import org.taktik.icure.services.external.rest.v1.dto.filter.patient.*;
import org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByHcPartyAndExternalIdFilter;
import org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByHcPartyAndSsinFilter;
import org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByHcPartyAndSsinsFilter;
import org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByHcPartyDateOfBirthBetweenFilter;
import org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByHcPartyDateOfBirthFilter;
import org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByHcPartyFilter;
import org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByHcPartyNameContainsFuzzyFilter;
import org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByIdsFilter;
import org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByHcPartyAndActiveFilter;
import org.taktik.icure.services.external.rest.v1.dto.filter.service.ServiceByHcPartyTagCodeDateFilter;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@JsonPolymorphismSupport({Filters.UnionFilter.class, Filters.IntersectionFilter.class, Filters.ComplementFilter.class, Filters.ConstantFilter.class,
		PatientByHcPartyFilter.class, PatientByHcPartyDateOfBirthFilter.class, PatientByHcPartyDateOfBirthBetweenFilter.class, PatientByHcPartyAndSsinFilter.class,
		PatientByHcPartyNameContainsFuzzyFilter.class,  PatientByHcPartyAndExternalIdFilter.class, PatientByIdsFilter.class, PatientByHcPartyNameFilter.class,
		PatientByHcPartyAndSsinsFilter.class, PatientByHcPartyNameContainsFuzzyFilter.class,  PatientByHcPartyAndExternalIdFilter.class, PatientByIdsFilter.class, PatientByHcPartyAndActiveFilter.class,
		ContactByHcPartyTagCodeDateFilter.class, ContactByHcPartyPatientTagCodeDateFilter.class, ContactByServiceIdsFilter.class, ServiceByHcPartyTagCodeDateFilter.class})
public abstract class Filter<O extends Identifiable<String>> implements org.taktik.icure.dto.filter.Filter<String,O>, Serializable {
	String desc;

	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}

	public abstract boolean matches(O item);

	@Override
	public List<O> applyTo(List<O> items) {
		return items.stream().filter(this::matches).collect(Collectors.toList());
	}

	@Override
	public Set<O> applyTo(Set<O> items) {
		return items.stream().filter(this::matches).collect(Collectors.toSet());
	}

}
