package org.taktik.icure.asyncdao.samv2.impl

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.ektorp.ComplexKey
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.queryView
import org.taktik.icure.asyncdao.impl.CouchDbDispatcher
import org.taktik.icure.asyncdao.impl.InternalDAOImpl
import org.taktik.icure.asyncdao.samv2.VmpGroupDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.db.StringUtils
import org.taktik.icure.entities.samv2.Amp
import org.taktik.icure.entities.samv2.VmpGroup
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.createQuery
import org.taktik.icure.utils.pagedViewQuery
import java.net.URI

@FlowPreview
@Repository("vmpGroupDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.samv2.VmpGroup' && !doc.deleted) emit( null, doc._id )}")
class VmpGroupDAOImpl(couchDbProperties: CouchDbProperties, @Qualifier("drugCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : InternalDAOImpl<VmpGroup>(VmpGroup::class.java, couchDbProperties, couchDbDispatcher, idGenerator), VmpGroupDAO {
    @View(name = "by_language_label", map = "classpath:js/vmpgroup/By_language_label.js")
    override fun findVmpGroupsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent> {
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
        val viewQuery = pagedViewQuery<VmpGroup,ComplexKey>(
                "by_language_label",
                from,
                to,
                paginationOffset.toPaginationOffset { sk -> ComplexKey.of(*sk.mapIndexed { i, s -> if (i==1) s.let { StringUtils.sanitizeString(it)} else s }.toTypedArray()) },
                false
        )
        return client.queryView(viewQuery, ComplexKey::class.java, String::class.java, Amp::class.java)
    }

    override fun listVmpGroupIdsByLabel(language: String?, label: String?): Flow<String> {
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
        val viewQuery = createQuery<VmpGroup>("by_language_label")
                .startKey(from)
                .endKey(to)
                .reduce(false)
                .includeDocs(false)
        return client.queryView<ComplexKey,String>(viewQuery).map { it.id }
    }

}
