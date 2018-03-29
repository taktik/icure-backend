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

package org.taktik.commons.filters.queryfilterproviders;

import java.util.function.Function;

public class WildcardInserter implements Function<String, String> {
	private boolean insertBefore;
	private boolean insertAfter;

	public WildcardInserter(boolean insertBefore, boolean insertAfter) {
		this.insertBefore = insertBefore;
		this.insertAfter = insertAfter;
	}

	@Override
	public String apply(String input) {
		StringBuilder sb = new StringBuilder();
		if (insertBefore) {
			sb.append('*');
		}
		sb.append(input);
		if (insertAfter) {
			sb.append('*');
		}
		return sb.toString();
	}
}