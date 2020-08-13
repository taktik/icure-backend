package org.taktik.icure.dao.samv2.impl

import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.icure.dao.impl.GenericDAOImpl
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.dao.samv2.NmpDAO
import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.samv2.Nmp

@Repository("nmpDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.samv2.Nmp' && !doc.deleted) emit( null, doc._id )}")
class NmpDAOImpl @Autowired
constructor(@Qualifier("couchdbDrugs") couchdb: CouchDbICureConnector, idGenerator: IDGenerator) : GenericDAOImpl<Nmp>(Nmp::class.java, couchdb, idGenerator), NmpDAO {
    @View(name = "by_language_label", map = "classpath:js/nmp/By_language_label.js")
    override fun findNmpsByLabel(language: String?, label: String?, pagination: PaginationOffset<*>?): PaginatedList<Nmp> {
        TODO("Not yet implemented")
    }

    override fun listNmpIdsByLabel(language: String?, label: String?): List<String> {
        TODO("Not yet implemented")
    }
}
