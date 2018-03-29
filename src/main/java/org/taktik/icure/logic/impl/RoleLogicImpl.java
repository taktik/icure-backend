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

package org.taktik.icure.logic.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.taktik.icure.constants.Permissions;
import org.taktik.icure.constants.Roles;
import org.taktik.icure.dao.UserDAO;
import org.taktik.icure.entities.embed.Permission;
import org.taktik.icure.entities.Role;
import org.taktik.icure.entities.User;
import org.taktik.icure.logic.RoleLogic;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
public class RoleLogicImpl extends PrincipalLogicImpl<Role> implements RoleLogic {
	private UserDAO userDAO;

	@Override
	public Role getRoleByName(String name) {
		return roleDAO.getByName(name);
	}

	@Override
	public Set<Role> getDescendantRoles(String roleId) {
		Role role = getRole(roleId);
		if (role != null) {
			return getDescendantRoles(role, new HashSet<>());
		}
		return null;
	}

	private Set<Role> getDescendantRoles(Role role, Set<Role> ignoredRoles) {
		// Build descendant roles list
		Set<Role> roles = new HashSet<>();
		if (role != null) {
			// Add this role to ignore list
			ignoredRoles.add(role);

			// Process children
			if (role.getChildren() != null) {
				for (Role child : getChildren(role)) {
					if (child != null) {
						if (!ignoredRoles.contains(child)) {
							roles.add(child);
							roles.addAll(getDescendantRoles(child, ignoredRoles));
						}
					}
				}
			}
		}
		return roles;
	}

	@Override
	public Role getRole(String id) {
		return roleDAO.get(id);
	}

	@Override
	public Set<User> getUsers(Role role) {
		return userDAO.getSet(role.getUsers());
	}

    @Override
    public void createDefaultRoleIfNecessary() {
        if (getRoleByName(Roles.DEFAULT_ROLE_NAME) != null) {
            return;
        }
        Role defaultRole = new Role();

        defaultRole.setName(Roles.DEFAULT_ROLE_NAME);
        defaultRole.setPermissions(new HashSet<>(Arrays.asList(Permission.granted(Permissions.Type.AUTHENTICATE))));

        saveRole(defaultRole);
    }

    private Role saveRole(Role role) {
		// Save role
		Role savedRole = roleDAO.save(role);

		// Invalidate PermissionSet/Filter from cache for all descendantRoles/Users
		Set<Role> descendantRoles = getDescendantRoles(savedRole, new HashSet<Role>());
		descendantRoles.add(savedRole);

		return savedRole;
	}

	@Override
	public Role newRole(Role role) {
		return saveRole(role);
	}


	private Set<Role> getChildren(Role role) {
		return roleDAO.getSet(role.getChildren());
	}

	@Override
	public boolean createEntities(Collection<Role> roles, Collection<Role> createdRoles) throws Exception {
		for (Role role : roles) {
			createdRoles.add(newRole(role));
		}
		return true;
	}

	@Override
	public List<Role> updateEntities(Collection<Role> roles) throws Exception {
		return roles.stream().map(this::saveRole).collect(Collectors.toList());
	}

	@Override
	public void deleteEntities(Collection<String> roleIds) throws Exception {
		roleDAO.removeByIds(roleIds);
	}

	@Override
	public void undeleteEntities(Collection<String> roleIds) throws Exception {
		roleDAO.unremoveByIds(roleIds);
	}

	@Override
	public List<Role> getAllEntities() {
		return roleDAO.getAll();
	}

	@Override
	public List<String> getAllEntityIds() {
		return roleDAO.getAll().stream().map(e->e.getId()).collect(Collectors.toList());
	}

	@Override
	public boolean exists(String id) {
		return roleDAO.contains(id);
	}

	@Override
	public boolean hasEntities() {
		return roleDAO.hasAny();
	}

	@Override
	public Role getEntity(String id) {
		return getRole(id);
	}

	@Override
	public Role getPrincipal(String roleId) {
		return getRole(roleId);
	}

	@Autowired
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
}
