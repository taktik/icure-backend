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
package org.taktik.icure.services.external.rest.v2.dto.embed

import java.io.Serializable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import io.swagger.v3.oas.annotations.media.Schema

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
@Schema(description = "A relationship between this patient and another person.")
data class PartnershipDto(
	@Schema(description = "Type of relationship.") val type: PartnershipTypeDto? = null, //codes are from CD-CONTACT-PERSON
	@Schema(description = "Status of the relationship.") val status: PartnershipStatusDto? = null,
	@Schema(description = "UUID of the contact person or patient in this relationship.") val partnerId: String? = null, //PersonDto: can either be a patient or a hcp
	@get:Deprecated("use type instead")
	val meToOtherRelationshipDescription: String? = null, //son if partnerId is my son - codes are from CD-CONTACT-PERSON
	@get:Deprecated("use type instead")
	val otherToMeRelationshipDescription: String? = null //father/mother if partnerId is my son
) : Serializable
