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

package org.taktik.icure.services.external.rest.v1.dto.be.therlink;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTY;
import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTY;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.HcParty;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.KmehrCd;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.KmehrId;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.KmehrPatient;

public class TherapeuticLinkHcParty implements Serializable {
	private String type;
	private String applicationID;
	private String nihii;
	private String inss;
	private String hubId;
	private String cbe;
	private String name;
	private String firstName;
	private String familyName;
	private String eHP;
	private List<KmehrId> ids = new ArrayList();
	private List<KmehrCd> cds = new ArrayList();

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getApplicationID() {
		return applicationID;
	}

	public void setApplicationID(String applicationID) {
		this.applicationID = applicationID;
	}

	public String getNihii() {
		return nihii;
	}

	public void setNihii(String nihii) {
		this.nihii = nihii;
	}

	public String getInss() {
		return inss;
	}

	public void setInss(String inss) {
		this.inss = inss;
	}

	public String getHubId() {
		return hubId;
	}

	public void setHubId(String hubId) {
		this.hubId = hubId;
	}

	public String getCbe() {
		return cbe;
	}

	public void setCbe(String cbe) {
		this.cbe = cbe;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String geteHP() {
		return eHP;
	}

	public void seteHP(String eHP) {
		this.eHP = eHP;
	}

	public List<KmehrId> getIds() {
		return ids;
	}

	public void setIds(List<KmehrId> ids) {
		this.ids = ids;
	}

	public List<KmehrCd> getCds() {
		return cds;
	}

	public void setCds(List<KmehrCd> cds) {
		this.cds = cds;
	}
}
