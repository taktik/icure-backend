/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.security

import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler

class AuthenticationSuccessHandler : SimpleUrlAuthenticationSuccessHandler() {
	@Throws(ServletException::class, IOException::class)
	override fun onAuthenticationSuccess(
		httpRequest: HttpServletRequest,
		httpResponse: HttpServletResponse,
		authentication: Authentication
	) {
		super.onAuthenticationSuccess(httpRequest, httpResponse, authentication)
	}

	override fun determineTargetUrl(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse): String {
		/* Get target URL */
		var target = super.determineTargetUrl(httpRequest, httpResponse)

		/* Replace using optional destination */
		val destination = httpRequest.getParameter("destination")
		if (destination != null) {
			target = destination
		}

		/* Add optional queryString */
		val queryString = httpRequest.getParameter("queryString")
		if (queryString != null) {
			target += queryString
		}
		return target
	}
}
