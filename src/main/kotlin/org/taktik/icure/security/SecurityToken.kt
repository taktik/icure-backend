package org.taktik.icure.security

import org.springframework.http.HttpMethod
import org.springframework.security.core.Authentication
import java.io.Serializable

data class SecurityToken(val method: HttpMethod, val path: String, val authentication: Authentication) : Serializable
