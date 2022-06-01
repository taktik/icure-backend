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
package org.taktik.icure.services.external.rest.v2.dto.gui.type

import java.io.Serializable

/**
 * Created by aduchate on 19/11/13, 10:33
 */
class Measure(
	val label: String = "",
	val unit: String = "",
	val value: Number? = null,
	val minRef: Number? = null,
	val maxRef: Number? = null,
	val severity: Number = 0,
) : Data(), Serializable {
	fun checkValue(): Int {
		if (severity != null && severity!!.toInt() > 0) {
			return TOO_HIGHT
		}
		return if (minRef == null) {
			if (maxRef == null) {
				OK
			} else {
				if (value == null) return OK
				if (value!!.toDouble() > maxRef!!.toDouble()) {
					TOO_HIGHT
				} else {
					OK
				}
			}
		} else {
			if (maxRef == null) {
				if (value == null) return OK
				if (value!!.toDouble() < minRef!!.toDouble()) {
					TOO_LOW
				} else {
					OK
				}
			} else {
				if (minRef == maxRef) {
					return OK
				}
				if (value == null) return OK
				if (maxRef!!.toDouble() > minRef!!.toDouble() && value!!.toDouble() > maxRef!!.toDouble()) {
					TOO_HIGHT
				} else if (value!!.toDouble() < minRef!!.toDouble()) {
					TOO_LOW
				} else {
					OK
				}
			}
		}
	}

	val restriction: String?
		get() = if (minRef == null) {
			if (maxRef == null) {
				null
			} else {
				"<" + maxRef
			}
		} else {
			if (maxRef == null) {
				">" + minRef
			} else {
				if (minRef == maxRef) {
					null
				} else "" + minRef + "-" + maxRef
			}
		}

	companion object {
		const val OK = 0
		const val TOO_LOW = 1
		const val TOO_HIGHT = 2
	}
}
