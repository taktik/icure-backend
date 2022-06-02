package org.taktik.icure.entities.embed

import java.io.Serializable
import org.taktik.icure.entities.base.EnumVersion

@EnumVersion(1L)
enum class PersonNameUse : Serializable {
	usual, official, temp, nickname, anonymous, maiden, old, other;
}
