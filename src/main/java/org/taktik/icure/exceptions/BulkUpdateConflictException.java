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

package org.taktik.icure.exceptions;

import java.util.List;
import javax.persistence.PersistenceException;

import org.taktik.icure.entities.base.StoredDocument;

public class BulkUpdateConflictException extends PersistenceException {
	List<UpdateConflictException> conflicts;
	List<? extends StoredDocument> savedDocuments;

	public BulkUpdateConflictException(List<UpdateConflictException> conflicts, List<? extends StoredDocument>savedDocuments) {
		this.conflicts = conflicts;
		this.savedDocuments = savedDocuments;
	}

	public List<UpdateConflictException> getConflicts() {
		return conflicts;
	}

	public void setConflicts(List<UpdateConflictException> conflicts) {
		this.conflicts = conflicts;
	}

	public List<? extends StoredDocument> getSavedDocuments() {
		return savedDocuments;
	}

	public void setSavedDocuments(List<? extends StoredDocument> savedDocuments) {
		this.savedDocuments = savedDocuments;
	}
}
