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

import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.GroupDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.entities.Group;

@Repository("groupDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Group' && !doc.deleted) emit( null, doc._id )}")
public class GroupDAOImpl extends GenericDAOImpl<Group> implements GroupDAO {
	@Autowired
	public GroupDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbConfig") CouchDbICureConnector couchdb, IDGenerator idGenerator, @Qualifier("entitiesCacheManager") CacheManager cacheManager) {
		super(Group.class, couchdb, idGenerator);
		initStandardDesignDocument();
	}
}
