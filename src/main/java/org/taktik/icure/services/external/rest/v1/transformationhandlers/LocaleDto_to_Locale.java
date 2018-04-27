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

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.services.external.rest.v1.dto.LocaleDto;
import org.taktik.icure.entities.Locale;
import org.taktik.icure.entities.LocalizedString;
import org.taktik.icure.logic.LocaleLogic;
import org.taktik.icure.services.internal.transformationservice.impl.AbstractTransformationHandler;
import org.taktik.commons.service.transformationservice.TransformationContext;
import org.taktik.commons.service.transformationservice.TransformationHandler;

public class LocaleDto_to_Locale extends AbstractTransformationHandler implements TransformationHandler<LocaleDto, Locale> {
	private LocaleLogic localeLogic;

	@Override
	public void transform(Collection<? extends LocaleDto> webLocales, Collection<? super Locale> locales, TransformationContext context) {
		for (LocaleDto localeDto : webLocales) {
			Locale locale = new Locale();
			locale.setIdentifier(localeDto.getIdentifier());

			// Lookup for an existing Locale
			Locale existingLocale = localeLogic.getLocaleByIdentifier(localeDto.getIdentifier());
			if (existingLocale != null) {
				locale.setId(existingLocale.getId());
			}

			locale.setName(transformationService.transform(localeDto.getName(), LocalizedString.class, context));
			locale.setIcon(localeDto.getIcon());
			locales.add(locale);
		}
	}

	@Autowired
	public void setLocaleLogic(LocaleLogic localeLogic) {
		this.localeLogic = localeLogic;
	}
}
