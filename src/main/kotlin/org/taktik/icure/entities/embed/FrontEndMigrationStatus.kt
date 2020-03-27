package org.taktik.icure.entities.embed

import org.taktik.icure.entities.base.EnumVersion
import java.io.Serializable

@EnumVersion(1L)
enum class FrontEndMigrationStatus : Serializable {
    STARTED, ERROR, SUCCESS
}
