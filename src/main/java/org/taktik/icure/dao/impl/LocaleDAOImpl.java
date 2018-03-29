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

package org.taktik.icure.dao.impl;

import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.LocaleDAO;
import org.taktik.icure.dao.LocalizedStringDAO;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.entities.Locale;

import java.util.List;

@Repository("localeDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Locale' && !doc.deleted) emit( null, doc._id )}")
public class LocaleDAOImpl extends CachedDAOImpl<Locale> implements LocaleDAO {
	private LocalizedStringDAO localizedStringDAO;

	@Autowired
    public LocaleDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbConfig") CouchDbICureConnector couchdb, IDGenerator idGenerator, @Qualifier("cacheManager") CacheManager cacheManager) {
        super(Locale.class, couchdb, idGenerator, cacheManager);
        initStandardDesignDocument();
	}

    @Override
    @View(name = "by_identifier", map = "function(doc) {\n" +
            "            if (doc.java_type == 'org.taktik.icure.entities.Locale' && !doc.deleted && doc.identifier) {\n" +
            "            emit(doc.identifier,doc._id);\n" +
            "}\n" +
            "}")
	public Locale getByIdentifier(String identifier) {
        List<Locale> result = queryView("by_identifier", identifier);
        return result != null && result.size() == 1 ? result.get(0):null;
    }

	@Override
	protected void beforeSave(Locale locale) {
		localizedStringDAO.save(locale.getName());
	}

	@Autowired
	public void setLocalizedStringDAO(LocalizedStringDAO localizedStringDAO) {
		this.localizedStringDAO = localizedStringDAO;
	}
}