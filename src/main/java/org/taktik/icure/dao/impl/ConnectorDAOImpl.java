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

import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.ConnectorDAO;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.entities.Connector;
import org.taktik.icure.entities.VirtualHost;
import org.taktik.icure.entities.base.StoredDocument;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository("connectorDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Connector' && !doc.deleted) emit( null, doc._id )}")
public class ConnectorDAOImpl extends CachedDAOImpl<Connector> implements ConnectorDAO {

    @Autowired
    public ConnectorDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbConfig") CouchDbICureConnector couchdb, IDGenerator idGenerator, @Qualifier("cacheManager") CacheManager cacheManager) {
		super(Connector.class, couchdb, idGenerator, cacheManager);

        initStandardDesignDocument();
    }

    @Override
    @View(name = "by_identifier", map ="classpath:js/connector/By_identifier_map.js")
	public Connector getByIdentifier(String connectorIdentifier) {
        List<Connector> result = queryView("by_identifier", connectorIdentifier);
        return result != null && result.size() > 0 ? result.get(0):null;
	}

    @Override
    @View(name = "by_vhost", map = "classpath:js/connector/By_vhost_map.js")
    public List<Connector> getConnectorsForVirtualHosts(Collection<String> virtualHostIds) {
        return queryView("by_vhost", virtualHostIds.toArray(new String[virtualHostIds.size()]));
    }

    @Override
	protected void beforeSave(Connector connector) {
        connector.setDefaultVirtualHost(StoredDocument.strip(connector.getDefaultVirtualHost()));
        connector.setVirtualHosts(connector.getVirtualHosts().stream().<VirtualHost>map(StoredDocument::strip).collect(Collectors.toSet()));
	}

	@Override
	protected void afterDelete(Connector connector) {
	}

	@Override
	protected void doFetchRelationship(Connector connector) {

	}
}