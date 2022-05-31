package org.taktik.icure.asyncdao.samv2.impl

import java.net.URI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.impl.CouchDbDispatcher
import org.taktik.icure.asyncdao.impl.InternalDAOImpl
import org.taktik.icure.asyncdao.samv2.VerseDAO
import org.taktik.icure.entities.samv2.Verse
import org.taktik.icure.properties.CouchDbProperties

@FlowPreview
@ExperimentalCoroutinesApi
@Repository("verseDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.samv2.Verse') emit( null, doc._id )}")
class VerseDAOImpl(couchDbProperties: CouchDbProperties, @Qualifier("chapIVCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) :
	InternalDAOImpl<Verse>(
		Verse::class.java, couchDbProperties, couchDbDispatcher, idGenerator
	),
	VerseDAO {
	@View(name = "by_chapter_paragraph", map = "classpath:js/verse/By_chapter_paragraph.js")
	override fun listVerses(chapterName: String, paragraphName: String): Flow<Verse> = flow {
		val dbInstanceUri = URI(couchDbProperties.url)
		val client = couchDbDispatcher.getClient(dbInstanceUri)

		val viewQuery = createQuery(client, "by_chapter_paragraph")
			.startKey(ComplexKey.of(chapterName, paragraphName, null, null))
			.endKey(ComplexKey.of(chapterName, paragraphName, ComplexKey.emptyObject(), ComplexKey.emptyObject()))
			.includeDocs(true)

		emitAll(client.queryViewIncludeDocs<ComplexKey, Int, Verse>(viewQuery).map { it.doc }.filter { it.endDate == null })
	}
}
