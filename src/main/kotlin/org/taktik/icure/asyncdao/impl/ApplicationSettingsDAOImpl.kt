package org.taktik.icure.asyncdao.impl

import ma.glasnost.orika.MapperFacade
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.icure.asyncdao.ApplicationSettingsDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.ApplicationSettings

@Repository("ApplicationSettingsDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.ApplicationSettings' && !doc.deleted) emit( null, doc._id )}")
class ApplicationSettingsDAOImpl(@Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator, mapper: MapperFacade) : GenericDAOImpl<ApplicationSettings>(ApplicationSettings::class.java, couchDbDispatcher, idGenerator, mapper), ApplicationSettingsDAO
