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
package org.taktik.icure.entities.base

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

/**
 * Created by aduchate on 21/04/13, 17:50
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class SystemTag : Code {
    constructor() {}
    constructor(code: String) : super(SYSTEM_TYPE, code, "1.0") {}

    companion object {
        const val SYSTEM_TYPE = "ICURE_SYSTEM"
        val MEDICAL_HISTORY = SystemTag("MEDICAL_HISTORY")
        val VISIT = SystemTag("VISIT")
        val PARACLINIC = SystemTag("PARACLINIC")
        val GENERAL_FOLLOWUP = SystemTag("GENERAL_FOLLOWUP")
        val GENERAL_STATUS = SystemTag("GENERAL_STATUS")
    }
}
