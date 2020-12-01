package org.taktik.icure.services.external.rest.v1.mapper.utils

import java.time.Instant

interface InstantMapper {
    fun map(instant: Instant): Long = instant.toEpochMilli()
    fun map(long: Long): Instant = Instant.ofEpochMilli(long)
    fun map()
}
