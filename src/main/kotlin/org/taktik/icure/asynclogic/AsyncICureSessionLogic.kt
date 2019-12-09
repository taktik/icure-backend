package org.taktik.icure.asynclogic

import javax.servlet.http.HttpSession

interface AsyncICureSessionLogic : AsyncSessionLogic {
    fun getOrCreateSession(): HttpSession?
    suspend fun getCurrentUserId(): String
    suspend fun getCurrentHealthcarePartyId(): String
}
