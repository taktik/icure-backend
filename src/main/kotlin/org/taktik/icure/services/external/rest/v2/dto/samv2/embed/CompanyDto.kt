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

package org.taktik.icure.services.external.rest.v2.dto.samv2.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CompanyDto(
	override val from: Long? = null,
	override val to: Long? = null,
	val authorisationNr: String? = null,
	val vatNr: Map<String, String>? = null,
	val europeanNr: String? = null,
	val denomination: String? = null,
	val legalForm: String? = null,
	val building: String? = null,
	val streetName: String? = null,
	val streetNum: String? = null,
	val postbox: String? = null,
	val postcode: String? = null,
	val city: String? = null,
	val countryCode: String? = null,
	val phone: String? = null,
	val language: String? = null,
	val website: String? = null
) : DataPeriodDto
