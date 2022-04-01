package org.taktik.icure.domain.result

data class AggregatedAccessLogs(val totalSize: Int, val scannedAccessLogs: Int, val patientIds: Set<String>, val nextKey: Long?)
