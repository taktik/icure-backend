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

package org.taktik.icure.utils

import java.util.*

tailrec fun <K>retry(trials: Int, closure: () -> K): K {
	try {
		return closure()
	} catch (e: Exception) {
		if (trials < 1) {
			throw e
		}
	}
	return retry(trials - 1, closure)
}

tailrec suspend fun <K>suspendRetry(trials: Int, closure: suspend () -> K): K {
	try {
		return closure()
	} catch (e: Exception) {
		if (trials < 1) {
			throw e
		}
	}
	return suspendRetry(trials - 1, closure)
}

fun UUID.xor(other: UUID): UUID {
	return UUID(this.mostSignificantBits.xor(other.mostSignificantBits), this.leastSignificantBits.xor(other.leastSignificantBits))
}
