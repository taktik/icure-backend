package org.taktik.icure.services.external.rest.v1.dto.embed

import java.io.Serializable


data class RightDto(
        val userId: String? = null,
        val isRead: Boolean = false,
        val isWrite: Boolean = false,
        val isAdministration: Boolean = false
) : Serializable
