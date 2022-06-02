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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder

/**
 * Created by aduchate on 21/01/13, 15:37
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Insurability(
	//Key from InsuranceParameter
	val parameters: Map<String, String> = emptyMap(),
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
