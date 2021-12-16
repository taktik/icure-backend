package org.taktik.icure.asynclogic.impl.filter.service

import javax.security.auth.login.LoginException
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.service.ServiceByHcPartyIdentifiersFilter
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.utils.getLoggedHealthCarePartyId

@org.springframework.stereotype.Service
class ServiceByHcPartyIdentifiersFilter(
        private val contactLogic: ContactLogic,
        private val sessionLogic: AsyncSessionLogic,
) : Filter<String, Service, ServiceByHcPartyIdentifiersFilter> {
    override fun resolve(filter: ServiceByHcPartyIdentifiersFilter, context: Filters) = flow {
        try {
            val hcPartyId = filter.healthcarePartyId ?: getLoggedHealthCarePartyId(sessionLogic)
            emitAll(contactLogic.listServiceIdsByHcPartyAndIdentifiers(hcPartyId, filter.identifiers).map { (serviceId, _) -> serviceId })
        } catch (e: LoginException) {
            throw IllegalArgumentException(e)
        }
    }
}
