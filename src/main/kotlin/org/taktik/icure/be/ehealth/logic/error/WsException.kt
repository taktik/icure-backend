/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.be.ehealth.logic.error

import org.taktik.icure.be.ehealth.logic.messages.AbstractMessage

/**
 * @author Bernard Paulus on 23/05/17.
 */
class WsException(message : String? = null, exception: Throwable? = null, val error: ErrorCode, val arguments: List<Any> = listOf()) : RuntimeException(message, exception) {
	override fun toString(): String {
		return "WsException(error=$error, arguments=$arguments, super=${super.toString()})"
	}
}
