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

import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.Filter;
import org.taktik.icure.entities.base.StoredDocument;

import java.io.Serializable;
import java.util.List;

public interface FilterDAO extends GenericDAO<Filter> {

    List<Filter> findByEntity(String entity);

    Filter findByNameAndEntity(String name, String entity);

    void addFilterView(Filter filter);

    <T extends StoredDocument> List<T> applyFilter(Filter filter, Serializable argument, PaginationOffset offset, Integer limit, Class<T> clazz);

    <T extends StoredDocument> List<T> applyFilter(Filter filter, Serializable key, Serializable startKey, Serializable endKey, Integer limit, Class<T> clazz);

    long countOfFilter(Filter filter, Serializable argument);

	List<Filter> findByEntity(String entityName, String userId);
}
