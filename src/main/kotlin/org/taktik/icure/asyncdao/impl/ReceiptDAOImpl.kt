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

package org.taktik.icure.asyncdao.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.ReceiptDAO
import org.taktik.icure.entities.Receipt
import org.taktik.icure.properties.CouchDbProperties

@Repository("receiptDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type === 'org.taktik.icure.entities.Receipt' && !doc.deleted) emit(null, doc._id)}")
class ReceiptDAOImpl(
	couchDbProperties: CouchDbProperties,
	@Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher,
	idGenerator: IDGenerator
) : GenericIcureDAOImpl<Receipt>(Receipt::class.java, couchDbProperties, couchDbDispatcher, idGenerator), ReceiptDAO {
	@View(name = "by_reference", map = "classpath:js/receipt/By_ref.js")
	override fun listByReference(ref: String): Flow<Receipt> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		emitAll(client.queryViewIncludeDocs<String, String, Receipt>(createQuery(client, "by_reference").startKey(ref).endKey(ref + "\ufff0").includeDocs(true)).map { it.doc })
	}

	@View(name = "by_date", map = "function(doc) { if (doc.java_type === 'org.taktik.icure.entities.Receipt' && !doc.deleted) emit(doc.created)}")
	override fun listReceiptsAfterDate(date: Long): Flow<Receipt> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		emitAll(client.queryViewIncludeDocs<String, String, Receipt>(createQuery(client, "by_date").startKey(999999999999L).endKey(date).descending(true).includeDocs(true)).map { it.doc })
	}

	@View(name = "by_category", map = "function(doc) { if (doc.java_type === 'org.taktik.icure.entities.Receipt' && !doc.deleted) emit([doc.category,doc.subCategory,doc.created])}")
	override fun listReceiptsByCategory(category: String, subCategory: String, startDate: Long, endDate: Long): Flow<Receipt> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		emitAll(client.queryViewIncludeDocs<Array<String>, String, Receipt>(createQuery(client, "by_date").startKey(ComplexKey.of(category, subCategory, startDate ?: 999999999999L)).endKey(ComplexKey.of(category, subCategory, endDate)).descending(true).includeDocs(true)).map { it.doc })
	}

	@View(name = "by_doc_id", map = "function(doc) { if (doc.java_type === 'org.taktik.icure.entities.Receipt' && !doc.deleted) emit(doc.documentId)}")
	override fun listReceiptsByDocId(date: Long): Flow<Receipt> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		emitAll(client.queryViewIncludeDocs<String, String, Receipt>(createQuery(client, "by_date").startKey(999999999999L).endKey(date).descending(true).includeDocs(true)).map { it.doc })
	}
}
