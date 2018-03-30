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

package org.taktik.icure.services.internal.transformationservice.impl.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.entities.LocalizedString;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.services.internal.transformationservice.impl.AbstractTransformationHandler;
import org.taktik.commons.service.transformationservice.TransformationContext;
import org.taktik.commons.service.transformationservice.TransformationHandler;

import java.util.Collection;

public class LocalizedString_to_String extends AbstractTransformationHandler implements TransformationHandler<LocalizedString, String> {
	private SessionLogic sessionLogic;

	@Override
	public void transform(Collection<? extends LocalizedString> localizedStrings, Collection<? super String> strings, TransformationContext context) {
		String locale = sessionLogic.getCurrentSessionContext().getLocale();

		for (LocalizedString localizedString : localizedStrings) {
			String string = localizedString.getString(locale);
			strings.add(string);
		}
	}

	@Autowired
	public void setSessionLogic(SessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}
}
