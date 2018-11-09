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
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.PropertyDAO;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.entities.Property;

import java.util.List;

@Repository("propertyDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Property' && !doc.deleted) emit(doc._id )}")
public class PropertyDAOImpl extends CachedDAOImpl<Property> implements PropertyDAO {
	private static final String IDENTIFIER = "type.identifier";

	@Autowired
    public PropertyDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbConfig") CouchDbICureConnector couchdb, IDGenerator idGenerator, @Qualifier("entitiesCacheManager") CacheManager cacheManager) {
        super(Property.class, couchdb, idGenerator, cacheManager);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "by_identifier", map = "classpath:js/property/By_identifier_Map.js")
    public Property getByIdentifier(String propertyIdentifier) {
		Cache.ValueWrapper wrappedValue = getWrapperFromCache("PID:"+propertyIdentifier);
        if (wrappedValue == null) {
            List<Property> result = queryView("by_identifier", propertyIdentifier);
			Property value = result != null && result.size() == 1 ? result.get(0) : null;

            if (value!=null && value.getId()!=null) { putInCache(value.getId(), value); }
			return value;
        }
        return (Property)wrappedValue.get();
    }

	@Override
	public void evictFromCache(Property entity) {
        super.evictFromCache(entity);
        cache.evict("PID:"+entity.getType().getIdentifier());
    }

	@Override
	public void putInCache(String key, Property entity) {
		super.putInCache(key, entity);
		cache.put("PID:"+entity.getType().getIdentifier(), entity);
	}
}
