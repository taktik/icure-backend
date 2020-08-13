package org.taktik.icure.dao.samv2.impl

import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.icure.dao.impl.GenericDAOImpl
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.dao.samv2.PharmaceuticalFormDAO
import org.taktik.icure.entities.samv2.embed.PharmaceuticalForm

@Repository("pharmaceuticalFormDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.samv2.embed.PharmaceuticalForm') emit( null, doc._id )}")
class PharmaceuticalFormDAOImpl @Autowired
constructor(@Qualifier("couchdbDrugs") couchdb: CouchDbICureConnector, idGenerator: IDGenerator) : GenericDAOImpl<PharmaceuticalForm>(PharmaceuticalForm::class.java, couchdb, idGenerator),PharmaceuticalFormDAO
