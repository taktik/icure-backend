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

package org.taktik.icure.logic.impl;

import com.google.common.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.constants.Roles;
import org.taktik.icure.dao.RoleDAO;
import org.taktik.icure.entities.Property;
import org.taktik.icure.entities.PropertyType;
import org.taktik.icure.entities.Role;
import org.taktik.icure.entities.base.Principal;
import org.taktik.icure.entities.embed.Permission;
import org.taktik.icure.logic.PrincipalLogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class PrincipalLogicImpl<P extends Principal> implements PrincipalLogic<P> {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected final TypeToken<P> typeToken = new TypeToken<P>(getClass()) {};

	// Bit indexes
	protected static int DEPENDENCY_INCLUDE_DIRECT = 1 << 1;
	protected static int DEPENDENCY_INCLUDE_HERITED = 1 << 2;

	protected RoleDAO roleDAO;

	protected Class<P> getPrincipalClass() {
		return (Class<P>) typeToken.getRawType();
	}

	protected Set<Role> getParents(Principal principal) {
		return roleDAO.getSet(principal.getParents());
	}


	@Override
	public Set<Property> getProperties(String principalId, boolean includeDirect, boolean includeHerited, boolean includeDefault) {
		Principal principal = getPrincipal(principalId);
		if (principal != null) {
			return buildProperties(principal, includeDirect, includeHerited, includeDefault, new HashSet<>());
		}

		return new HashSet<>();
	}

	@Override
	public Set<Permission> getPermissions(String principalId, String virtualHostId, boolean includeDirect, boolean includeHerited, boolean includeDefault) {
		return null;
	}

	protected Set<Property> buildProperties(Principal principal, boolean includeDirect, boolean includeHerited, boolean includeDefault, Set<PropertyType> ignoredPropertyTypes) {
		log.trace("buildProperties() : principal={}({}), includeDirect={}, includeHerited={}, includeDefault={}", principal.getClass().getSimpleName(), principal.getId(),  includeDirect, includeHerited, includeDefault);

		// Prepare set of properties
		Set<Property> properties = new HashSet<>();

		if (includeDirect) {
			// First add properties directly linked to the principal
			Set<Property> principalProperties = principal.getProperties();
			for (Property p : principalProperties) {
				if (!ignoredPropertyTypes.contains(p.getType())) {
					ignoredPropertyTypes.add(p.getType());
					properties.add(p);
				}
			}
		}

		if (includeHerited) {
			// Get the parent roles, sorted by natural order
			List<Role> parentRolesSorted = new ArrayList<>(getParents(principal));
			Collections.sort(parentRolesSorted, (r1, r2) -> r1.getName().compareToIgnoreCase(r2.getName()));

			// Add properties directly linked to the parents
			for (Role parent : parentRolesSorted) {
				Set<Property> parentProperties = buildProperties(parent, true, false, false, ignoredPropertyTypes);
				properties.addAll(parentProperties);
			}

			// Add properties herited from grand parents
			for (Role parent : parentRolesSorted) {
				Set<Property> parentProperties = buildProperties(parent, false, true, false, ignoredPropertyTypes);
				properties.addAll(parentProperties);
			}
		}

		if (includeDefault) {
			// Get the default role and add property if not overridden in child role
			Role defaultRole = roleDAO.getByName(Roles.DEFAULT_ROLE_NAME);
			if (defaultRole!=null) {
				Set<Property> defaultProperties = defaultRole.getProperties();
				for (Property defaultProp : defaultProperties) {
					if (!ignoredPropertyTypes.contains(defaultProp.getType())) {
						properties.add(defaultProp);
					}
				}
			}
		}

		return properties;
	}

	@Override
	public Set<Role> getAscendantRoles(String principalId) {
		Principal principal = getPrincipal(principalId);
		if (principal != null) {
			return buildAscendantRoles(principal, new HashSet<>());
		}

		return null;
	}

	protected Set<Role> buildAscendantRoles(Principal principal, Set<Role> ignoredRoles) {
		Set<Role> roles = new HashSet<>();
		if (principal != null) {
			// Add this role to ignore list
			if (principal instanceof Role) {
				ignoredRoles.add((Role) principal);
			}

			// Process parents
			if (principal.getParents() != null) {
				for (Role parent : getParents(principal)) {
					if (parent != null) {
						if (!ignoredRoles.contains(parent)) {
							roles.add(parent);
							roles.addAll(buildAscendantRoles(parent, ignoredRoles));
						}
					}
				}
			}
		}

		return roles;
	}

	@Autowired
	public void setRoleDAO(RoleDAO roleDAO) {
		this.roleDAO = roleDAO;
	}
}