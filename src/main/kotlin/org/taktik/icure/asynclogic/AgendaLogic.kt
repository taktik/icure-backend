package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.entities.Agenda

interface AgendaLogic : EntityPersister<Agenda, String> {
    suspend fun createAgenda(agenda: Agenda): Agenda?
    suspend fun deleteAgenda(ids: List<String>): List<DocIdentifier>

    suspend fun getAgenda(agenda: String): Agenda?
    suspend fun modifyAgenda(agenda: Agenda): Agenda?
    fun getAllAgendaForUser(userId: String): Flow<Agenda>
    fun getReadableAgendaForUser(userId: String): Flow<Agenda>
}
