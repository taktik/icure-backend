package org.taktik.icure.asyncdao.impl

import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.icure.asyncdao.ApplicationSettingsDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.ApplicationSettings
import org.taktik.icure.properties.CouchDbProperties

@Repository("ApplicationSettingsDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.ApplicationSettings' && !doc.deleted) emit( null, doc._id )}")
class ApplicationSettingsDAOImpl(couchDbProperties: CouchDbProperties,
                                 @Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericDAOImpl<ApplicationSettings>(couchDbProperties, ApplicationSettings::class.java, couchDbDispatcher, idGenerator), ApplicationSettingsDAO
