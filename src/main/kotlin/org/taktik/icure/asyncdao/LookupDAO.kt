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

package org.taktik.icure.asyncdao

import org.taktik.icure.dao.Option
import org.taktik.icure.entities.base.Identifiable
import java.net.URI

interface LookupDAO<T : Identifiable<String>> {
    /**
     * Get an existing entity
     *
     * @param dbInstanceUrl URI of the couchdb cluster
     * @param groupId of the targeted db
     * @param id Id of the entity to get
     * @param options Any eventual option for fetching the entity. Used if you need to retrieve conflicting revisions,
     * revisions' history, etc...
     * @return The entity
     */
    suspend fun get(id: String, vararg options: Option): T?

    /**
     * Gets a specific revision of an existing entity
     *
     * @param dbInstanceUrl URI of the couchdb cluster
     * @param groupId of the targeted db
     * @param id Id of the entity to get
     * @param options Any eventual option for fetching the entity. Used if you need to retrieve conflicting revisions,
     * revisions' history, etc...
     * @return The entity
     */
    suspend fun get(id: String, rev: String?, vararg options: Option): T?

    /**
     * Save entity and indicate it is a new entity
     *
     * @param dbInstanceUrl URI of the couchdb cluster
     * @param groupId of the targeted db
     * @param entity The entity to save
     * @return Returns the saved entity
     */
    suspend fun create(entity: T): T?

    /**
     * Save entity without knowing if it's a new entity or not
     *
     * @param dbInstanceUrl URI of the couchdb cluster
     * @param groupId of the targeted db
     * @param entity The entity to save
     * @return Returns the saved entity
     */
    suspend fun save(entity: T): T?
}
