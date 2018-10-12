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

import org.taktik.icure.dao.GenericDAO;
import org.taktik.icure.entities.base.Identifiable;
import org.taktik.icure.logic.EntityPersister;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class GenericLogicImpl<E extends Identifiable<String>, D extends GenericDAO<E>> implements EntityPersister<E, String> {

	@Override
	public boolean createEntities(Collection<E> entities, Collection<E> createdEntities) throws Exception {
		return createdEntities.addAll(getGenericDAO().create(entities));
	}

	@Override
	public List<E> updateEntities(Collection<E> entities) {
		return new ArrayList<>(getGenericDAO().save(entities));
	}

	@Override
	public void deleteEntities(Collection<String> identifiers) {
		getGenericDAO().removeByIds(identifiers);
	}

	@Override
	public void undeleteEntities(Collection<String> identifiers) {
		getGenericDAO().unremoveByIds(identifiers);
	}

	@Override
	public List<E> getAllEntities() {
		return getGenericDAO().getAll();
	}

	@Override
	public List<String> getAllEntityIds() {
		return getGenericDAO().getAllIds();
	}

	@Override
	public boolean hasEntities() {
		return getGenericDAO().hasAny();
	}

	@Override
	public boolean exists(String id) {
		return getGenericDAO().contains(id);
	}

	@Override
	public E getEntity(String id) {
		return getGenericDAO().get(id);
	}

	protected abstract D getGenericDAO();
}
