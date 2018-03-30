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

public class Role_to_RoleDto extends AbstractTransformationHandler implements TransformationHandler<Role, RoleDto> {

	@Override
	public void transform(Collection<? extends Role> roles, Collection<? super RoleDto> webRoles, TransformationContext context) {
		for (Role role : roles) {
			RoleDto roleDto = new RoleDto();
			roleDto.setId(role.getId());
			roleDto.setName(role.getName());

			// Set parents
			roleDto.setParents(role.getParents());
			roleDto.setChildren(role.getChildren());
			roleDto.setUsers(role.getUsers());

			// Set properties
			roleDto.setProperties(new HashSet<>());
			transformationService.transform(role.getProperties(), roleDto.getProperties(), Property.class, PropertyDto.class, context);

			// Set permissions
			roleDto.setPermissions(new HashSet<>());
			transformationService.transform(role.getPermissions(), roleDto.getPermissions(), Permission.class, PermissionDto.class, context);

			webRoles.add(roleDto);
		}
	}
}