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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.taktik.icure.entities.embed.Permission;
import org.taktik.icure.entities.Property;
import org.taktik.icure.entities.User;
import org.taktik.icure.services.external.rest.v1.dto.PermissionDto;
import org.taktik.icure.services.external.rest.v1.dto.PropertyDto;
import org.taktik.icure.services.external.rest.v1.dto.UserDto;
import org.taktik.icure.services.internal.transformationservice.impl.AbstractTransformationHandler;
import org.taktik.commons.service.transformationservice.TransformationContext;
import org.taktik.commons.service.transformationservice.TransformationHandler;

public class User_to_UserDto extends AbstractTransformationHandler implements TransformationHandler<User, UserDto> {
	@Override
	public void transform(Collection<? extends User> users, Collection<? super UserDto> webUsers, TransformationContext context) {
		for (User user : users) {
			UserDto userDto = new UserDto();
			userDto.setId(user.getId());
			userDto.setType(user.getType());
			userDto.setStatus(user.getStatus());

			userDto.setLogin(user.getLogin());
			userDto.setName(user.getName());
			userDto.setEmail(user.getEmail());

			userDto.setCreatedDate((user.getCreatedDate() != null) ? user.getCreatedDate().toEpochMilli() : null);
			userDto.setExpirationDate((user.getExpirationDate() != null) ? user.getExpirationDate().toEpochMilli() : null);
			userDto.setLastLoginDate((user.getLastLoginDate() != null) ? user.getLastLoginDate().toEpochMilli() : null);

			userDto.setPasswordToken(user.getPasswordToken());
			userDto.setPasswordTokenExpirationDate((user.getPasswordTokenExpirationDate() != null) ? user.getPasswordTokenExpirationDate().toEpochMilli() : null);

			userDto.setActivationToken(user.getActivationToken());
			userDto.setActivationTokenExpirationDate((user.getActivationTokenExpirationDate() != null) ? user.getActivationTokenExpirationDate().toEpochMilli() : null);

			userDto.setTermsOfUseDate((user.getTermsOfUseDate() != null) ? user.getTermsOfUseDate().toEpochMilli() : null);

			userDto.setRoles(new ArrayList<String>(user.getRoles()));

			// Set properties
			userDto.setProperties(new HashSet<>());
			transformationService.transform(user.getProperties(), userDto.getProperties(), Property.class, PropertyDto.class, context);

			// Set permissions
			userDto.setPermissions(new HashSet<>());
			transformationService.transform(user.getPermissions(), userDto.getPermissions(), Permission.class, PermissionDto.class, context);

			webUsers.add(userDto);
		}
	}
}