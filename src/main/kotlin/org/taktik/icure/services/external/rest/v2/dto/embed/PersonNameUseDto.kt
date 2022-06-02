package org.taktik.icure.services.external.rest.v2.dto.embed

import java.io.Serializable
import org.taktik.icure.entities.base.EnumVersion

@EnumVersion(1L)
enum class PersonNameUseDto : Serializable {
	usual, official, temp, nickname, anonymous, maiden, old, other;
}
