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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ektorp.ComplexKey;
import org.ektorp.DbAccessException;
import org.ektorp.ViewResultException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by emad7105 on 16/10/2014.
 */
public class QueryResultWithKeysParser<T> {
	private static final String NOT_FOUND_ERROR = "not_found";
	private static final String ROWS_FIELD_NAME = "rows";
	private static final String VALUE_FIELD_NAME = "value";
	private static final String ID_FIELD_NAME = "id";
	private static final String ERROR_FIELD_NAME = "error";
	private static final String KEY_FIELD_NAME = "key";
	private static final String INCLUDED_DOC_FIELD_NAME = "doc";
	private static final String TOTAL_ROWS_FIELD_NAME = "total_rows";
	private static final String OFFSET_FIELD_NAME = "offset";
	private static final String UPDATE_SEQUENCE_NAME = "update_seq";

	private int totalRows = -1;
	private int offset = -1;
	private List<ComplexKey> keys;
	private List<T> rows;
	private Long updateSequence;

	private String firstId;
	private JsonNode firstKey;

	private String lastId;
	private JsonNode lastKey;

	private final ObjectMapper mapper;
	private final Class<T> type;
	private boolean ignoreNotFound;

	public QueryResultWithKeysParser(Class<T> type, ObjectMapper mapper) {
		this.type = type;
		this.mapper = mapper;
	}

	public void parseResult(InputStream json) throws IOException {
		JsonParser jp = mapper.getFactory().createParser(json);

		try {
			parseResult(jp);
		} finally {
			jp.close();
		}
	}

	private void parseResult(JsonParser jp) throws IOException {
		if (jp.nextToken() != JsonToken.START_OBJECT) {
			throw new DbAccessException("Expected data to start with an Object");
		}

		Map<String, String> errorFields = new HashMap<String, String>();
		// Issue #98: Can't assume order of JSON fields.
		while (jp.nextValue() != JsonToken.END_OBJECT) {
			String currentName = jp.getCurrentName();
			if (OFFSET_FIELD_NAME.equals(currentName)) {
				offset = jp.getIntValue();
			} else if (TOTAL_ROWS_FIELD_NAME.equals(currentName)) {
				totalRows = jp.getIntValue();
			} else if (ROWS_FIELD_NAME.equals(currentName)) {
				rows = new ArrayList<T>();
				keys = new ArrayList<ComplexKey>();
				parseRows(jp);
			} else if (UPDATE_SEQUENCE_NAME.equals(currentName)) {
				updateSequence = jp.getLongValue();
			} else {
				// Handle cloudant errors.
				errorFields.put(jp.getCurrentName(), jp.getText());
			}
		}

		if (!errorFields.isEmpty()) {
			JsonNode error = mapper.convertValue(errorFields, JsonNode.class);
			throw new DbAccessException(error.toString());
		}
	}

	private void parseRows(JsonParser jp) throws IOException {
		if (jp.getCurrentToken() != JsonToken.START_ARRAY) {
			throw new DbAccessException("Expected rows to start with an Array");
		}

		// Parses the first row that isn't an error row to find out which field
		// to use (doc or value).
		String dataField = null;
		while (dataField == null && jp.nextToken() == JsonToken.START_OBJECT) {
			Row row = jp.readValueAs(Row.class);
			if (row.error != null) {
				if (!ignoreError(row.error)) {
					throw new ViewResultException(row.key, row.error);
				}
				continue;
			}

			// obtaining keys
			Object[] rKeys = new Object[row.key.size()];
			Iterator<JsonNode> it = row.key.elements();
			int i = 0;
			while (it.hasNext()) {
				rKeys[i] = mapper.readValue(it.next().traverse(), String.class);
				i++;
			}
			keys.add(ComplexKey.of(rKeys));

			if (row.doc != null) {
				dataField = INCLUDED_DOC_FIELD_NAME;
				rows.add(mapper.readValue(row.doc.traverse(), type));
			} else {
				dataField = VALUE_FIELD_NAME;
				rows.add(mapper.readValue(row.value.traverse(), type));
			}
			firstId = row.id;
			firstKey = row.key;
		}
		// After the while, either we point at END_ARRAY but we have no
		// dataField (all rows were error),
		// or we point at an END_OBJECT (end of a row) and have determined which
		// data field to use.
		if (dataField == null)
			return;

		// Parse all the remaining rows; jp points at START_OBJECT except after
		// the last row
		while (jp.nextToken() != JsonToken.END_ARRAY) {
			String currentId = null;
			JsonNode currentKey = null;
			String error = null;
			T value = null;
			// Parse the fields of a row; jp points at a value token except
			// after the last field.
			while (jp.nextValue() != JsonToken.END_OBJECT) {
				String currentName = jp.getCurrentName();
				if (ID_FIELD_NAME.equals(currentName)) {
					currentId = jp.getText();
				} else if (KEY_FIELD_NAME.equals(currentName)) {
					currentKey = jp.readValueAsTree();
				} else if (dataField.equals(currentName)) {
					value = jp.readValueAs(type);
				} else if (ERROR_FIELD_NAME.equals(currentName)) {
					error = jp.getText();
				} else {
					// Skip fields value that are of no interest to us.
					jp.skipChildren();
				}
			}
			if (error != null && !ignoreError(error)) {
				throw new ViewResultException(currentKey, error);
			}
			// If the current row is an error row, then value will be null
			if (value != null) {
				lastId = currentId;
				lastKey = currentKey;
				rows.add(value);

				// obtaining keys
				Object[] rKeys = new Object[currentKey.size()];
				Iterator<JsonNode> it = currentKey.elements();
				int i = 0;
				while (it.hasNext()) {
					rKeys[i] = mapper.readValue(it.next().traverse(), String.class);
					i++;
				}
				keys.add(ComplexKey.of(rKeys));
			}
		}
	}

	private boolean ignoreError(String error) {
		return ignoreNotFound && NOT_FOUND_ERROR.equals(error);
	}

	public int getTotalRows() {
		return totalRows;
	}

	public int getOffset() {
		return offset;
	}

	public List<T> getRows() {
		return rows;
	}

	public List<ComplexKey> getKeys() {
		return keys;
	}

	public String getLastId() {
		return lastId;
	}

	public JsonNode getLastKey() {
		return lastKey;
	}

	public String getFirstId() {
		return firstId;
	}

	public JsonNode getFirstKey() {
		return firstKey;
	}

	public void setIgnoreNotFound(boolean ignoreNotFound) {
		this.ignoreNotFound = ignoreNotFound;
	}

	public Long getUpdateSequence() {
		return updateSequence;
	}

	@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
	private static class Row {
		private String id;
		private JsonNode key;
		private JsonNode value;
		private JsonNode doc;
		private String error;
	}
}
