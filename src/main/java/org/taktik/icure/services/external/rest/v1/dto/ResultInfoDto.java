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

package org.taktik.icure.services.external.rest.v1.dto;

import java.util.List;

public class ResultInfoDto {
	private String ssin;
	private String lastName;
	private String firstName;
	private Long   dateOfBirth;
	private String sex;

	private String documentId;
	private String protocol;
	private Boolean complete;
	private Long   demandDate;
	private String labo;
	private String engine;

	private List<CodeDto> codes;

	public String getSsin() {
		return ssin;
	}

	public void setSsin(String ssin) {
		this.ssin = ssin;
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

	public Long getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Long dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public Boolean getComplete() {
		return complete;
	}

	public void setComplete(Boolean complete) {
		this.complete = complete;
	}

	public Long getDemandDate() {
		return demandDate;
	}

	public void setDemandDate(Long demandDate) {
		this.demandDate = demandDate;
	}

	public String getLabo() {
		return labo;
	}

	public void setLabo(String labo) {
		this.labo = labo;
	}

	public String getEngine() {
		return engine;
	}

	public void setEngine(String engine) {
		this.engine = engine;
	}

	public List<CodeDto> getCodes() {
		return codes;
	}

	public void setCodes(List<CodeDto> codes) {
		this.codes = codes;
	}
}
