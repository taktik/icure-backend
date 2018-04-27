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

import org.taktik.commons.filters.Filter;
import org.taktik.commons.filters.FilterOnProperty;

/**
 * Created by IntelliJ IDEA.
 * User: abaudoux
 * Date: 04/10/12
 * Time: 10:38
 */
public class IsNullFilterProvider implements QueryFilterProvider {

	private String property;
	private boolean reversed;

	public IsNullFilterProvider(String property, boolean reversed) {
		this.property = property;
		this.reversed = reversed;
	}

	@Override
	public Filter getFilterForQuery(String q) {
		Boolean value = Boolean.parseBoolean(q);
		if (value) {
			return new FilterOnProperty(property, reversed?FilterOnProperty.Operator.NOT_EQUAL:FilterOnProperty.Operator.EQUAL, false, null);
		} else {
			return new FilterOnProperty(property, reversed?FilterOnProperty.Operator.EQUAL:FilterOnProperty.Operator.NOT_EQUAL, false, null);
		}
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public boolean isReversed() {
		return reversed;
	}

	public void setReversed(boolean reversed) {
		this.reversed = reversed;
	}
}
