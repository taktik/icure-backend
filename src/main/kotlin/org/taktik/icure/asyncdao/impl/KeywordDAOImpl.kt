/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.asyncdao.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.queryView
import org.taktik.icure.asyncdao.KeywordDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.Keyword
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.createQuery
import java.net.URI

@Repository("keywordDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Keyword' && !doc.deleted) emit( null, doc._id )}")
internal class KeywordDAOImpl(couchDbProperties: CouchDbProperties,
                              @Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericIcureDAOImpl<Keyword>(Keyword::class.java, couchDbProperties, couchDbDispatcher, idGenerator), KeywordDAO {

    override suspend fun getKeyword(keywordId: String): Keyword? {
        return get(keywordId)
    }

    @View(name = "by_user", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Keyword' && !doc.deleted) emit( doc.userId, doc)}")
    override fun getByUserId(userId: String): Flow<Keyword> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        return client.queryView<String, Keyword>(createQuery<Keyword>("by_user").startKey(userId).endKey(userId).includeDocs(false)).mapNotNull { it.value }
    }
}
