/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.asynclogic.impl

import java.nio.ByteBuffer
import java.nio.ByteBuffer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service
import org.taktik.icure.asyncdao.ReceiptDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.ReceiptLogic
import org.taktik.icure.entities.Receipt
import org.taktik.icure.entities.embed.ReceiptBlobType

@Service
class ReceiptLogicImpl(
	private val receiptDAO: ReceiptDAO,
	private val sessionLogic: AsyncSessionLogic
) : GenericLogicImpl<Receipt, ReceiptDAO>(sessionLogic), ReceiptLogic {


	override fun listReceiptsByReference(ref: String): Flow<Receipt> = flow {
		emitAll(receiptDAO.listByReference(ref))
	}


	override fun getAttachment(receiptId: String, attachmentId: String): Flow<ByteBuffer> = flow {
		emitAll(receiptDAO.getAttachment(receiptId, attachmentId))
	}

	override suspend fun addReceiptAttachment(receipt: Receipt, blobType: ReceiptBlobType, payload: ByteArray): Receipt {
		val newAttachmentId = DigestUtils.sha256Hex(payload)
		val modifiedReceipt = modifyEntities(listOf(receipt.copy(attachmentIds = receipt.attachmentIds + (blobType to newAttachmentId)))).first()
		val contentType = "application/octet-stream"
		return modifiedReceipt.copy(rev = receiptDAO.createAttachment(modifiedReceipt.id, newAttachmentId, modifiedReceipt.rev ?: error("Invalid receipt : no rev"), contentType, flowOf(ByteBuffer.wrap(payload))))
	}

	override fun getGenericDAO(): ReceiptDAO {
		return receiptDAO
	}
}
