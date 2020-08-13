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

package org.taktik.icure.dao;

import org.ektorp.AttachmentInputStream;
import org.taktik.icure.entities.base.Identifiable;

import javax.persistence.PersistenceException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public interface GenericDAO<T extends Identifiable<String>> extends LookupDAO<T> {

    List<T> getAll();

	String getAttachment(String documentId, String attachmentId);

	AttachmentInputStream getAttachmentInputStream(String documentId, String attachmentId, String rev);

	/**
	 * *
	 * @param documentId document id
	 * @param rev document revision
	 * @param data AttachmentInputStream
	 * @return
	 */
    String createAttachment(String documentId, String rev, AttachmentInputStream data);

    String deleteAttachment(String documentId, String rev, String attachmentId);

    Set<T> getSet(Collection<String> ids);

    List<T> getList(Collection<String> ids);

    <K extends Collection<T>> K create(K entities) throws PersistenceException;

    <K extends Collection<T>> K save(K entities) throws PersistenceException;

    void remove(T entity) throws PersistenceException;

    void unremove(T entity) throws PersistenceException;

    void purge(T entity) throws PersistenceException;

	void removeById(String id) throws PersistenceException;

	void unremoveById(String id) throws PersistenceException;

    void purgeById(String id) throws PersistenceException;

    void remove(Collection<T> entities) throws PersistenceException;

    void unremove(Collection<T> entities) throws PersistenceException;

    void purge(Collection<T> entities) throws PersistenceException;

    void removeByIds(Collection<String> ids) throws PersistenceException;

    void unremoveByIds(Collection<String> ids) throws PersistenceException;

    void purgeByIds(Collection<String> ids) throws PersistenceException;

    void visitAll(final Function<T, Boolean> callback) throws PersistenceException;

	boolean contains(String id);

	boolean hasAny();

	void refreshIndex();

	List<String> getAllIds();

    void warmupIndex();

    void initStandardDesignDocument();

	void initStandardDesignDocument(String groupId);

}
