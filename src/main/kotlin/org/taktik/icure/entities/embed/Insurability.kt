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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import java.io.Serializable
import java.util.HashMap

/**
 * Created by aduchate on 21/01/13, 15:37
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Insurability : Serializable {
    //Key from InsuranceParameter
    var parameters: Map<String, String> = HashMap()
    var hospitalisation: Boolean? = null
    var ambulatory: Boolean? = null
    var dental: Boolean? = null
    var identificationNumber // N° in form (number for the insurance's identification)
            : String? = null
    var insuranceId // UUID to identify Partena, etc. (link to Insurance object's document ID)
            : String? = null
    var startDate: Long? = null
    var endDate: Long? = null
    var titularyId //UUID of the contact person who is the titulary of the insurance
            : String? = null

    @get:JsonIgnore
    @set:JsonIgnore
    @JsonIgnore
    var insuranceDescription: String? = null

    fun mergeFrom(other: Insurability?) {
        //TODO: implement
    }
}
