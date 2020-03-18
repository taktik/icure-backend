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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import ma.glasnost.orika.MapperFacade
import org.ektorp.ComplexKey
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.queryView
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.InsuranceDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.db.StringUtils
import org.taktik.icure.entities.Insurance
import org.taktik.icure.utils.createQuery
import java.net.URI

@Repository("insuranceDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Insurance' && !doc.deleted) emit( null, doc._id )}")
class InsuranceDAOImpl(@Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator, mapper: MapperFacade) : GenericDAOImpl<Insurance>(Insurance::class.java, couchDbDispatcher, idGenerator, mapper), InsuranceDAO {

    @View(name = "all_by_code", map = "classpath:js/insurance/all_by_code_map.js")
    override fun listByCode(dbInstanceUrl: URI, groupId: String, code: String): Flow<Insurance> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        return client.queryViewIncludeDocs<String, String, Insurance>(createQuery<Insurance>("all_by_code").key(code).includeDocs(true)).map { it.doc }
    }

    @View(name = "all_by_name", map = "classpath:js/insurance/all_by_name_map.js")
    override fun listByName(dbInstanceUrl: URI, groupId: String, name: String): Flow<Insurance> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val sanitizedName = StringUtils.sanitizeString(name)

        val ids = client.queryView<Array<String>, String>(createQuery<Insurance>("all_by_name").startKey(ComplexKey.of(sanitizedName)).endKey(ComplexKey.of(sanitizedName + "\uFFF0")).includeDocs(false)).mapNotNull { it.value }
        return getList(dbInstanceUrl, groupId, ids)
    }
}
