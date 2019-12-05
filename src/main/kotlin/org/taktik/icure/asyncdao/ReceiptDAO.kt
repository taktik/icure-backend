package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.ektorp.support.View
import org.taktik.icure.dao.GenericDAO
import org.taktik.icure.entities.Receipt
import java.net.URI

interface ReceiptDAO: GenericDAO<Receipt> {
    fun listByReference(dbInstanceUrl: URI, groupId: String, ref: String): Flow<Receipt>

    fun listAfterDate(dbInstanceUrl: URI, groupId: String, date: Long): Flow<Receipt>

    fun listByCategory(dbInstanceUrl: URI, groupId: String, category: String, subCategory: String, startDate: Long, endDate: Long): Flow<Receipt>

    fun listByDocId(dbInstanceUrl: URI, groupId: String, date: Long): Flow<Receipt>
}
