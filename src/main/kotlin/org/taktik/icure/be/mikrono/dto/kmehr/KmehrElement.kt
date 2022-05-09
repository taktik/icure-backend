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
package org.taktik.icure.be.mikrono.dto.kmehr

import java.io.Serializable
import java.util.*
import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * Created by IntelliJ IDEA.
 * User: aduchate
 * Date: 15 sept. 2010
 * Time: 12:14:21
 * To change this template use File | Settings | File Templates.
 */
open class KmehrElement : Serializable {
	var id: String? = null
	var otherIds: MutableList<String?>? = ArrayList()
	open var types: MutableList<String?>? = ArrayList()

	fun addType(s: String?) {
		if (types == null) {
			types = ArrayList()
		}
		types!!.add(s)
	}

	fun addId(s: String?) {
		if (id == null) {
			id = s
		} else {
			if (otherIds == null) {
				otherIds = ArrayList()
			}
			otherIds!!.add(s)
		}
	}

	fun getId(s: String): String? {
		if (id != null && id!!.startsWith("$s:")) {
			return id!!.split(":").toTypedArray()[1]
		}
		if (otherIds != null) {
			for (sid in otherIds!!) {
				if (sid!!.startsWith("$s:")) {
					return sid.split(":").toTypedArray()[1]
				}
			}
		}
		return null
	}

	@get:JsonIgnore
	val ids: List<String?>
		get() {
			val result: MutableList<String?> = ArrayList()
			result.add(id)
			if (otherIds != null) {
				result.addAll(otherIds!!)
			}
			return result
		}
}
