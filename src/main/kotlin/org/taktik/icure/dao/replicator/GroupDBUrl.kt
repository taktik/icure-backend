package org.taktik.icure.dao.replicator

import org.taktik.icure.entities.Group

import java.net.URI
import java.net.URISyntaxException
import java.net.URL

class GroupDBUrl(couchDbUrl: String) {
    private val couchDbUrl = URI(couchDbUrl)

    fun getDbName(group: Group): String {
        return "icure-" + group.id + "-base"
    }

    fun getInstanceUrl(group: Group): String =
        try {
            val server = group.servers?.firstOrNull()?.let { URI(it) } ?: couchDbUrl

            URI(server.scheme,
                null,
                    server.host,
                    server.port, null, null, null).toString()
        } catch (e: URISyntaxException) {
            throw IllegalArgumentException("failed to build url", e)
        }

}
