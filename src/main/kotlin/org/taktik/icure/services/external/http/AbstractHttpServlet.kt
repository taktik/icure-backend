/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.taktik.icure.services.external.http

import org.slf4j.LoggerFactory
import java.io.IOException
import javax.servlet.Servlet
import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

abstract class AbstractHttpServlet : Servlet {
    protected val log = LoggerFactory.getLogger(javaClass)
    protected var theServletConfig: ServletConfig? = null
    @Throws(ServletException::class)
    override fun init(servletConfig: ServletConfig) {
        this.theServletConfig = servletConfig
    }

    override fun getServletConfig(): ServletConfig {
        return theServletConfig!!
    }

    override fun getServletInfo(): String? {
        return null
    }

    override fun destroy() {
        theServletConfig = null
    }

    @Throws(ServletException::class, IOException::class)
    override fun service(servletRequest: ServletRequest, servletResponse: ServletResponse) {
        if (servletRequest is HttpServletRequest && servletResponse is HttpServletResponse) {
            try {
                handleRequest(servletRequest, servletResponse)
            } catch (se: ServletException) {
                throw se
            } catch (se: IOException) {
                throw se
            } catch (e: Exception) {
                throw ServletException(e)
            }
        }
    }

    @Throws(Exception::class)
    protected abstract fun handleRequest(httpRequest: HttpServletRequest?, httpResponse: HttpServletResponse?)
}
