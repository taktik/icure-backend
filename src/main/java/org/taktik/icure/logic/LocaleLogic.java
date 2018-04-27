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

package org.taktik.icure.logic;

import org.taktik.icure.entities.Locale;

import java.util.List;

public interface LocaleLogic extends EntityPersister<Locale, String> {
	List<String> getValidLocaleIdentifiers(String[] localeIdentifiers);

	List<org.taktik.icure.entities.Locale> getAllLocale();

	org.taktik.icure.entities.Locale getLocale(String id);

	org.taktik.icure.entities.Locale getLocaleByIdentifier(String identifier);

	org.taktik.icure.entities.Locale newLocale(org.taktik.icure.entities.Locale locale);

	void deleteLocale(org.taktik.icure.entities.Locale locale);

	void undeleteLocale(org.taktik.icure.entities.Locale locale);

	void save(Locale locale);

	String getDefaultLocale();

	boolean isLocaleSelectable();
}