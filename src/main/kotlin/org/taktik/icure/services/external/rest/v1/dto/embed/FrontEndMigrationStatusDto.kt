package org.taktik.icure.services.external.rest.v1.dto.embed

import org.taktik.icure.services.external.rest.v1.dto.base.EnumVersionDto
import java.io.Serializable
import com.github.pozo.KotlinBuilder@EnumVersionDto(1L)

enum class FrontEndMigrationStatusDto : Serializable {
    STARTED, ERROR, SUCCESS
}
