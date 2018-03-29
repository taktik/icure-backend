/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v1.dto.be.ehealth;

import java.io.Serializable;
import java.util.List;

public class AuthorWithPatient implements Serializable {
	protected PatientId patient;

	protected List<HcParty> hcparties;

	public List<HcParty> getHcparties() {
		return hcparties;
	}

	public void setHcparties(List<HcParty> hcparties) {
		this.hcparties = hcparties;
	}

	public PatientId getPatient() {
		return patient;
	}

	public void setPatient(PatientId patient) {
		this.patient = patient;
	}
}
