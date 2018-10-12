package org.taktik.icure.logic.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.ektorp.AttachmentInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.taktik.icure.dao.EntityReferenceDAO;
import org.taktik.icure.dao.ReceiptDAO;
import org.taktik.icure.entities.EntityReference;
import org.taktik.icure.entities.Receipt;
import org.taktik.icure.entities.embed.ReceiptBlobType;
import org.taktik.icure.logic.ReceiptLogic;
import org.taktik.icure.logic.impl.GenericLogicImpl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
public class ReceiptLogicImpl extends GenericLogicImpl<Receipt, ReceiptDAO> implements ReceiptLogic {
	private ReceiptDAO receiptDAO;

	@Override
	public List<Receipt> listByReference(String ref) {
		return receiptDAO.listByReference(ref);
	}

	@Override
	public byte[] getAttachment(String receiptId, String attachmentId) throws IOException {
		return IOUtils.toByteArray(receiptDAO.getAttachmentInputStream(receiptId, attachmentId));
	}

	@Override
	public void addReceiptAttachment(Receipt receipt, ReceiptBlobType blobType, byte[] payload) {
		String newAttachmentId = DigestUtils.sha256Hex(payload);

		receipt.getAttachmentIds().put(blobType, newAttachmentId);
		updateEntities(Collections.singletonList(receipt));

		AttachmentInputStream a = new AttachmentInputStream(newAttachmentId, new ByteArrayInputStream(payload), "application/octet-stream");
		receipt.setRev(receiptDAO.createAttachment(receipt.getId(), receipt.getRev(), a));
	}

	@Autowired
	public void setReceiptDAO(ReceiptDAO receiptDAO) {
		this.receiptDAO = receiptDAO;
	}

	@Override
	protected ReceiptDAO getGenericDAO() {
		return receiptDAO;
	}
}
