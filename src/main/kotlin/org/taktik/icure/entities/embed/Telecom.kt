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
import org.taktik.icure.entities.base.Encryptable
import java.io.Serializable

/**
 * Created by aduchate on 21/01/13, 14:47
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Telecom : Serializable, Comparable<Telecom>, Encryptable {
    var telecomType: TelecomType? = null
    var telecomNumber: String? = null
    var telecomDescription: String? = null
    override var encryptedSelf: String? = null

    constructor() {}
    constructor(telecomType: TelecomType?, telecomNumber: String?) {
        this.telecomType = telecomType
        this.telecomNumber = telecomNumber
    }

    constructor(telecomType: TelecomType?, telecomNumber: String?, telecomDescription: String?) {
        this.telecomType = telecomType
        this.telecomNumber = telecomNumber
        this.telecomDescription = telecomDescription
    }

    override fun compareTo(other: Telecom): Int {
        return telecomType!!.compareTo(other.telecomType!!)
    }

    fun mergeFrom(other: Telecom) {
        if (telecomType == null && other.telecomType != null) {
            telecomType = other.telecomType
        }
        if (telecomNumber == null && other.telecomNumber != null) {
            telecomNumber = other.telecomNumber
        }
        if (encryptedSelf == null && other.encryptedSelf != null) {
            encryptedSelf = other.encryptedSelf
        }
    }

    fun forceMergeFrom(other: Telecom) {
        if (other.telecomType != null) {
            telecomType = other.telecomType
        }
        if (other.telecomNumber != null) {
            telecomNumber = other.telecomNumber
        }
        if (other.encryptedSelf != null) {
            encryptedSelf = other.encryptedSelf
        }
    }
}
