package org.taktik.icure.entities.embed

import org.taktik.icure.entities.base.EnumVersion
import java.io.Serializable

@EnumVersion(1L)
enum class DeactivationReason : Serializable {
    deceased, moved, other_doctor, retired, no_contact, unknown, none
}
