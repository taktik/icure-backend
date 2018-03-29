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


import javax.persistence.Entity;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
public class NodeDto extends StoredDto implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	protected String name;

	protected String profiles;

	protected String services;

	protected String daemons;

	protected String ipAddress;

	private Set<PropertyDto> properties = new HashSet<>();

	protected Set<String> connectors = new HashSet<>();

	public Set<PropertyDto> getProperties() {
		return properties;
	}

	public void setProperties(Set<PropertyDto> properties) {
		this.properties = properties;
	}

	public String getName() {
		return name;
	}

	public void setName(String value) {
		this.name = value;
	}

	public String getProfiles() {
		return profiles;
	}

	public void setProfiles(String value) {
		this.profiles = value;
	}

	public String getServices() {
		return services;
	}

	public void setServices(String value) {
		this.services = value;
	}

	public Set<String> getConnectors() {
		return connectors;
	}

	public void setConnectors(Set<String> connectors) {
		this.connectors = connectors;
	}

	public String getDaemons() {
		return daemons;
	}

	public void setDaemons(String daemons) {
		this.daemons = daemons;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		NodeDto node = (NodeDto) o;

		if (id != null ? !id.equals(node.id) : node.id != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}