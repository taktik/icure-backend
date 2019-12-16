package org.taktik.icure.asynclogic.impl.filter.healthelement

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toSet
import org.taktik.icure.asynclogic.AsyncICureSessionLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.dto.filter.healthelement.HealthElementByHcPartyTagCodeFilter
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.asynclogic.HealthElementLogic
import org.taktik.icure.utils.getLoggedHealthCarePartyId
import java.util.*
import javax.security.auth.login.LoginException

class HealthElementByHcPartyTagCodeFilter(private val healthElementLogic: HealthElementLogic,
                                          private val sessionLogic: AsyncICureSessionLogic) : Filter<String, HealthElement, HealthElementByHcPartyTagCodeFilter> {

    override suspend fun resolve(filter: HealthElementByHcPartyTagCodeFilter, context: Filters): Flow<String> {
        try {
            val hcPartyId = if (filter.healthCarePartyId != null) filter.healthCarePartyId else getLoggedHealthCarePartyId(sessionLogic)
            var ids: HashSet<String>? = null
            if (filter.tagType != null && filter.tagCode != null) {
                ids = HashSet(healthElementLogic.findByHCPartyAndTags(hcPartyId, filter.tagType, filter.tagCode).toSet())
            }
            if (filter.codeType != null && filter.codeNumber != null) {
                val byCode = HashSet(healthElementLogic.findByHCPartyAndCodes(hcPartyId, filter.codeType, filter.codeNumber).toSet())
                if (ids == null) {
                    ids = byCode
                } else {
                    ids.retainAll(byCode)
                }
            }
            if (filter.status != null) {
                val byStatus = HashSet(healthElementLogic.findByHCPartyAndStatus(hcPartyId, filter.status).toSet())
                if (ids == null) {
                    ids = byStatus
                } else {
                    ids.retainAll(byStatus)
                }
            }
            return (ids ?: HashSet()).asFlow()
        } catch (e: LoginException) {
            throw IllegalArgumentException(e)
        }
    }
}
