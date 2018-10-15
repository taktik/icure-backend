package org.taktik.icure.dao;

import org.taktik.icure.entities.Receipt;
import org.taktik.icure.entities.Receipt;

import java.util.List;

public interface ReceiptDAO extends GenericDAO<Receipt> {
	List<Receipt> listByReference(String ref);
}
