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

package org.taktik.icure.entities.embed;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aduchate on 21/01/13, 15:37
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Insurability implements Serializable {
	//Key from InsuranceParameter
    private Map<String,String> parameters = new HashMap<>();

	private Boolean hospitalisation;
	private Boolean ambulatory;
	private Boolean dental;

	private String identificationNumber; // N° in form (number for the insurance's identification)
    private String insuranceId; // UUID to identify Partena, etc. (link to Insurance object's document ID)

	private Long startDate;
	private Long endDate;

	private String titularyId; //UUID of the contact person who is the titulary of the insurance

	@JsonIgnore
	private String insuranceDescription;

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

	@JsonIgnore
	public String getInsuranceDescription() {
		return insuranceDescription;
	}

	@JsonIgnore
	public void setInsuranceDescription(String insuranceDescription) {
		this.insuranceDescription = insuranceDescription;
	}
}
