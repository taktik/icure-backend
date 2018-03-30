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

package org.taktik.icure.utils.beans;

import java.util.Objects;

public class IndexedObject implements Comparable<IndexedObject> {
	private int index;
	private Object object;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public static IndexedObject build(int index, Object object) {
		IndexedObject indexedObject = new IndexedObject();
		indexedObject.setIndex(index);
		indexedObject.setObject(object);
		return indexedObject;
	}

	@SuppressWarnings("NullableProblems")
	@Override
	public int compareTo(IndexedObject other) {
		if (this.index < other.index) return -1;
		if (this.index > other.index) return 1;
		return 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(index, object);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final IndexedObject other = (IndexedObject) obj;
		return Objects.equals(this.index, other.index) && Objects.equals(this.object, other.object);
	}
}
