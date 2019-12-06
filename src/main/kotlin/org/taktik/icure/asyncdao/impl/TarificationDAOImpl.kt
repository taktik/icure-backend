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
import org.apache.axis2.databinding.types.xsd.Integer
import org.ektorp.ComplexKey
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.TarificationDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.db.StringUtils
import org.taktik.icure.entities.Tarification
import java.net.URI

@Repository("tarificationDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Tarification' && !doc.deleted) emit( null, doc._id )}")
class TarificationDAOImpl(@Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericDAOImpl<Tarification>(Tarification::class.java, couchDbDispatcher, idGenerator), TarificationDAO {

    @View(name = "by_type_code_version", map = "classpath:js/tarif/By_type_code_version.js", reduce = "function(keys, values, rereduce) {if (rereduce) {return sum(values);} else {return values.length;}}")
    override fun findTarifications(dbInstanceUrl: URI, groupId: String, type: String?, code: String?, version: String?): Flow<Tarification> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return client.queryViewIncludeDocs<ComplexKey, String, Tarification>(createQuery("by_type_code_version")
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
    }

    @View(name = "by_region_type_code_version", map = "classpath:js/tarif/By_region_type_code_version.js", reduce = "function(keys, values, rereduce) {if (rereduce) {return sum(values);} else {return values.length;}}")
    override fun findTarifications(dbInstanceUrl: URI, groupId: String, region: String?, type: String?, code: String?, version: String?): Flow<Tarification> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return client.queryViewIncludeDocs<ComplexKey, String, Tarification>(
                createQuery("by_region_type_code_version")
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
    }

    override fun findTarifications(dbInstanceUrl: URI, groupId: String, region: String?, type: String?, code: String?, version: String?, pagination: PaginationOffset<List<String?>?>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val from = pagination?.startKey?.let { ComplexKey.of(it) }
                ?: ComplexKey.of(
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
        val viewQuery = pagedViewQuery("by_region_type_code_version", from, to, PaginationOffset(pagination.limit, pagination.startDocumentId), false)
        return client.queryViewIncludeDocs<ComplexKey, String, Tarification>(viewQuery)
    }

    @View(name = "by_language_label", map = "classpath:js/tarif/By_language_label.js")
    override fun findTarificationsByLabel(dbInstanceUrl: URI, groupId: String, region: String?, language: String?, label: String?, pagination: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val label = label?.let { StringUtils.sanitizeString(it) }

        val startKey: MutableList<String?> = pagination.startKey.toMutableList()
        startKey.takeIf { it.size > 2 }?.get(2)?.let { startKey[2] = StringUtils.sanitizeString(it) }
        val from = startKey.let { ComplexKey.of(*startKey.toTypedArray()) }
                .takeIf { it.components.isEmpty() }.let {
                    ComplexKey.of(
                            region ?: "\u0000",
                            language ?: "\u0000",
                            label ?: "\u0000"
                    )
                }

        val to = ComplexKey.of(
                if (region == null) ComplexKey.emptyObject() else if (language == null) region + "\ufff0" else region,
                if (language == null) ComplexKey.emptyObject() else if (label == null) language + "\ufff0" else language,
                if (label == null) ComplexKey.emptyObject() else label + "\ufff0"
        )
        val viewQuery = pagedViewQuery("by_language_label", from, to, PaginationOffset(pagination.limit, pagination.startDocumentId), false)
        return client.queryViewIncludeDocs<ComplexKey, Integer, Tarification>(viewQuery)
    }

    @View(name = "by_language_type_label", map = "classpath:js/tarif/By_language_label.js")
    override fun findTarificationsByLabel(dbInstanceUrl: URI, groupId: String, region: String?, language: String?, type: String?, label: String?, pagination: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val label = label?.let { StringUtils.sanitizeString(it) }

        val startKey = pagination.startKey.toMutableList()
        startKey.takeIf { it.size > 3 }?.get(3)?.let { startKey[3] = StringUtils.sanitizeString(it) }
        val from = startKey.let { ComplexKey.of(*startKey.toTypedArray()) }
                .takeIf { it.components.isEmpty() }.let {
                    ComplexKey.of(
                            region ?: "\u0000",
                            language ?: "\u0000",
                            type ?: "\u0000",
                            label ?: "\u0000"
                    )
                }

        val to = ComplexKey.of(
                if (region == null) ComplexKey.emptyObject() else if (language == null) region + "\ufff0" else region,
                if (language == null) ComplexKey.emptyObject() else if (type == null) language + "\ufff0" else language,
                if (type == null) ComplexKey.emptyObject() else if (label == null) type + "\ufff0" else language,
                if (label == null) ComplexKey.emptyObject() else label + "\ufff0"
        )
        val viewQuery = pagedViewQuery("by_language_label", from, to, PaginationOffset(pagination.limit, pagination.startDocumentId), false)
        return client.queryViewIncludeDocs<ComplexKey, Integer, Tarification>(viewQuery)
    }
}
