/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */
package org.taktik.icure.asynclogic.impl

import java.net.URI
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import kotlinx.coroutines.flow.toList
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.icure.applications.utils.JarUtils
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.asyncdao.ICureDAO
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.ICureLogic
import org.taktik.icure.asynclogic.PropertyLogic
import org.taktik.icure.constants.PropertyTypes
import org.taktik.icure.entities.embed.DatabaseSynchronization
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.services.external.rest.v1.dto.ReplicationInfoDto

@Service
class ICureLogicImpl(
	couchDbProperties: CouchDbProperties,
	private val sessionLogic: AsyncSessionLogic,
	private val iCureDAO: ICureDAO,
	private val propertyLogic: PropertyLogic,
	private val allDaos: List<GenericDAO<*>>,
	private val userDAO: UserDAO
) : ICureLogic {

	private val dbInstanceUri = URI(couchDbProperties.url)

	override suspend fun getIndexingStatus(): Map<String, Number>? {
		return iCureDAO.getIndexingStatus(dbInstanceUri)
	}

	override suspend fun getReplicationInfo(): ReplicationInfoDto {
		val changes: Map<DatabaseSynchronization, Long> = iCureDAO.getPendingChanges(dbInstanceUri)
		return changes.toList().fold(ReplicationInfoDto()) { r, (db, pending) ->
			r.copy(
				active = true,
				pendingFrom = if (db.source?.contains(dbInstanceUri.host) == true) ((r.pendingFrom ?: 0) + pending).toInt() else r.pendingFrom,
				pendingTo = if (db.source?.contains(dbInstanceUri.host) == true) ((r.pendingTo ?: 0) + pending).toInt() else r.pendingTo
			)
		}
	}

	override suspend fun modifyDesignDoc(daoEntityName: String, warmup: Boolean) {
		allDaos
			.firstOrNull { dao: GenericDAO<*> -> dao.javaClass.simpleName.startsWith(daoEntityName + "DAO") }
			?.let { dao: GenericDAO<*> ->
				dao.forceInitStandardDesignDocument()
				if (warmup) {
					val allIds = dao.getEntityIds(1).toList()
				}
			}
	}

	override suspend fun modifyDesignDocs() {
		allDaos.forEach { dao: GenericDAO<*> ->
			try {
				dao.forceInitStandardDesignDocument()
			} catch (ignored: Throwable) {
			}
		}
	}

	override suspend fun setLogLevel(logLevel: String, packageName: String): String {
		val retVal: String
		val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
		if (logLevel.equals("TRACE", ignoreCase = true)) {
			loggerContext.getLogger(packageName).level = Level.TRACE
			retVal = "ok"
		} else if (logLevel.equals("DEBUG", ignoreCase = true)) {
			loggerContext.getLogger(packageName).level = Level.DEBUG
			retVal = "ok"
		} else if (logLevel.equals("INFO", ignoreCase = true)) {
			loggerContext.getLogger(packageName).level = Level.INFO
			retVal = "ok"
		} else if (logLevel.equals("WARN", ignoreCase = true)) {
			loggerContext.getLogger(packageName).level = Level.WARN
			retVal = "ok"
		} else if (logLevel.equals("ERROR", ignoreCase = true)) {
			loggerContext.getLogger(packageName).level = Level.ERROR
			retVal = "ok"
		} else {
			retVal = "Error, not a known loglevel: $logLevel"
		}
		return retVal
	}

	override fun getVersion(): String {
		val manifest = JarUtils.getManifest()
		return if (manifest != null) {
			val version = manifest.mainAttributes.getValue("Build-revision")
			version?.trim { it <= ' ' } ?: ""
		} else {
			propertyLogic.getSystemPropertyValue<Any>(PropertyTypes.System.VERSION.identifier).toString().trim { it <= ' ' }
		}
	}
}
