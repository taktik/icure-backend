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

package org.taktik.icure.be.mikrono.dto;

import java.util.List;

import org.taktik.icure.entities.Patient;

public class MikronoSynchronizationResult {
	Long lastAccessIcure; //In ms
	List<Patient> patients;

	public MikronoSynchronizationResult(Long lastAccessIcure, List<Patient> patients) {
		this.lastAccessIcure = lastAccessIcure;
		this.patients = patients;
	}

	public Long getLastAccessIcure() {
		return lastAccessIcure;
	}

	public void setLastAccessIcure(Long lastAccessIcure) {
		this.lastAccessIcure = lastAccessIcure;
	}

	public List<Patient> getPatients() {
		return patients;
	}

	public void setPatients(List<Patient> patients) {
		this.patients = patients;
	}
}
