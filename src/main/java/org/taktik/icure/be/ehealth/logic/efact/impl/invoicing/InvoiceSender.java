/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.be.ehealth.logic.efact.impl.invoicing;

@SuppressWarnings("unused")
public class InvoiceSender {
	Long inamiNumber;
	String bicNumber;
	String ibanNumber;
    Long bce = 999999922L;

    public String lastName;
    public String firstName;
    public Long phoneNumber;
    private Integer conventionCode;

    public InvoiceSender(Long inamiNumber, String bicNumber, String ibanNumber) {
		super();
		this.inamiNumber = inamiNumber;
		this.bicNumber = bicNumber;
		this.ibanNumber = ibanNumber;
	}

	public Long getInamiNumber() {
		return inamiNumber;
	}

	public String getBicNumber() {
		return bicNumber;
	}

	public String getIbanNumber() {
		return ibanNumber;
	}

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getBce() {
        return bce;
    }

    public void setBce(Long bce) {
        this.bce = bce;
    }

    public void setConventionCode(Integer conventionCode) {
        this.conventionCode = conventionCode;
    }

    public Integer getConventionCode() {
        return conventionCode;
    }

	public boolean isSpecialist() {
		return inamiNumber % 1000L >= 10;
	}
}
