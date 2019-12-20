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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ComparisonChain;
import org.ektorp.ComplexKey;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.EntityTemplateDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.db.StringUtils;
import org.taktik.icure.entities.EntityTemplate;

@Repository("entityTemplateDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.EntityTemplate' && !doc.deleted) emit( null, doc._id )}")
public class EntityTemplateDAOImpl extends CachedDAOImpl<EntityTemplate> implements EntityTemplateDAO {
	@Autowired
	public EntityTemplateDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbHealthdata") CouchDbICureConnector couchdb, IDGenerator idGenerator, @Qualifier("entitiesCacheManager") CacheManager cacheManager) {
		super(EntityTemplate.class, couchdb, idGenerator, cacheManager);
	}

	@Override
	@View(name = "by_user_type_descr", map = "classpath:js/entitytemplate/By_user_type_descr.js")
	public List<EntityTemplate> getByUserIdTypeDescr(String userId, String type, String searchString, Boolean includeEntities) {
		String descr = (searchString!=null)? StringUtils.sanitizeString(searchString):null;
		ViewQuery viewQuery = createQuery("by_user_type_descr").startKey(ComplexKey.of(userId, type, descr)).endKey(ComplexKey.of(userId, type, (descr != null ? descr : "") + "\ufff0")).includeDocs(includeEntities==null?false:includeEntities);

		Map<String, EntityTemplate> result = new HashMap<>();
		db.queryView(viewQuery, EntityTemplate.class).forEach((e)->result.put(e.getId(),e));

		return result.values().stream().sorted((a,b)-> ComparisonChain.start()
				.compare(a.getUserId(), b.getUserId())
				.compare(a.getEntityType(),b.getEntityType())
				.compare(a.getDescr(),b.getDescr())
				.compare(a.getId(),b.getId()).result()).collect(Collectors.toList());
	}

	@Override
	@View(name = "by_type_descr", map = "classpath:js/entitytemplate/By_type_descr.js")
	public List<EntityTemplate> getByTypeDescr(String type, String searchString, Boolean includeEntities) {
		String descr = (searchString!=null)? StringUtils.sanitizeString(searchString):null;
		ViewQuery viewQuery = createQuery("by_type_descr").startKey(ComplexKey.of(type, descr)).endKey(ComplexKey.of(type, (descr != null ? descr : "") + "\ufff0")).includeDocs(includeEntities==null?false:includeEntities);

		Map<String, EntityTemplate> result = new HashMap<>();
		db.queryView(viewQuery, EntityTemplate.class).forEach((e)->result.put(e.getId(),e));

		return result.values().stream().sorted((a,b)-> ComparisonChain.start()
				.compare(a.getUserId(), b.getUserId())
				.compare(a.getEntityType(),b.getEntityType())
				.compare(a.getDescr(),b.getDescr())
				.compare(a.getId(),b.getId()).result()).collect(Collectors.toList());
	}

}
