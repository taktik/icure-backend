package org.taktik.icure.dao.samv2.impl

import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.icure.dao.impl.GenericDAOImpl
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.dao.samv2.ProductIdDAO
import org.taktik.icure.entities.samv2.ProductId

@Repository("productIdDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.samv2.ProductId' && !doc.deleted) emit( null, doc._id )}")
class ProductIdDAOImpl @Autowired
constructor(@Qualifier("couchdbDrugs") couchdb: CouchDbICureConnector, idGenerator: IDGenerator) : GenericDAOImpl<ProductId>(ProductId::class.java, couchdb, idGenerator), ProductIdDAO {
    init {
        initStandardDesignDocument();
    }
}
