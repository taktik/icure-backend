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

package org.taktik.icure.services.external.rest.v1.dto.be.therlink;

import java.io.Serializable;

public class TherapeuticLinkPatient implements Serializable {
	private String inss;
	private String regNrWithMut;
	private String mutuality;
	private String firstName;
	private String lastName;
	private String middleName;
	private String eidCardNumber;
	private String sisCardNumber;
	private String isiCardNumber;

	public String getInss() {
		return inss;
	}

	public void setInss(String inss) {
		this.inss = inss;
	}

	public String getRegNrWithMut() {
		return regNrWithMut;
	}

	public void setRegNrWithMut(String regNrWithMut) {
		this.regNrWithMut = regNrWithMut;
	}

	public String getMutuality() {
		return mutuality;
	}

	public void setMutuality(String mutuality) {
		this.mutuality = mutuality;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getEidCardNumber() {
		return eidCardNumber;
	}

	public void setEidCardNumber(String eidCardNumber) {
		this.eidCardNumber = eidCardNumber;
	}

	public String getSisCardNumber() {
		return sisCardNumber;
	}

	public void setSisCardNumber(String sisCardNumber) {
		this.sisCardNumber = sisCardNumber;
	}

	public String getIsiCardNumber() {
		return isiCardNumber;
	}

	public void setIsiCardNumber(String isiCardNumber) {
		this.isiCardNumber = isiCardNumber;
	}
}
