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

import org.springframework.stereotype.Service
import org.taktik.icure.applications.utils.JarUtils
import org.taktik.icure.constants.PropertyTypes
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.asyncdao.ICureDAO
import org.taktik.icure.asynclogic.*
import org.taktik.icure.entities.embed.DatabaseSynchronization
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.services.external.rest.v1.dto.ReplicationInfoDto
import java.net.URI

@Service
class ICureLogicImpl(couchDbProperties: CouchDbProperties,
                     private val sessionLogic: AsyncSessionLogic,
                     private val iCureDAO: ICureDAO,
                     private val propertyLogic: PropertyLogic,
                     private val allDaos: List<GenericDAO<*>>) : ICureLogic {

    private val dbInstanceUri = URI(couchDbProperties.url)

    override suspend fun getIndexingStatus(groupId: String): Map<String, Number>? {
        return iCureDAO.getIndexingStatus(groupId)
    }

    override suspend fun getReplicationInfo(groupId: String): ReplicationInfoDto {
        val changes: Map<DatabaseSynchronization, Long> = iCureDAO.getPendingChanges(groupId)
        return changes.toList().fold(ReplicationInfoDto()) { r, (db, pending) ->
            r.active = true
            if (db.source.contains(dbInstanceUri.host)) {
                r.pendingFrom += pending
            }
            if (db.target.contains(dbInstanceUri.host)) {
                r.pendingTo += pending
            }
            r
        }
    }

    override suspend fun updateDesignDoc(daoEntityName: String) {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        allDaos
                .firstOrNull { dao: GenericDAO<*> -> dao.javaClass.simpleName.startsWith(daoEntityName + "DAO") }
                ?.let { dao: GenericDAO<*> -> dao.forceInitStandardDesignDocument(dbInstanceUri, groupId) } // TODO AD: missing function from GenericLogic
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
