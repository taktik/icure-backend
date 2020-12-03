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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import org.taktik.couchdb.annotation.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.queryView
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.InsuranceDAO
import org.taktik.couchdb.id.IDGenerator
import org.taktik.icure.db.StringUtils
import org.taktik.icure.entities.Insurance
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.createQuery

@Repository("insuranceDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Insurance' && !doc.deleted) emit( null, doc._id )}")
class InsuranceDAOImpl(couchDbProperties: CouchDbProperties,
                       @Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericDAOImpl<Insurance>(couchDbProperties, Insurance::class.java, couchDbDispatcher, idGenerator), InsuranceDAO {

    @View(name = "all_by_code", map = "classpath:js/insurance/all_by_code_map.js")
    override fun listByCode(code: String): Flow<Insurance> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        return client.queryViewIncludeDocs<String, String, Insurance>(createQuery<Insurance>("all_by_code").key(code).includeDocs(true)).map { it.doc }
    }

    @View(name = "all_by_name", map = "classpath:js/insurance/all_by_name_map.js")
    override fun listByName(name: String): Flow<Insurance> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val sanitizedName = StringUtils.sanitizeString(name)

        val ids = client.queryView<Array<String>, String>(createQuery<Insurance>("all_by_name").startKey(ComplexKey.of(sanitizedName)).endKey(ComplexKey.of(sanitizedName + "\uFFF0")).includeDocs(false)).mapNotNull { it.value }
        return getList(ids)
    }
}
