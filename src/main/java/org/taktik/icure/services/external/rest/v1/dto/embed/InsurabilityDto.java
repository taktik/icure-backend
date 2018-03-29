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

package org.taktik.icure.services.external.rest.v1.dto.embed;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by aduchate on 21/01/13, 15:37
 */
public class InsurabilityDto implements Serializable {
	//Key from InsuranceParameter
	private Map<String,String> parameters;

	private Boolean hospitalisation;
	private Boolean ambulatory;
	private Boolean dental;

	private String identificationNumber;
    private String insuranceId;

	private String titularyId;

	private Long startDate;
	private Long endDate;

	public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(String insuranceId) {
        this.insuranceId = insuranceId;
    }

	public Boolean getHospitalisation() {
		return hospitalisation;
	}

	public void setHospitalisation(Boolean hospitalisation) {
		this.hospitalisation = hospitalisation;
	}

	public Boolean getAmbulatory() {
		return ambulatory;
	}

	public void setAmbulatory(Boolean ambulatory) {
		this.ambulatory = ambulatory;
	}

	public Boolean getDental() {
		return dental;
	}

	public void setDental(Boolean dental) {
		this.dental = dental;
	}

	public String getIdentificationNumber() {
		return identificationNumber;
	}

	public void setIdentificationNumber(String identificationNumber) {
		this.identificationNumber = identificationNumber;
	}

	public Long getStartDate() {
		return startDate;
	}

	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}

	public Long getEndDate() {
		return endDate;
	}

	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}

	public String getTitularyId() {
		return titularyId;
	}

	public void setTitularyId(String titularyId) {
		this.titularyId = titularyId;
	}
}
