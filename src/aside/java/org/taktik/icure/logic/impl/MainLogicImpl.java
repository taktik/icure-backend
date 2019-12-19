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

package org.taktik.icure.logic.impl;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.comparators.ComparableComparator;
import org.apache.commons.collections4.comparators.ComparatorChain;
import org.apache.commons.collections4.comparators.NullComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.taktik.commons.collections.SortDir;
import org.taktik.commons.collections.SortOrder;
import org.taktik.icure.entities.*;
import org.taktik.icure.logic.*;

import java.io.Serializable;
import java.util.*;

@Service
public class MainLogicImpl implements MainLogic {
	private static final Logger log = LoggerFactory.getLogger(MainLogicImpl.class);

	private Set<EntityPersisterInfos<?, ?>> entityPersisterInfosList = new HashSet<EntityPersisterInfos<?, ?>>();

	private ReplicationLogic replicationLogic;
	private RoleLogic roleLogic;
	private UserLogic userLogic;
	private FormLogic formLogic;
	private EntityTemplateLogic entityTemplateLogic;

    //	private EntityPersister<Hotfolder, String> hotfolderLogic;
//	private EntityPersister<Form, String> formLogic;
//	private EntityPersister<String, String> templateLogic;

	private static class EntityPersisterInfos<E, I> {
		private EntityPersister<E, I> persister;
		private Class<E> entityClass;
		private Class<I> identifierClass;

		public EntityPersisterInfos(EntityPersister<E, I> persiter, Class<E> entityClass, Class<I> identifierClass) {
			this.persister = persiter;
			this.entityClass = entityClass;
			this.identifierClass = identifierClass;
		}

		public EntityPersister<E, I> getPersister() {
			return persister;
		}

		public Class<E> getEntityClass() {
			return entityClass;
		}

		public Class<I> getIdentifierClass() {
			return identifierClass;
		}
	}

	protected <E, I> void registerEntityPersister(EntityPersister<E, I> persister, Class<E> entityClass, Class<I> identifierClass) {
		// Check parameters
		if (persister == null || entityClass == null || identifierClass == null) {
			return;
		}

		entityPersisterInfosList.add(new EntityPersisterInfos<E, I>(persister, entityClass, identifierClass));
	}

	@SuppressWarnings("unchecked")
	private <E, I> EntityPersister<E, I> getEntityPersister(Class<E> entityClass) {
		for (EntityPersisterInfos<?, ?> entityPersisterInfos : entityPersisterInfosList) {
			if (entityPersisterInfos.getEntityClass().equals(entityClass)) {
				return (EntityPersister<E, I>) entityPersisterInfos.getPersister();
			}
		}

		log.error("Could not find an entity persister for " + entityClass.getSimpleName() + " !");
		return null;
	}

	private <E> String getEntityClassName(Class<E> entityClass, Boolean plural) {
		return entityClass.getSimpleName().substring(0, 1).toLowerCase() + entityClass.getSimpleName().substring(1) + (plural == null ? "(s)" : plural ? "s" : "");
	}

    @Override
    public <E extends Serializable>E get(Class<E> c, String id) {
		EntityPersister<E, ?> entityPersister = getEntityPersister(c);
		return entityPersister.getEntity(id);
    }

    @Override
	public <E> int getEntitiesCount(Class<E> entityClass, Predicate<E> predicate) {
		if (log.isTraceEnabled()) {
			log.trace("Counting " + getEntityClassName(entityClass, null) + " using predicate : " + predicate);
		}

		EntityPersister<E, ?> entityPersister = getEntityPersister(entityClass);
		List<E> entities = entityPersister.getAllEntities();
		CollectionUtils.filter(entities, predicate);
		int entitiesCount = entities.size();

		if (log.isTraceEnabled()) {
			log.trace("Counted " + entitiesCount + " " + getEntityClassName(entityClass, entitiesCount > 1));
		}
		return entitiesCount;
	}

	@Override
	public <E> List<E> getEntities(Class<E> entityClass, Predicate<E> predicate, Integer offset, Integer limit, List<SortOrder<String>> sortOrders) {
		if (log.isTraceEnabled()) {
			log.trace("Getting " + getEntityClassName(entityClass, null) + " (offset=" + offset + ", limit=" + limit + ", sortOrders=" + ((sortOrders != null && !sortOrders.isEmpty()) ? (Arrays.toString(sortOrders.toArray(new SortOrder[sortOrders.size()]))) : "/") + ")" + " using predicate : " + predicate);
		}

		EntityPersister<E, ?> entityPersister = getEntityPersister(entityClass);
		List<E> entities = entityPersister.getAllEntities();
		if (entities == null) {
			return Collections.emptyList();
		}
		CollectionUtils.filter(entities, predicate);
		sort(entities, sortOrders);

		if (offset != null && limit != null) {
			if (offset < entities.size()) {
				entities = entities.subList(offset, Math.min(entities.size(), offset + limit));
			} else {
				entities = Collections.emptyList();
			}
		}

		if (log.isTraceEnabled()) {
			log.trace("Returned " + entities.size() + " " + getEntityClassName(entityClass, entities.size() > 1));
		}
		return entities;
	}

	private <E> void sort(List<E> entities, List<SortOrder<String>> sortOrders) {
		if (sortOrders != null && sortOrders.size() > 0) {
			ComparatorChain<E> comparatorChain = new ComparatorChain<>();
			for (SortOrder<String> sortOrder : sortOrders) {
				comparatorChain.addComparator(new BeanComparator<>(sortOrder.getKey(), new NullComparator<>(ComparableComparator.comparableComparator(), true)), sortOrder.getDirection() == SortDir.DESC);
			}
			Collections.sort(entities, comparatorChain);
		}
	}

	@Override
	public <E> List<E> createEntities(Class<E> entityClass, List<E> entities) throws Exception {
		// Check parameters
		if (entityClass == null || entities == null) {
			return null;
		}

		// Get entity persister
		EntityPersister<E, ?> entityPersister = getEntityPersister(entityClass);
		if (entityPersister == null) {
			return null;
		}

		// Create entities
		List<E> createdEntities = new ArrayList<E>();
		entityPersister.createEntities(entities, createdEntities);

		if (log.isTraceEnabled()) {
			log.trace("Created " + createdEntities.size() + " " + getEntityClassName(entityClass, createdEntities.size() > 1));
		}
		return createdEntities;
	}

	@Override
	public <E> void updateEntities(Class<E> entityClass, Set<E> entities) throws Exception {
		// Check parameters
		if (entityClass == null || entities == null) {
			return;
		}

		// Get entity persister
		EntityPersister<E, ?> entityPersister = getEntityPersister(entityClass);
		if (entityPersister == null) {
			return;
		}

		// Update entities
		entityPersister.updateEntities(entities);
		if (log.isTraceEnabled()) {
			log.trace("Updated " + entities.size() + " " + getEntityClassName(entityClass, entities.size() > 1));
		}
	}

	@Override
	public <E, I> void deleteEntities(Class<E> entityClass, Class<I> entityIdentifierClass, Set<I> entityIdentifiers) throws Exception {
		// Check parameters
		if (entityClass == null || entityIdentifierClass == null || entityIdentifiers == null) {
			return;
		}

		// Get entity persister
		EntityPersister<E, I> entityPersister = getEntityPersister(entityClass);
		if (entityPersister == null) {
			return;
		}

		// Delete entities
		entityPersister.deleteEntities(entityIdentifiers);
		if (log.isTraceEnabled()) {
			log.trace("Deleted " + entityIdentifiers.size() + " " + getEntityClassName(entityClass, entityIdentifiers.size() > 1));
		}
	}

	@Override
	public <E> List<E> getReplicatedObjects(List<E> objects) {
		return objects;
	}

	public void init() {
		registerEntityPersister(roleLogic, Role.class, String.class);
		registerEntityPersister(userLogic, User.class, String.class);
		registerEntityPersister(formLogic, Form.class, String.class);
		registerEntityPersister(replicationLogic, Replication.class, String.class);
		registerEntityPersister(entityTemplateLogic, EntityTemplate.class, String.class);
	}

	@Autowired
	public void setReplicationLogic(ReplicationLogic replicationLogic) {
		this.replicationLogic = replicationLogic;
	}

	@Autowired
	public void setFormLogic(FormLogic formLogic) {
		this.formLogic = formLogic;
	}

	@Autowired
	public void setEntityTemplateLogic(EntityTemplateLogic entityTemplateLogic) {
		this.entityTemplateLogic = entityTemplateLogic;
	}

	@Autowired
	public void setRoleLogic(RoleLogic roleLogic) {
		this.roleLogic = roleLogic;
	}
	@Autowired
	public void setUserLogic(UserLogic userLogic) {
		this.userLogic = userLogic;
	}
}