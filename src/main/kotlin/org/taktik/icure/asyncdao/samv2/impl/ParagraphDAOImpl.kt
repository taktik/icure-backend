package org.taktik.icure.asyncdao.samv2.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.id.IDGenerator
import org.taktik.icure.asyncdao.impl.CouchDbDispatcher
import org.taktik.icure.asyncdao.impl.InternalDAOImpl
import org.taktik.icure.asyncdao.samv2.ParagraphDAO
import org.taktik.icure.entities.samv2.Paragraph
import org.taktik.icure.properties.CouchDbProperties

@FlowPreview
@ExperimentalCoroutinesApi
@Repository("paragraphDAO")
@Profile("app")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.samv2.Paragraph') emit( null, doc._id )}")
class ParagraphDAOImpl(couchDbProperties: CouchDbProperties, @Qualifier("chapIVCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : InternalDAOImpl<Paragraph>(
        Paragraph::class.java, couchDbProperties, couchDbDispatcher, idGenerator), ParagraphDAO
