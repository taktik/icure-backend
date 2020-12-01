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
package org.taktik.icure.services.external.rest.v1.dto.embed

/**
 * Created by aduchate on 29/03/13, 18:37
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class DelegationDto(
        //This is not the owner of a piece of date (patient, contact). It is the owner of the delegation.
        var owner: String? = null, // owner id
        var delegatedTo: String? = null, // delegatedTo id
        var key: String? = null, // An arbitrary key (generated, patientId, any ID, etc.), usually prefixed with the entity ID followed by ":", encrypted using an exchange AES key.
        var tags: List<String> = listOf() // Used for rights
) : Serializable
