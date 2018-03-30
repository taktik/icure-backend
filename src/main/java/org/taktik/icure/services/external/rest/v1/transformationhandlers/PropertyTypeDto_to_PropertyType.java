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

import org.taktik.icure.entities.LocalizedString;
import org.taktik.icure.entities.PropertyType;
import org.taktik.icure.services.external.rest.v1.dto.PropertyTypeDto;
import org.taktik.icure.services.internal.transformationservice.impl.AbstractTransformationHandler;
import org.taktik.commons.service.transformationservice.TransformationContext;
import org.taktik.commons.service.transformationservice.TransformationHandler;

import java.util.Collection;

public class PropertyTypeDto_to_PropertyType extends AbstractTransformationHandler implements TransformationHandler<PropertyTypeDto, PropertyType> {

	@Override
	public void transform(Collection<? extends PropertyTypeDto> webPropertyTypes, Collection<? super PropertyType> propertyTypes, TransformationContext context) {
		for (PropertyTypeDto propertyTypeDto : webPropertyTypes) {
			PropertyType propertyType = new PropertyType();
			propertyType.setIdentifier(propertyTypeDto.getIdentifier());
			propertyType.setName(transformationService.transform(propertyTypeDto.getName(), LocalizedString.class, context));
			propertyType.setType(propertyTypeDto.getType());
			propertyType.setScope(propertyTypeDto.getScope());
			propertyType.setLocalized(propertyTypeDto.getLocalized());
			propertyType.setUnique(propertyTypeDto.getUnique());
			propertyType.setEditor(propertyTypeDto.getEditor());
			propertyTypes.add(propertyType);
		}
	}
}