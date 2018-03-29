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

import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.entities.LocalizedString;
import org.taktik.icure.logic.LocalizedStringLogic;
import org.taktik.icure.services.external.rest.v1.dto.LocalizedStringDto;
import org.taktik.icure.services.internal.transformationservice.impl.AbstractTransformationHandler;
import org.taktik.commons.service.transformationservice.TransformationContext;
import org.taktik.commons.service.transformationservice.TransformationHandler;

import java.util.Collection;

public class LocalizedStringDto_to_LocalizedString extends AbstractTransformationHandler implements TransformationHandler<LocalizedStringDto, LocalizedString> {
	private LocalizedStringLogic localizedStringLogic;

	@Override
	public void transform(Collection<? extends LocalizedStringDto> webLocalizedStrings, Collection<? super LocalizedString> localizedStrings, TransformationContext context) {
		for (LocalizedStringDto localizedStringDto : webLocalizedStrings) {
			LocalizedString localizedString = new LocalizedString();
			localizedString.setId(localizedStringDto.getId());
			localizedString.setIdentifier(localizedStringDto.getIdentifier());

			// Lookup for an existing LocalizedString if there is an identifier
			if (localizedStringDto.getIdentifier() != null) {
				LocalizedString existingLocalizedString = localizedStringLogic.getLocalizedStringByIdentifier(localizedStringDto.getIdentifier());
				if (existingLocalizedString != null) {
					localizedString.setId(existingLocalizedString.getId());
				}
			}

			// Transform values
			if (localizedStringDto.getValues() != null) {
				localizedString.setValues(localizedStringDto.getValues());
			}

			// Format localizedString
			localizedStringLogic.format(localizedString);

			localizedStrings.add(localizedString);
		}
	}

	@Autowired
	public void setLocalizedStringLogic(LocalizedStringLogic localizedStringLogic) {
		this.localizedStringLogic = localizedStringLogic;
	}

}
