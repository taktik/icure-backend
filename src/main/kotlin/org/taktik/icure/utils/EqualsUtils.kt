package org.taktik.icure.utils

import java.time.Instant

fun Instant.between(start: Instant, end: Instant): Boolean = this.isAfter(start) && this.isBefore(end)
