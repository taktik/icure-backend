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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.taktik.icure.dao.Option;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.entities.base.StoredDocument;
import org.taktik.icure.exceptions.BulkUpdateConflictException;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class CachedDAOImpl<T extends StoredDocument> extends GenericDAOImpl<T> {
    final static String ALL_ENTITIES_CACHE_KEY = "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX";

	private final Cache cache;
    private static final Logger log = LoggerFactory.getLogger(CachedDAOImpl.class);

    public CachedDAOImpl(Class<T> clazz, CouchDbICureConnector couchDb, IDGenerator idGenerator, CacheManager cacheManager) {
        super(clazz, couchDb, idGenerator);
        this.cache = cacheManager.getCache(entityClass.getName());
        log.debug("Cache impl = {}", this.cache.getNativeCache());
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
            String fullId = getFullId(id);
            Cache.ValueWrapper value = cache.get(fullId);

            if (value != null) {
                T o = (T) value.get();
                if (o != null) {
                    log.debug("Cache HIT  = {}, {} - {}", fullId, o.getId(), o.getRev());
                    result.add(o);
                } else {
                    log.debug("Cache HIT  = {}, Null value", fullId);
                }
            } else {
                log.debug("Cache MISS = {}", fullId);
                missingKeys.add(id);
            }
        }

        // Get missing values from storage
        if (!missingKeys.isEmpty()) {
            List<T> entities = super.getList(missingKeys).stream().filter(Objects::nonNull).collect(Collectors.toList());
            for (T e : entities) {
                String fullId = getFullId(keyManager.getKey(e));

                log.debug("Cache SAVE = {}, {} - {}", fullId, e.getId(), e.getRev());
                cache.put(fullId, e);
            }
            result.addAll(entities);
        }
        return result;
    }

    @Override
    public T get(String id, Option... options) {
        String fullId = getFullId(id);
        Cache.ValueWrapper value = cache.get(fullId);
        if (value == null) {
            log.debug("Cache MISS = {}", fullId);
            T e = super.get(id, options);
            log.debug("Cache SAVE = {}, {} - {}", fullId, e.getId(), e.getRev());
            cache.put(fullId, e);
            return e;
        } else {
            T o = (T) value.get();
            if (o != null) {
                log.debug("Cache HIT  = {}, {} - {}", fullId, o.getId(), o.getRev());
            } else {
                log.debug("Cache HIT  = {}, Null value", fullId);
            }
            return o;
        }
    }

    @Override
    public T find(String id, Option... options) {
        String fullId = getFullId(id);
        Cache.ValueWrapper value = cache.get(fullId);
        if (value == null) {
            log.debug("Cache MISS = {}", fullId);
            T e = super.find(id, options);
            log.debug("Cache SAVE = {}, {} - {}", fullId, e.getId(), e.getRev());
            cache.put(fullId, e);
            return e;
        } else {
            T o = (T) value.get();
            if (o != null) {
                log.debug("Cache HIT  = {}, {} - {}", fullId, o.getId(), o.getRev());
            } else {
                log.debug("Cache HIT  = {}, Null value", fullId);
            }
            return o;
        }
    }

    public T getFromCache(String id) {
        String fullId = getFullId(id);
        Cache.ValueWrapper value = cache.get(fullId);
        if (value == null) {
            log.debug("Cache MISS = {}", fullId);
            return null;
        } else {
            T o = (T) value.get();
            if (o != null) {
                log.debug("Cache HIT  = {}, {} - {}", fullId, o.getId(), o.getRev());
            } else {
                log.debug("Cache HIT  = {}, Null value", fullId);
            }
            return o;
        }
    }

	public void putInCache(String key, T value) {
        String fullId = getFullId(key);
        if (value != null) { log.debug("Cache SAVE = {}, {} - {}", fullId, value.getId(), value.getRev()); } else {
            log.debug("Cache SAVE = {}, null placeholder", fullId);
        }
        cache.put(fullId, value);
    }

    public void putInCache(String groupId, String dbInstanceUrl, String key, T value) {
        String fullId = (groupId == null ? "FALLBACK" : (((CouchDbICureConnector) this.db).getCouchDbICureConnector(groupId, dbInstanceUrl).getUuid())) + ":" + key;
        if (value != null) { log.debug("Cache SAVE = {}, {} - {}", fullId, value.getId(), value.getRev()); } else {
            log.debug("Cache SAVE = {}, null placeholder", fullId);
        }
        cache.put(fullId, value);
    }


    public void evictFromCache(T entity) {
        String fullId = getFullId(keyManager.getKey(entity));
        String fullId1 = getFullId(ALL_ENTITIES_CACHE_KEY);
        log.debug("Cache EVICT= {}", fullId);
        log.debug("Cache EVICT= {}", fullId1);
        cache.evict(fullId);
        cache.evict(fullId1);
	}

    public void evictFromCache(String groupId, String dbInstanceUrl, String id) {
        String fullId = (groupId == null ? "FALLBACK" : (((CouchDbICureConnector) this.db).getCouchDbICureConnector(groupId, dbInstanceUrl).getUuid())) + ":" + id;
        String fullId1 = (groupId == null ? "FALLBACK" : (((CouchDbICureConnector) this.db).getCouchDbICureConnector(groupId, dbInstanceUrl).getUuid())) + ":" + ALL_ENTITIES_CACHE_KEY;
        log.debug("Cache EVICT= {}", fullId);
        log.debug("Cache EVICT= {}", fullId1);
        cache.evict(fullId);
        cache.evict(fullId1);
    }

    public void evictFromCache(String id) {
        log.debug("Cache EVICT= {}", id);
        cache.evict(id);
    }

    protected Cache.ValueWrapper getWrapperFromCache(String groupId, String dbInstanceUrl, String id) {
        String fullId = (groupId == null ? "FALLBACK" : (((CouchDbICureConnector) this.db).getCouchDbICureConnector(groupId, dbInstanceUrl).getUuid())) + ":" + id;
        Cache.ValueWrapper value = cache.get(fullId);
        if (value != null) {
            log.debug("Cache HIT  = {}, WRAPPER", fullId);
        } else {
            log.debug("Cache MISS = {}, WRAPPER", fullId);
        }

        return value;
    }

    protected Cache.ValueWrapper getWrapperFromCache(String id) {
        String fullId = getFullId(id);
        Cache.ValueWrapper value = cache.get(fullId);

        if (value != null) {
            log.debug("Cache HIT  = {}, WRAPPER", fullId);
        } else {
            log.debug("Cache MISS = {}, WRAPPER", fullId);
        }

        return value;
	}

	@Override
    public List<T> getAll() {
        String fullId = getFullId(ALL_ENTITIES_CACHE_KEY);

        Cache.ValueWrapper valueWrapper = cache.get(fullId);
        if (valueWrapper == null) {
            log.debug("Cache MISS = {}", fullId);
            List<T> allEntities = super.getAll();
            cache.put(fullId, allEntities);
            log.debug("Cache SAVE = {}", fullId);
            return allEntities;
        } else {
            log.debug("Cache HIT  = {}", fullId);

            return (List<T>) valueWrapper.get();
        }
    }

    @Override
    protected T save(Boolean newEntity, T entity) {
        String fullId1 = getFullId(ALL_ENTITIES_CACHE_KEY);
        try {
            entity = super.save(newEntity, entity);
        } catch (UpdateConflictException e) {
            String fullId = getFullId(keyManager.getKey(entity));

            log.debug("Cache EVICT= {}", fullId);
            log.debug("Cache EVICT= {}", fullId1);

            cache.evict(fullId);
            cache.evict(fullId1);

            throw e;
        }
        putInCache(keyManager.getKey(entity), entity);
        cache.evict(fullId1);
        log.debug("Cache EVICT= {}", fullId1);

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
        String fullId1 = getFullId(ALL_ENTITIES_CACHE_KEY);
        try {
            entities = super.save(newEntity, entities);
        } catch (UpdateConflictException | BulkUpdateConflictException e) {
            for (T entity:entities) {
                String fullId = getFullId(keyManager.getKey(entity));
                log.debug("Cache EVICT= {}", fullId);
                cache.evict(fullId);
            }

            log.debug("Cache EVICT= {}", fullId1);
            cache.evict(fullId1);

            throw e;
        }
        for (T entity:entities) {
			putInCache(keyManager.getKey(entity), entity);
        }
        cache.evict(fullId1);
        log.debug("Cache EVICT= {}", fullId1);

        return entities;
    }
}
