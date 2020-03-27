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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Episode : Serializable {
    @NotNull
    var id: String? = null
    var name: String? = null
    var comment: String? = null

    //Usually one of the following is used
    @NotNull(autoFix = AutoFix.FUZZYNOW)
    var startDate // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20140101235960.
            : Long? = null

    @NotNull(autoFix = AutoFix.FUZZYNOW)
    var endDate // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20140101235960.
            : Long? = null

    fun solveConflictWith(other: Episode): Episode {
        name = if (name == null) other.name else name
        comment = if (comment == null) other.comment else comment
        startDate = if (other.startDate == null) startDate else if (startDate == null) other.startDate else java.lang.Long.valueOf(Math.min(startDate!!, other.startDate!!))
        return this
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
