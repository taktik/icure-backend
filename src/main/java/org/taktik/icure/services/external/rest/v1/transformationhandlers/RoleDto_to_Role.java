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

package org.taktik.icure.services.external.rest.v1.transformationhandlers;

import org.taktik.icure.entities.Property;
import org.taktik.icure.entities.Role;
import org.taktik.icure.entities.embed.Permission;
import org.taktik.icure.services.external.rest.v1.dto.PermissionDto;
import org.taktik.icure.services.external.rest.v1.dto.PropertyDto;
import org.taktik.icure.services.external.rest.v1.dto.RoleDto;
import org.taktik.icure.services.internal.transformationservice.impl.AbstractTransformationHandler;
import org.taktik.commons.service.transformationservice.TransformationContext;
import org.taktik.commons.service.transformationservice.TransformationHandler;

import java.util.Collection;
import java.util.HashSet;

public class RoleDto_to_Role extends AbstractTransformationHandler implements TransformationHandler<RoleDto, Role> {

	@Override
	public void transform(Collection<? extends RoleDto> webRoles, Collection<? super Role> roles, TransformationContext context) {
		for (RoleDto roleDto : webRoles) {
			Role role = new Role();
			role.setId(roleDto.getId());
			role.setName(roleDto.getName());

			role.setParents(roleDto.getParents());
			role.setChildren(roleDto.getChildren());
			role.setUsers(roleDto.getUsers());

			// Set properties
			role.setProperties(new HashSet<>());
			transformationService.transform(roleDto.getProperties(), role.getProperties(), PropertyDto.class, Property.class, context);

			// Set permissions
			role.setPermissions(new HashSet<>());
			transformationService.transform(roleDto.getPermissions(), role.getPermissions(), PermissionDto.class, Permission.class, context);

			roles.add(role);
		}
	}
}