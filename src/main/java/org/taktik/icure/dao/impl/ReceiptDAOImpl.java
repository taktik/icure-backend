package org.taktik.icure.dao.impl;

import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.ReceiptDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.entities.EntityReference;
import org.taktik.icure.entities.Receipt;

import java.util.List;

@Repository("receiptDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type === 'org.taktik.icure.entities.Receipt' && !doc.deleted) emit(doc._id)}")
public class ReceiptDAOImpl extends GenericIcureDAOImpl<Receipt> implements ReceiptDAO {
	public ReceiptDAOImpl(@Qualifier("couchdbHealthdata") CouchDbICureConnector db, IDGenerator idGenerator) {
		super(Receipt.class, db, idGenerator);
		this.initStandardDesignDocument();
	}

	@Override
	@View(name = "by_reference", map = "classpath:js/receipt/By_ref.js")
	public List<Receipt> listByReference(String ref) {
		ViewQuery viewQuery = createQuery("by_reference").startKey(ref).endKey(ref+"\ufff0").includeDocs(true);
		List<Receipt> receipts = db.queryView(viewQuery, Receipt.class);

		return receipts;
	}
}
