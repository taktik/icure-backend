package org.taktik.icure.asyncdao.samv2.impl

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
import org.taktik.icure.asyncdao.samv2.AmpDAO
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.asyncdao.samv2.VmpDAO
import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.db.StringUtils
import org.taktik.icure.entities.samv2.Amp
import org.taktik.icure.entities.samv2.Vmp
import org.taktik.icure.properties.CouchDbProperties
import java.net.URI

@Repository("vmpDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.samv2.Vmp' && !doc.deleted) emit( null, doc._id )}")
class VmpDAOImpl(val couchDbProperties: CouchDbProperties, @Qualifier("drugCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericDAOImpl<Vmp>(Vmp::class.java, couchDbDispatcher, idGenerator), VmpDAO {
    @View(name = "by_groupcode", map = "classpath:js/vmp/By_groupcode.js")
    override fun findVmpsByGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri, null)

        val from = paginationOffset.startKey ?: vmpgCode
        val to = vmpgCode

        val viewQuery = pagedViewQuery("by_groupcode", from, to, PaginationOffset(paginationOffset.limit, paginationOffset.startDocumentId), false)
        return client.queryView(viewQuery, String::class.java, String::class.java, Vmp::class.java)
    }

    @View(name = "by_language_label", map = "classpath:js/vmp/By_language_label.js")
    override fun findVmpsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent> {
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
        val viewQuery = pagedViewQuery(
                "by_language_label",
                from,
                to,
                PaginationOffset(from, paginationOffset.startDocumentId, paginationOffset.offset, paginationOffset.limit),
                false
        )
        return client.queryView(viewQuery, ComplexKey::class.java, String::class.java, Vmp::class.java)
    }

    @View(name = "by_groupid", map = "classpath:js/vmp/By_groupid.js")
    override fun findVmpsByGroupId(vmpgId: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri, null)

        val from = paginationOffset.startKey ?: vmpgId
        val to = vmpgId

        val viewQuery = pagedViewQuery("by_groupid", from, to, PaginationOffset(paginationOffset.limit, paginationOffset.startDocumentId), false)
        return client.queryView(viewQuery, String::class.java, String::class.java, Vmp::class.java)
    }

    override fun listVmpIdsByGroupCode(vmpgCode: String): Flow<String> {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri, null)

        val from = vmpgCode
        val to = vmpgCode

        val viewQuery = createQuery("by_groupcode")
                .startKey(from)
                .endKey(to)
                .reduce(false)
                .includeDocs(false)
        return client.queryView<String, String>(viewQuery).map { it.id }
    }

    override fun listVmpIdsByGroupId(vmpgId: String): Flow<String> {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri, null)

        val from = vmpgId
        val to = vmpgId

        val viewQuery = createQuery("by_groupid")
                .startKey(from)
                .endKey(to)
                .reduce(false)
                .includeDocs(false)
        return client.queryView<String, String>(viewQuery).map { it.id }
    }

    override fun listVmpIdsByLabel(language: String?, label: String?): Flow<String> {
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
        val viewQuery = createQuery("by_language_label")
                .startKey(from)
                .endKey(to)
                .reduce(false)
                .includeDocs(false)
        return client.queryView<ComplexKey,String>(viewQuery).map { it.id }
    }

}
