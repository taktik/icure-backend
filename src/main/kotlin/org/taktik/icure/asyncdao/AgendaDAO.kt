package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.entities.Agenda
import java.net.URI

interface AgendaDAO: GenericDAO<Agenda> {
    fun getAllAgendaForUser(dbInstanceUrl: URI, groupId: String, userId: String): Flow<Agenda>

    fun getReadableAgendaForUser(dbInstanceUrl: URI, groupId: String, userId: String): Flow<Agenda>
}
