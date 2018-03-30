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

import java.util.Collection;
import java.util.Date;

import org.taktik.icure.services.external.rest.v1.dto.TypedValueDto;
import org.taktik.icure.entities.embed.TypedValue;
import org.taktik.icure.services.internal.transformationservice.impl.AbstractTransformationHandler;
import org.taktik.commons.service.transformationservice.TransformationContext;
import org.taktik.commons.service.transformationservice.TransformationHandler;

public class TypedValue_to_TypedValueDto extends AbstractTransformationHandler implements TransformationHandler<TypedValue, TypedValueDto> {
	@Override
	public void transform(Collection<? extends TypedValue> typedValues, Collection<? super TypedValueDto> webTypedValues, TransformationContext context) {
		for (TypedValue typedValue : typedValues) {
			TypedValueDto typedValueDto = new TypedValueDto();
			typedValueDto.setType(typedValue.getType());
			typedValueDto.setBooleanValue(typedValue.getBooleanValue() == null ? null : (typedValue.getBooleanValue() ? 1 : 0));
			typedValueDto.setIntegerValue(typedValue.getIntegerValue());
			typedValueDto.setDoubleValue(typedValue.getDoubleValue());
			typedValueDto.setStringValue(typedValue.getStringValue());
			typedValueDto.setDateValue((typedValue.getDateValue() != null) ? Date.from(typedValue.getDateValue()) : null);
			webTypedValues.add(typedValueDto);
		}
	}
}