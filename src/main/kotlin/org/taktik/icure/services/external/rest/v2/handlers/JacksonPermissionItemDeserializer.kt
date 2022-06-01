package org.taktik.icure.services.external.rest.v2.handlers

import java.io.IOException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import org.taktik.icure.services.external.rest.v2.dto.security.AlwaysPermissionItemDto
import org.taktik.icure.services.external.rest.v2.dto.security.PermissionItemDto

class JacksonPermissionItemDeserializer : JsonDeserializer<PermissionItemDto>() {

	override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): PermissionItemDto {
		return try {
			val codec = p?.codec
			val tree = codec?.readTree<JsonNode>(p)

			deserializeObject(codec!!, tree!!)
		} catch (ex: Exception) {
			if (ex is IOException) {
				throw ex
			}
			throw JsonMappingException(p, "Object deserialize error", ex)
		}
	}

	fun deserializeObject(codec: ObjectCodec, tree: JsonNode): PermissionItemDto {
		return when (tree["itemType"]?.asText()) {
			"AlwaysPermissionItemDto" -> codec.treeToValue(tree, AlwaysPermissionItemDto::class.java)
			else -> throw IllegalArgumentException("Unknown Permission Item")
		}
	}
}
