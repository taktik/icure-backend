package org.taktik.couchdb.exception

import com.fasterxml.jackson.databind.JsonNode

class ViewResultException(val key: JsonNode?, val error: String?) : DbAccessException(String.format("key: %s error: \"%s\"", key, error))
