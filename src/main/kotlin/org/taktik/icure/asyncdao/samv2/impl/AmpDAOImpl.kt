package org.taktik.icure.asyncdao.samv2.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.ektorp.ComplexKey
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.queryView
import org.taktik.icure.asyncdao.impl.CouchDbDispatcher
import org.taktik.icure.asyncdao.impl.GenericDAOImpl
import org.taktik.icure.asyncdao.impl.InternalDAOImpl
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.asyncdao.samv2.AmpDAO
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.db.StringUtils
import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.samv2.Amp
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.createQuery
import org.taktik.icure.utils.pagedViewQuery
import java.net.URI

@ExperimentalCoroutinesApi
@Repository("ampDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.samv2.Amp' && !doc.deleted) emit( null, doc._id )}")
class AmpDAOImpl(couchDbProperties: CouchDbProperties, @Qualifier("drugCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : InternalDAOImpl<Amp>(Amp::class.java, couchDbProperties, couchDbDispatcher, idGenerator), AmpDAO {

    @View(name = "by_dmppcode", map = "classpath:js/amp/By_dmppcode.js")
    override fun findAmpsByDmppCode(dmppCode: String): Flow<ViewQueryResultEvent> {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri, null)

        val from = dmppCode
        val to = dmppCode

        val viewQuery = createQuery<Amp>("by_dmppcode")
                .startKey(from)
                .endKey(to)
                .includeDocs(true)
        return client.queryView(viewQuery, String::class.java, String::class.java, Amp::class.java)
    }


    @View(name = "by_groupcode", map = "classpath:js/amp/By_groupcode.js")
    override fun findAmpsByVmpGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri, null)

        val from = paginationOffset.startKey ?: vmpgCode
        val to = vmpgCode

        val viewQuery = pagedViewQuery<Amp, String>("by_groupcode", from, to, PaginationOffset(paginationOffset.limit, paginationOffset.startDocumentId), false)
        return client.queryView(viewQuery, String::class.java, String::class.java, Amp::class.java)
    }

    @View(name = "by_groupid", map = "classpath:js/amp/By_groupid.js")
    override fun findAmpsByVmpGroupId(vmpgId: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri, null)

        val from = paginationOffset.startKey ?: vmpgId
        val to = vmpgId

        val viewQuery = pagedViewQuery<Amp,String>("by_groupid", from, to, PaginationOffset(paginationOffset.limit, paginationOffset.startDocumentId), false)
        return client.queryView(viewQuery, String::class.java, String::class.java, Amp::class.java)
    }

    @View(name = "by_vmpcode", map = "classpath:js/amp/By_vmpcode.js")
    override fun findAmpsByVmpCode(vmpCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri, null)

        val from = paginationOffset.startKey ?: vmpCode
        val to = vmpCode

        val viewQuery = pagedViewQuery<Amp,String>("by_vmpcode", from, to, PaginationOffset(paginationOffset.limit, paginationOffset.startDocumentId), false)
        return client.queryView(viewQuery, String::class.java, String::class.java, Amp::class.java)
    }

    @View(name = "by_vmpid", map = "classpath:js/amp/By_vmpid.js")
    override fun findAmpsByVmpId(vmpId: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri, null)

        val from = paginationOffset.startKey ?: vmpId
        val to = vmpId

        val viewQuery = pagedViewQuery<Amp,String>("by_vmpid", from, to, PaginationOffset(paginationOffset.limit, paginationOffset.startDocumentId), false)
        return client.queryView(viewQuery, String::class.java, String::class.java, Amp::class.java)
    }

    override fun listAmpIdsByVmpGroupCode(vmpgCode: String): Flow<String> {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri, null)

        val from = vmpgCode
        val to = vmpgCode

        val viewQuery = createQuery<Amp>("by_groupcode")
                .startKey(from)
                .endKey(to)
                .reduce(false)
                .includeDocs(false)
        return client.queryView<String, String>(viewQuery).map { it.id }
    }

    override fun listAmpIdsByVmpGroupId(vmpgId: String): Flow<String> {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri, null)

        val from = vmpgId
        val to = vmpgId

        val viewQuery = createQuery<Amp>("by_groupid")
                .startKey(from)
                .endKey(to)
                .reduce(false)
                .includeDocs(false)
        return client.queryView<String, String>(viewQuery).map { it.id }
    }

    override fun listAmpIdsByVmpCode(vmpCode: String): Flow<String> {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri, null)

        val from = vmpCode
        val to = vmpCode

        val viewQuery = createQuery<Amp>("by_code")
                .startKey(from)
                .endKey(to)
                .reduce(false)
                .includeDocs(false)
        return client.queryView<String, String>(viewQuery).map { it.id }
    }

    override fun listAmpIdsByVmpId(vmpId: String): Flow<String> {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri, null)

        val from = vmpId
        val to = vmpId

        val viewQuery = createQuery<Amp>("by_id")
                .startKey(from)
                .endKey(to)
                .reduce(false)
                .includeDocs(false)
        return client.queryView<String, String>(viewQuery).map { it.id }
    }

    @View(name = "by_language_label", map = "classpath:js/amp/By_language_label.js")
    override fun findAmpsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent> {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri, null)

        val sanitizedLabel= label?.let { StringUtils.sanitizeString(it) }
        val startKey = paginationOffset.startKey
        val from = if (startKey == null)
            ComplexKey.of(
                    language ?: "\u0000",
                    sanitizedLabel ?: "\u0000"
            )
        else
            ComplexKey.of(*startKey.mapIndexed { i, s -> if (i==1) s?.let { StringUtils.sanitizeString(it)} else s }.toTypedArray())
        val to = ComplexKey.of(
                language ?: ComplexKey.emptyObject(),
                if (sanitizedLabel == null) ComplexKey.emptyObject() else sanitizedLabel + "\ufff0"
        )
        val viewQuery = pagedViewQuery<Amp,ComplexKey>(
                "by_language_label",
                from,
                to,
                PaginationOffset(from, paginationOffset.startDocumentId, paginationOffset.offset, paginationOffset.limit),
                false
        )
        return client.queryView(viewQuery, ComplexKey::class.java, String::class.java, Amp::class.java)
    }

    override fun listAmpIdsByLabel(language: String?, label: String?): Flow<String> {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri, null)

        val sanitizedLabel= label?.let { StringUtils.sanitizeString(it) }
        val from = ComplexKey.of(
                    language ?: "\u0000",
                    sanitizedLabel ?: "\u0000"
            )
        val to = ComplexKey.of(
                language ?: ComplexKey.emptyObject(),
                if (sanitizedLabel == null) ComplexKey.emptyObject() else sanitizedLabel + "\ufff0"
        )
        val viewQuery = createQuery<Amp>("by_language_label")
                .startKey(from)
                .endKey(to)
                .reduce(false)
                .includeDocs(false)
        return client.queryView<ComplexKey,String>(viewQuery).map { it.id }
    }


}
