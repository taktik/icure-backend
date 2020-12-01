package org.taktik.icure.asyncdao.samv2.impl

import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.icure.asyncdao.impl.CouchDbDispatcher
import org.taktik.icure.asyncdao.impl.InternalDAOImpl
import org.taktik.icure.asyncdao.samv2.PharmaceuticalFormDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.samv2.embed.PharmaceuticalForm
import org.taktik.icure.properties.CouchDbProperties

@Repository("pharmaceuticalFormDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.samv2.embed.PharmaceuticalForm') emit( null, doc._id )}")
class PharmaceuticalFormDAOImpl(couchDbProperties: CouchDbProperties, @Qualifier("drugCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : InternalDAOImpl<PharmaceuticalForm>(PharmaceuticalForm::class.java, couchDbProperties, couchDbDispatcher, idGenerator), PharmaceuticalFormDAO
