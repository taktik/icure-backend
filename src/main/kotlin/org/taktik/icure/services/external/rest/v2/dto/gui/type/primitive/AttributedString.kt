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
package org.taktik.icure.services.external.rest.v2.dto.gui.type.primitive

import java.io.Serializable
import java.io.UnsupportedEncodingException
import org.taktik.icure.services.external.rest.v2.dto.gui.type.Data

/**
 * Created by aduchate on 19/11/13, 10:41
 */
class AttributedString(val rtfString: String? = null, val rtfData: ByteArray? = null) : Data(), Primitive {
	fun length(): Int {
		return if (rtfString!!.length > 0) rtfString!!.length else rtfData!!.size
	}

	override fun getPrimitiveValue(): Serializable? {
		return try {
			String(rtfData!!, Charsets.UTF_8)
		} catch (e: UnsupportedEncodingException) {
			throw IllegalStateException(e)
		}
	}

	companion object {
		fun getRtfUnicodeEscapedString(s: String?): String {
			val sb = StringBuilder()
			for (i in 0 until s!!.length) {
				val c = s[i]
				if (c.code == 0x0a || c.code == 0x0d) sb.append("\\line\n") else if (c.code <= 0x7f) sb.append(c) else sb.append(
					"\\u"
				).append(
					c.code
				).append("?")
			}
			return sb.toString()
		}
	}
}
