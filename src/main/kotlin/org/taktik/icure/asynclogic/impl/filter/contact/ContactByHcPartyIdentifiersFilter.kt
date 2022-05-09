package org.taktik.icure.asynclogic.impl.filter.contact

import javax.security.auth.login.LoginException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.entities.Contact
import org.taktik.icure.utils.getLoggedHealthCarePartyId

@org.springframework.stereotype.Service
class ContactByHcPartyIdentifiersFilter(
        private val contactLogic: ContactLogic,
        private val sessionLogic: AsyncSessionLogic,
) : Filter<String, Contact, org.taktik.icure.domain.filter.contact.ContactByHcPartyIdentifiersFilter> {

    override fun resolve(filter: org.taktik.icure.domain.filter.contact.ContactByHcPartyIdentifiersFilter, context: Filters): Flow<String> = flow {
        try {
            val hcPartyId = filter.healthcarePartyId ?: getLoggedHealthCarePartyId(sessionLogic)
            emitAll(contactLogic.listContactIdsByHcPartyAndIdentifiers(hcPartyId, filter.identifiers))
        } catch (e: LoginException) {
            throw IllegalArgumentException(e)
        }
    }
}
