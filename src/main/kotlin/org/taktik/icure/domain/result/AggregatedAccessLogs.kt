package org.taktik.icure.domain.result

import org.taktik.icure.entities.Patient

data class AggregatedAccessLogs(val totalSize: Int, val scannedAccessLogs: Int, val patientIds: List<Patient>, val nextKey: Long?, val nextDocumentId: String?)
