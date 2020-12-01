/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.be.ehealth.dto;

import java.io.Serializable;
import java.util.Map;

public class Error implements Serializable {
	private String code;
	private String descr;
	private String url;
	private String zone;
	private Map<String, String> codeDescription;

	public Error() {
	}

	public Error(String code, String url, String descr, Map<String,String> codeDescription) {
		this.code = code;
		this.url = url;
		this.descr = descr;
		this.codeDescription = codeDescription;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getCodeDescription() {
		return codeDescription;
	}

	public void setCodeDescription(Map<String, String> codeDescription) {
		this.codeDescription = codeDescription;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	@Override
	public String toString() {
		return "Error{" +
				"code='" + code + '\'' +
				", description='" + descr + '\'' +
				", url='" + url + '\'' +
				'}';
	}
}
