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

package org.taktik.icure.logic;

import java.util.Collection;
import java.util.List;

import org.taktik.icure.entities.EntityTemplate;

public interface EntityTemplateLogic extends EntityPersister<EntityTemplate, String> {
	EntityTemplate createEntityTemplate(EntityTemplate entityTemplate);

	EntityTemplate modifyEntityTemplate(EntityTemplate entityTemplate);

	EntityTemplate getEntityTemplate(String id);

	List<EntityTemplate> getEntityTemplates(Collection<String> selectedIds);

	List<EntityTemplate> findEntityTemplates(String userId, String entityType, String searchString, Boolean includeEntities);

	List<EntityTemplate> findAllEntityTemplates(String entityType, String searchString, Boolean includeEntities);
}
