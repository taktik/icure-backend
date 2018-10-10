package org.taktik.icure.dao.impl;

import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.EntityReferenceDAO;
import org.taktik.icure.dao.PatientDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.entities.EntityReference;
import org.taktik.icure.entities.Patient;

import java.util.List;

@Repository("entityReferenceDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.EntityReference' && !doc.deleted) emit(doc._id)}")
public class EntityReferenceDAOImpl extends GenericDAOImpl<EntityReference> implements EntityReferenceDAO {
	public EntityReferenceDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbHealthdata") CouchDbICureConnector couchdb, IDGenerator idGenerator) {
		super(EntityReference.class, couchdb, idGenerator);
		initStandardDesignDocument();
	}

	@Override
	public EntityReference getLatest(@NotNull String prefix) {
		ViewQuery viewQuery = createQuery("all").startKey(prefix+"\ufff0").descending(true).includeDocs(true).limit(1);
		List<EntityReference> entityReferences = db.queryView(viewQuery, EntityReference.class);
		return entityReferences.size()>0?entityReferences.get(0):null;
	}
}
