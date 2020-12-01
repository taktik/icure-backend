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
package org.taktik.icure.asynclogic.impl.filter.service

import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.service.ServiceBySecretForeignKeys
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.utils.getLoggedHealthCarePartyId
import javax.security.auth.login.LoginException

class ServiceBySecretForeignKeys(private val contactLogic: ContactLogic,
                                 private val sessionLogic: AsyncSessionLogic) : Filter<String, Service, ServiceBySecretForeignKeys> {
    override fun resolve(filter: ServiceBySecretForeignKeys, context: Filters) = flow {
        try {
            val hcPartyId = if (filter.healthcarePartyId != null) filter.healthcarePartyId else getLoggedHealthCarePartyId(sessionLogic)
            emitAll(contactLogic.findServicesBySecretForeignKeys(hcPartyId!!, filter.patientSecretForeignKeys))
        } catch (e: LoginException) {
            throw IllegalArgumentException(e)
        }
    }
}
