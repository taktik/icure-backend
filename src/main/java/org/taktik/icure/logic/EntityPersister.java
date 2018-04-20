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

import org.taktik.icure.validation.aspect.Check;

import java.util.Collection;
import java.util.List;

public interface EntityPersister<E, I> {

	boolean createEntities(@Check Collection<E> entities, Collection<E> createdEntities) throws Exception;

	List<E> updateEntities(@Check Collection<E> entities) throws Exception;

	void deleteEntities(Collection<I> identifiers) throws Exception;

	void undeleteEntities(Collection<I> identifiers) throws Exception;

	List<E> getAllEntities();
	List<String> getAllEntityIds();

	boolean hasEntities();

	boolean exists(String id);

	E getEntity(String id);
}
