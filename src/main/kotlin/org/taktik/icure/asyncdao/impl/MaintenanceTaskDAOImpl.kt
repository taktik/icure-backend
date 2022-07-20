/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asyncdao.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.queryView
import org.taktik.icure.asyncdao.MaintenanceTaskDAO
import org.taktik.icure.entities.MaintenanceTask
import org.taktik.icure.entities.embed.Identifier
import org.taktik.icure.properties.CouchDbProperties

@Repository("maintenanceTaskDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type === 'org.taktik.icure.entities.MaintenanceTask' && !doc.deleted) emit(null, doc._id)}")
class MaintenanceTaskDAOImpl(
	couchDbProperties: CouchDbProperties,
	@Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher,
	idGenerator: IDGenerator
) : GenericIcureDAOImpl<MaintenanceTask>(MaintenanceTask::class.java, couchDbProperties, couchDbDispatcher, idGenerator), MaintenanceTaskDAO {

	@OptIn(ExperimentalCoroutinesApi::class)
	@View(name = "by_hcparty_identifier", map = "classpath:js/maintenancetask/By_hcparty_identifier_map.js")
	override fun listMaintenanceTasksByHcPartyAndIdentifier(healthcarePartyId: String, identifiers: List<Identifier>) = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val queryView = createQuery(client, "by_hcparty_identifier")
			.keys(
				identifiers.map {
					ComplexKey.of(healthcarePartyId, it.system, it.value)
				}
			)

		emitAll(
			client.queryView<ComplexKey, String>(queryView)
				.mapNotNull {
					if (it.key == null || it.key!!.components.size < 3) {
						return@mapNotNull null
					}
					return@mapNotNull it.id
				}
		)
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	@View(name = "by_date", map = "function(doc) { if (doc.java_type === 'org.taktik.icure.entities.MaintenanceTask' && !doc.deleted) emit(doc.created)}")
	override fun listMaintenanceTasksAfterDate(date: Long) = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		emitAll(client.queryView<Long, Void>(createQuery(client, "by_date").endKey(date).descending(true).includeDocs(false)).map { it.id })
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	@View(name = "by_hcparty_type", map = "classpath:js/maintenancetask/By_hcparty_type_map.js")
	override fun listMaintenanceTasksByHcPartyAndType(healthcarePartyId: String, type: String) = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val queryView = createQuery(client, "by_hcparty_type")
			.keys(listOf(ComplexKey.of(healthcarePartyId, type)))
			.includeDocs(false)

		emitAll(client.queryView<ComplexKey, Void>(queryView).map { it.id })
	}
}
