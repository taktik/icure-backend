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

package org.taktik.icure.services.external.rest.v1.dto;

import java.io.Serializable;

import org.taktik.icure.services.external.rest.v1.dto.base.Identifiable;


public abstract class StoredDto implements Identifiable<String>,Serializable {
	String id;
    String rev;
    Long deletionDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public Long getDeletionDate() {
        return deletionDate;
    }

    public void setDeletionDate(Long deletionDate) {
        this.deletionDate = deletionDate;
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof StoredDto)) return false;

		StoredDto storedDto = (StoredDto) o;

		//noinspection RedundantIfStatement
		if (id != null ? !id.equals(storedDto.id) : storedDto.id != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
//
//	private TreeMap<String, String> reversedTreeMap() {
//		return new TreeMap<String, String>((o1, o2) -> o2.compareTo(o1));
//	}
}
