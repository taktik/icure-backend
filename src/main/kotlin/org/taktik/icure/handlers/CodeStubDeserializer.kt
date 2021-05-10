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

package org.taktik.icure.handlers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.boot.jackson.JsonObjectDeserializer
import org.taktik.icure.entities.base.CodeStub

class CodeStubDeserializer : JsonObjectDeserializer<CodeStub>() {
    override fun deserializeObject(jsonParser: JsonParser?, context: DeserializationContext?, codec: ObjectCodec, tree: JsonNode): CodeStub {
        val id = tree["_id"]?.textValue()
        val code = tree["code"]?.textValue()
        val type = tree["type"]?.textValue()
        val version = tree["version"]?.textValue()
        val context = tree["context"]?.textValue()
        val label = tree["label"]

        val codeStub = CodeStub(id = id
                ?: "$type|$code|$version", code = code, type = type, version = version, context = context, label = label?.let { codec.treeToValue(it, Map::class.java) as Map<String, String> } ?: mapOf())
        return codeStub
    }
}
