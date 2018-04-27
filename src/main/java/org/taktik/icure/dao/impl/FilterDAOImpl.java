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

import org.ektorp.ComplexKey;
import org.ektorp.UpdateConflictException;
import org.ektorp.ViewQuery;
import org.ektorp.impl.NameConventions;
import org.ektorp.support.DesignDocument;
import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.FilterDAO;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.Filter;
import org.taktik.icure.entities.base.StoredDocument;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Repository("filterDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Filter' && !doc.deleted) emit( null, doc._id )}")
class FilterDAOImpl extends CachedDAOImpl<Filter> implements FilterDAO {
    private final String customQueriesDesignDocumentId = NameConventions.designDocName("_CustomQueries");

    @Autowired
    public FilterDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbBase") CouchDbICureConnector couchdb, IDGenerator idGenerator, @Qualifier("cacheManager") CacheManager cacheManager) {
        super(Filter.class, couchdb, idGenerator, cacheManager);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "by_entity", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Filter' && !doc.deleted && doc.filterEntity) {emit(doc.filterEntity, doc._id)} }")
    public List<Filter> findByEntity(String entity) {
        return queryView("by_entity", entity);
    }

	@Override
	@View(name = "by_entity_and_user_id", map = "classpath:js/filter/by_entity_and_user_id_map.js")
	public List<Filter> findByEntity(String entityName, String userId) {
		return queryView("by_entity_and_user_id", ComplexKey.of(entityName, userId));
	}

    @Override
    @View(name = "by_name_entity", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Filter' && !doc.deleted && doc.filterEntity && doc.name) {emit([doc.filterEntity,doc.name], doc._id)} }")
    public Filter findByNameAndEntity(String name, String entity) {
        List<Filter> view = queryView("by_name_entity", ComplexKey.of(entity, name));
        return view.size() > 0 ? view.get(0) : null;
    }

    @Override
    @Deprecated
    public void addFilterView(Filter filter) {
        DesignDocument designDoc;
        if (db.contains(customQueriesDesignDocumentId)) {
            designDoc = getDesignDocumentFactory().getFromDatabase(db, customQueriesDesignDocumentId);
        } else {
            designDoc = getDesignDocumentFactory().newDesignDocumentInstance();
            designDoc.setId(customQueriesDesignDocumentId);
        }

        DesignDocument generated = new DesignDocument();
        generated.addView("_" + filter.getId(), new DesignDocument.View(compile(filter)));

        String[] mapReduceCounts = compileCountOf(filter);
        generated.addView("_countof_" + filter.getId(), new DesignDocument.View(mapReduceCounts[0], mapReduceCounts[1]));

        boolean changed = designDoc.mergeWith(generated);
        if (changed) {
            try {
                db.update(designDoc);
            } catch (UpdateConflictException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Override
    @Deprecated
    public <T extends StoredDocument> List<T> applyFilter(Filter filter, Serializable argument, PaginationOffset offset, Integer limit, Class<T> clazz) {
        //Not transactional aware
        ViewQuery query = new ViewQuery().designDocId(customQueriesDesignDocumentId)
                .viewName("_" + filter.getId()).includeDocs(true)
                .key(argument);
        if (offset != null) {
            if (offset.getStartKey() != null) {
                query = query.startKey(offset.getStartKey());
                query = query.startDocId(offset.getStartDocumentId());
            } else if (offset.getOffset() != null) {
                query = query.skip(offset.getOffset());
            }
        }
        if (limit != null && limit > 0) {
            query = query.limit(limit);
        }

        return db.queryView(query, clazz);
    }

    @Override
    @Deprecated
    public <T extends StoredDocument> List<T> applyFilter(Filter filter, Serializable key, Serializable startKey, Serializable endKey, Integer limit, Class<T> clazz) {
        //Not transactional aware
        ViewQuery query = new ViewQuery().designDocId(customQueriesDesignDocumentId)
                .viewName("_" + filter.getId()).includeDocs(true);

        if (startKey != null && endKey != null) {
            query = query.startKey(startKey);
            query = query.endKey(endKey);
        } else if (key != null) {
            query = query.key(key);
        }

        if (limit != null) {
            query = query.limit(limit);
        }

        return db.queryView(query, clazz);
    }

    @Override
    @Deprecated
    public long countOfFilter(Filter filter, Serializable argument) {
        //Not transactional aware
        ViewQuery query = new ViewQuery().designDocId(customQueriesDesignDocumentId)
                .viewName("_countof_" + filter.getId())
                .key(argument);
        if (db.queryView(query).getRows().size() == 0) return 0;
        return db.queryView(query).getRows().get(0).getValueAsNode().asLong();
    }

    @Deprecated
	private String compile(Filter f) {
        if (f.getData() == null) {
            String naturalSortKey = null;
            try {
                naturalSortKey = (String) Class.forName(f.getFilterEntity(), true, Filter.class.getClassLoader()).getMethod("naturalSortKey").invoke(null);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Unknown entity " + f.getFilterEntity(), e);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Bad naturalSortKey access rigths for " + f.getFilterEntity(), e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException("Error while accessing naturalSortKey for " + f.getFilterEntity(), e);
            } catch (NoSuchMethodException e) {
                //That can happen, fall back on null
            } catch (SecurityException e) {
                throw new IllegalStateException("Error while accessing naturalSortKey for " + f.getFilterEntity(), e);
            }
            return "function(doc) { if (doc.java_type == '" + f.getFilterEntity() + "' && !doc.deleted) emit( " + (naturalSortKey != null ? "doc." + naturalSortKey : "null") + ", doc._id )}";
        }
        throw new IllegalStateException("Not yet implemented");
    }

    @Deprecated
    private String[] compileCountOf(Filter f) {
        if (f.getData() == null) {
            return new String[] {"function(doc) { if (doc.java_type == '" + f.getFilterEntity() + "' && !doc.deleted) emit( null, 1 )}", "function(keys, values, combine) { return sum(values); }"};
        }
        throw new IllegalStateException("Not yet implemented");
    }
}