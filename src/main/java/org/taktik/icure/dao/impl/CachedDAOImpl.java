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

import org.apache.commons.lang3.Validate;
import org.ektorp.UpdateConflictException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.taktik.icure.dao.Option;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.entities.User;
import org.taktik.icure.entities.base.StoredDocument;

import javax.persistence.PersistenceException;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class CachedDAOImpl<T extends StoredDocument> extends GenericDAOImpl<T> {
	protected final Cache cache;
    protected final static String ALL_ENTITIES_CACHE_KEY = "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX";

    public CachedDAOImpl(Class<T> clazz, CouchDbICureConnector couchDb, IDGenerator idGenerator, CacheManager cacheManager) {
        super(clazz, couchDb, idGenerator);
        this.cache = cacheManager.getCache(entityClass.getSimpleName());

        initStandardDesignDocument();
        Validate.notNull(cache, "No cache found for: " + entityClass);
    }

    private String getFullId(String id) {
        return ((CouchDbICureConnector) this.db).getCurrentUserRealConnector().getUuid()+":"+id;
    }

    @Override
    public List<T> getList(Collection<String> ids) {
        List<String> missingKeys = new ArrayList<>();
        ArrayList<T> result = new ArrayList<>();

        // Get cached values
        for (String id : ids) {
            Cache.ValueWrapper value = cache.get(getFullId(id));
            if (value != null) {
                if (value.get() != null) {
                    result.add((T) value.get());
                }
            } else {
                missingKeys.add(id);
            }
        }

        // Get missing values from storage
        if (!missingKeys.isEmpty()) {
            List<T> entities = super.getList(missingKeys).stream().filter(Objects::nonNull).collect(Collectors.toList());
            for (T e : entities) {
                cache.put(getFullId(keyManager.getKey(e)), e);
            }
            result.addAll(entities);
        }
        return result;
    }

    @Override
    public T get(String id, Option... options) {
        Cache.ValueWrapper value = cache.get(getFullId(id));
        if (value == null) {
            T res = super.get(id, options);
            cache.put(getFullId(id), res);
            return res;
        }
        return (T) value.get();
    }

	public T getFromCache(String id) {
        Cache.ValueWrapper value = cache.get(getFullId(id));
        return value == null ? null : (T) value.get();
    }

	public void putInCache(String key, T value) {
        cache.put(getFullId(key), value);
    }

    public void evictFromCache(T entity) {
		cache.evict(getFullId(keyManager.getKey(entity)));
		cache.evict(getFullId(ALL_ENTITIES_CACHE_KEY));
	}

    public void evictFromCache(String groupId, String id) {
        cache.evict(((CouchDbICureConnector) this.db).getCouchDbICureConnector(groupId).getUuid()+":"+id);
    }

    public void evictFromCache(String id) {
        cache.evict(id);
    }

	protected Cache.ValueWrapper getWrapperFromCache(String id) {
		return cache.get(getFullId(id));
	}

	@Override
    public List<T> getAll() {
        Cache.ValueWrapper valueWrapper = cache.get(getFullId(ALL_ENTITIES_CACHE_KEY));
        if (valueWrapper == null) {
            List<T> allEntities = super.getAll();
            cache.put(getFullId(ALL_ENTITIES_CACHE_KEY), allEntities);
            return allEntities;
        } else {
            return (List<T>) valueWrapper.get();
        }
    }

    @Override
    protected T save(Boolean newEntity, T entity) {
        try {
            entity = super.save(newEntity, entity);
        } catch (UpdateConflictException e) {
            cache.evict(getFullId(keyManager.getKey(entity)));
            cache.evict(getFullId(ALL_ENTITIES_CACHE_KEY));

            throw e;
        }
		putInCache(keyManager.getKey(entity), entity);
        cache.evict(getFullId(ALL_ENTITIES_CACHE_KEY));

        return entity;
    }

    @Override
    public void remove(T entity) {
        super.removeByIds(Collections.singletonList(entity.getId()));
        evictFromCache(entity);
    }

    @Override
    public void unremove(T entity) {
        super.unremoveByIds(Collections.singletonList(entity.getId()));
        evictFromCache(entity);
    }

    @Override
    public void purge(T entity) {
        super.purge(entity);
        evictFromCache(entity);
    }

    @Override
    public void remove(Collection<T> entities) throws PersistenceException {
        super.remove(entities);
        for (T entity:entities) {
            evictFromCache(entity);
        }
    }

    @Override
    public void unremove(Collection<T> entities) throws PersistenceException {
        super.unremove(entities);
        for (T entity:entities) {
            evictFromCache(entity);
        }
    }

    @Override
    public void purge(Collection<T> entities) throws PersistenceException {
        super.purge(entities);
        for (T entity:entities) {
            evictFromCache(entity);
        }
    }

    @Override
    public void visitAll(Function<T, Boolean> callback) throws PersistenceException {
        super.visitAll(callback);
    }

    @Override
    protected <K extends Collection<T>> K save(Boolean newEntity, K entities) {
        try {
            entities = super.save(newEntity, entities);
        } catch (UpdateConflictException e) {
            for (T entity:entities) {
                cache.evict(getFullId(keyManager.getKey(entity)));
            }
            cache.evict(getFullId(ALL_ENTITIES_CACHE_KEY));

            throw e;
        }
        for (T entity:entities) {
			putInCache(keyManager.getKey(entity), entity);
        }
        cache.evict(getFullId(ALL_ENTITIES_CACHE_KEY));

        return entities;
    }
}
