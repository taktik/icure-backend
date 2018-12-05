package org.taktik.icure.dao.impl;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbInstance;
import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
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
    public FrontEndMigrationDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbConfig") CouchDbICureConnector couchdb, IDGenerator idGenerator, @Qualifier("entitiesCacheManager") CacheManager cacheManager) {
        super(FrontEndMigration.class, couchdb, idGenerator);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "by_userid_name", map = "function(doc) {\n" +
            "            if (doc.java_type == 'org.taktik.icure.entities.FrontEndMigration' && !doc.deleted && doc.name && doc.userId) {\n" +
            "            emit(doc.name,doc._id);\n" +
            "}\n" +
            "}")
    public FrontEndMigration getByUserIdName(String userId, String name) {
        List<FrontEndMigration> result = queryView("by_userid_name", ComplexKey.of(userId, name));
        return result != null && result.size() == 1 ? result.get(0):null;
    }

}
