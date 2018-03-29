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

package org.taktik.icure.services.external.rest.v1.transformationhandlers;

import java.util.Collection;
import java.util.HashSet;

import org.taktik.icure.services.external.rest.v1.dto.PermissionDto;
import org.taktik.icure.services.external.rest.v1.dto.PermissionCriterionDto;
import org.taktik.icure.entities.embed.Permission;
import org.taktik.icure.entities.embed.PermissionCriterion;
import org.taktik.icure.services.internal.transformationservice.impl.AbstractTransformationHandler;
import org.taktik.commons.service.transformationservice.TransformationContext;
import org.taktik.commons.service.transformationservice.TransformationHandler;

public class Permission_to_PermissionDto extends AbstractTransformationHandler implements TransformationHandler<Permission, PermissionDto> {
	@Override
	public void transform(Collection<? extends Permission> permissions, Collection<? super PermissionDto> PermissionDtos, TransformationContext context) {
		for (Permission permission : permissions) {
			PermissionDto PermissionDto = new PermissionDto();
			PermissionDto.setGrant(permission.getGrant());
			PermissionDto.setRevoke(permission.getRevoke());
			PermissionDto.setCriteria(new HashSet<>());
			transformationService.transform(permission.getCriteria(), PermissionDto.getCriteria(), PermissionCriterion.class, PermissionCriterionDto.class, context);
			PermissionDtos.add(PermissionDto);
		}
	}
}