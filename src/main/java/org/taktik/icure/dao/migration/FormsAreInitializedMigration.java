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

package org.taktik.icure.dao.migration;


import java.util.LinkedList;
import java.util.List;

import org.ektorp.DocumentNotFoundException;
import org.ektorp.support.CouchDbRepositorySupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.FormDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.Form;

@Repository("formsAreInitializedMigration")
public class FormsAreInitializedMigration extends CouchDbRepositorySupport<MigrationStub> implements DbMigration {

	private FormDAO formDAO;

	@Autowired
	protected FormsAreInitializedMigration(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbHealthdata") CouchDbICureConnector couchdb) {
		super(MigrationStub.class, couchdb, false);
	}

	@Override
	public boolean hasBeenApplied() {
		try {
			this.get(this.getClass().getCanonicalName());
			return true;
		} catch (DocumentNotFoundException ignored) {}
		return false;
	}

	@Override
	public void apply() {
		PaginatedList<Form> page = formDAO.findAll(new PaginationOffset<>(100));
		while (true) {
			List<Form> changedForms = new LinkedList<>();
			page.getRows().forEach(r -> {
				if (r.isHasBeenInitialized() == null || !r.isHasBeenInitialized()) {
					r.setHasBeenInitialized(true);
					changedForms.add(r);
				}
			});
			if (changedForms.size()>0) {
				formDAO.save(changedForms);
			}
			if (page.getRows().size()<100) { break; }
			page = formDAO.findAll(new PaginationOffset<>(page));
		}
		this.update(new MigrationStub(this.getClass().getCanonicalName()));
	}

	@Autowired
	public void setFormDAO(FormDAO formDAO) {
		this.formDAO = formDAO;
	}
}
