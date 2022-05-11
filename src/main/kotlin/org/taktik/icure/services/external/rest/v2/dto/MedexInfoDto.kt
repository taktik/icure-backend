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

package org.taktik.icure.services.external.rest.v2.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import io.swagger.v3.oas.annotations.media.Schema

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedexInfoDto(
	val beginDate: Long,
	val endDate: Long,
	val author: HealthcarePartyDto? = null,
	val patient: PatientDto? = null,
	@Schema(defaultValue = "\"fr\"") val patientLanguage: String = "fr",
	@Schema(defaultValue = "\"incapacity\"") val incapacityType: // incapacity or incapacityextension
		String = "incapacity",

	/*
		Possible values:
		illness
		hospitalisation
		sickness
		pregnancy
		workaccident
		occupationaldisease
	 */
	@Schema(defaultValue = "\"sickness\"") val incapacityReason: String = "sickness",
	val outOfHomeAllowed: Boolean = true,

	/*
	"Optional field
	But mandatory when incapacityreason = workaccident; this field must contain the accident date.
	when incapacityreason = occupationaldisease this field must contain the request date for a dossier for occupatialdesease.
	This date must be < or =  beginmoment of the incapacity period."
	 */
	val certificateDate: Long? = null,
	val contentDate: Long? = null,
	val diagnosisICPC: String? = null,
	val diagnosisICD: String? = null,
	val diagnosisDescr: String? = null
)
