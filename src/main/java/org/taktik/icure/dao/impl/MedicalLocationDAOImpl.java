package org.taktik.icure.dao.impl;

import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.MedicalLocationDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.entities.MedicalLocation;

import java.util.List;

@Repository("MedicalLocationDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.MedicalLocation' && !doc.deleted) emit( null, doc._id )}")
public class MedicalLocationDAOImpl  extends GenericDAOImpl<MedicalLocation> implements MedicalLocationDAO  {
    @Autowired
    public MedicalLocationDAOImpl(@Qualifier("couchdbBase") CouchDbICureConnector db, IDGenerator idGenerator) {
        super(MedicalLocation.class, db, idGenerator);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "by_post_code", map = "classpath:js/medicallocation/By_post_code_map.js")
    public List<MedicalLocation> byPostCode(String postCode) {
        return queryView("by_post_code", postCode);
    }
}
