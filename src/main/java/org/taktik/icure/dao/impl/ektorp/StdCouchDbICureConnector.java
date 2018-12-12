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

import org.ektorp.AttachmentInputStream;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.DbPath;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.Options;
import org.ektorp.Page;
import org.ektorp.PageRequest;
import org.ektorp.Revision;
import org.ektorp.StreamingViewResult;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.http.HttpResponse;
import org.ektorp.http.HttpStatus;
import org.ektorp.http.StdResponseHandler;
import org.ektorp.http.URI;
import org.ektorp.impl.ObjectMapperFactory;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.util.Assert;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by emad7105 on 16/10/2014.
 */
public class StdCouchDbICureConnector extends StdCouchDbConnector implements CouchDbICureConnector {
	private String uuid = UUID.randomUUID().toString();

	public StdCouchDbICureConnector(String databaseName, CouchDbInstance dbInstance) {
		super(databaseName, dbInstance);
	}

	public StdCouchDbICureConnector(String databaseName, CouchDbInstance dbi, ObjectMapperFactory om) {
		super(databaseName, dbi, om);
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
	public CouchDbICureConnector getCouchDbICureConnector(String groupId) {
		return this;
	}

	public <T> List<CouchKeyValue<T>> queryViewWithKeys(final ViewQuery query, final Class<T> type) {
		Assert.notNull(query, "query may not be null");
		query.dbPath(dbURI.toString());

		EmbeddedDocViewWithKeysResponseHandler<T> rhk = new EmbeddedDocViewWithKeysResponseHandler<T>(
				type, objectMapper, query.isIgnoreNotFound());

		return executeQuery(query, rhk);
	}

	@Override
	public CouchDbICureConnector getFallbackConnector() {
		return this;
	}
}
