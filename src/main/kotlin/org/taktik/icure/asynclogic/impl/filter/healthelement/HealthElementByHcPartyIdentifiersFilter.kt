package org.taktik.icure.asynclogic.impl.filter.healthelement

import javax.security.auth.login.LoginException
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.HealthElementLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.healthelement.HealthElementByHcPartyIdentifiersFilter
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.utils.getLoggedHealthCarePartyId

class HealthElementByHcPartyIdentifiersFilter(
    private val healthElementLogic: HealthElementLogic,
    private val sessionLogic: AsyncSessionLogic
) : Filter<String, HealthElement, HealthElementByHcPartyIdentifiersFilter> {
    override fun resolve(filter: HealthElementByHcPartyIdentifiersFilter, context: Filters) = flow {
        try {
            val hcPartyId: String = filter.hcPartyId ?: getLoggedHealthCarePartyId(sessionLogic)
            emitAll(healthElementLogic.listHealthElementsIdsByHcPartyAndIdentifiers(hcPartyId, filter.identifiers))
        } catch (e: LoginException) {
            throw IllegalArgumentException(e)
        }
    }

}
