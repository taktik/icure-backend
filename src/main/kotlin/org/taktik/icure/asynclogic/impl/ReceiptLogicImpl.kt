package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service
import org.taktik.icure.asyncdao.ReceiptDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.ReceiptLogic
import org.taktik.icure.entities.Receipt
import org.taktik.icure.entities.embed.ReceiptBlobType
import java.nio.ByteBuffer

@Service
class ReceiptLogicImpl(private val receiptDAO: ReceiptDAO,
                       private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<Receipt, ReceiptDAO>(sessionLogic), ReceiptLogic {

    @ExperimentalCoroutinesApi
    override fun listByReference(ref: String): Flow<Receipt> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(receiptDAO.listByReference(dbInstanceUri, groupId, ref))
    }

    @ExperimentalCoroutinesApi
    override fun getAttachment(receiptId: String, attachmentId: String): Flow<ByteBuffer> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(receiptDAO.getAttachment(dbInstanceUri, groupId, receiptId, attachmentId))
    }

    override suspend fun addReceiptAttachment(receipt: Receipt, blobType: ReceiptBlobType, payload: ByteArray) {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val newAttachmentId = DigestUtils.sha256Hex(payload)
        receipt.attachmentIds[blobType] = newAttachmentId
        updateEntities(listOf(receipt)).collect()
        val contentType = "application/octet-stream"
        receipt.rev = receiptDAO.createAttachment(dbInstanceUri, groupId, receipt.id, newAttachmentId, receipt.rev, contentType, flowOf(ByteBuffer.wrap(payload)))
    }

    override fun getGenericDAO(): ReceiptDAO {
        return receiptDAO
    }
}
