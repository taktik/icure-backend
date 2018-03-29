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

	List<String> secondaryDatabases = new ArrayList<>();

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

	/*
	@Override
	public AttachmentInputStream getAttachment(final String id, final String attachmentId) {
		return getAttachment(id, attachmentId, null);
	}

	@Override
	public AttachmentInputStream getAttachment(String id, String attachmentId, String revision) {
		assertDocIdHasValue(id);

		Assert.hasText(attachmentId, "attachmentId may not be null or empty");
		Assert.hasText(revision, "revision may not be null or empty");

		AttachmentInputStream ais = null;

		List<String> databases = getCombineDatabases();

		for(String db : databases) {
			try {
				URI uri = URI.prototype(DbPath.fromString(db).getPath()).append(id).append(attachmentId);
				if (revision!=null) { uri = uri.param("rev", revision); }
				ais = getAttachment(attachmentId, uri);

				return ais;
			} catch (DocumentNotFoundException ignored) {}
		}
		return null;
	}

	@NotNull
	private List<String> getCombineDatabases() {
		List<String> databases = new ArrayList<>();
		databases.add(getDatabaseName());
		databases.addAll(secondaryDatabases);
		return databases;
	}

	private AttachmentInputStream getAttachment(String attachmentId, URI uri) throws DocumentNotFoundException {
		HttpResponse r = restTemplate.get(uri.toString());

		if (!r.isSuccessful()) {throw new DocumentNotFoundException(uri.toString()); }
		return new AttachmentInputStream(attachmentId, r.getContent(),
				r.getContentType(), r.getContentLength());
	}


	@Override
	public <T> T get(Class<T> c, String id, Options options) {
		Assert.notNull(c, "Class may not be null");
		assertDocIdHasValue(id);
		for(String db : getCombineDatabases()) {
			URI uri = URI.prototype(DbPath.fromString(db).getPath()).append(id);
			applyOptions(options, uri);
			HttpResponse r = restTemplate.get(uri.toString());
			if (r.isSuccessful()) {
				try {
					return objectMapper.readValue(r.getContent(), c);
				} catch (IOException e) {
					return null;
				}
			}
		}
		return null;
	}

	@Override
	public <T> T find(final Class<T> c, String id, Options options) {
		Assert.notNull(c, "Class may not be null");
		assertDocIdHasValue(id);
		for(String db : getCombineDatabases()) {
			URI uri = URI.prototype(DbPath.fromString(db).getPath()).append(id);
			applyOptions(options, uri);
			HttpResponse r = restTemplate.get(uri.toString());
			if (r.isSuccessful()) {
				try {
					return objectMapper.readValue(r.getContent(), c);
				} catch (IOException e) {
					return null;
				}
			} else {
				if (r.getCode() != HttpStatus.NOT_FOUND) { throw StdResponseHandler.createDbAccessException(r); }
			}
		}
		return null;
	}

	@Override
	public List<Revision> getRevisions(String id) {
		return super.getRevisions(id);
	}

	@Override
	public List<String> getAllDocIds() {
		return super.getAllDocIds();
	}

	@Override
	public <T> List<T> queryView(ViewQuery query, Class<T> type) {
		return super.queryView(query, type);
	}

	@Override
	public List<String> queryViewForIds(ViewQuery query) {
		return super.queryViewForIds(query);
	}

	@Override
	public List<ComplexKey> queryViewForComplexKeys(ViewQuery query) {
		return super.queryViewForComplexKeys(query);
	}

	@Override
	public <T> Page<T> queryForPage(ViewQuery query, PageRequest pr, Class<T> type) {
		return super.queryForPage(query, pr, type);
	}

	@Override
	public ViewResult queryView(ViewQuery query) {
		return super.queryView(query);
	}

	@Override
	public StreamingViewResult queryForStreamingView(ViewQuery query) {
		return super.queryForStreamingView(query);
	}

	@Override
	public InputStream queryForStream(ViewQuery query) {
		return super.queryForStream(query);
	}
	*/
}
