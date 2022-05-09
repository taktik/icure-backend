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

import java.lang.IllegalArgumentException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import org.taktik.icure.domain.ContactIdServiceId

class JacksonContactIdServiceIdDeserializer : JsonDeserializer<ContactIdServiceId>() {
	override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ContactIdServiceId {
		return if (p.currentToken()?.isScalarValue == true) {
			ContactIdServiceId(p.readValueAs(String::class.java))
		} else {
			p.readValueAs(HashMap::class.java).let {
				ContactIdServiceId(contactId = it["contactId"] as? String ?: throw IllegalArgumentException("Missing contactId"), serviceId = it["serviceId"] as? String, modified = it["modified"] as? Long)
			}
		}
	}
}
