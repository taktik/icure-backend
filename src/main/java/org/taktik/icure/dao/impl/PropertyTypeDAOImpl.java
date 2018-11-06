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
import org.taktik.icure.dao.PropertyTypeDAO;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.entities.PropertyType;

import java.util.List;

@Repository("propertyTypeDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.PropertyType' && !doc.deleted) emit( null, doc._id )}")
public class PropertyTypeDAOImpl extends CachedDAOImpl<PropertyType> implements PropertyTypeDAO {
    @Autowired
    public PropertyTypeDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbConfig") CouchDbICureConnector couchdb, IDGenerator idGenerator, @Qualifier("entitiesCacheManager") CacheManager cacheManager) {
        super(PropertyType.class, couchdb, idGenerator, cacheManager);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "by_identifier", map = "function(doc) {\n" +
            "            if (doc.java_type == 'org.taktik.icure.entities.PropertyType' && !doc.deleted && doc.identifier) {\n" +
            "            emit(doc.identifier,doc._id);\n" +
            "}\n" +
            "}")
    public PropertyType getByIdentifier(String propertyTypeIdentifier) {
		Cache.ValueWrapper wrappedValue = getWrapperFromCache("PID:"+propertyTypeIdentifier);
		if (wrappedValue == null) {
			List<PropertyType> result = queryView("by_identifier", propertyTypeIdentifier);
			PropertyType value = result != null && result.size() == 1 ? result.get(0) : null;

			if (value!=null && value.getId()!=null) { putInCache(value.getId(), value); }
			return value;
		}
		return (PropertyType)wrappedValue.get();
    }


	@Override
	public void evictFromCache(PropertyType entity) {
		super.evictFromCache(entity);
		cache.evict("PID:"+entity.getIdentifier());
	}

	@Override
	public void putInCache(String key, PropertyType entity) {
		super.putInCache(key, entity);
		cache.put("PID:"+entity.getIdentifier(), entity);
	}

}
