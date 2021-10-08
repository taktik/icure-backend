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

package org.taktik.icure.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("icure.couchdb")
data class CouchDbProperties(
	var autoUpdateViewOnChange: Boolean  = false,
	var prefix: String = "",
	var url:String = "http://127.0.0.1:5984",
    var altUrls:String = "",
	var maxConnections:Int = 500,
	var socketTimeout:Int = 60_000,
	var username :String? = "icure",
	var password :String? = object : Any() {
		internal var t: Int = 0
		override fun toString(): String {
			val buf = ByteArray(14)
			t = 1207948484
			buf[0] = t.ushr(6).toByte()
			t = -1573666950
			buf[1] = t.ushr(16).toByte()
			t = 629722509
			buf[2] = t.ushr(2).toByte()
			t = 1524225294
			buf[3] = t.ushr(17).toByte()
			t = 515550243
			buf[4] = t.ushr(15).toByte()
			t = -1300745127
			buf[5] = t.ushr(23).toByte()
			t = 979810152
			buf[6] = t.ushr(17).toByte()
			t = 1681262155
			buf[7] = t.ushr(4).toByte()
			t = 1691953774
			buf[8] = t.ushr(20).toByte()
			t = 638657025
			buf[9] = t.ushr(3).toByte()
			t = 702639289
			buf[10] = t.ushr(18).toByte()
			t = 1714387445
			buf[11] = t.ushr(21).toByte()
			t = -1553099596
			buf[12] = t.ushr(16).toByte()
			t = 1080621827
			buf[13] = t.ushr(24).toByte()
			return String(buf)
		}
	}.toString(),
    var desingDocumentStatusCheckTimeoutMilliseconds: Long = 2000,
    var cachedDesignDocuemntTtlMinutes: Long = 15
) {
    fun altUrlsList() = if (altUrls.isNullOrBlank()) listOf() else altUrls.split(";")
}

