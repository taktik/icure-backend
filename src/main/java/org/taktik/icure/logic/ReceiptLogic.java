package org.taktik.icure.logic;

import org.taktik.icure.entities.Receipt;
import org.taktik.icure.entities.embed.ReceiptBlobType;

import java.io.IOException;
import java.util.List;

public interface ReceiptLogic extends EntityPersister<Receipt, String> {
	List<Receipt> listByReference(String ref);

	byte[] getAttachment(String receiptId, String attachmentId) throws IOException;

	void addReceiptAttachment(Receipt receipt, ReceiptBlobType blobType, byte[] payload);
}
