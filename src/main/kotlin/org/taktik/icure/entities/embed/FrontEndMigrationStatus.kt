package org.taktik.icure.entities.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.entities.base.EnumVersion
import java.io.Serializable

@EnumVersion(1L)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
enum class FrontEndMigrationStatus : Serializable {
    STARTED, ERROR, SUCCESS
}
