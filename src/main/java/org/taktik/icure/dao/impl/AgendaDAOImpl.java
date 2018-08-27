package org.taktik.icure.dao.impl;

import org.ektorp.ComplexKey;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.AgendaDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.Agenda;
import org.taktik.icure.entities.CalendarItem;

import java.util.List;

@Repository("AgendaDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Agenda' && !doc.deleted) emit( null, doc._id )}")
public class AgendaDAOImpl extends GenericDAOImpl<Agenda> implements AgendaDAO {
    @Autowired
    public AgendaDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbHealthdata") CouchDbICureConnector db, IDGenerator idGenerator) {
        super(Agenda.class, db, idGenerator);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "by_user", map = "classpath:js/agenda/by_user.js")
    public List<Agenda> getAllAgendaForUser(String userId) {

        ViewQuery viewQuery = createQuery("by_user")
            .startKey(userId)
            .endKey(userId)
            .includeDocs(false);

        return db.queryView(viewQuery, Agenda.class);
    }

    @Override
    @View(name = "readable_by_user", map = "classpath:js/agenda/readable_by_user.js")
    public List<Agenda> getReadableAgendaForUser(String userId) {
        ViewQuery viewQuery = createQuery("readable_by_user")
            .startKey(userId)
            .endKey(userId)
            .includeDocs(false);

        return db.queryView(viewQuery, Agenda.class);
    }
}
