/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
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


import org.ektorp.DocumentNotFoundException;
import org.ektorp.support.CouchDbRepositorySupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.FormDAO;
import org.taktik.icure.dao.InsuranceDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.Form;
import org.taktik.icure.entities.Insurance;
import org.taktik.icure.entities.base.StoredDocument;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository("insurancesParentsMigration")
public class InsurancesParentsMigration extends CouchDbRepositorySupport<MigrationStub> implements DbMigration {

	InsuranceDAO insuranceDAO;

	@Autowired
	protected InsurancesParentsMigration(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbBase") CouchDbICureConnector couchdb) {
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
		List<Insurance> parents = Stream.of("100", "200", "300", "400", "500", "600", "900").map(c -> insuranceDAO.listByCode(c).iterator().next()).filter(Objects::nonNull).collect(Collectors.toList());

		List<Insurance> all = insuranceDAO.getAll();
		all.forEach(i->i.setParent(i.getCode().equals("306")?i.getId():parents.stream().filter(p->p.getCode().startsWith(i.getCode().substring(0,1))).findAny().map(Insurance::getId).orElse(null)));
		insuranceDAO.save(all);

		this.update(new MigrationStub(this.getClass().getCanonicalName()));
	}

	@Autowired
	public void setInsuranceDAO(InsuranceDAO insuranceDAO) {
		this.insuranceDAO = insuranceDAO;
	}
}
