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

package org.taktik.icure.logic;

import org.apache.commons.collections4.Predicate;
import org.taktik.commons.collections.SortOrder;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public interface MainLogic {
    <E extends Serializable>E get(Class<E> c, String id);

    <E> int getEntitiesCount(Class<E> entityClass, Predicate<E> predicate);

	<E> List<E> getEntities(Class<E> entityClass, Predicate<E> predicate, Integer offset, Integer limit, List<SortOrder<String>> sortOrders);

	<E> List<E> createEntities(Class<E> entityClass, List<E> entities) throws Exception;

	<E> void updateEntities(Class<E> entityClass, Set<E> entities) throws Exception;

	<E, I> void deleteEntities(Class<E> entityClass, Class<I> entityIdentifierClass, Set<I> entityIdentifiers) throws Exception;

	<E> List<E> getReplicatedObjects(List<E> objects);
}