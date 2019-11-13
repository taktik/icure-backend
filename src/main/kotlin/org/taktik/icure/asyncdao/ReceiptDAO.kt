package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.ektorp.support.View
import org.taktik.icure.dao.GenericDAO
import org.taktik.icure.entities.Receipt
import java.net.URI

interface ReceiptDAO {
    fun listByReference(dbInstanceUrl: URI, groupId: String, ref: String): Flow<Receipt>

    @View(name = "by_doc_id", map = "function(doc) { if (doc.java_type === 'org.taktik.icure.entities.Receipt' && !doc.deleted) emit(doc.documentId)}")
    fun listAfterDate(dbInstanceUrl: URI, groupId: String, date: Long): Flow<Receipt>

    @View(name = "by_category", map = "function(doc) { if (doc.java_type === 'org.taktik.icure.entities.Receipt' && !doc.deleted) emit([doc.category,doc.subCategory,doc.created])}")
    fun listByCategory(dbInstanceUrl: URI, groupId: String, category: String, subCategory: String, startDate: Long, endDate: Long): Flow<Receipt>

    @View(name = "by_doc_id", map = "function(doc) { if (doc.java_type === 'org.taktik.icure.entities.Receipt' && !doc.deleted) emit(doc.documentId)}")
    fun listByDocId(dbInstanceUrl: URI, groupId: String, date: Long): Flow<Receipt>
}
