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

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.ektorp.CouchDbInstance;
import org.ektorp.ReplicationCommand;
import org.ektorp.ReplicationTask;
import org.ektorp.http.HttpResponse;
import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.ReplicationDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.entities.Replication;
import org.taktik.icure.entities.embed.DatabaseSynchronization;

@Repository("replicationDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Replication' && !doc.deleted) emit( null, doc._id )}")
public class ReplicationDAOImpl extends GenericDAOImpl<Replication> implements ReplicationDAO {
	private static final String NAME = "name";

	private Gson gson = new GsonBuilder().create();
	private CouchDbInstance couchdbInstance;

	@Autowired
    public ReplicationDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbConfig") CouchDbICureConnector couchdb, @Qualifier("couchdbInstance") CouchDbInstance couchdbInstance, IDGenerator idGenerator, @Qualifier("entitiesCacheManager") CacheManager cacheManager) {
        super(Replication.class, couchdb, idGenerator);

        this.couchdbInstance = couchdbInstance;

		initStandardDesignDocument();
    }

    @Override
    @View(name = "by_name", map = "function(doc) {\n" +
            "            if (doc.java_type == 'org.taktik.icure.entities.Replication' && !doc.deleted && doc.name) {\n" +
            "            emit(doc.name,doc._id);\n" +
            "}\n" +
            "}")
    public Replication getByName(String name) {
        List<Replication> result = queryView("by_name", name);
        return result != null && result.size() == 1 ? result.get(0):null;
    }

	@Override
	public Map<DatabaseSynchronization,Number> getPendingChanges() {
		HttpResponse active_tasks = db.getConnection().getUncached("/_active_tasks");
		InputStreamReader inputStreamReader;
		try {
			inputStreamReader = new InputStreamReader(active_tasks.getContent(), "UTF8");
			List<Map<String,Object>> json = gson.fromJson(inputStreamReader, List.class);
			Map<DatabaseSynchronization,Number> result = new HashMap<>();
			for (Map<String,Object> status:json) {
				String source = (String) status.get("source");
				String target = (String) status.get("target");
				if (source != null && target != null) {
					result.put(new DatabaseSynchronization(source, target), (Number) status.get("changes_pending"));
				}
			}
			return result;
		} catch (UnsupportedEncodingException e) {
			//
		}
		return null;
	}

	@Override
	public List<ReplicationTask> getActiveReplications() {
		return this.couchdbInstance.getActiveTasks().stream().filter(task -> task instanceof ReplicationTask).map(task -> (ReplicationTask)task).collect(Collectors.toList());
	}

	@Override
	public void startReplication(DatabaseSynchronization databaseSynchronization, boolean continuous) {
		this.couchdbInstance.replicate(new ReplicationCommand.Builder().source(databaseSynchronization.getSource()).target(databaseSynchronization.getTarget()).continuous(continuous).filter(databaseSynchronization.getFilter()).build());
	}

	@Override
	public void cancelReplication(DatabaseSynchronization databaseSynchronization) {
		this.couchdbInstance.replicate(new ReplicationCommand.Builder().source(databaseSynchronization.getSource()).target(databaseSynchronization.getTarget()).continuous(true).filter(databaseSynchronization.getFilter()).cancel(true).build());
	}
}
