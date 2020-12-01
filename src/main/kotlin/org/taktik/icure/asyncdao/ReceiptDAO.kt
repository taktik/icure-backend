package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.entities.Receipt
import java.net.URI

interface ReceiptDAO: GenericDAO<Receipt> {
    fun listByReference(ref: String): Flow<Receipt>

    fun listAfterDate(date: Long): Flow<Receipt>

    fun listByCategory(category: String, subCategory: String, startDate: Long, endDate: Long): Flow<Receipt>

    fun listByDocId(date: Long): Flow<Receipt>
}
