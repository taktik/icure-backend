/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.security

import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler

class AuthenticationFailureHandler : SimpleUrlAuthenticationFailureHandler() {
	@Throws(IOException::class, ServletException::class)
	override fun onAuthenticationFailure(
		httpRequest: HttpServletRequest,
		httpResponse: HttpServletResponse,
		exception: AuthenticationException
	) {
		val failureURL = httpRequest.session.getAttribute(FAILURE_URL) as String?
		if (failureURL == null) {
			super.onAuthenticationFailure(httpRequest, httpResponse, exception)
		} else {
			saveException(httpRequest, exception)
			logger.debug("Redirecting to $failureURL")
			httpResponse.sendRedirect(failureURL)
		}
	}

	companion object {
		const val FAILURE_URL = "failureURL"
	}
}
