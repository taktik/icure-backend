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
import org.taktik.couchdb.annotation.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.TarificationDAO
import org.taktik.couchdb.id.IDGenerator
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.db.StringUtils
import org.taktik.icure.entities.Tarification
import org.taktik.icure.properties.CouchDbProperties



@Repository("tarificationDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Tarification' && !doc.deleted) emit( null, doc._id )}")
class TarificationDAOImpl(couchDbProperties: CouchDbProperties,
                          @Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericDAOImpl<Tarification>(couchDbProperties, Tarification::class.java, couchDbDispatcher, idGenerator), TarificationDAO {

    @View(name = "by_type_code_version", map = "classpath:js/tarif/By_type_code_version.js", reduce = "_count")
    override fun findTarifications(type: String?, code: String?, version: String?): Flow<Tarification> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        emitAll(
                client.queryViewIncludeDocs<ComplexKey, String, Tarification>(createQuery(client, "by_type_code_version")
                        .includeDocs(true)
                        .reduce(false)
                        .startKey(ComplexKey.of(
                                type ?: "\u0000",
                                code ?: "\u0000",
                                version ?: "\u0000"
                        ))
                        .endKey(ComplexKey.of(
                                type ?: ComplexKey.emptyObject(),
                                code ?: ComplexKey.emptyObject(),
                                version ?: ComplexKey.emptyObject()
                        ))).map { it.doc }

        )
    }

    @View(name = "by_region_type_code_version", map = "classpath:js/tarif/By_region_type_code_version.js", reduce = "_count")
    override fun findTarifications(region: String?, type: String?, code: String?, version: String?): Flow<Tarification> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        emitAll(
                client.queryViewIncludeDocs<Array<String>, String, Tarification>(
                        createQuery(client, "by_region_type_code_version")
                                .includeDocs(true)
                                .reduce(false)
                                .startKey(ComplexKey.of(
                                        region ?: "\u0000",
                                        type ?: "\u0000",
                                        code ?: "\u0000",
                                        version ?: "\u0000"
                                ))
                                .endKey(ComplexKey.of(
                                        region ?: ComplexKey.emptyObject(),
                                        type ?: ComplexKey.emptyObject(),
                                        code ?: ComplexKey.emptyObject(),
                                        version ?: ComplexKey.emptyObject()
                                ))).map { it.doc }
        )
    }

    override fun findTarifications(region: String?, type: String?, code: String?, version: String?, pagination: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val from = ComplexKey.of(
                        region ?: "\u0000",
                        type ?: "\u0000",
                        code ?: "\u0000",
                        version ?: "\u0000"
                )
        val to = ComplexKey.of(
                region?.let { it + "" } ?: ComplexKey.emptyObject(),
                type?.let { it + "" } ?: ComplexKey.emptyObject(),
                code?.let { it + "" } ?: ComplexKey.emptyObject(),
                version?.let { it + "" } ?: ComplexKey.emptyObject()
        )
        val viewQuery = pagedViewQuery<Tarification, ComplexKey>(client, "by_region_type_code_version", from, to, pagination.toPaginationOffset { ComplexKey.of(*it.toTypedArray()) }, false)
        emitAll(client.queryView(viewQuery, ComplexKey::class.java, String::class.java, Tarification::class.java))

    }

    @View(name = "by_language_label", map = "classpath:js/tarif/By_language_label.js")
    override fun findTarificationsByLabel(region: String?, language: String?, label: String?, pagination: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val label = label?.let { StringUtils.sanitizeString(it) }

        val startKey = pagination.startKey?.toMutableList()
        startKey?.takeIf { it.size > 2 }?.get(2)?.let { startKey[2] = StringUtils.sanitizeString(it) }
        val from = ComplexKey.of(
                region ?: "\u0000",
                language ?: "\u0000",
                label ?: "\u0000"
        )

        val to = ComplexKey.of(
                if (region == null) ComplexKey.emptyObject() else if (language == null) region + "\ufff0" else region,
                if (language == null) ComplexKey.emptyObject() else if (label == null) language + "\ufff0" else language,
                if (label == null) ComplexKey.emptyObject() else label + "\ufff0"
        )
        val viewQuery = pagedViewQuery<Tarification, ComplexKey>(client, "by_language_label", from, to, pagination.toPaginationOffset { ComplexKey.of(*it.toTypedArray()) }, false)
        emitAll(client.queryView(viewQuery, Array<String>::class.java, Integer::class.java, Tarification::class.java))
    }

    @View(name = "by_language_type_label", map = "classpath:js/tarif/By_language_label.js")
    override fun findTarificationsByLabel(region: String?, language: String?, type: String?, label: String?, pagination: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val label = label?.let { StringUtils.sanitizeString(it) }

        val startKey = pagination.startKey?.toMutableList()
        startKey?.takeIf { it.size > 3 }?.get(3)?.let { startKey[3] = StringUtils.sanitizeString(it) }
        val from = ComplexKey.of(
                region ?: "\u0000",
                language ?: "\u0000",
                type ?: "\u0000",
                label ?: "\u0000"
        )

        val to = ComplexKey.of(
                if (region == null) ComplexKey.emptyObject() else if (language == null) region + "\ufff0" else region,
                if (language == null) ComplexKey.emptyObject() else if (type == null) language + "\ufff0" else language,
                if (type == null) ComplexKey.emptyObject() else if (label == null) type + "\ufff0" else language,
                if (label == null) ComplexKey.emptyObject() else label + "\ufff0"
        )
        val viewQuery = pagedViewQuery<Tarification, ComplexKey>(client, "by_language_label", from, to, pagination.toPaginationOffset { ComplexKey.of(*it.toTypedArray()) }, false)
        emitAll(client.queryView(viewQuery, Array<String>::class.java, Integer::class.java, Tarification::class.java))
    }
}
