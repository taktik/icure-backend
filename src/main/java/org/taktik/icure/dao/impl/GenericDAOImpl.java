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

import org.apache.commons.lang3.ArrayUtils;
import org.ektorp.AttachmentInputStream;
import org.ektorp.BulkDeleteDocument;
import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.DocumentOperationResult;
import org.ektorp.Options;
import org.ektorp.UpdateConflictException;
import org.ektorp.ViewQuery;
import org.ektorp.support.DesignDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.taktik.icure.dao.GenericDAO;
import org.taktik.icure.dao.Option;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator;
import org.taktik.icure.dao.impl.keymanagers.KeyManager;
import org.taktik.icure.dao.impl.keymanagers.UniversallyUniquelyIdentifiableKeyManager;
import org.taktik.icure.entities.base.StoredDocument;
import org.taktik.icure.exceptions.BulkUpdateConflictException;

import javax.persistence.PersistenceException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class GenericDAOImpl<T extends StoredDocument> extends CouchDbICureRepositorySupport<T> implements GenericDAO<T> {
	private static final Logger log = LoggerFactory.getLogger(GenericDAOImpl.class);

	protected final Class<T> entityClass;
	protected final KeyManager<T, String> keyManager;
	protected UUIDGenerator uuidGenerator;

	public void setUuidGenerator(UUIDGenerator uuidGenerator) {
		this.uuidGenerator = uuidGenerator;
	}

	public GenericDAOImpl(Class<T> entityClass, @Qualifier("couchdb") CouchDbICureConnector db, IDGenerator idGenerator) {
		super(entityClass, db);
		this.entityClass = entityClass;
		this.keyManager = new UniversallyUniquelyIdentifiableKeyManager<>(idGenerator);
	}

	protected Map<String, Object> getStorageOptions() {
		return null;
	}

	@Override
	public boolean contains(String id) {
		if (log.isDebugEnabled()) {
			log.debug(entityClass.getSimpleName() + ".contains: " + id);
		}
		return super.contains(id);
	}

	@Override
	public boolean hasAny() {
		return designDocContainsAllView() && db.queryView(createQuery("all").limit(1)).getSize() > 0;
	}

	@Override
	public List<T> getAll() {
		if (log.isDebugEnabled()) {
			log.debug(entityClass.getSimpleName() + ".getAll");
		}
		List<T> result = super.getAll();

		result.forEach(this::postLoad);

		return result;
	}

	@Override
	public String getAttachment(String documentId, String attachmentId) {
		AttachmentInputStream attachmentInputStream = db.getAttachment(documentId, attachmentId);
		BufferedReader reader = new BufferedReader(new InputStreamReader(attachmentInputStream));

		@SuppressWarnings("MismatchedQueryAndUpdateOfStringBuilder") StringBuilder sb = new StringBuilder();
		reader.lines().forEach(sb::append);
		return sb.toString();
	}

	@Override
	public AttachmentInputStream getAttachmentInputStream(String documentId, String attachmentId, String rev) {
		return rev != null ? db.getAttachment(documentId, attachmentId, rev):db.getAttachment(documentId, attachmentId);
	}

	@Override
	public String createAttachment(String documentId, String rev, AttachmentInputStream data) {
		// return Document Revision
		return db.createAttachment(documentId, rev, data);
	}

	@Override
	public String deleteAttachment(String documentId, String rev, String attachmentId) {
		// return Document Revision
		return db.deleteAttachment(documentId, rev, attachmentId);
	}

	@Override
	public T get(String id, Option... options) {
		if (log.isDebugEnabled()) {
			log.debug(entityClass.getSimpleName() + ".get: " + id + " [" + ArrayUtils.toString(options) + "]");
		}
		try {
			T result = super.get(id, asEktorpOptions(options));

			postLoad(result);

			return result;
		} catch (DocumentNotFoundException e) {
			log.warn("Document not found",e);
		}
		return null;
	}

	public T get(String id, String rev) {
		try {
			T result = super.get(id, rev);

			postLoad(result);

			return result;
		} catch (DocumentNotFoundException e) {
			log.warn("Document not found",e);
		}
		return null;
	}

	public T get(String id) {
		try {
			T result = super.get(id);

			postLoad(result);

			return result;
		} catch (DocumentNotFoundException e) {
			log.warn("Document not found",e);
		}
		return null;
	}


	@Override
	public Set<T> getSet(Collection<String> ids) {
		if (log.isDebugEnabled()) {
			log.debug(entityClass.getSimpleName() + ".get: " + ids);
		}
		return new HashSet<>(getList(ids));
	}

	@Override
	public List<T> getList(Collection<String> ids) {
		if (log.isDebugEnabled()) {
			log.debug(entityClass.getSimpleName() + ".get: " + ids);
		}
		ViewQuery q = new ViewQuery()
				.allDocs()
				.includeDocs(true)
				.keys(ids);
		q.setIgnoreNotFound(true);
		List<T> result = queryResults(q);

		result.forEach(this::postLoad);

		return result;
	}

	@Override
	public T newInstance() {
		// Instantiate new entity
		T entity;
		try {
			entity = entityClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Could not instantiate entity of class " + entityClass.getName(), e);
		}

		// Set new key
		keyManager.setNewKey(entity, entityClass.getSimpleName());

		return entity;
	}

	@Override
	public T create(T entity) {
		return save(true, entity);
	}

	@Override
	public T save(T entity) {
		return save(null, entity);
	}

	protected T save(Boolean newEntity, T entity) {
		if (entity != null) {
			if (log.isDebugEnabled()) {
				log.debug(entityClass.getSimpleName() + ".save: " + entity.getId() + ":" + entity.getRev());
			}

			// Before save
			beforeSave(entity);

			// Check if key is missing and if this is a new entity
			boolean missingKey = (entity.getId() == null);

			// Add new key if missing
			if (missingKey) {
				keyManager.setNewKey(entity, entityClass.getSimpleName());
				newEntity = true;
			} else {
				if (newEntity == null) {
					newEntity = entity.getRev() == null;
				}
			}

			if (!newEntity) {
				//saveRevHistory(entity, null);
			}

			// Save entity
			super.update(entity);

			// After save
			afterSave(entity);
		}

		return entity;
	}

	protected void beforeSave(T entity) {
	}

	protected void afterSave(T entity) {
	}

	@Override
	public void remove(T entity) {
		if (entity != null) {
			if (log.isDebugEnabled()) {
				log.debug(entityClass.getSimpleName() + ".remove: " + entity);
			}
			// Before remove
			beforeDelete(entity);
			// Delete
			super.remove(entity);
			// After remove
			afterDelete(entity);
		}
	}

	@Override
	public void unremove(T entity) {
		if (entity != null) {
			if (log.isDebugEnabled()) {
				log.debug(entityClass.getSimpleName() + ".unremove: " + entity);
			}
			beforeUnDelete(entity);
			super.unremove(entity);
			afterUnDelete(entity);
		}
	}

	@Override
	public void purge(T entity) {
		if (entity != null) {
			if (log.isDebugEnabled()) {
				log.debug(entityClass.getSimpleName() + ".remove: " + entity);
			}
			// Before remove
			beforeDelete(entity);
			// Delete
			super.purge(entity);
			// After remove
			afterDelete(entity);
		}
	}


	@Override
	public void removeById(String id) {
		if (id != null) {
			if (log.isDebugEnabled()) {
				log.debug(entityClass.getSimpleName() + ".removeById: " + id);
			}
			// Get entity by id
			T entity = get(id);
			// Delete entity
			remove(entity);
		}
	}

	@Override
	public void unremoveById(String id) {
		if (id != null) {
			if (log.isDebugEnabled()) {
				log.debug(entityClass.getSimpleName() + ".unremoveById: " + id);
			}
			// Get entity by id
			T entity = get(id);
			// Delete entity
			unremove(entity);
		}
	}

	@Override
	public void purgeById(String id) throws PersistenceException {
		if (id != null) {
			if (log.isDebugEnabled()) {
				log.debug(entityClass.getSimpleName() + ".removeById: " + id);
			}
			// Get entity by id
			T entity = get(id);
			// Purge entity
			purge(entity);
		}
	}

	@Override
	public void remove(Collection<T> entities) throws PersistenceException {
		if (log.isDebugEnabled()) {
			log.debug("remove " + entities);
		}
		try {
			for (T entity : entities) {
				beforeDelete(entity);
				entity.setDeletionDate(System.currentTimeMillis());
			}
			db.executeBulk(entities);
			for (T entity : entities) {
				afterDelete(entity);
			}
		} catch (Exception e) {
			throw new PersistenceException("failed to remove entities ", e);
		}
	}

	@Override
	public void unremove(Collection<T> entities) throws PersistenceException {
		if (log.isDebugEnabled()) {
			log.debug("unremove " + entities);
		}
		try {
			for (T entity : entities) {
				beforeUnDelete(entity);
				entity.setDeletionDate(null);
			}
			db.executeBulk(entities);
			for (T entity : entities) {
				afterUnDelete(entity);
			}
		} catch (Exception e) {
			throw new PersistenceException("failed to unremove entities ", e);
		}
	}

	@Override
	public void purge(Collection<T> entities) throws PersistenceException {
		if (log.isDebugEnabled()) {
			log.debug("remove " + entities);
		}
		try {
			for (T entity : entities) {
				beforeDelete(entity);
			}
			db.executeBulk(entities.stream().map(BulkDeleteDocument::of).collect(Collectors.toList()));
			for (T entity : entities) {
				afterDelete(entity);
			}
		} catch (Exception e) {
			throw new PersistenceException("failed to remove entities ", e);
		}
	}

	@Override
	public void purgeByIds(Collection<String> ids) throws PersistenceException {
		purge(getList(ids));
	}

	@Override
	public void removeByIds(Collection<String> ids) throws PersistenceException {
		remove(getList(ids));
	}

	@Override
	public void unremoveByIds(Collection<String> ids) throws PersistenceException {
		unremove(getList(ids));
	}

	@Override
	public <K extends Collection<T>> K create(K entities) {
		return save(true, entities);
	}

	@Override
	public <K extends Collection<T>> K save(K entities) {
		return save(null, entities);
	}

	protected <K extends Collection<T>> K save(Boolean newEntity, K entities) {
		if (entities != null) {
			if (log.isDebugEnabled()) {
				log.debug(entityClass.getSimpleName() + ".save: " + entities.stream().filter(Objects::nonNull).map(entity->entity.getId() + ":" + entity.getRev()).collect(Collectors.joining(",")));
			}

			List<T> updatedEntities = new ArrayList<>();

			// Before save
			for (T entity : entities) {
				beforeSave(entity);

				// Check if key is missing and if this is a new entity
				boolean missingKey = (entity.getId() == null);
				newEntity = (newEntity != null) ? newEntity : (missingKey || entity.getRev()==null);

				// Add new key if missing
				if (missingKey) {
					keyManager.setNewKey(entity, entityClass.getSimpleName());
				}

				if (!newEntity) {
					updatedEntities.add(entity);
				}
			}

			//final Map<String,T> previousEntities = getList(updatedEntities.stream().map(T::getId).collect(Collectors.toList())).stream().collect(Collectors.toMap(T::getId,Function.<T>identity()));
			//updatedEntities.forEach((e)->saveRevHistory(e,previousEntities.get(e.getId())));

            // Save entity
			List<T> orderedEntities = new ArrayList<>(entities);
			List<DocumentOperationResult> results = db.executeBulk(orderedEntities);

			List<org.taktik.icure.exceptions.UpdateConflictException> conflicts = new ArrayList<>();
			for (DocumentOperationResult r : results) {
				if (r.getError() != null && r.getError().equals("conflict")) {
					conflicts.add(new org.taktik.icure.exceptions.UpdateConflictException(orderedEntities.stream().filter(e->e.getId().equals(r.getId())).findAny().orElse(null)));
				}
			}
			if (conflicts.size()>0) {
				throw new BulkUpdateConflictException(conflicts,orderedEntities);
			}

			orderedEntities.stream().filter(e -> e.getRev() != null).forEach(this::afterSave);
		}

		return entities;
	}

	@Override
	public void visitAll(final Function<T, Boolean> callback) throws PersistenceException {
		log.debug("visitAll");
		try {
			ViewQuery q = new ViewQuery()
					.allDocs()
					.includeDocs(true);
			db.queryView(q, entityClass).stream().forEach((t) -> callback.apply(refresh(t)));
		} catch (Exception e) {
			throw new PersistenceException("Failed to fetch all entites", e);
		}
	}

	protected void beforeDelete(T entity) {
	}

	protected void afterDelete(T entity) {
	}

	protected void beforeUnDelete(T entity) {
	}

	protected void afterUnDelete(T entity) {
	}

	public void postLoad(T entity) {
		doFetchRelationship(entity);
	}

	protected void doFetchRelationship(T object) {
	}

	@Override
	public void refreshIndex() {
		try {
			this.hasAny();
		} catch (Exception ignored) {}
	}

	@Override
	public List<String> getAllIds() {
		if (log.isDebugEnabled()) {
			log.debug(entityClass.getSimpleName() + ".getAllIds");
		}
		if (designDocContainsAllView()) {
			return db.queryView(createQuery("all").includeDocs(false), String.class);
		}

		return new ArrayList<>();
	}

    @Override
    public void warmupIndex(){
        if (log.isDebugEnabled()) {
            log.debug(entityClass.getSimpleName() + ".getAllIds");
        }
        if (designDocContainsAllView()) {
            db.queryView(createQuery("all").includeDocs(false).limit(1), String.class);
        }
    }

	private Options asEktorpOptions(Option... options) {
		Options ektorpOptions = new Options();
		for (Option option : options) {
			ektorpOptions.param(option.paramName(), "true");
		}
		return ektorpOptions;
	}

	public void initStandardDesignDocument(String groupId) {
		if (groupId==null || !(db instanceof CouchDbICureConnector)) {
			this.initStandardDesignDocument();
		} else {
			initDesignDocInternal(groupId,0);
		}
	}

	private void initDesignDocInternal(String groupId, int invocations) {
		CouchDbConnector cdb = (db instanceof CouchDbICureConnector) ? ((CouchDbICureConnector) db).getCouchDbICureConnector(groupId) : db;
		DesignDocument designDoc;
		if (cdb.contains(stdDesignDocumentId)) {
			designDoc = getDesignDocumentFactory().getFromDatabase(cdb, stdDesignDocumentId);
		} else {
			designDoc = getDesignDocumentFactory().newDesignDocumentInstance();
			designDoc.setId(stdDesignDocumentId);
		}
		log.debug("Generating DesignDocument for {}", type);
		DesignDocument generated = getDesignDocumentFactory().generateFrom(this);
		boolean changed = designDoc.mergeWith(generated, false);
		if (log.isDebugEnabled()) {
			debugDesignDoc(designDoc);
		}
		if (changed) {
			log.debug("DesignDocument changed or new. Updating database");
			try {
				cdb.update(designDoc);
			} catch (UpdateConflictException e) {
				log.warn("Update conflict occurred when trying to update design document: {}", designDoc.getId());
				if (invocations == 0) {
					backOff();
					log.info("retrying initStandardDesignDocument for design document: {}", designDoc.getId());
					initDesignDocInternal(groupId, 1);
				}
			}
		} else {
			log.debug("DesignDocument was unchanged. Database was not updated.");
		}
	}
}
