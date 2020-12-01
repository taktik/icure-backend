package org.taktik.couchdb

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
