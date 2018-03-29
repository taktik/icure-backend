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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.taktik.icure.services.external.rest.v1.dto.embed.DatabaseSynchronizationDto;

import java.util.ArrayList;
import java.util.List;

public class ReplicationDto extends StoredDto {
	private static final long serialVersionUID = 1L;

	protected String name;

	protected String context;

	protected List<DatabaseSynchronizationDto> databaseSynchronizations = new ArrayList<>();

	public List<DatabaseSynchronizationDto> getDatabaseSynchronizations() {

		return databaseSynchronizations;
	}

	public void setDatabaseSynchronizations(List<DatabaseSynchronizationDto> databaseSynchronizations) {
		this.databaseSynchronizations = databaseSynchronizations;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ReplicationDto replication = (ReplicationDto) o;

		if (id != null ? !id.equals(replication.id) : replication.id != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

}
