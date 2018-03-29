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

package org.taktik.icure.dao.migration;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.support.CouchDbRepositorySupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.EntityTemplateDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.entities.EntityTemplate;

@Repository("healthElementTemplateMigration")
public class HealthElementTemplateMigration extends CouchDbRepositorySupport<MigrationStub> implements DbMigration {

	EntityTemplateDAO entityTemplateDAO;
	Gson gsonMapper;

	@Autowired
	protected HealthElementTemplateMigration(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbHealthdata") CouchDbICureConnector couchdb) {
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
		List<EntityTemplate> templates = entityTemplateDAO.getAll();
		Set<EntityTemplate> modified = new HashSet<>();
		templates.forEach(t-> {
			try {
				if (t.getEntityType() != null && t.getEntityType().equals("org.taktik.icure.entities.HealthElement")) { t.setEntityType("org.taktik.icure.entities.HealthElementTemplate"); modified.add(t); }
			} catch (JsonSyntaxException ignored) {}
		});
		entityTemplateDAO.save(modified);
		this.update(new MigrationStub(this.getClass().getCanonicalName()));
	}

	@Autowired
	public void setEntityTemplateDAO(EntityTemplateDAO entityTemplateDAO) {
		this.entityTemplateDAO = entityTemplateDAO;
	}
}
