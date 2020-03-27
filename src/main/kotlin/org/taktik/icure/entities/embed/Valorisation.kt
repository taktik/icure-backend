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
import java.io.Serializable
import java.util.Objects

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Valorisation : Serializable {
    var startOfValidity //yyyyMMdd
            : Long? = null
    var endOfValidity //yyyyMMdd
            : Long? = null
    var predicate: String? = null
    var totalAmount //=reimbursement+doctorSupplement+intervention
            : Double? = null
    var reimbursement: Double? = null
    var patientIntervention: Double? = null
    var doctorSupplement: Double? = null
    var vat: Double? = null
    var label //ex: {en: Rheumatic Aortic Stenosis, fr: Sténose rhumatoïde de l'Aorte}
            : Map<String, String>? = null

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as Valorisation
        return startOfValidity == that.startOfValidity &&
                endOfValidity == that.endOfValidity &&
                predicate == that.predicate &&
                totalAmount == that.totalAmount &&
                reimbursement == that.reimbursement &&
                patientIntervention == that.patientIntervention &&
                doctorSupplement == that.doctorSupplement &&
                vat == that.vat &&
                label == that.label
    }

    override fun hashCode(): Int {
        return Objects.hash(startOfValidity, endOfValidity, predicate, totalAmount, reimbursement, patientIntervention, doctorSupplement, vat, label)
    }
}
