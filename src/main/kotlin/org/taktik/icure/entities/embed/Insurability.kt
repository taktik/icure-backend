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
import com.github.pozo.KotlinBuilder
import java.io.Serializable

/**
 * Created by aduchate on 21/01/13, 15:37
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Insurability(
        //Key from InsuranceParameter
        val parameters: Map<String, String> = mapOf(),
        val hospitalisation: Boolean? = null,
        val ambulatory: Boolean? = null,
        val dental: Boolean? = null,
        val identificationNumber: String? = null, // N° in form (number for the insurance's identification)
        val insuranceId: String? = null, // UUID to identify Partena, etc. (link to Insurance object's document ID)
        val startDate: Long? = null,
        val endDate: Long? = null,
        val titularyId: String? = null, //UUID of the contact person who is the titulary of the insurance
        override val encryptedSelf: String? = null
) : Encrypted, Serializable
