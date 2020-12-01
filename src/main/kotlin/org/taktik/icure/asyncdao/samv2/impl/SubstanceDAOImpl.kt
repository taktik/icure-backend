package org.taktik.icure.asyncdao.samv2.impl

import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.icure.asyncdao.impl.CouchDbDispatcher
import org.taktik.icure.asyncdao.impl.InternalDAOImpl
import org.taktik.icure.asyncdao.samv2.SubstanceDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.samv2.embed.Substance
import org.taktik.icure.properties.CouchDbProperties

@Repository("substanceDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.samv2.embed.Substance') emit( null, doc._id )}")
class SubstanceDAOImpl(couchDbProperties: CouchDbProperties, @Qualifier("drugCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : InternalDAOImpl<Substance>(Substance::class.java, couchDbProperties, couchDbDispatcher, idGenerator), SubstanceDAO
