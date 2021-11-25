package org.taktik.icure.asynclogic.impl.filter.service

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toSet
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.service.ServiceByHcPartyIdentifierFilter
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.utils.getLoggedHealthCarePartyId
import javax.security.auth.login.LoginException

class ServiceByHcPartyIdentifierFilter(
        private val contactLogic: ContactLogic,
        private val sessionLogic: AsyncSessionLogic,
) : Filter<String, Service, ServiceByHcPartyIdentifierFilter> {
    override fun resolve(filter: ServiceByHcPartyIdentifierFilter, context: Filters) = flow {
        try {
            val hcPartyId = filter.healthcarePartyId ?: getLoggedHealthCarePartyId(sessionLogic)
            var ids: HashSet<String>? = null
            if (filter.value != null) {
                ids = HashSet(contactLogic.listServiceIdsByIdentifier(
                        hcPartyId,
                        filter.system ?: "https://dxm.icure.dev",
                        filter.value!!
                ).toSet())
            }

            emitAll((ids ?: HashSet()).asFlow())
        } catch (e: LoginException) {
            throw IllegalArgumentException(e)
        }
    }
}
