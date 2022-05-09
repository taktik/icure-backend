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
import kotlinx.coroutines.flow.mapNotNull
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.queryView
import org.taktik.icure.asyncdao.KeywordDAO
import org.taktik.icure.entities.Keyword
import org.taktik.icure.properties.CouchDbProperties

@Repository("keywordDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Keyword' && !doc.deleted) emit( null, doc._id )}")
internal class KeywordDAOImpl(
	couchDbProperties: CouchDbProperties,
	@Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher,
	idGenerator: IDGenerator
) : GenericIcureDAOImpl<Keyword>(Keyword::class.java, couchDbProperties, couchDbDispatcher, idGenerator), KeywordDAO {

	override suspend fun getKeyword(keywordId: String): Keyword? {
		return get(keywordId)
	}

	@View(name = "by_user", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Keyword' && !doc.deleted) emit( doc.userId, doc)}")
	override fun getKeywordsByUserId(userId: String): Flow<Keyword> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		emitAll(client.queryView<String, Keyword>(createQuery(client, "by_user").startKey(userId).endKey(userId).includeDocs(false)).mapNotNull { it.value })
	}
}
