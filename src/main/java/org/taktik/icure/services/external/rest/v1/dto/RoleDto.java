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

package org.taktik.icure.services.external.rest.v1.dto;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.taktik.icure.constants.Roles;

import javax.persistence.Entity;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
public class RoleDto extends StoredDto implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	protected String name;

    @ApiModelProperty(dataType = "string")
    protected Roles.VirtualHostDependency virtualHostDependency;

	protected Set<PropertyDto> properties = new HashSet<>();

	protected Set<PermissionDto> permissions = new HashSet<>();

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

	public Roles.VirtualHostDependency getVirtualHostDependency() {
		return virtualHostDependency;
	}

	public void setVirtualHostDependency(Roles.VirtualHostDependency virtualHostDependency) {
		this.virtualHostDependency = virtualHostDependency;
	}

	public Set<PermissionDto> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<PermissionDto> permissions) {
		this.permissions = permissions;
	}

	public Set<PropertyDto> getProperties() {
		return properties;
	}

	public void setProperties(Set<PropertyDto> properties) {
		this.properties = properties;
	}

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

		RoleDto role = (RoleDto) o;

		if (id != null ? !id.equals(role.id) : role.id != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}
