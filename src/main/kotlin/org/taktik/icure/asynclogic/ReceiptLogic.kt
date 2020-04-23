package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.asyncdao.ReceiptDAO
import org.taktik.icure.entities.Receipt
import org.taktik.icure.entities.embed.ReceiptBlobType
import java.nio.ByteBuffer

interface ReceiptLogic : EntityPersister<Receipt, String> {
    fun listByReference(ref: String): Flow<Receipt>
    fun getAttachment(receiptId: String, attachmentId: String): Flow<ByteBuffer>

    suspend fun addReceiptAttachment(receipt: Receipt, blobType: ReceiptBlobType, payload: ByteArray) : Receipt
    fun getGenericDAO(): ReceiptDAO
}
