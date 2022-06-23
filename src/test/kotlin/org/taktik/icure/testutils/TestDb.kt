package org.taktik.icure.testutils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.taktik.couchdb.id.UUIDGenerator
import org.taktik.icure.asyncdao.impl.CouchDbDispatcher
import org.taktik.icure.config.CouchDbConfig
import org.taktik.icure.properties.CouchDbProperties

object TestDb {
	private const val PREFIX = "test-volatile"
// 	private val databaseHost = System.getProperty("icure.test.couchdb.server.url")
// 	private val userName = System.getProperty("icure.test.couchdb.username")
// 	private val password = System.getProperty("icure.test.couchdb.password")
	private val databaseHost = "http://127.0.0.1:5984"
	private val userName = "admin"
	private val password = "admin"

	val properties: CouchDbProperties = CouchDbProperties(
		prefix = PREFIX,
		url = databaseHost,
		username = userName,
		password = password
	)

	private val couchDbConfig = CouchDbConfig(properties)

	fun dispatcher(name: String) = CouchDbDispatcher(
		couchDbConfig.httpClient(couchDbConfig.connectionProvider()),
		ObjectMapper().registerKotlinModule(),
		PREFIX,
		name,
		userName,
		password,
		1
	)

	val idGenerator = UUIDGenerator()
}
