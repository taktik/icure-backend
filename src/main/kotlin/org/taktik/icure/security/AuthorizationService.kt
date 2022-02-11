package org.taktik.icure.security

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.taktik.icure.asyncdao.HealthcarePartyDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.constants.Roles
import java.io.Serializable

@Service
class AuthorizationService(val sessionLogic: AsyncSessionLogic, val healthcarePartyDAO: HealthcarePartyDAO) {
    fun <T>apply(condition: suspend () -> Boolean, protectedMethod: () -> Flow<T>): Flow<T> = flow {
        if (condition()) {
            emitAll(protectedMethod())
        } else {
            throw AccessDeniedException("Unauthorized user : Check user rights or access to required data")
        }
    }

    suspend fun <T>apply(condition: suspend () -> Boolean, protectedMethod: suspend () -> T): T {
        if (condition()) {
            return protectedMethod()
        } else {
            throw AccessDeniedException("Unauthorized user : Check user rights or access to required data")
        }
    }

    fun <T>or(condition1: suspend () -> Boolean, condition2: suspend () -> Boolean, protectedMethod: () -> Flow<T>): Flow<T> = flow {
        if (condition1() || condition2()) {
            emitAll(protectedMethod())
        } else {
            throw AccessDeniedException("Unauthorized user : Check user rights or access to required data")
        }
    }
    fun <T>or(condition1: suspend () -> Boolean, condition2: suspend () -> Boolean, condition3: suspend () -> Boolean, protectedMethod: () -> Flow<T>): Flow<T> = flow {
        if (condition1() || condition2()) {
            emitAll(protectedMethod())
        } else {
            throw AccessDeniedException("Unauthorized user : Check user rights or access to required data")
        }
    }
    suspend fun <T>or(condition1: suspend () -> Boolean, condition2: suspend () -> Boolean, protectedMethod: suspend () -> T): T {
        if (condition1() || condition2()) {
            return protectedMethod()
        } else {
            throw AccessDeniedException("Unauthorized user : Check user rights or access to required data")
        }
    }
    suspend fun <T>or(condition1: suspend () -> Boolean, condition2: suspend () -> Boolean, condition3: suspend () -> Boolean, protectedMethod: suspend () -> T): T {
        if (condition1() || condition2() || condition3()) {
            return protectedMethod()
        } else {
            throw AccessDeniedException("Unauthorized user : Check user rights or access to required data")
        }
    }

    fun <T>and(condition1: suspend () -> Boolean, condition2: suspend () -> Boolean, protectedMethod: () -> Flow<T>): Flow<T> = flow {
        if (condition1() && condition2()) {
            emitAll(protectedMethod())
        } else {
            throw AccessDeniedException("Unauthorized user : Check user rights or access to required data")
        }
    }
    fun <T>and(condition1: suspend () -> Boolean, condition2: suspend () -> Boolean, condition3: suspend () -> Boolean, protectedMethod: () -> Flow<T>): Flow<T> = flow {
        if (condition1() && condition2()) {
            emitAll(protectedMethod())
        } else {
            throw AccessDeniedException("Unauthorized user : Check user rights or access to required data")
        }
    }
    suspend fun <T>and(condition1: suspend () -> Boolean, condition2: suspend () -> Boolean, protectedMethod: suspend () -> T): T {
        if (condition1() && condition2()) {
            return protectedMethod()
        } else {
            throw AccessDeniedException("Unauthorized user : Check user rights or access to required data")
        }
    }
    suspend fun <T>and(condition1: suspend () -> Boolean, condition2: suspend () -> Boolean, condition3: suspend () -> Boolean, protectedMethod: suspend () -> T): T {
        if (condition1() && condition2() || condition3()) {
            return protectedMethod()
        } else {
            throw AccessDeniedException("Unauthorized user : Check user rights or access to required data")
        }
    }

    suspend fun isPatient() = sessionLogic.getCurrentSessionContext().getUserDetails().authorities.contains(Roles.GrantedAuthority.ROLE_PATIENT as Serializable)
    fun <T>isPatient(protectedMethod: () -> Flow<T>): Flow<T> = apply( { isPatient() }, protectedMethod)
    suspend fun <T>isPatient(protectedMethod: suspend () -> T):T = apply( { isPatient() }, protectedMethod)

    suspend fun isHcp() = sessionLogic.getCurrentSessionContext().getUserDetails().authorities.map { it.authority }.contains(Roles.GrantedAuthority.ROLE_HCP)
    fun <T>isHcpAsFlow(protectedMethod: () -> Flow<T>): Flow<T> = apply( { isHcp() }, protectedMethod)
    suspend fun <T>isHcp(protectedMethod: suspend () -> T):T = apply( { isHcp() }, protectedMethod)

    suspend fun isUser() = sessionLogic.getCurrentSessionContext().getUserDetails().authorities.map { it.authority }.contains(Roles.GrantedAuthority.ROLE_USER)
    fun <T>isUserAsFlow(protectedMethod: () -> Flow<T>): Flow<T> = apply( { isUser() }, protectedMethod)
    suspend fun <T>isUser(protectedMethod: suspend () -> T):T = apply( { isUser() }, protectedMethod)

    suspend fun hasHcpIdInHierarchy(loggedHcpId: String, checkedHcpId: String) : Boolean = loggedHcpId == checkedHcpId || healthcarePartyDAO.get(loggedHcpId)?.parentId?.let {
        hasHcpIdInHierarchy(it, checkedHcpId)
    } ?: false

    suspend fun isLoggedAs(checkedHcpId: String) = hasHcpIdInHierarchy(sessionLogic.getCurrentHealthcarePartyId(), checkedHcpId)
    fun <T>isLoggedAsFlow(checkedHcpId: String, protectedMethod: () -> Flow<T>): Flow<T> = apply( { isLoggedAs(checkedHcpId) }, protectedMethod)
    suspend fun <T>isLoggedAs(checkedHcpId: String, protectedMethod: suspend () -> T):T = apply( { isLoggedAs(checkedHcpId) }, protectedMethod)
}
