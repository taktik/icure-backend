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

import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONSENTschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONSENTvalues;

public class KmehrCd implements Serializable {
	protected String sv;
	protected String sl;
	protected String dn;
	protected String l;
	protected String s;

	protected String value;

	public KmehrCd() {
	}

	public KmehrCd(String s, String sl, String sv, String value) {

		this.s = s;
		this.sl = sl;
		this.sv = sv;
		this.value = value;
	}

	public String getSv() {
		return sv;
	}

	public void setSv(String sv) {
		this.sv = sv;
	}

	public String getSl() {
		return sl;
	}

	public void setSl(String sl) {
		this.sl = sl;
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public String getL() {
		return l;
	}

	public void setL(String l) {
		this.l = l;
	}

	public String getS() {
		return s;
	}

	public void setS(String s) {
		this.s = s;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}