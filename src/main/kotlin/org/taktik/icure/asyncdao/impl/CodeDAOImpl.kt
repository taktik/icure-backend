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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.queryView
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.CodeDAO
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.db.StringUtils
import org.taktik.icure.entities.base.Code
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.spring.asynccache.AsyncCacheManager


private const val s1 = "\u0000"

@ExperimentalCoroutinesApi
@Repository("codeDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.base.Code' && !doc.deleted) emit( null, doc._id )}")
class CodeDAOImpl(couchDbProperties: CouchDbProperties,
                  @Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator, @Qualifier("asyncCacheManager") asyncCacheManager: AsyncCacheManager) : CachedDAOImpl<Code>(Code::class.java, couchDbProperties, couchDbDispatcher, idGenerator, asyncCacheManager), CodeDAO {

    @View(name = "by_type_code_version", map = "classpath:js/code/By_type_code_version.js", reduce = "_count")
    override fun listCodesBy(type: String?, code: String?, version: String?): Flow<Code> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        emitAll(
                client.queryViewIncludeDocsNoValue<Array<String>, Code>(
                        createQuery(client, "by_type_code_version")
                                .includeDocs(true)
                                .reduce(false)
                                .startKey(ComplexKey.of(
                                        type,
                                        code,
                                        version
                                ))
                                .endKey(ComplexKey.of(
                                        type ?: ComplexKey.emptyObject(),
                                        code ?: ComplexKey.emptyObject(),
                                        version ?: ComplexKey.emptyObject()
                                ))).map { it.doc }

        )
    }

    override fun listCodesByType(type: String?): Flow<String> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        emitAll(
                client.queryView<String, String>(
                        createQuery(client, "by_type_code_version")
                                .includeDocs(false)
                                .group(true)
                                .groupLevel(2)
                                .startKey(ComplexKey.of(type, null, null))
                                .endKey(ComplexKey.of(if (type == null) null else type + "\ufff0", null, null))).mapNotNull { it.key }
        )
    }

    @View(name = "by_region_type_code_version", map = "classpath:js/code/By_region_type_code_version.js", reduce = "_count")
    override fun listCodesBy(region: String?, type: String?, code: String?, version: String?): Flow<Code> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        emitAll(
                client.queryViewIncludeDocsNoValue<Array<String>, Code>(
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

    override fun listCodesByRegionAndType(region: String?, type: String?): Flow<String> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        emitAll(client.queryView<List<String>, String>(
                createQuery(client, "by_region_type_code_version")
                        .includeDocs(false)
                        .group(true)
                        .groupLevel(2)
                        .startKey(ComplexKey.of(region, type ?: "", null, null))
                        .endKey(ComplexKey.of(region, if (type == null) ComplexKey.emptyObject() else type + "\ufff0", null, null))
        ).mapNotNull { it.key?.get(1) })
    }

    override fun findCodesBy(region: String?, type: String?, code: String?, version: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val from = ComplexKey.of(region, type, code, version)
        val to = ComplexKey.of(
                region ?: ComplexKey.emptyObject(),
                type ?: ComplexKey.emptyObject(),
                if (code == null) ComplexKey.emptyObject() else code + "\ufff0",
                if (version == null) ComplexKey.emptyObject() else version + "\ufff0"
        )

        val viewQuery = pagedViewQuery<Code, ComplexKey>(
                client,
                "by_region_type_code_version",
                from,
                to,
                paginationOffset.toPaginationOffset { ComplexKey.of(*it.toTypedArray()) },
                false
        )
        emitAll(client.queryView(viewQuery, Array<String>::class.java, String::class.java, Code::class.java))
    }

    @View(name = "by_language_label", map = "classpath:js/code/By_language_label.js")
    override fun findCodesByLabel(region: String?, language: String?, label: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val sanitizedLabel= label?.let { StringUtils.sanitizeString(it) }
        val startKey = paginationOffset.startKey
        val from =
            ComplexKey.of(
                    region ?: "\u0000",
                    language ?: "\u0000",
                    sanitizedLabel ?: "\u0000"
            )

        val to = ComplexKey.of(
                if (region == null) ComplexKey.emptyObject() else if (language == null) region + "\ufff0" else region,
                if (language == null) ComplexKey.emptyObject() else if (sanitizedLabel == null) language + "\ufff0" else language,
                if (sanitizedLabel == null) ComplexKey.emptyObject() else sanitizedLabel + "\ufff0"
        )

        val viewQuery = pagedViewQuery<Code, ComplexKey>(
                client,
                "by_language_label",
                from,
                to,
                paginationOffset.toPaginationOffset { sk -> ComplexKey.of(*sk.mapIndexed { i, s -> if (i==2) s?.let { StringUtils.sanitizeString(it)} else s }.toTypedArray()) },
                false
        )
        emitAll(client.queryView(viewQuery, Array<String>::class.java, String::class.java, Code::class.java))
    }

    @View(name = "by_language_type_label", map = "classpath:js/code/By_language_type_label.js")
    override fun findCodesByLabel(region: String?, language: String?, type: String?, label: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val sanitizedLabel= label?.let { StringUtils.sanitizeString(it) }
        val from = ComplexKey.of(
                    region ?: "\u0000",
                    language ?: "\u0000",
                    type ?: "\u0000",
                    sanitizedLabel ?: "\u0000"
            )

        val to = ComplexKey.of(
			if (region == null) ComplexKey.emptyObject() else if (language == null) region + "\ufff0" else region,
			language ?: ComplexKey.emptyObject(),
			type ?: ComplexKey.emptyObject(),
			if (sanitizedLabel == null) ComplexKey.emptyObject() else sanitizedLabel + "\ufff0"
		)

        val viewQuery = pagedViewQuery<Code, ComplexKey>(
                client,
                "by_language_type_label",
                from,
                to,
                paginationOffset.toPaginationOffset { sk -> ComplexKey.of(*sk.mapIndexed { i, s -> if (i==3) s?.let { StringUtils.sanitizeString(it)} else s }.toTypedArray()) },
                false
        )
        emitAll(client.queryView(viewQuery, Array<String>::class.java, String::class.java, Code::class.java))
    }

    @View(name = "by_qualifiedlink_id", map = "classpath:js/code/By_qualifiedlink_id.js")
    override fun findCodesByQualifiedLinkId(region: String?, linkType: String, linkedId: String?, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val from =
            ComplexKey.of(
                    linkType,
                    linkedId
            )
        val to = ComplexKey.of(
                        linkType,
                        linkedId ?: ComplexKey.emptyObject()
            )

        val viewQuery = pagedViewQuery<Code, ComplexKey>(
                client,
                "by_qualifiedlink_id",
                from,
                to,
                paginationOffset.toPaginationOffset { ComplexKey.of(*it.toTypedArray()) },
                false
        )
        emitAll(client.queryView(viewQuery, Array<String>::class.java, String::class.java, Code::class.java))
    }

    override fun listCodeIdsByLabel(region: String?, language: String?, label: String?): Flow<String> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val sanitizedLabel= label?.let { StringUtils.sanitizeString(it) }
        val from =
            ComplexKey.of(
                region ?: "\u0000",
                language ?: "\u0000",
                    sanitizedLabel ?: "\u0000"
                         )

        val to = ComplexKey.of(
            if (region == null) ComplexKey.emptyObject() else if (language == null) region + "\ufff0" else region,
            if (language == null) ComplexKey.emptyObject() else if (sanitizedLabel == null) language + "\ufff0" else language,
            if (sanitizedLabel == null) ComplexKey.emptyObject() else sanitizedLabel + "\ufff0"
                              )

        emitAll(
                client.queryView<String, String>(
                        createQuery(client, "by_language_label")
                                .includeDocs(false)
                                .startKey(from)
                                .endKey(to)).mapNotNull { it.key }
        )
    }

    override fun listCodeIdsByLabel(region: String?, language: String?, type: String?, label: String?): Flow<String> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val sanitizedLabel= label?.let { StringUtils.sanitizeString(it) }
        val from =
            ComplexKey.of(
                region ?: "\u0000",
                language ?: "\u0000",
                type ?: "\u0000",
                    sanitizedLabel ?: "\u0000"
                         )
        val to = ComplexKey.of(
            if (region == null) ComplexKey.emptyObject() else if (language == null) region + "\ufff0" else region,
            language ?: ComplexKey.emptyObject(),
            type ?: ComplexKey.emptyObject(),
            if (sanitizedLabel == null) ComplexKey.emptyObject() else sanitizedLabel + "\ufff0"
                              )

        emitAll(client.queryView<String,String>(
                createQuery(client, "by_language_type_label")
                        .includeDocs(false)
                        .startKey(from)
                        .endKey(to)).mapNotNull { it.id }
        )

    }

    override fun listCodeIdsByQualifiedLinkId(linkType: String, linkedId: String?): Flow<String> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val from = ComplexKey.of(
                linkType,
                linkedId
        )
        val to = ComplexKey.of(
                        linkType,
                        linkedId ?: ComplexKey.emptyObject()
        )

        emitAll(client.queryView<String,String>(
                createQuery(client, "by_qualifiedlink_id")
                        .includeDocs(false)
                        .startKey(from)
                        .endKey(to)).mapNotNull { it.id })
    }

    override fun getCodesByIdsForPagination(ids: List<String>): Flow<ViewQueryResultEvent> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        emitAll(client.getForPagination(ids, Code::class.java))
    }

	override suspend fun isValid(codeType: String, codeCode: String, codeVersion: String?) = listCodesBy(codeType, codeCode, codeVersion).firstOrNull() != null

    override suspend fun getCodeByLabel(region: String, label: String, ofType: String, labelLang : List<String>) : Code? {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val sanitizedLabel= label.let { StringUtils.sanitizeString(it) }
        for (lang in labelLang) {
            val codeFlow = client.queryViewIncludeDocsNoValue<Array<String>, Code>(
                    createQuery(client, "by_region_type_code_version")
                            .includeDocs(true)
                            .reduce(false)
                            .key(ComplexKey.of(
                                    region,
                                    lang,
                                    ofType,
                                    sanitizedLabel
                            ))).map { it.doc }.filter { c -> c.label?.get(lang)?.let { StringUtils.sanitizeString(it) } == sanitizedLabel }
            val code = codeFlow.firstOrNull()
            if (code != null) {
                return code
            }
        }

		//throw IllegalArgumentException("code of type $ofType not found for label $label in languages $labelLang")
        return null
	}
}
