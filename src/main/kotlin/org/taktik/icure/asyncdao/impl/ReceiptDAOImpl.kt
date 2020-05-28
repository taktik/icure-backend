package org.taktik.icure.asyncdao.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.ektorp.ComplexKey
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.ReceiptDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.Receipt
import org.taktik.icure.utils.createQuery
import java.net.URI

@Repository("receiptDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type === 'org.taktik.icure.entities.Receipt' && !doc.deleted) emit(doc._id)}")
class ReceiptDAOImpl(@Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericIcureDAOImpl<Receipt>(Receipt::class.java, couchDbDispatcher, idGenerator, mapper), ReceiptDAO {
    @View(name = "by_reference", map = "classpath:js/receipt/By_ref.js")
    override fun listByReference(dbInstanceUrl: URI, groupId: String, ref: String): Flow<Receipt> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return client.queryViewIncludeDocs<String, String, Receipt>(createQuery<Receipt>("by_reference").startKey(ref).endKey(ref + "\ufff0").includeDocs(true)).map { it.doc }
    }

    @View(name = "by_date", map = "function(doc) { if (doc.java_type === 'org.taktik.icure.entities.Receipt' && !doc.deleted) emit(doc.created)}")
    override fun listAfterDate(dbInstanceUrl: URI, groupId: String, date: Long): Flow<Receipt> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return client.queryViewIncludeDocs<String, String, Receipt>(createQuery<Receipt>("by_date").startKey(999999999999L).endKey(date).descending(true).includeDocs(true)).map { it.doc }
    }

    @View(name = "by_category", map = "function(doc) { if (doc.java_type === 'org.taktik.icure.entities.Receipt' && !doc.deleted) emit([doc.category,doc.subCategory,doc.created])}")
    override fun listByCategory(dbInstanceUrl: URI, groupId: String, category: String, subCategory: String, startDate: Long, endDate: Long): Flow<Receipt> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return client.queryViewIncludeDocs<Array<String>, String,Receipt>(createQuery<Receipt>("by_date").startKey(ComplexKey.of(category, subCategory, startDate ?: 999999999999L)).endKey(ComplexKey.of(category, subCategory, endDate)).descending(true).includeDocs(true)).map { it.doc }
    }

    @View(name = "by_doc_id", map = "function(doc) { if (doc.java_type === 'org.taktik.icure.entities.Receipt' && !doc.deleted) emit(doc.documentId)}")
    override fun listByDocId(dbInstanceUrl: URI, groupId: String, date: Long): Flow<Receipt> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return client.queryViewIncludeDocs<String, String, Receipt>(createQuery<Receipt>("by_date").startKey(999999999999L).endKey(date).descending(true).includeDocs(true)).map{ it.doc }
    }
}
