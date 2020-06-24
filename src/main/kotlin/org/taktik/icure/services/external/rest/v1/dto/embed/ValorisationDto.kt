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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ValorisationDto(
        val startOfValidity: Long? = null, //yyyyMMdd
        val endOfValidity: Long? = null, //yyyyMMdd
        val predicate: String? = null,
        val totalAmount: Double? = null, //=reimbursement+doctorSupplement+intervention
        val reimbursement: Double? = null,
        val patientIntervention: Double? = null,
        val doctorSupplement: Double? = null,
        val vat: Double? = null,
        val label: Map<String, String>? = mapOf(), //ex: {en: Rheumatic Aortic Stenosis, fr: Sténose rhumatoïde de l'Aorte}
        override val encryptedSelf: String? = null
) : EncryptedDto, Serializable
