package org.taktik.icure.asynclogic.impl.filter.healthelement

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toSet
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.HealthElementLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.healthelement.HealthElementByHcPartyTagCodeFilter
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.utils.getLoggedHealthCarePartyId
import java.util.*
import javax.security.auth.login.LoginException

class HealthElementByHcPartyTagCodeFilter(private val healthElementLogic: HealthElementLogic,
                                          private val sessionLogic: AsyncSessionLogic) : Filter<String, HealthElement, HealthElementByHcPartyTagCodeFilter> {

    override fun resolve(filter: HealthElementByHcPartyTagCodeFilter, context: Filters) = flow<String> {
        try {
            val hcPartyId: String = filter.healthCarePartyId ?: getLoggedHealthCarePartyId(sessionLogic)
            var ids: HashSet<String>? = null
            if (filter.tagType != null && filter.tagCode != null) {
                ids = HashSet(healthElementLogic.findByHCPartyAndTags(hcPartyId, filter.tagType!!, filter.tagCode!!).toSet())
            }
            if (filter.codeType != null && filter.codeNumber != null) {
                val byCode = HashSet(healthElementLogic.findByHCPartyAndCodes(hcPartyId, filter.codeType!!, filter.codeNumber!!).toSet())
                if (ids == null) {
                    ids = byCode
                } else {
                    ids.retainAll(byCode)
                }
            }
            if (filter.status != null) {
                val byStatus = HashSet(healthElementLogic.findByHCPartyAndStatus(hcPartyId, filter.status!!).toSet())
                if (ids == null) {
                    ids = byStatus
                } else {
                    ids.retainAll(byStatus)
                }
            }
            ids?.forEach { emit(it) }
        } catch (e: LoginException) {
            throw IllegalArgumentException(e)
        }
    }
}
