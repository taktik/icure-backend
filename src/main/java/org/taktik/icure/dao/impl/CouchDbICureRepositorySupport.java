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

package org.taktik.icure.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.ektorp.ComplexKey;
import org.ektorp.Page;
import org.ektorp.PageRequest;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.db.PaginatedDocumentKeyIdPair;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.base.StoredDocument;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Created by aduchate on 22/07/13, 10:38
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
class CouchDbICureRepositorySupport<T extends StoredDocument> extends CouchDbRepositorySupport<T> {
	private static final ObjectMapper MAPPER = new ObjectMapper();
	public static final int DEFAULT_LIMIT = 1000;

	@SuppressWarnings("unchecked")
	public CouchDbICureRepositorySupport(Class clazz, CouchDbICureConnector db) {
		super(clazz, db);
	}

	@Override
	public void remove(T entity) {
		entity.setDeletionDate(System.currentTimeMillis());
		this.update(entity);
	}

	public void unremove(T entity) {
		entity.setDeletionDate(null);
		this.update(entity);
	}

	public void purge(T entity) {
		super.remove(entity);
	}

	T refresh(T entity) {
		return entity;
	}

	protected List<T> queryView(String viewName, long key) {
		ViewQuery q = createQuery(viewName)
				.includeDocs(true)
				.key(key);

		return queryResults(q);
	}

	public List<T> queryResults(ViewQuery query) {
		return db.queryView(query, type);
	}

	private <P> PaginatedList<T> pagedQueryView(String viewName, P startKey, P endKey, PaginationOffset<P> pagination, boolean descending, Function<P, ValueNode> startToNode) {
		int limit = pagination.getLimit() != null ? pagination.getLimit() : DEFAULT_LIMIT;
		int page = pagination.getPage() != null ? pagination.getPage() : 0;
		String startDocId = pagination.getStartDocumentId();

		ViewQuery viewQuery = createQuery(viewName)
				.startKey(startKey)
				.includeDocs(true)
				.startDocId(startDocId)
				.limit(limit)
				.descending(descending);

		if (endKey != null) {
			viewQuery = viewQuery.endKey(endKey);
		}

		PageRequest.Builder builder = new PageRequest.Builder().pageSize(limit).page(page);

		if (startKey !=null || startDocId != null) {
			builder.nextKey(new PageRequest.KeyIdPair(pagination.getStartKey() == null ? null : startToNode.apply(pagination.getStartKey()), startDocId)).page(Math.max(page,1));
		}

		PageRequest pr = builder.build();
		Page<T> ts = db.queryForPage(viewQuery, pr, type);

		Object sk = ts.getTotalSize() > ts.getPageSize() && ts.getNextPageRequest() != null ? ts.getNextPageRequest().getStartKey() : null;
		return new PaginatedList<>(
				ts.getPageSize(),
				ts.getTotalSize(),
				ts.getRows(),
				sk != null ?
						new PaginatedDocumentKeyIdPair((sk instanceof  NullNode) ? null : (sk instanceof LongNode) ? Collections.singletonList(""+((LongNode) sk).longValue()) : Collections.singletonList(((TextNode) sk).textValue()), ts.getNextPageRequest().getStartKeyDocId())
						: null
		);
	}

	protected PaginatedList<T> pagedQueryView(String viewName, String startKey, String endKey, PaginationOffset pagination, boolean descending) {
		return pagedQueryView(viewName, startKey, endKey, pagination, descending, TextNode::valueOf);
	}

	protected PaginatedList<T> pagedQueryView(String viewName, Long startKey, Long endKey, PaginationOffset pagination, boolean descending) {
		return pagedQueryView(viewName, startKey, endKey, pagination, descending, LongNode::valueOf);
	}

	protected PaginatedList<T> pagedQueryView(String viewName, ComplexKey startKey, ComplexKey endKey, PaginationOffset pagination, boolean descending) {
		int limit = pagination != null && pagination.getLimit() != null ? pagination.getLimit() : DEFAULT_LIMIT;
		int page = pagination != null && pagination.getPage() != null ? pagination.getPage() : 1;
		String startDocId = pagination != null ? pagination.getStartDocumentId() : null;

		ViewQuery viewQuery = createQuery(viewName)
				.includeDocs(true)
				.startKey(startKey) //Shouldn't be necessary
				.reduce(false)
				.startDocId(startDocId) //Shouldn't be necessary
				.limit(limit)
				.descending(descending);

		if (endKey != null) {
			viewQuery = viewQuery.endKey(endKey);
		}

		Object passedStartKey = pagination != null ? pagination.getStartKey() : null;
		ComplexKey cplxStartKey = null;
		if (passedStartKey instanceof List) {
			cplxStartKey = ComplexKey.of(((List) passedStartKey).toArray());
		} else if (passedStartKey instanceof Object[]) {
			cplxStartKey = ComplexKey.of((Object[]) passedStartKey);
		} else if (passedStartKey instanceof ComplexKey){
			cplxStartKey = (ComplexKey) passedStartKey;
		}

		JsonNode node = (cplxStartKey != null) ? cplxStartKey.toJson() : null;

		PageRequest pr = new PageRequest.Builder().pageSize(limit).page(page)
				.nextKey(new PageRequest.KeyIdPair(node, startDocId)).build();

		Page<T> ts = db.queryForPage(viewQuery, pr, type);

		try {
			return new PaginatedList<>(
					ts.getPageSize(),
					ts.getTotalSize(),
					ts.getRows(),
					ts.getTotalSize() > ts.getPageSize() && ts.getNextPageRequest() != null ? new PaginatedDocumentKeyIdPair(MAPPER.treeToValue((TreeNode) ts.getNextPageRequest().getStartKey(), List.class), ts.getNextPageRequest().getStartKeyDocId()) : null
			);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}
	}
	protected PaginatedList<String> pagedQueryViewOfIds(String viewName, ComplexKey startKey, ComplexKey endKey, PaginationOffset pagination) {
		int limit = pagination != null && pagination.getLimit() != null ? pagination.getLimit() : DEFAULT_LIMIT;
		int page = pagination != null && pagination.getPage() != null ? pagination.getPage() : 1;
		String startDocId = pagination != null ? pagination.getStartDocumentId() : null;

		ViewQuery viewQuery = createQuery(viewName)
				.startKey(startKey)
				.reduce(false)
				.limit(limit)
				.includeDocs(false);

		if (endKey != null) {
			viewQuery = viewQuery.endKey(endKey);
		}

		PageRequest pr = new PageRequest.Builder().pageSize(limit).page(page).nextKey(new PageRequest.KeyIdPair(pagination.getStartKey() == null ? null : ((ComplexKey)pagination.getStartKey()).toJson(), startDocId)).build();

		Page<String> ts = db.queryForPage(viewQuery, pr, String.class);

		try {
			return new PaginatedList<>(
					ts.getPageSize(),
					ts.getTotalSize(),
					ts.getRows(),
					ts.getTotalSize() > ts.getPageSize() && ts.getNextPageRequest() != null ? new PaginatedDocumentKeyIdPair(MAPPER.treeToValue((TreeNode) ts.getNextPageRequest().getStartKey(), List.class), ts.getNextPageRequest().getStartKeyDocId()) : null
			);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}
	}

	protected List<T> queryView(String viewName, String[] keys) {
		ViewQuery q = createQuery(viewName)
				.includeDocs(true)
				.keys(Arrays.asList(keys));

		return queryResults(q);
	}

	protected List<T> queryView(String viewName, ComplexKey[] keys) {
		ViewQuery q = createQuery(viewName)
				.includeDocs(true)

				.keys(Arrays.asList(keys));

		return queryResults(q);
	}

	protected List<T> queryView(String viewName, String startKey, String endKey) {
		return queryResults(createQuery(viewName)
						.includeDocs(true)
						.startKey(startKey)
						.endKey(endKey)
		);
	}

	protected List<T> queryView(String viewName, ComplexKey startKey, ComplexKey endKey) {
		return queryResults(createQuery(viewName)
				.includeDocs(true)
				.startKey(startKey)
				.endKey(endKey));
	}

	protected List<T> queryView(String viewName, ComplexKey startKey, ComplexKey endKey, String startDocId, int limit) {
		ViewQuery viewQuery = createQuery(viewName)
				.includeDocs(true)
				.startKey(startKey)
				.startDocId(startDocId)
				.endKey(endKey);

		if (startDocId != null) viewQuery.limit(limit);

		return queryResults(viewQuery);
	}

	protected List<T> queryView(String viewName, ComplexKey key, boolean reduce) {
		return queryResults(createQuery(viewName)
				.includeDocs(true)
				.key(key)
				.reduce(reduce));
	}

}
