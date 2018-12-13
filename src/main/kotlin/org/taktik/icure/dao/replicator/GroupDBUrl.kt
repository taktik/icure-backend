package org.taktik.icure.dao.replicator

import org.taktik.icure.entities.Group

import java.net.URI
import java.net.URISyntaxException

class GroupDBUrl(couchDbUrl: String) {
    private val couchDbUrl = URI(couchDbUrl)

    fun getDbName(group: Group): String {
        return "icure-" + group.id + "-base"
    }

    fun getInstanceUrl(group: Group): String =
        try {
            URI(couchDbUrl.scheme,
                null,
                couchDbUrl.host,
                couchDbUrl.port, null, null, null).toString()
        } catch (e: URISyntaxException) {
            throw IllegalArgumentException("failed to build url", e)
        }

    fun getDbUrl(group: Group): String =
        try {
            URI(couchDbUrl.scheme,
                group.id + ":" + group.password,
                couchDbUrl.host,
                couchDbUrl.port,
                "/" + getDbName(group), null, null).toString()
        } catch (e: URISyntaxException) {
            throw IllegalArgumentException("failed to build url", e)
        }

    fun getLocalDbUrl(group: Group): String =
        try {
            URI(couchDbUrl.scheme,
                group.id + ":" + group.password,
                "127.0.0.1",
                couchDbUrl.port,
                "/" + getDbName(group), null, null).toString()
        } catch (e: URISyntaxException) {
            throw IllegalArgumentException("failed to build url", e)
        }


}
