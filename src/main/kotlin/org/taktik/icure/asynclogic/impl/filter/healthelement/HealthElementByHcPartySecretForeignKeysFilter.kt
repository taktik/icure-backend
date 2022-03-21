/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.asynclogic.impl.filter.healthelement

import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.HealthElementLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.healthelement.HealthElementByHcPartySecretForeignKeysFilter
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.utils.getLoggedHealthCarePartyId
import javax.security.auth.login.LoginException

@Service
class HealthElementByHcPartySecretForeignKeysFilter(private val healthElementLogic: HealthElementLogic,
                                                    private val sessionLogic: AsyncSessionLogic) : Filter<String, HealthElement, HealthElementByHcPartySecretForeignKeysFilter> {
    override fun resolve(filter: HealthElementByHcPartySecretForeignKeysFilter, context: Filters) = flow {
        try {
            val hcPartyId = if (filter.healthcarePartyId != null) filter.healthcarePartyId else getLoggedHealthCarePartyId(sessionLogic)
            emitAll(healthElementLogic.listHealthElementIdsByHcPartyAndSecretPatientKeys(hcPartyId!!, filter.patientSecretForeignKeys.toList()))
        } catch (e: LoginException) {
            throw IllegalArgumentException(e)
        }
    }
}
