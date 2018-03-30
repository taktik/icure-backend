/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.be.ehealth.logic.recipe.impl

import java.io.Serializable
import java.util.Date

class Feedback(var rid: String? = null,
               var sentBy: Long? = null,
               var sentDate: Date? = null,
               var textContent: String? = null
) : Serializable, Comparable<Feedback> {

    override fun compareTo(other: Feedback): Int {
        if (sentDate == null) {
            return 1
        }
        return sentDate!!.compareTo(other.sentDate!!)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Feedback) return false

        if (rid != other.rid) return false
        if (sentBy != other.sentBy) return false
        if (sentDate != other.sentDate) return false
        if (textContent != other.textContent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rid?.hashCode() ?: 0
        result = 31 * result + (sentBy?.hashCode() ?: 0)
        result = 31 * result + (sentDate?.hashCode() ?: 0)
        result = 31 * result + (textContent?.hashCode() ?: 0)
        return result
    }

}
