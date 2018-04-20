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

package org.taktik.icure.logic.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.taktik.icure.constants.PropertyTypes;
import org.taktik.icure.dao.LocaleDAO;
import org.taktik.icure.entities.Locale;
import org.taktik.icure.logic.LocaleLogic;
import org.taktik.icure.logic.PropertyLogic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@org.springframework.stereotype.Service
public class LocaleLogicImpl implements LocaleLogic {
	private LocaleDAO localeDAO;
	private PropertyLogic propertyLogic;

	@Autowired
	public void setLocaleDAO(LocaleDAO localeDAO) {
		this.localeDAO = localeDAO;
	}

	@Override
	public List<String> getValidLocaleIdentifiers(String[] localeIdentifiers) {
		List<String> validLocaleIdentifiers = new ArrayList<String>();

		for (String localeIdentifier : localeIdentifiers) {
			if (localeIdentifier != null) {
				// Remove second part after _ if needed
				if (localeIdentifier.indexOf('_') != -1) {
					localeIdentifier = localeIdentifier.substring(0, localeIdentifier.indexOf('_'));
				}

				// Search for Locale
				Locale locale = getLocaleByIdentifier(localeIdentifier);
				if (locale != null) {
					validLocaleIdentifiers.add(locale.getIdentifier());
				}
			}
		}

		return validLocaleIdentifiers;
	}

	@Override
	public boolean isLocaleSelectable() {
		return propertyLogic.getSystemPropertyValue(PropertyTypes.System.LOCALE_SELECTABLE.getIdentifier()) != null && propertyLogic.<Boolean>getSystemPropertyValue(PropertyTypes.System.LOCALE_SELECTABLE.getIdentifier());
	}

	@Override
	public String getDefaultLocale() {
		return propertyLogic.getSystemPropertyValue(PropertyTypes.System.LOCALE_DEFAULT.getIdentifier());
	}

	@Override
	public List<Locale> getAllLocale() {
		return localeDAO.getAll();
	}

	@Override
	public Locale getLocale(String id) {
		return localeDAO.get(id);
	}

	@Override
	public Locale getLocaleByIdentifier(String identifier) {
		return localeDAO.getByIdentifier(identifier);
	}

	@Override
	public Locale newLocale(Locale locale) {
		return localeDAO.save(locale);
	}

	@Override
	public void deleteLocale(Locale locale) {
		localeDAO.remove(locale);
	}

	@Override
	public void undeleteLocale(Locale locale) {
		localeDAO.unremove(locale);
	}

	@Override
	public void save(Locale locale) {
		localeDAO.save(locale);
	}

	@Override
	public boolean createEntities(Collection<Locale> locales, Collection<Locale> createdLocales) throws Exception {
		for (Locale locale : locales) {
			createdLocales.add(newLocale(locale));
		}
		return true;
	}

	@Override
	public List<Locale> updateEntities(Collection<Locale> locales) throws Exception {
		return locales.stream().map(l->localeDAO.save(l)).collect(Collectors.toList());
	}

	@Override
	public void deleteEntities(Collection<String> localeIdentifiers) throws Exception {
		for (String localeIdentifier : localeIdentifiers) {
			Locale locale = getLocaleByIdentifier(localeIdentifier);

			// Delete all LocalizedStringValues for this locale
			//localizedStringValueLogic.deleteLocalizedStringValues(locale);

			// Delete locale
			deleteLocale(locale);
		}
	}

	@Override
	public void undeleteEntities(Collection<String> localeIdentifiers) throws Exception {
		for (String localeIdentifier : localeIdentifiers) {
			Locale locale = getLocaleByIdentifier(localeIdentifier);

			// Delete all LocalizedStringValues for this locale
			//localizedStringValueLogic.deleteLocalizedStringValues(locale);

			// Delete locale
			undeleteLocale(locale);
		}
	}

	@Override
	public List<Locale> getAllEntities() {
		return localeDAO.getAll();
	}

	@Override
	public List<String> getAllEntityIds() {
		return localeDAO.getAll().stream().map(e->e.getId()).collect(Collectors.toList());
	}

	@Override
	public boolean hasEntities() {
		return localeDAO.hasAny();
	}

	@Override
	public boolean exists(String id) {
		return localeDAO.contains(id);
	}

	@Override
	public Locale getEntity(String id) {
		return getLocale(id);
	}

	@Autowired
	public void setPropertyLogic(PropertyLogic propertyLogic) {
		this.propertyLogic = propertyLogic;
	}
}
