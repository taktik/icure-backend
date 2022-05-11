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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.AgendaDAO
import org.taktik.icure.asynclogic.AgendaLogic
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.entities.Agenda
import org.taktik.icure.exceptions.DeletionException

@Service
class AgendaLogicImpl(private val agendaDAO: AgendaDAO, private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<Agenda, AgendaDAO>(sessionLogic), AgendaLogic {

	override suspend fun createAgenda(agenda: Agenda) = fix(agenda) { agenda ->
		agendaDAO.create(agenda)
	}

	override fun deleteAgendas(ids: List<String>): Flow<DocIdentifier> {
		return try {
			deleteEntities(ids)
		} catch (e: Exception) {
			throw DeletionException(e.message, e)
		}
	}

	override suspend fun getAgenda(agenda: String): Agenda? {
		return agendaDAO.get(agenda)
	}

	override suspend fun modifyAgenda(agenda: Agenda) = fix(agenda) { agenda ->
		agendaDAO.save(agenda)
	}

	override fun getAgendasByUser(userId: String) = flow<Agenda> {
		emitAll(agendaDAO.getAgendasByUser(userId))
	}

	override fun getReadableAgendaForUser(userId: String) = flow<Agenda> {
		emitAll(agendaDAO.getReadableAgendaByUser(userId))
	}

	override fun getAnonymousAgendasByUser(userId: String) = flow {
		val agendasByUser = getAgendasByUser(userId).toList()
		emitAll(agendasByUser.filter { it.rights.any { r -> r.read && r.userId == null } }.asFlow())
	}

	override fun getGenericDAO(): AgendaDAO {
		return agendaDAO
	}
}
