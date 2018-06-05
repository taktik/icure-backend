package org.taktik.icure.dao.replicator;

import org.taktik.icure.entities.Group;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Bernard Paulus - 14/03/2017
 */
public class GroupDBUrl {
    private final URI couchDbUrl;

    public GroupDBUrl(String couchDbUrl) {
        try {
            this.couchDbUrl = new URI(couchDbUrl);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("malformed couchdb url: " + couchDbUrl, e);
        }
    }

    public String getDbName(Group group) {
        return "icure-" + group.getId() + "-base";
    }

    public String getInstanceUrl(Group group) {
        URI result;
        try {
            result = new URI(couchDbUrl.getScheme(),
                    null,
                    couchDbUrl.getHost(),
                    couchDbUrl.getPort(),
                    null,
                    null,
                    null);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("failed to build url", e);
        }
        return result.toString();
    }

    public String getDbUrl(Group group) {
        URI result;
        try {
            result = new URI(couchDbUrl.getScheme(),
                    group.getId() + ":" + group.getPassword(),
                    couchDbUrl.getHost(),
                    couchDbUrl.getPort(),
                    "/" + getDbName(group),
                    null,
                    null);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("failed to build url", e);
        }
        return result.toString();
    }

    public String getLocalDbUrl(Group group) {
        URI result;
        try {
            result = new URI(couchDbUrl.getScheme(),
                    group.getId() + ":" + group.getPassword(),
                    "127.0.0.1",
                    couchDbUrl.getPort(),
                    "/" + getDbName(group),
                    null,
                    null);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("failed to build url", e);
        }
        return result.toString();
    }


}
