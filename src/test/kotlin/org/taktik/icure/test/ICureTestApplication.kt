package org.taktik.icure.test

import javax.annotation.PreDestroy
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.JndiDataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.PropertySource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.TaskScheduler
import org.taktik.couchdb.exception.DocumentNotFoundException
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.asyncdao.InternalDAO
import org.taktik.icure.asynclogic.CodeLogic
import org.taktik.icure.asynclogic.ICureLogic
import org.taktik.icure.asynclogic.PropertyLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.constants.Users
import org.taktik.icure.exceptions.DuplicateDocumentException
import org.taktik.icure.properties.CouchDbProperties
import reactor.netty.http.client.HttpClient

@SpringBootApplication(
	scanBasePackages = [
		"org.springframework.boot.autoconfigure.aop",
		"org.springframework.boot.autoconfigure.context",
		"org.springframework.boot.autoconfigure.validation",
		"org.springframework.boot.autoconfigure.websocket",
		"org.taktik.icure.config",
		"org.taktik.icure.asyncdao",
		"org.taktik.icure.asynclogic",
		"org.taktik.icure.be.ehealth.logic",
		"org.taktik.icure.be.format.logic",
		"org.taktik.icure.properties",
		"org.taktik.icure.services.external.http",
		"org.taktik.icure.services.external.rest.v1.controllers",
		"org.taktik.icure.services.external.rest.v1.mapper",
		"org.taktik.icure.services.external.rest.v1.wscontrollers",
		"org.taktik.icure.services.external.rest.v2.controllers",
		"org.taktik.icure.services.external.rest.v2.mapper",
		"org.taktik.icure.services.external.rest.v2.wscontrollers",
		"org.taktik.icure.errors",
	],
	exclude = [
		FreeMarkerAutoConfiguration::class,
		CacheAutoConfiguration::class,
		DataSourceAutoConfiguration::class,
		JndiDataSourceAutoConfiguration::class,
		ErrorWebFluxAutoConfiguration::class
	]
)
@PropertySource("classpath:icure-test.properties")
@TestConfiguration
class ICureTestApplication {

	private val log = LoggerFactory.getLogger(this.javaClass)

	@Bean
	fun performStartupTasks(@Qualifier("threadPoolTaskExecutor") taskExecutor: TaskExecutor, taskScheduler: TaskScheduler, iCureLogic: ICureLogic, codeLogic: CodeLogic, propertyLogic: PropertyLogic, allDaos: List<GenericDAO<*>>, internalDaos: List<InternalDAO<*>>, couchDbProperties: CouchDbProperties, userLogic: UserLogic) = ApplicationRunner {
		val client = HttpClient.create()
		try { // Check if I already have a database running
			client.get().uri("http://127.0.0.1:5984")
				.response()
				.block()
		} catch (e: Exception) { // If not, I use docker to create a container
			println("Starting docker")
			ProcessBuilder(("docker run " +
				"-p 5984:5984 " +
				"-e COUCHDB_USER=admin -e COUCHDB_PASSWORD=admin " +
				"-d --name couchdb-test " +
				"couchdb:3.2.2").split(' '))
				.start()
				.waitFor()

			// Polling, waiting for the database to initialize
			runBlocking {
				var waitingForDb = true
				while (waitingForDb) {
					waitingForDb = try {
						client.get().uri("http://127.0.0.1:5984")
							.response()
							.awaitFirstOrNull()
						false
					} catch (e: reactor.netty.http.client.PrematureCloseException) {
						true
					}
				}
			}
		}

		// Import of the test code
		val resolver = PathMatchingResourcePatternResolver(javaClass.classLoader)
		resolver.getResources("classpath*:/org/taktik/icure/db/codes/test/**.xml").forEach {
			val md5 = it.filename!!.replace(Regex(".+\\.([0-9a-f]{20}[0-9a-f]+)\\.xml"), "$1")
			runBlocking { codeLogic.importCodesFromXml(md5, it.filename!!.replace(Regex("(.+)\\.[0-9a-f]{20}[0-9a-f]+\\.xml"), "$1"), it.inputStream) }
		}

		runBlocking {
			allDaos.forEach {
				it.forceInitStandardDesignDocument(true)
			}
			internalDaos.forEach {
				it.forceInitStandardDesignDocument(true)
			}

			// Creation of the test user
			try {
				userLogic.newUser(Users.Type.database, "icuretest", "icuretest", "icure")// Creates a test user if it does not exist
			} catch (e: DuplicateDocumentException) {
				log.info("Test user already exists!")
			}finally {
				log.info("iCure test user\nusername: icuretest\npassword: icuretest")
			}
		}
	}

	// At the end of the tests, I destroy the docker container
	@PreDestroy
	fun destroyDockerDBContainer() {
		ProcessBuilder(("docker rm -f couchdb-test").split(' '))
			.start()
			.waitFor()
	}
}
