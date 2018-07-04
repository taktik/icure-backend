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

package org.taktik.icure.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.taktik.icure.entities.base.Identifiable;
import org.taktik.icure.entities.base.StoredDocument;
import org.taktik.icure.entities.embed.DatabaseSynchronization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Replication extends StoredDocument implements Identifiable<String>, Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	protected String name;

	protected String context;

	protected List<DatabaseSynchronization> databaseSynchronizations = new ArrayList<>();

	public Replication() {
	}

	public Replication(String name, String context, List<DatabaseSynchronization> databaseSynchronizations) {
		this.name = name;
		this.context = context;
		this.databaseSynchronizations = databaseSynchronizations;
	}

	public List<DatabaseSynchronization> getDatabaseSynchronizations() {

		return databaseSynchronizations;
	}

	public void setDatabaseSynchronizations(List<DatabaseSynchronization> databaseSynchronizations) {
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

		Replication replication = (Replication) o;

		if (id != null ? !id.equals(replication.id) : replication.id != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

}