package org.taktik.icure.dao;

import org.ektorp.support.View;
import org.taktik.icure.entities.Receipt;
import org.taktik.icure.entities.Receipt;

import java.util.List;

public interface ReceiptDAO extends GenericDAO<Receipt> {
	List<Receipt> listByReference(String ref);

	@View(name = "by_doc_id", map = "function(doc) { if (doc.java_type === 'org.taktik.icure.entities.Receipt' && !doc.deleted) emit(doc.documentId)}")
	abstract List<Receipt> listAfterDate(Long date);

	@View(name = "by_category", map = "function(doc) { if (doc.java_type === 'org.taktik.icure.entities.Receipt' && !doc.deleted) emit([doc.category,doc.subCategory,doc.created])}")
	List<Receipt> listByCategory(String category, String subCategory, Long startDate, Long endDate);

	@View(name = "by_doc_id", map = "function(doc) { if (doc.java_type === 'org.taktik.icure.entities.Receipt' && !doc.deleted) emit(doc.documentId)}")
	List<Receipt> listByDocId(Long date);
}
