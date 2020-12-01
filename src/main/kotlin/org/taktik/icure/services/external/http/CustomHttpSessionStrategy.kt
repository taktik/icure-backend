/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.http
// TODO SH later: jsessionid
/*import org.springframework.session.Session
import org.springframework.session.web.http.CookieHttpSessionStrategy
import org.springframework.session.web.http.HeaderHttpSessionStrategy
import org.springframework.session.web.http.HttpSessionStrategy
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CustomHttpSessionStrategy : HttpSessionStrategy {
    private val cok: HttpSessionStrategy = CookieHttpSessionStrategy()
    private val hdr: HttpSessionStrategy = HeaderHttpSessionStrategy()
    private val url: HttpSessionStrategy = UrlHttpSessionStrategy(cok)


    private fun getStrategy(request: HttpServletRequest?) : HttpSessionStrategy =
        when {
            request?.isRequestedSessionIdFromURL ?: false -> url
            else -> cok
        }

    override fun onInvalidateSession(request: HttpServletRequest?, response: HttpServletResponse?) =
        getStrategy(request).onInvalidateSession(request, response)

    override fun getRequestedSessionId(request: HttpServletRequest?): String? =
        getStrategy(request).getRequestedSessionId(request)

    override fun onNewSession(session: Session?, request: HttpServletRequest?, response: HttpServletResponse?) =
        getStrategy(request).onNewSession(session, request, response)

    class UrlHttpSessionStrategy(private val mainStrategy : HttpSessionStrategy) : HttpSessionStrategy {
        override fun onInvalidateSession(request: HttpServletRequest?, response: HttpServletResponse?) {
            mainStrategy.onInvalidateSession(request,response)
        }

        override fun getRequestedSessionId(request: HttpServletRequest?): String? {
            return request?.requestURL?.split(";jsessionid=")?.getOrNull(1)
        }

        override fun onNewSession(session: Session?, request: HttpServletRequest?, response: HttpServletResponse?) {
            mainStrategy.onNewSession(session, request, response)
        }
    }

}

*/
