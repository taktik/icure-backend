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
package org.taktik.icure.asynclogic

import org.taktik.icure.entities.Replication
import org.taktik.icure.entities.embed.DatabaseSynchronization

interface ReplicationLogic : EntityPersister<Replication, String> {
    val pendingChanges: Map<DatabaseSynchronization?, Number?>?
    fun startReplications()
    @Throws(Exception::class)
    fun createGroupReplication(protocol: String?, replicationHost: String?, port: String?, groupId: String?, password: String?): Replication?

    fun createReplication(userHcpReplication: Replication?): Replication?
    @Throws(Exception::class)
    fun createBaseTemplateReplication(protocol: String?, replicationHost: String?, port: String?, language: String?, specialtyCode: String?): Replication?

    fun startDatabaseSynchronisations(replication: Replication?, continuous: Boolean)
}
