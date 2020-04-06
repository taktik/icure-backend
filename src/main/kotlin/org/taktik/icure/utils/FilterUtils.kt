package org.taktik.icure.utils

import org.taktik.icure.asynclogic.AsyncSessionLogic
import javax.security.auth.login.LoginException

suspend fun getLoggedHealthCarePartyId(sessionLogic: AsyncSessionLogic): String {
    val user = sessionLogic.getCurrentSessionContext().getUser()
    if (user.healthcarePartyId == null) {
        throw LoginException("You must be logged to perform this action. ")
    }
    return user.healthcarePartyId!!
}
