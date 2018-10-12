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

import org.ektorp.UpdateConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.taktik.icure.dao.GenericDAO;
import org.taktik.icure.entities.base.Versionable;
import org.taktik.icure.exceptions.BulkUpdateConflictException;
import org.taktik.icure.logic.VersionableLogic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @param <V> A Versionable-implementing entity.
 * @param <D> The type of a GenericDAO implementation.
 */
public abstract class VersionableLogicImpl<V extends Versionable<String>, D extends GenericDAO<V>>
		extends GenericLogicImpl<V, D> implements VersionableLogic<V, String> {

	private static Logger logger = LoggerFactory.getLogger(VersionableLogicImpl.class);

	@Override
	public List<V> updateEntities(Collection<V> entities) {
		try {
			return new ArrayList<>(getGenericDAO().save(entities));
		} catch (BulkUpdateConflictException e) {
			for (org.taktik.icure.exceptions.UpdateConflictException ex : e.getConflicts()) {
				//resolveConflict((V) ex.getDoc());
				logger.warn("Documents of class {} with id {} and rev {} could not be merged",ex.getDoc().getClass().getSimpleName(),ex.getDoc().getId(),ex.getDoc().getRev());
			}
			return null;
		}
	}


	private V getCommonOriginal(V leader, V receiver) {

		String documentId = leader.getId();

		Set<String> leaderRevs = leader.getRevHistory().keySet();
		Set<String> receiverRevs = receiver.getRevHistory().keySet();

		String commonRev = null;
		String examinedReceiverRev = null;
		Iterator<String> receiverRevsIterator = receiverRevs.iterator();
		while (commonRev == null && receiverRevsIterator.hasNext()) {
			examinedReceiverRev = receiverRevsIterator.next();
			if (leaderRevs.contains(examinedReceiverRev)) {
				commonRev = examinedReceiverRev;
			}
		}
		if (commonRev == null) {
			// This happens if leader has no revision history rev yet. So we fall back on the oldest rev in the receiver's revs history
			commonRev = examinedReceiverRev;
		}
		if (commonRev == null) {
			return null;
		}
		return getGenericDAO().get(documentId, commonRev);
	}
}
