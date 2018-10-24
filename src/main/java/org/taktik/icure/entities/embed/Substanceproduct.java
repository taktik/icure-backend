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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.taktik.icure.entities.base.Code;
import org.taktik.icure.entities.base.CodeStub;
import org.taktik.icure.validation.AutoFix;
import org.taktik.icure.validation.ValidCode;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Substanceproduct implements Serializable {
	@ValidCode(autoFix = AutoFix.NORMALIZECODE)
	protected List<CodeStub> intendedcds;
	@ValidCode(autoFix = AutoFix.NORMALIZECODE)
	protected List<CodeStub> deliveredcds;
	protected String intendedname;
	protected Object deliveredname;

	public List<CodeStub> getIntendedcds() {
		return intendedcds;
	}

	public void setIntendedcds(List<CodeStub> intendedcds) {
		this.intendedcds = intendedcds;
	}

	public List<CodeStub> getDeliveredcds() {
		return deliveredcds;
	}

	public void setDeliveredcds(List<CodeStub> deliveredcds) {
		this.deliveredcds = deliveredcds;
	}

	public String getIntendedname() {
		return intendedname;
	}

	public void setIntendedname(String intendedname) {
		this.intendedname = intendedname;
	}

	public Object getDeliveredname() {
		return deliveredname;
	}

	public void setDeliveredname(Object deliveredname) {
		this.deliveredname = deliveredname;
	}
}
