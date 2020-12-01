package org.taktik.icure.services.external.rest.v1.dto.base

interface CodeIdentificationDto<K> {
    val id: K
    val code: String?
    val context: String?
    val type: String?
    val version: String?
    val label: Map<String, String>

    fun normalizeIdentification(): CodeIdentificationDto<K>
}
