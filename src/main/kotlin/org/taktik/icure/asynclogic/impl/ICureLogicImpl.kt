/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.flow.toList
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
import java.net.URI

@Service
class ICureLogicImpl(couchDbProperties: CouchDbProperties,
                     private val sessionLogic: AsyncSessionLogic,
                     private val iCureDAO: ICureDAO,
                     private val propertyLogic: PropertyLogic,
                     private val allDaos: List<GenericDAO<*>>,
                     private val userDAO: UserDAO) : ICureLogic {

    private val dbInstanceUri = URI(couchDbProperties.url)

    override suspend fun getIndexingStatus(): Map<String, Number>? {
        return iCureDAO.getIndexingStatus(dbInstanceUri)
    }

    override suspend fun getReplicationInfo(): ReplicationInfoDto {
        val changes: Map<DatabaseSynchronization, Long> = iCureDAO.getPendingChanges(dbInstanceUri)
        return changes.toList().fold(ReplicationInfoDto()) { r, (db, pending) ->
            r.copy(
                    active = true,
                    pendingFrom = if(db.source?.contains(dbInstanceUri.host) == true) ((r.pendingFrom ?: 0) + pending).toInt() else r.pendingFrom,
                    pendingTo = if(db.source?.contains(dbInstanceUri.host) == true) ((r.pendingTo ?: 0) + pending).toInt() else r.pendingTo
            )
        }
    }

    override suspend fun updateDesignDoc(daoEntityName: String, warmup:Boolean) {
        allDaos
                .firstOrNull { dao: GenericDAO<*> -> dao.javaClass.simpleName.startsWith(daoEntityName + "DAO") }
                ?.let { dao: GenericDAO<*> ->
                    dao.forceInitStandardDesignDocument()
                    if (warmup) {
                        val allIds = dao.getAllIds().toList()
                    }
                }
    }

    override suspend fun updateAllDesignDoc() {
        allDaos.forEach { dao: GenericDAO<*> ->
            try {
                dao.forceInitStandardDesignDocument()
            } catch (ignored: Throwable) {
            }
        }
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
