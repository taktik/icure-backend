/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.dao;

import org.taktik.icure.entities.base.Identifiable;

public interface LookupDAO<T extends Identifiable<String>> {
	/**
	 * Get an existing entity
	 *
	 * @param id Id of the entity to get
	 * @param options Any eventual option for fetching the entity. Used if you need to retrieve conflicting revisions,
	 *                   revisions' history, etc...
	 * @return The entity
	 */
	T get(String id, Option... options);

	/**
	 * Gets a specific revision of an existing entity
	 */
	T get(String id, String rev);

	/**
	 * Create new instance of entity and generate a new key for it
	 *
	 * @return The new instance of entity
	 */
	T newInstance();

	/**
	 * Save entity and indicate it is a new entity
	 *
	 * @param entity The entity to save
	 * @return Returns the saved entity
	 */
	T create(T entity);

	/**
	 * Save entity without knowing if it's a new entity or not
	 *
	 * @param entity The entity to save
	 * @return Returns the saved entity
	 */
	T save(T entity);
}