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

import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.entities.embed.Permission;
import org.taktik.icure.entities.Property;
import org.taktik.icure.entities.User;
import org.taktik.icure.logic.UserLogic;
import org.taktik.icure.services.external.rest.v1.dto.PermissionDto;
import org.taktik.icure.services.external.rest.v1.dto.PropertyDto;
import org.taktik.icure.services.external.rest.v1.dto.UserDto;
import org.taktik.icure.services.internal.transformationservice.impl.AbstractTransformationHandler;
import org.taktik.commons.service.transformationservice.TransformationContext;
import org.taktik.commons.service.transformationservice.TransformationHandler;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;

public class UserDto_to_User extends AbstractTransformationHandler implements TransformationHandler<UserDto, User> {
	private UserLogic userLogic;

	@Override
	public void transform(Collection<? extends UserDto> webUsers, Collection<? super User> users, TransformationContext context) {
		for (UserDto userDto : webUsers) {
			User user = new User();
			user.setId(userDto.getId());
			user.setType(userDto.getType());
			user.setStatus(userDto.getStatus());

			user.setLogin(userDto.getLogin());
			user.setName(userDto.getName());
			user.setEmail(userDto.getEmail());

			user.setCreatedDate((userDto.getCreatedDate() != null) ? Instant.ofEpochMilli(userDto.getCreatedDate()) : null);
			user.setExpirationDate((userDto.getExpirationDate() != null) ? Instant.ofEpochMilli(userDto.getExpirationDate()) : null);
			user.setLastLoginDate((userDto.getLastLoginDate() != null) ? Instant.ofEpochMilli(userDto.getLastLoginDate()) : null);

            if (userDto.getId() != null) {
				User existingUser = userLogic.getUser(userDto.getId());
				if (existingUser != null) {
					user.setPasswordHash(existingUser.getPasswordHash());
				}
			}
			user.setPasswordToken(userDto.getPasswordToken());
			user.setPasswordTokenExpirationDate((userDto.getPasswordTokenExpirationDate() != null) ? Instant.ofEpochMilli(userDto.getPasswordTokenExpirationDate()) : null);

			user.setActivationToken(userDto.getActivationToken());
			user.setActivationTokenExpirationDate((userDto.getActivationTokenExpirationDate() != null) ? Instant.ofEpochMilli(userDto.getActivationTokenExpirationDate()) : null);

			user.setTermsOfUseDate((userDto.getTermsOfUseDate() != null) ? Instant.ofEpochMilli(userDto.getTermsOfUseDate()) : null);

			user.setRoles(new HashSet(userDto.getRoles()));

			// Set properties
			user.setProperties(new HashSet<>());
			transformationService.transform(userDto.getProperties(), user.getProperties(), PropertyDto.class, Property.class, context);

			// Set permissions
			user.setPermissions(new HashSet<>());
			transformationService.transform(userDto.getPermissions(), user.getPermissions(), PermissionDto.class, Permission.class, context);

			users.add(user);
		}
	}

	@Autowired
	public void setUserLogic(UserLogic userLogic) {
		this.userLogic = userLogic;
	}
}
