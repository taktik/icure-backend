package org.taktik.icure.dao.impl;

import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.PlaceDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.entities.Place;

import java.util.List;

@Repository("PlaceDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Place' && !doc.deleted) emit( null, doc._id )}")
public class PlaceDAOImpl extends GenericDAOImpl<Place> implements PlaceDAO {
    @Autowired
    public PlaceDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbHealthdata") CouchDbICureConnector db, IDGenerator idGenerator) {
        super(Place.class, db, idGenerator);
        initStandardDesignDocument();
    }
}
