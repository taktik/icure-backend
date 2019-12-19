package org.taktik.icure.asyncdao.replicator

import kotlinx.coroutines.Job
import org.taktik.icure.entities.Group

interface Replicator {
    suspend fun startReplication(group: Group): Job
}
