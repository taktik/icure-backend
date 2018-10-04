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

package org.taktik.icure.dao.impl;

import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.KeywordDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.entities.Keyword;

import java.util.List;


@Repository("keywordDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Keyword' && !doc.deleted) emit( doc, doc._id )}")
class KeywordDAOImpl extends GenericIcureDAOImpl<Keyword> implements KeywordDAO {
    @Autowired
    public KeywordDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbBase") CouchDbICureConnector couchdb, IDGenerator idGenerator) {
        super(Keyword.class, couchdb, idGenerator);
        initStandardDesignDocument();
    }

	@Override
	public Keyword getKeyword(String keywordId) {
		return get(keywordId);
	}

    @View(name = "by_user", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Keyword' && !doc.deleted) emit( doc.userId, doc)}")
    @Override
    public List<Keyword> getByUserId(String userId) {
        ViewQuery viewQuery = createQuery("by_user")
                .startKey(userId)
                .endKey(userId)
                .includeDocs(false);

        return db.queryView(viewQuery, Keyword.class);
    }


}
