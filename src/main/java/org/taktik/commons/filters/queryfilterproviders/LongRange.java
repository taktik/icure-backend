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

package org.taktik.commons.filters.queryfilterproviders;

public class LongRange {
	private Long fromValue;
	private Long toValue;

	public LongRange(Long fromValue, Long toValue) {
		this.fromValue = fromValue;
		this.toValue = toValue;
	}

	public Long getFromValue() {
		return fromValue;
	}

	public void setFromValue(Long fromValue) {
		this.fromValue = fromValue;
	}

	public Long getToValue() {
		return toValue;
	}

	public void setToValue(Long toValue) {
		this.toValue = toValue;
	}

	public boolean isNull() {
		return fromValue == null && toValue == null;
	}
}