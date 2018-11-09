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
import java.util.Set;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Measure implements Serializable {
	Double value;

	Double min;
	Double max;
	Double ref;

	Integer severity;

	String unit;

	@ValidCode(autoFix = AutoFix.NORMALIZECODE)
	Set<CodeStub> unitCodes;

	String comment;

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Double getMin() {
		return min;
	}

	public void setMin(Double min) {
		this.min = min;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(Double max) {
		this.max = max;
	}

	public Double getRef() {
		return ref;
	}

	public void setRef(Double ref) {
		this.ref = ref;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Integer getSeverity() {
		return severity;
	}

	public void setSeverity(Integer severity) {
		this.severity = severity;
	}

	public Set<CodeStub> getUnitCodes() {
		return unitCodes;
	}

	public void setUnitCodes(Set<CodeStub> unitCodes) {
		this.unitCodes = unitCodes;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
