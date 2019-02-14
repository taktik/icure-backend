package org.taktik.icure.dao.impl;

import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.ApplicationSettingsDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.entities.ApplicationSettings;

@Repository("ApplicationSettingsDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.ApplicationSettings' && !doc.deleted) emit( null, doc._id )}")
public class ApplicationSettingsDAOImpl extends GenericDAOImpl<ApplicationSettings> implements ApplicationSettingsDAO {
    @Autowired
    public ApplicationSettingsDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbBase") CouchDbICureConnector couchdb, IDGenerator idGenerator) {
        super(ApplicationSettings.class, couchdb, idGenerator);
        initStandardDesignDocument();
    }
}
