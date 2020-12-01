package org.taktik.icure.asyncdao.impl

import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.icure.asyncdao.PlaceDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.Place
import org.taktik.icure.properties.CouchDbProperties

@Repository("PlaceDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Place' && !doc.deleted) emit( null, doc._id )}")
class PlaceDAOImpl(couchDbProperties: CouchDbProperties,
                   @Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericDAOImpl<Place>(couchDbProperties, Place::class.java, couchDbDispatcher, idGenerator), PlaceDAO
