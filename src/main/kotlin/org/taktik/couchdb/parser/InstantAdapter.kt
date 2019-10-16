package org.taktik.couchdb.parser

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.math.BigDecimal
import java.time.Instant


internal class InstantAdapter {
    private val bd_1000000 = BigDecimal.valueOf(1000000)
    private val bd_1000 = BigDecimal.valueOf(1000)


    @FromJson
    fun fromJson(instant: String): Instant {
        return getInstant(BigDecimal(instant))
    }

    @ToJson
    fun toJson(instant: Instant): String {
        return getBigDecimal(instant).toString()
    }

    private fun getBigDecimal(value: Instant): BigDecimal {
        return BigDecimal.valueOf(1000L * value.epochSecond).add(BigDecimal.valueOf(value.nano.toLong()).divide(bd_1000000))
    }

    private fun getInstant(value: BigDecimal): Instant {
        return Instant.ofEpochSecond(value.divide(bd_1000).toLong(), value.remainder(bd_1000).multiply(bd_1000000).toLong())
    }
}