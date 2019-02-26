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

package org.taktik.icure.dao.impl.ektorp;

import org.apache.commons.codec.digest.DigestUtils;
import org.ektorp.CouchDbInstance;
import org.ektorp.impl.ObjectMapperFactory;
import org.ektorp.impl.StdCouchDbConnector;

/**
 * Created by emad7105 on 16/10/2014.
 */
public class StdCouchDbICureConnector extends StdCouchDbConnector implements CouchDbICureConnector {
	private final String uuid;

	public StdCouchDbICureConnector(String databaseName, CouchDbInstance dbInstance) {
		super(databaseName, dbInstance);
		uuid = DigestUtils.sha256Hex(databaseName+':'+dbInstance.getUuid());
	}

	public StdCouchDbICureConnector(String databaseName, CouchDbInstance dbi, ObjectMapperFactory om) {
		super(databaseName, dbi, om);
		uuid = DigestUtils.sha256Hex(databaseName+':'+dbi.getUuid());
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	@Override
	public CouchDbICureConnector getCurrentUserRealConnector() {
		return this;
	}

	@Override
	public CouchDbICureConnector getCouchDbICureConnector(String groupId, String dbInstanceUrl, boolean allowFallback) {
		return this;
	}

	@Override
	public CouchDbICureConnector getFallbackConnector() {
		return this;
	}
}
