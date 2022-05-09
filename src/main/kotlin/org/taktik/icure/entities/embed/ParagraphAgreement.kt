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

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ParagraphAgreement(
	val timestamp: Long? = null,
	val paragraph: String? = null,
	val accepted: Boolean? = null,
	val inTreatment: Boolean? = null,
	val canceled: Boolean? = null,
	val careProviderReference: String? = null,
	val decisionReference: String? = null,
	val start: Long? = null,
	val end: Long? = null,
	val cancelationDate: Long? = null,
	val quantityValue: Double? = null,
	val quantityUnit: String? = null,
	val ioRequestReference: String? = null,
	val responseType: String? = null,
	val refusalJustification: Map<String, String>? = null,
	val verses: Set<Long>? = null,
	val coverageType: String? = null,
	val unitNumber: Double? = null,
	val strength: Double? = null,
	val strengthUnit: String? = null,
	val agreementAppendices: List<AgreementAppendix>? = null,
	val documentId: String? = null
) : Serializable
