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

package org.taktik.icure.logic.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.taktik.icure.dao.LocalizedStringDAO;
import org.taktik.icure.entities.LocalizedString;
import org.taktik.icure.logic.LocalizedStringLogic;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Transactional
@org.springframework.stereotype.Service
public class LocalizedStringLogicImpl implements LocalizedStringLogic {
	private LocalizedStringDAO localizedStringDAO;

	@Autowired
	public void setLocalizedStringDAO(LocalizedStringDAO localizedStringDAO) {
		this.localizedStringDAO = localizedStringDAO;
	}

	@Override
	public void format(LocalizedString localizedString) {
		trimValues(localizedString);
	}

	@Override
	public String localize(LocalizedString localizedString, String[] localeIdentifiers) {
		String bestLocalizedStringValue = null;

		if (localizedString != null && localizedString.getValues() != null) {
			if (localeIdentifiers != null && localeIdentifiers.length > 0) {
				Integer bestIndex = null;

				for (Map.Entry<String, String> localizedStringValue : localizedString.getValues().entrySet()) {
					String localizedStringValueLocaleIdentifier = localizedStringValue.getKey();
					String localizedStringValueStr = localizedStringValue.getValue();

					if (localizedStringValueStr != null) {
						for (int i = 0; i < localeIdentifiers.length; i++) {
							if (localizedStringValueLocaleIdentifier.equals(localeIdentifiers[i])) {
								// Optimization
								if (i == 0) {
									return localizedStringValueStr;
								}

								// Update bestIndex and bestLocalizedStringValue
								if (bestIndex == null || bestIndex > i) {
									bestIndex = i;
									bestLocalizedStringValue = localizedStringValueStr;
								}
							}
						}
					}
				}
			}
		}

		return bestLocalizedStringValue;
	}

	@Override
	public LocalizedString save(LocalizedString localizedString) {
		return localizedStringDAO.save(localizedString);
	}

	private void trimValues(LocalizedString localizedString) {
		if (localizedString != null) {
			Iterator<Map.Entry<String, String>> iterator = localizedString.getValues().entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, String> entry = iterator.next();
				String value = entry.getValue();
				value = value == null ? "" : value.trim();
				if (value.isEmpty()) {
					iterator.remove();
				} else {
					entry.setValue(value);
				}
			}
		}
	}

	@Override
	public boolean createEntities(Collection<LocalizedString> localizedStrings, Collection<LocalizedString> createdLocalizedStrings) throws Exception {
		for (LocalizedString localizedString : localizedStrings) {
			// Format localizedString
			format(localizedString);

			// Add new localizedString
			createdLocalizedStrings.add(newLocalizedString(localizedString));
		}
		return true;
	}

	@Override
	public List<LocalizedString> updateEntities(Collection<LocalizedString> localizedStrings) throws Exception {
		return localizedStrings.stream().map(ls->{
			// Format localizedString
			format(ls);
			// Merge localized string
			return localizedStringDAO.save(ls);
		}).collect(Collectors.toList());
	}

	@Override
	public void deleteEntities(Collection<String> localizedStringIds) throws Exception {
		localizedStringDAO.removeByIds(localizedStringIds);
	}

	@Override
	public void undeleteEntities(Collection<String> localizedStringIds) throws Exception {
		localizedStringDAO.removeByIds(localizedStringIds);
	}

	@Override
	public List<LocalizedString> getAllEntities() {
		return localizedStringDAO.getAll();
	}

	@Override
	public List<String> getAllEntityIds() {
		return localizedStringDAO.getAll().stream().map(e->e.getId()).collect(Collectors.toList());
	}

	@Override
	public boolean hasEntities() {
		return localizedStringDAO.hasAny();
	}

	@Override
	public boolean exists(String id) {
		return localizedStringDAO.contains(id);
	}

	@Override
	public LocalizedString getEntity(String id) {
		return getLocalizedString(id);
	}

	@Override
	public List<LocalizedString> getIdentifiableLocalizedStrings() {
		return localizedStringDAO.getIdentifiableLocalizedStrings();
	}

	@Override
	public LocalizedString getLocalizedString(String id) {
		return localizedStringDAO.get(id);
	}

	@Override
	public LocalizedString getLocalizedStringByIdentifier(String identifier) {
		return localizedStringDAO.getByIdentifier(identifier);
	}

	@Override
	public LocalizedString newLocalizedString(LocalizedString localizedstring) {
		return localizedStringDAO.save(localizedstring);
	}

	@Override
	public void deleteLocalizedString(LocalizedString localizedstring) {
		localizedStringDAO.remove(localizedstring);
	}

	@Override
	public void undeleteLocalizedString(LocalizedString localizedstring) {
		localizedStringDAO.unremove(localizedstring);
	}
}
