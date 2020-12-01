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

package org.taktik.couchdb.parser

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.math.BigDecimal
import java.time.Instant


internal class InstantAdapter {
    private val bd_1000000 = BigDecimal.valueOf(1000000)
    private val bd_1000 = BigDecimal.valueOf(1000)


    @FromJson
    fun fromJson(instant: Long): Instant {
        return getInstant(BigDecimal(instant))
    }

    @ToJson
    fun toJson(instant: Instant) : Long {
        return getBigDecimal(instant).toLong()
    }

    private fun getBigDecimal(value: Instant): BigDecimal {
        return BigDecimal.valueOf(1000L * value.epochSecond).add(BigDecimal.valueOf(value.nano.toLong()).divide(bd_1000000))
    }

    private fun getInstant(value: BigDecimal): Instant {
        return Instant.ofEpochSecond(value.divide(bd_1000).toLong(), value.remainder(bd_1000).multiply(bd_1000000).toLong())
    }
}
