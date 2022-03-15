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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.taktik.couchdb.annotation.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.queryView
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.impl.CouchDbDispatcher
import org.taktik.icure.asyncdao.impl.InternalDAOImpl
import org.taktik.icure.asyncdao.samv2.AmpDAO
import org.taktik.couchdb.id.IDGenerator
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.db.StringUtils
import org.taktik.icure.entities.samv2.Amp
import org.taktik.icure.entities.samv2.Paragraph
import org.taktik.icure.entities.samv2.SamVersion
import org.taktik.icure.properties.CouchDbProperties


import java.net.URI

@ExperimentalCoroutinesApi
@Repository("ampDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.samv2.Amp' && !doc.deleted) emit( null, doc._id )}")
class AmpDAOImpl(couchDbProperties: CouchDbProperties, @Qualifier("drugCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : InternalDAOImpl<Amp>(Amp::class.java, couchDbProperties, couchDbDispatcher, idGenerator), AmpDAO {

    @View(name = "by_dmppcode", map = "classpath:js/amp/By_dmppcode.js")
    override fun findAmpsByDmppCode(dmppCode: String): Flow<ViewQueryResultEvent> = flow {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri)

        val from = dmppCode
        val to = dmppCode

        val viewQuery = createQuery(client, "by_dmppcode")
                .startKey(from)
                .endKey(to)
                .includeDocs(true)
        emitAll(client.queryView(viewQuery, String::class.java, String::class.java, Amp::class.java))
    }


    @View(name = "by_groupcode", map = "classpath:js/amp/By_groupcode.js")
    override fun findAmpsByVmpGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> = flow {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri)

        val from = vmpgCode
        val to = vmpgCode

        val viewQuery = pagedViewQuery<Amp, String>(client, "by_groupcode", from, to, paginationOffset, false)
        emitAll(client.queryView(viewQuery, String::class.java, String::class.java, Amp::class.java))
    }

    @View(name = "by_atc", map = "classpath:js/amp/By_atc.js")
    override fun findAmpsByAtc(atc: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> = flow {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri)

        val from = atc
        val to = atc

        val viewQuery = pagedViewQuery<Amp,String>(client, "by_atc", from, to, paginationOffset, false)
        emitAll(client.queryView(viewQuery, String::class.java, String::class.java, Amp::class.java))
    }

    @View(name = "by_groupid", map = "classpath:js/amp/By_groupid.js")
    override fun findAmpsByVmpGroupId(vmpgId: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> = flow {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri)

        val from = vmpgId
        val to = vmpgId

        val viewQuery = pagedViewQuery<Amp,String>(client, "by_groupid", from, to, paginationOffset, false)
        emitAll(client.queryView(viewQuery, String::class.java, String::class.java, Amp::class.java))
    }

    @View(name = "by_vmpcode", map = "classpath:js/amp/By_vmpcode.js")
    override fun findAmpsByVmpCode(vmpCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> = flow {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri)

        val from = vmpCode
        val to = vmpCode

        val viewQuery = pagedViewQuery<Amp,String>(client, "by_vmpcode", from, to, paginationOffset, false)
        emitAll(client.queryView(viewQuery, String::class.java, String::class.java, Amp::class.java))
    }

    @View(name = "by_vmpid", map = "classpath:js/amp/By_vmpid.js")
    override fun findAmpsByVmpId(vmpId: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> = flow {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri)

        val from = vmpId
        val to = vmpId

        val viewQuery = pagedViewQuery<Amp,String>(client, "by_vmpid", from, to, paginationOffset, false)
        emitAll(client.queryView(viewQuery, String::class.java, String::class.java, Amp::class.java))
    }

    override fun listAmpIdsByVmpGroupCode(vmpgCode: String): Flow<String> = flow {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri)

        val from = vmpgCode
        val to = vmpgCode

        val viewQuery = createQuery(client, "by_groupcode")
                .startKey(from)
                .endKey(to)
                .reduce(false)
                .includeDocs(false)
        emitAll(client.queryView<String, String>(viewQuery).map { it.id })
    }

    override fun listAmpIdsByVmpGroupId(vmpgId: String): Flow<String> = flow {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri)

        val from = vmpgId
        val to = vmpgId

        val viewQuery = createQuery(client, "by_groupid")
                .startKey(from)
                .endKey(to)
                .reduce(false)
                .includeDocs(false)
        emitAll(client.queryView<String, String>(viewQuery).map { it.id })
    }

    override fun listAmpIdsByVmpCode(vmpCode: String): Flow<String> = flow {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri)

        val from = vmpCode
        val to = vmpCode

        val viewQuery = createQuery(client, "by_code")
                .startKey(from)
                .endKey(to)
                .reduce(false)
                .includeDocs(false)
        emitAll(client.queryView<String, String>(viewQuery).map { it.id })
    }

    override fun listAmpIdsByVmpId(vmpId: String): Flow<String> = flow {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri)

        val from = vmpId
        val to = vmpId

        val viewQuery = createQuery(client, "by_id")
                .startKey(from)
                .endKey(to)
                .reduce(false)
                .includeDocs(false)
        emitAll(client.queryView<String, String>(viewQuery).map { it.id })
    }

    override suspend fun getVersion(): SamVersion? {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri)

        return client.get("org.taktik.icure.samv2", SamVersion::class.java)
    }

    @View(name = "by_language_label", map = "classpath:js/amp/By_language_label.js")
    override fun findAmpsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent> = flow {
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
        val viewQuery = pagedViewQuery<Amp, ComplexKey>(
                client,
                "by_language_label",
                from,
                to,
                paginationOffset.toPaginationOffset { sk -> ComplexKey.of(*sk.mapIndexed { i, s -> if (i==1) s.let { StringUtils.sanitizeString(it)} else s }.toTypedArray()) },
                false
        )
        emitAll(client.queryView(viewQuery, ComplexKey::class.java, String::class.java, Amp::class.java))
    }

    @View(name = "by_chapter_paragraph", map = "classpath:js/amp/By_chapter_paragraph.js")
    override fun findAmpsByChapterParagraph(chapter: String, paragraph: String, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent> = flow {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri)

        val viewQuery = pagedViewQuery<Amp, ComplexKey>(
                client,
                "by_chapter_paragraph",
                paginationOffset.startKey?.let { ComplexKey.of(it[0], it[1])} ?: ComplexKey.of(chapter, paragraph),
                ComplexKey.of(chapter, paragraph),
                paginationOffset.toPaginationOffset { sk -> ComplexKey.of(*sk.mapIndexed { i, s -> if (i==1) s.let { StringUtils.sanitizeString(it)} else s }.toTypedArray()) },
                false
        )
        emitAll(client.queryView(viewQuery, ComplexKey::class.java, Int::class.java, Amp::class.java))
    }

    override fun listAmpIdsByLabel(language: String?, label: String?): Flow<String> = flow {
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
        val viewQuery = createQuery(client, "by_language_label")
                .startKey(from)
                .endKey(to)
                .reduce(false)
                .includeDocs(false)
        emitAll(client.queryView<ComplexKey,String>(viewQuery).map { it.id })
    }

    override fun listAmpsByVmpGroupCodes(vmpgCodes: List<String>): Flow<Amp> = flow {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri)

        val viewQuery = createQuery(client, "by_groupcode")
                .keys(vmpgCodes)
                .reduce(false)
                .includeDocs(true)
        emitAll(client.queryViewIncludeDocs<String, Int,Amp>(viewQuery).map { it.doc })
    }

    override fun listAmpsByDmppCodes(dmppCodes: List<String>): Flow<Amp> = flow {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri)

        val viewQuery = createQuery(client, "by_groupcode")
                .keys(dmppCodes)
                .reduce(false)
                .includeDocs(true)
        emitAll(client.queryViewIncludeDocs<String, Int,Amp>(viewQuery).map { it.doc })
    }

    override fun listAmpsByVmpGroupIds(vmpGroupIds: List<String>): Flow<Amp> = flow {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri)

        val viewQuery = createQuery(client, "by_groupcid")
                .keys(vmpGroupIds)
                .reduce(false)
                .includeDocs(true)
        emitAll(client.queryViewIncludeDocs<String, Int,Amp>(viewQuery).map { it.doc })
    }

    override fun listAmpsByVmpCodes(vmpCodes: List<String>): Flow<Amp> = flow {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri)

        val viewQuery = createQuery(client, "by_vmpcode")
                .keys(vmpCodes)
                .reduce(false)
                .includeDocs(true)
        emitAll(client.queryViewIncludeDocs<String, Int,Amp>(viewQuery).map { it.doc })
    }

    override fun listAmpsByVmpIds(vmpIds: List<String>): Flow<Amp> = flow {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri)

        val viewQuery = createQuery(client, "by_vmpid")
                .keys(vmpIds)
                .reduce(false)
                .includeDocs(true)
        emitAll(client.queryViewIncludeDocs<String, Int,Amp>(viewQuery).map { it.doc })
    }

}
