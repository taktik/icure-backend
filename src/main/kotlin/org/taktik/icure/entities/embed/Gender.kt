/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */
package org.taktik.icure.entities.embed

import java.io.Serializable
import org.taktik.icure.entities.base.EnumVersion

/**
 * Created by aduchate on 21/01/13, 14:56
 */
@EnumVersion(1L)
enum class Gender(val code: String) : Serializable {
	male("M"), female("F"), indeterminate("I"), changed("C"), changedToMale("Y"), changedToFemale("X"), unknown("U");

	override fun toString(): String {
		return code
	}

	companion object {
		fun fromCode(code: String?): Gender? {
			return code?.let { c -> values().find { c == it.code } }
		}
	}
}
