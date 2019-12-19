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

import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.dao.EntityTemplateDAO;
import org.taktik.icure.entities.EntityTemplate;
import org.taktik.icure.entities.base.StoredDocument;
import org.taktik.icure.logic.EntityTemplateLogic;
import org.taktik.icure.validation.aspect.Check;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class EntityTemplateLogicImpl implements EntityTemplateLogic {
	EntityTemplateDAO entityTemplateDAO;

	@Override
	public EntityTemplate createEntityTemplate(EntityTemplate entityTemplate) {
		List<EntityTemplate> createdEntityTemplates = new ArrayList<>(1);
		try {
			this.createEntities(Collections.singleton(entityTemplate), createdEntityTemplates);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid template",e);
		}

		return createdEntityTemplates.size()>0?createdEntityTemplates.get(0):null;
	}

	@Override
	public EntityTemplate modifyEntityTemplate(EntityTemplate entityTemplate) {
		Set<EntityTemplate> entityTemplates = Collections.singleton(entityTemplate);
		try {
			this.updateEntities(entityTemplates);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid template",e);
		}
		return entityTemplates.iterator().next();
	}

	@Override
	public boolean createEntities(@Check Collection<EntityTemplate> entities, Collection<EntityTemplate> createdEntities) throws Exception {
		return createdEntities.addAll(entityTemplateDAO.create(entities));
	}

	@Override
	public List<EntityTemplate> updateEntities(@Check Collection<EntityTemplate> entities) throws Exception {
		return new ArrayList<>(entityTemplateDAO.save(entities));
	}

	@Override
	public void deleteEntities(Collection<String> identifiers) throws Exception {
		entityTemplateDAO.removeByIds(identifiers);
	}

	@Override
	public void undeleteEntities(Collection<String> identifiers) throws Exception {
		entityTemplateDAO.unremoveByIds(identifiers);
	}

	@Override
	public List<EntityTemplate> getAllEntities() {
		return entityTemplateDAO.getAll();
	}

	@Override
	public List<String> getAllEntityIds() {
		return entityTemplateDAO.getAll().stream().map(StoredDocument::getId).collect(Collectors.toList());
	}

	@Override
	public boolean exists(String id) {
		return entityTemplateDAO.contains(id);
	}

	@Override
	public EntityTemplate getEntity(String id) {
		return getEntityTemplate(id);
	}

	@Override
	public boolean hasEntities() {
		return entityTemplateDAO.hasAny();
	}

	@Autowired
	public void setEntityTemplateDAO(EntityTemplateDAO entityTemplateDAO) {
		this.entityTemplateDAO = entityTemplateDAO;
	}

	@Override
	public EntityTemplate getEntityTemplate(String id) {
		return entityTemplateDAO.get(id);
	}

	@Override
	public List<EntityTemplate> getEntityTemplates(Collection<String> selectedIds) {
		return entityTemplateDAO.getList(selectedIds);
	}

	@Override
	public List<EntityTemplate> findEntityTemplates(String userId, String entityType, String searchString, Boolean includeEntities) {
		return entityTemplateDAO.getByUserIdTypeDescr(userId,entityType,searchString,includeEntities);
	}

	@Override
	public List<EntityTemplate> findAllEntityTemplates(String entityType, String searchString, Boolean includeEntities) {
		return entityTemplateDAO.getByTypeDescr(entityType,searchString,includeEntities);
	}
}
