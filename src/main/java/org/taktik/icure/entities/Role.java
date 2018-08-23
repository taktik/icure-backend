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
import org.taktik.icure.constants.Roles;
import org.taktik.icure.entities.base.Principal;
import org.taktik.icure.entities.base.StoredDocument;
import org.taktik.icure.entities.embed.Permission;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Role extends StoredDocument implements Principal, Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	protected String name;

	protected Roles.VirtualHostDependency virtualHostDependency;

	protected Set<Property> properties = new HashSet<>();

	protected Set<Permission> permissions = new HashSet<>();

	protected Set<String> children = new HashSet<>();
	protected Set<String> parents = new HashSet<>();
	protected Set<String> users = new HashSet<>();
	protected Set<String> virtualHosts = new HashSet<>();

	public Set<String> getChildren() {
		return children;
	}

	public void setChildren(Set<String> value) {
		this.children = value;
	}

	public Set<String> getParents() {
		return parents;
	}

	public void setParents(Set<String> value) {
		this.parents = value;
	}

	public Set<String> getUsers() {
		return users;
	}

	public void setUsers(Set<String> users) {
		this.users = users;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Roles.VirtualHostDependency getVirtualHostDependency() {
		return virtualHostDependency;
	}

	public void setVirtualHostDependency(Roles.VirtualHostDependency virtualHostDependency) {
		this.virtualHostDependency = virtualHostDependency;
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}

	public Set<Property> getProperties() {
		return properties;
	}

	public void setProperties(Set<Property> properties) {
		this.properties = properties;
	}

	@Override
	public Set<String> getVirtualHosts() {
		return virtualHosts;
	}

	public void setVirtualHosts(Set<String> virtualHosts) {
		this.virtualHosts = virtualHosts;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Role role = (Role) o;

		if (id != null ? !id.equals(role.id) : role.id != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}