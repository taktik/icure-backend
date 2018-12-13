package org.taktik.icure.dao.impl;

import org.ektorp.ComplexKey;
import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.FrontEndMigrationDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.entities.FrontEndMigration;

import java.util.List;

@Repository("frontEndMigrationDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.FrontEndMigration' && !doc.deleted) emit( null, doc._id )}")
public class FrontEndMigrationDAOImpl extends GenericDAOImpl<FrontEndMigration> implements FrontEndMigrationDAO {


    @Autowired
    public FrontEndMigrationDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbBase") CouchDbICureConnector couchdb, IDGenerator idGenerator) {
        super(FrontEndMigration.class, couchdb, idGenerator);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "by_userid_name", map = "function(doc) {\n" +
            "            if (doc.java_type == 'org.taktik.icure.entities.FrontEndMigration' && !doc.deleted && doc.name && doc.userId) {\n" +
            "            emit([doc.userId, doc.name],doc._id);\n" +
            "}\n" +
            "}")
    public List<FrontEndMigration> getByUserIdName(String userId, String name) {
        if(name == null){
            // This is a range query
            ComplexKey startKey = ComplexKey.of(userId);
            ComplexKey endKey = ComplexKey.of(userId, ComplexKey.emptyObject());
            List<FrontEndMigration> result = queryView("by_userid_name", startKey, endKey);
            return result;
        }
        List<FrontEndMigration> result = queryView("by_userid_name", ComplexKey.of(userId, name));
        return result;
    }

}
