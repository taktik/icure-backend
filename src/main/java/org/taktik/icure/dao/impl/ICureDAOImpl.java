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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.ICureDAO;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository("iCureDAO")
public class ICureDAOImpl implements ICureDAO {
	CouchDbConnector couchdbConfig;
	CouchDbInstance couchdbInstance;

	private Gson gson = new GsonBuilder().create();

	@Override
	public Map<String, Number> getIndexingStatus(String groupId) {
		HttpResponse active_tasks = couchdbConfig.getConnection().getUncached("/_active_tasks");
		InputStreamReader inputStreamReader;
		try {
			inputStreamReader = new InputStreamReader(active_tasks.getContent(), "UTF8");
			List<Map<String,Object>> json = gson.fromJson(inputStreamReader, List.class);
			Map<String, List<Number>> statusesMap = new HashMap<>();
			for (Map<String,Object> status:json) {
				String designDoc = (String) status.get("design_document");
				Number progress = (Number) status.get("progress");
				String database = (String) status.get("database");

				if (groupId != null && database != null && !database.contains(groupId)) { continue; }

				if (designDoc != null && progress != null) {
					List<Number> statuses = statusesMap.get(designDoc);
					if (statuses == null) {statusesMap.put(designDoc, statuses = new LinkedList<>()); }
					statuses.add(progress);
				}
			}

			Map<String, Number> results = new HashMap<>();
			for (Map.Entry<String, List<Number>> e: statusesMap.entrySet()) {
				results.put(e.getKey(), e.getValue().stream().collect(Collectors.averagingInt(Number::intValue)));
			}
			return results;

		} catch (UnsupportedEncodingException e) {
			//
		}
		return null;
	}

	@Autowired
	public void setCouchdbConfig(CouchDbConnector couchdbConfig) {
		this.couchdbConfig = couchdbConfig;
	}

	@Autowired
	public void setCouchdbInstance(CouchDbInstance couchdbInstance) {
		this.couchdbInstance = couchdbInstance;
	}

}
