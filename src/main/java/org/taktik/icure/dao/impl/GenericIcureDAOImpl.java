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

package org.taktik.icure.dao.impl;

import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.entities.base.StoredICureDocument;

import javax.persistence.PersistenceException;
import java.util.Collection;
import java.util.Objects;

/**
 * @author Bernard Paulus - 07/03/2017
 */
public class GenericIcureDAOImpl<T extends StoredICureDocument> extends GenericDAOImpl<T> {
    public GenericIcureDAOImpl(Class<T> entityClass, CouchDbICureConnector db, IDGenerator idGenerator) {
        super(entityClass, db, idGenerator);
    }

    @Override
    protected <K extends Collection<T>> K save(Boolean newEntity, K entities) {
        entities.stream().filter(Objects::nonNull)
                .forEach(GenericIcureDAOImpl::setTimestamps);
        return super.save(newEntity, entities);
    }

    @Override
    protected T save(Boolean newEntity, T entity) {
        if (entity != null) {
            setTimestamps(entity);
        }
        return super.save(newEntity, entity);
    }

    @Override
    public void unremove(Collection<T> entities) throws PersistenceException {
        entities.stream().filter(Objects::nonNull)
                .forEach(GenericIcureDAOImpl::setTimestamps);
        super.unremove(entities);
    }

    @Override
    public void unremove(T entity) {
        if (entity != null) {
            setTimestamps(entity);
        }
        super.unremove(entity);
    }

    private static void setTimestamps(StoredICureDocument entity) {
        long epochMillis = System.currentTimeMillis();
        if (entity.getCreated() == null) {
            entity.setCreated(epochMillis);
        }
        entity.setModified(epochMillis);
    }
}
