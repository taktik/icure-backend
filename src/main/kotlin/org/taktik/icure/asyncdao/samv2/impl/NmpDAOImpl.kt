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

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.queryView
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.impl.CouchDbDispatcher
import org.taktik.icure.asyncdao.impl.InternalDAOImpl
import org.taktik.icure.asyncdao.samv2.NmpDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.db.StringUtils
import org.taktik.icure.entities.samv2.Nmp
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.createQuery
import org.taktik.icure.utils.pagedViewQuery
import java.net.URI

@FlowPreview
@Repository("nmpDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.samv2.Nmp' && !doc.deleted) emit( null, doc._id )}")
class NmpDAOImpl(couchDbProperties: CouchDbProperties, @Qualifier("drugCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : InternalDAOImpl<Nmp>(Nmp::class.java, couchDbProperties, couchDbDispatcher, idGenerator), NmpDAO {
    @View(name = "by_language_label", map = "classpath:js/nmp/By_language_label.js")
    override fun findNmpsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent> {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri)

        val sanitizedLabel= label?.let { StringUtils.sanitizeString(it) }
        val from = ComplexKey.of(
                    language ?: "\u0000",
                    sanitizedLabel ?: "\u0000"
            )
        val to = ComplexKey.of(
                language ?: ComplexKey.emptyObject(),
                if (sanitizedLabel == null) ComplexKey.emptyObject() else sanitizedLabel + "\ufff0"
        )
        val viewQuery = pagedViewQuery<Nmp, ComplexKey>(
                "by_language_label",
                from,
                to,
                paginationOffset.toPaginationOffset { sk -> ComplexKey.of(*sk.mapIndexed { i, s -> if (i==1) s.let { StringUtils.sanitizeString(it)} else s }.toTypedArray()) },
                false
        )
        return client.queryView(viewQuery, ComplexKey::class.java, String::class.java, Nmp::class.java)
    }

    override fun listNmpIdsByLabel(language: String?, label: String?): Flow<String> {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri)

        val sanitizedLabel= label?.let { StringUtils.sanitizeString(it) }
        val from = ComplexKey.of(
                language ?: "\u0000",
                sanitizedLabel ?: "\u0000"
        )
        val to = ComplexKey.of(
                language ?: ComplexKey.emptyObject(),
                if (sanitizedLabel == null) ComplexKey.emptyObject() else sanitizedLabel + "\ufff0"
        )
        val viewQuery = createQuery<Nmp>("by_language_label")
                .startKey(from)
                .endKey(to)
                .reduce(false)
                .includeDocs(false)
        return client.queryView<ComplexKey,String>(viewQuery).map { it.id }
    }

    @View(name = "by_cnk", map = "classpath:js/nmp/By_cnk.js")
    override fun listNmpsByCnks(cnks: List<String>): Flow<Nmp> {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri)

        val viewQuery = createQuery<Nmp>("by_cnk")
                .keys(cnks)
                .reduce(false)
                .includeDocs(true)
        return client.queryViewIncludeDocs<String, Int, Nmp>(viewQuery).map { it.doc }
    }

}
