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

import org.taktik.icure.services.external.rest.v1.dto.LocalizedStringDto;
import org.taktik.icure.entities.LocalizedString;
import org.taktik.icure.services.internal.transformationservice.impl.AbstractTransformationHandler;
import org.taktik.commons.service.transformationservice.TransformationContext;
import org.taktik.commons.service.transformationservice.TransformationHandler;

public class LocalizedString_to_LocalizedStringDto extends AbstractTransformationHandler implements TransformationHandler<LocalizedString, LocalizedStringDto> {
	@Override
	public void transform(Collection<? extends LocalizedString> localizedStrings, Collection<? super LocalizedStringDto> LocalizedStringDtos, TransformationContext context) {
		for (LocalizedString localizedString : localizedStrings) {
			LocalizedStringDto LocalizedStringDto = new LocalizedStringDto();
			LocalizedStringDto.setId(localizedString.getId());
			LocalizedStringDto.setIdentifier(localizedString.getIdentifier());
			LocalizedStringDto.setValues(localizedString.getValues());
			LocalizedStringDtos.add(LocalizedStringDto);
		}
	}
}