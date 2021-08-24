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

enum class DocumentTypeDto {
    admission, alert, bvt_sample, clinicalpath, clinicalsummary, contactreport, quote, invoice, death, discharge, dischargereport, ebirth_baby_medicalform, ebirth_baby_notification, ebirth_mother_medicalform, ebirth_mother_notification, ecare_safe_consultation, epidemiology, intervention, labrequest, labresult, medicaladvisoragreement, medicationschemeelement, note, notification, pharmaceuticalprescription, prescription, productdelivery, quickdischargereport, radiationexposuremonitoring, referral, report, request, result, sumehr, telemonitoring, template, template_admin, treatmentsuspension, vaccination;

    companion object {
        fun fromName(name: String): DocumentTypeDto? = values().find { it.name == name }
    }
}
