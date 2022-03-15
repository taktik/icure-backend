package org.taktik.icure.asyncdao.samv2.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.impl.CouchDbDispatcher
import org.taktik.icure.asyncdao.impl.InternalDAOImpl
import org.taktik.icure.asyncdao.samv2.AmpDAO
import org.taktik.icure.asyncdao.samv2.ParagraphDAO
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.db.StringUtils
import org.taktik.icure.entities.Agenda
import org.taktik.icure.entities.samv2.Amp
import org.taktik.icure.entities.samv2.Paragraph
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.distinct
import java.net.URI
import java.text.DecimalFormat

@FlowPreview
@ExperimentalCoroutinesApi
@Repository("paragraphDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.samv2.Paragraph') emit( null, doc._id )}")
class ParagraphDAOImpl(couchDbProperties: CouchDbProperties, @Qualifier("chapIVCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator, val ampDAO: AmpDAO) : InternalDAOImpl<Paragraph>(
        Paragraph::class.java, couchDbProperties, couchDbDispatcher, idGenerator), ParagraphDAO {
    @View(name = "by_language_label", map = "classpath:js/amp/By_language_label.js")
    override fun findParagraphs(searchString: String, language: String, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent> = flow {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri)

        val sanitizedLabel= searchString.let { StringUtils.sanitizeString(it) }
        val viewQuery = pagedViewQuery<Amp, ComplexKey>(
                client,
                "by_language_label",
                ComplexKey.of(
                        language,
                        sanitizedLabel
                ),
                ComplexKey.of(
                        language,
                        sanitizedLabel
                ),
                paginationOffset.toPaginationOffset { sk -> ComplexKey.of(*sk.mapIndexed { i, s -> if (i == 1) s.let { StringUtils.sanitizeString(it)} else s }.toTypedArray()) },
                false
        )
        emitAll(client.queryView(viewQuery, ComplexKey::class.java, String::class.java, Amp::class.java))
    }

    @View(name = "by_chapter_paragraph", map = "classpath:js/paragraph/By_chapter_paragraph.js")
    override fun findParagraphsWithCnk(cnk: Long, language: String): Flow<Paragraph> = flow {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri)

        val legalReferences = ampDAO.listAmpsByDmppCodes(listOf(DecimalFormat("0000000").format(cnk))).flatMapConcat { it.ampps.flatMap { it.dmpps.flatMap { (it.reimbursements ?: emptySet()).mapNotNull { it.legalReferencePath } } }.asFlow() }.distinct().toList()

        val viewQuery = createQuery(client, "by_chapter_paragraph")
                .keys(legalReferences.mapNotNull { it.split("-").takeIf { it.size == 3 && it[1] == "IV" }?.let { ComplexKey.of("IV", it[2]) } })
                .includeDocs(true)
        emitAll(client.queryViewIncludeDocs<String, Int, Paragraph>(viewQuery).map { it.doc }.filter { it.endDate == null })
    }

    override suspend fun getParagraph(chapterName: String, paragraphName: String): Paragraph? {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri)

        val viewQuery = createQuery(client, "by_chapter_paragraph")
                .startKey(ComplexKey.of(chapterName, paragraphName))
                .endKey(ComplexKey.of(chapterName, paragraphName))
                .includeDocs(true)

        return client.queryViewIncludeDocs<String, Int, Paragraph>(viewQuery).map { it.doc }.first { it.endDate == null }
    }
}
