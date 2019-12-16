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
package org.taktik.icure.asynclogic.impl.filter.contact

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.toSet
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.AsyncICureSessionLogic
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.dto.filter.contact.ContactByHcPartyPatientTagCodeDateFilter
import org.taktik.icure.entities.Contact
import org.taktik.icure.utils.getLoggedHealthCarePartyId
import java.util.*
import javax.security.auth.login.LoginException

@Service
class ContactByHcPartyPatientTagCodeDateFilter(private val contactLogic: ContactLogic,
                                               private val sessionLogic: AsyncICureSessionLogic) : Filter<String, Contact, ContactByHcPartyPatientTagCodeDateFilter> {

    override suspend fun resolve(filter: ContactByHcPartyPatientTagCodeDateFilter, context: Filters): Flow<String> {
        try {
            val hcPartyId = if (filter.healthcarePartyId != null) filter.healthcarePartyId else getLoggedHealthCarePartyId(sessionLogic)
            var ids: HashSet<String>? = null
            var patientSecretForeignKeys = filter.patientSecretForeignKeys
            if (patientSecretForeignKeys == null) {
                patientSecretForeignKeys = if (filter.patientSecretForeignKey != null) {
                    listOf(filter.patientSecretForeignKey)
                } else {
                    listOf()
                }
            }
            if (filter.tagType != null && filter.tagCode != null) {
                ids = HashSet(contactLogic.listServiceIdsByTag(
                        hcPartyId,
                        patientSecretForeignKeys, filter.tagType,
                        filter.tagCode,
                        filter.startServiceValueDate, filter.endServiceValueDate).toSet())
            }
            if (filter.codeType != null && filter.codeCode != null) {
                val byCode = contactLogic.listServiceIdsByCode(
                        hcPartyId,
                        patientSecretForeignKeys, filter.tagType,
                        filter.tagCode,
                        filter.startServiceValueDate, filter.endServiceValueDate).toList()
                if (ids == null) {
                    ids = HashSet(byCode)
                } else {
                    ids.retainAll(byCode)
                }
            }
            return if (ids != null) contactLogic.findByServices(ids) else flowOf()
        } catch (e: LoginException) {
            throw IllegalArgumentException(e)
        }
    }
}
