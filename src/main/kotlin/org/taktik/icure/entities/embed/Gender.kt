/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.taktik.icure.entities.embed

import org.taktik.icure.entities.base.EnumVersion
import java.io.Serializable

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
