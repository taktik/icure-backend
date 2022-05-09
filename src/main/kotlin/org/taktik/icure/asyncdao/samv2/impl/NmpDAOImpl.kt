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

package org.taktik.icure.asyncdao.samv2.impl

import java.net.URI
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.queryView
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.impl.CouchDbDispatcher
import org.taktik.icure.asyncdao.impl.InternalDAOImpl
import org.taktik.icure.asyncdao.samv2.NmpDAO
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.db.StringUtils
import org.taktik.icure.entities.samv2.Nmp
import org.taktik.icure.properties.CouchDbProperties

@FlowPreview
@Repository("nmpDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.samv2.Nmp' && !doc.deleted) emit( null, doc._id )}")
class NmpDAOImpl(couchDbProperties: CouchDbProperties, @Qualifier("drugCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : InternalDAOImpl<Nmp>(Nmp::class.java, couchDbProperties, couchDbDispatcher, idGenerator), NmpDAO {
	@View(name = "by_language_label", map = "classpath:js/nmp/By_language_label.js")
	override fun findNmpsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent> = flow {
		val dbInstanceUri = URI(couchDbProperties.url)
		val client = couchDbDispatcher.getClient(dbInstanceUri)

		val sanitizedLabel = label?.let { StringUtils.sanitizeString(it) }
		val from = ComplexKey.of(
			language ?: "\u0000",
			sanitizedLabel ?: "\u0000"
		)
		val to = ComplexKey.of(
			language ?: ComplexKey.emptyObject(),
			if (sanitizedLabel == null) ComplexKey.emptyObject() else sanitizedLabel + "\ufff0"
		)
		val viewQuery = pagedViewQuery<Nmp, ComplexKey>(
			client,
			"by_language_label",
			from,
			to,
			paginationOffset.toPaginationOffset { sk -> ComplexKey.of(*sk.mapIndexed { i, s -> if (i == 1) s.let { StringUtils.sanitizeString(it) } else s }.toTypedArray()) },
			false
		)
		emitAll(client.queryView(viewQuery, ComplexKey::class.java, String::class.java, Nmp::class.java))
	}

	override fun listNmpIdsByLabel(language: String?, label: String?): Flow<String> = flow {
		val dbInstanceUri = URI(couchDbProperties.url)
		val client = couchDbDispatcher.getClient(dbInstanceUri)

		val sanitizedLabel = label?.let { StringUtils.sanitizeString(it) }
		val from = ComplexKey.of(
			language ?: "\u0000",
			sanitizedLabel ?: "\u0000"
		)
		val to = ComplexKey.of(
			language ?: ComplexKey.emptyObject(),
			if (sanitizedLabel == null) ComplexKey.emptyObject() else sanitizedLabel + "\ufff0"
		)
		val viewQuery = createQuery(client, "by_language_label")
			.startKey(from)
			.endKey(to)
			.reduce(false)
			.includeDocs(false)
		emitAll(client.queryView<ComplexKey, String>(viewQuery).map { it.id })
	}

	@View(name = "by_cnk", map = "classpath:js/nmp/By_cnk.js")
	override fun listNmpsByCnks(cnks: List<String>): Flow<Nmp> = flow {
		val dbInstanceUri = URI(couchDbProperties.url)
		val client = couchDbDispatcher.getClient(dbInstanceUri)

		val viewQuery = createQuery(client, "by_cnk")
			.keys(cnks)
			.reduce(false)
			.includeDocs(true)
		emitAll(client.queryViewIncludeDocs<String, Int, Nmp>(viewQuery).map { it.doc })
	}
}
