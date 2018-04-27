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

import org.taktik.icure.entities.PropertyType;
import org.taktik.icure.services.external.rest.v1.dto.LocalizedStringDto;
import org.taktik.icure.services.external.rest.v1.dto.PropertyTypeDto;
import org.taktik.icure.services.internal.transformationservice.impl.AbstractTransformationHandler;
import org.taktik.commons.service.transformationservice.TransformationContext;
import org.taktik.commons.service.transformationservice.TransformationHandler;

import java.util.Collection;

public class PropertyType_to_PropertyTypeDto extends AbstractTransformationHandler implements TransformationHandler<PropertyType, PropertyTypeDto> {
	@Override
	public void transform(Collection<? extends PropertyType> propertyTypes, Collection<? super PropertyTypeDto> webPropertyTypes, TransformationContext context) {
		for (PropertyType propertyType : propertyTypes) {
			PropertyTypeDto propertyTypeDto = new PropertyTypeDto();
			propertyTypeDto.setIdentifier(propertyType.getIdentifier());
			propertyTypeDto.setName(transformationService.transform(propertyType.getName(), LocalizedStringDto.class, context));
			propertyTypeDto.setType(propertyType.getType());
			propertyTypeDto.setScope(propertyType.getScope());
			propertyTypeDto.setLocalized(propertyType.getLocalized());
			propertyTypeDto.setUnique(propertyType.getUnique());
			propertyTypeDto.setEditor(propertyType.getEditor());
			webPropertyTypes.add(propertyTypeDto);
		}
	}
}