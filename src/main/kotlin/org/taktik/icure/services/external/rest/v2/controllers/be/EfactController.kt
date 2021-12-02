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

package org.taktik.icure.services.external.rest.v2.controllers.be

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.mono
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.DocumentLogic
import org.taktik.icure.asynclogic.EfactLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.InsuranceLogic
import org.taktik.icure.asynclogic.InvoiceLogic
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.entities.Invoice
import org.taktik.icure.services.external.rest.v2.dto.MapOfIdsDto
import java.util.*


@RestController("efactControllerV2")
@RequestMapping("/rest/v2/be_efact")
@Tag(name = "beefact")
class EfactController(val efactLogic: EfactLogic,
                      val sessionLogic: AsyncSessionLogic,
                      val healthcarePartyLogic: HealthcarePartyLogic,
                      val invoiceLogic: InvoiceLogic,
                      val patientLogic: PatientLogic,
                      val documentLogic: DocumentLogic,
                      val insuranceLogic: InsuranceLogic) {

    @Operation(summary = "create batch and message")
    @PostMapping("/{insuranceId}/{newMessageId}/{numericalRef}")
    fun createBatchAndMessage(@PathVariable insuranceId: String,
                              @PathVariable newMessageId: String,
                              @PathVariable numericalRef: Long,
                              @RequestBody ids: MapOfIdsDto) = mono {
        val hcp = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val ins = insuranceLogic.getInsurance(insuranceId)

        val invoices = HashMap<String, List<Invoice>>()
        for ((key, value) in ids.mapOfIds) {
            invoices[key] = invoiceLogic.getInvoices(value).toList()
        }

        if (hcp != null && ins != null) {
            efactLogic.prepareBatch(newMessageId, numericalRef, hcp, ins, false, invoices)
        }else null
    }
}

