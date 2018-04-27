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

package org.taktik.commons.collections;

import java.util.Comparator;

public enum SortDir {
	NONE {
		@Override
		public <X> Comparator<X> comparator(Comparator<X> c) {
			return c;
		}
	},

	ASC {
		@Override
		public <X> Comparator<X> comparator(final Comparator<X> c) {
			return c::compare;
		}
	},

	DESC {
		@Override
		public <X> Comparator<X> comparator(final Comparator<X> c) {
			return (o1, o2) -> c.compare(o2, o1);
		}
	};

	public static SortDir findDir(String sortDir) {
		if (sortDir != null) {
			sortDir = sortDir.trim().toUpperCase();
			if (sortDir.equals("ASC") || sortDir.equals("ASCENDING")) {
				return SortDir.ASC;
			} else if (sortDir.equals("DESC") || sortDir.equals("DESCENDING")) {
				return SortDir.DESC;
			}
		}

		return null;
	}

	public static SortDir toggle(SortDir sortDir) {
		return (sortDir == ASC) ? DESC : ASC;
	}

	/**
	 * An example of how to use this :
	 * 
	 * List<Something> list = ...
	 * 
	 * Collections.sort(list, SortDir.ASC.comparator(new Comparator() { public int compare(Object o1, Object o2) { return ... } });
	 * 
	 * 
	 * @return a Comparator that wraps the specific comparator that orders the results according to the sort direction
	 */
	public abstract <X> Comparator<X> comparator(Comparator<X> c);
}