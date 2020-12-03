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

package org.taktik.net

import org.apache.http.client.utils.URIBuilder
import org.apache.http.message.BasicNameValuePair

fun java.net.URI.append(s: String?): java.net.URI {
    return s?.let { s -> URIBuilder(this).let { it.setPathSegments(it.pathSegments + s.trim('/').split("/")) }.build() } ?: this
}

fun java.net.URI.param(k: String, v: String): java.net.URI {
    return URIBuilder(this).setParameter(k, v).build()
}

fun java.net.URI.params(map: Map<String, String>): java.net.URI {
    return URIBuilder(this).setParameters(map.entries.map { (k, v) -> BasicNameValuePair(k, v) }.toList()).build()
}
