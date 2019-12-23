package org.taktik.icure.services.external.rest.v1.controllers.be

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import kotlinx.coroutines.flow.toList
import ma.glasnost.orika.MapperFacade
import org.springframework.web.bind.annotation.*
import org.taktik.icure.entities.Invoice
import org.taktik.icure.asynclogic.*
import org.taktik.icure.services.external.rest.v1.dto.MapOfIdsDto
import org.taktik.icure.services.external.rest.v1.dto.be.efact.MessageWithBatch
import java.util.*


@RestController
@RequestMapping("/rest/v1/be_efact")
@Api(tags = ["be_efact"])
class EfactController(val mapper: MapperFacade,
                      val efactLogic: EfactLogic,
                      val sessionLogic: AsyncSessionLogic,
                      val healthcarePartyLogic: HealthcarePartyLogic,
                      val invoiceLogic: InvoiceLogic,
                      val patientLogic: PatientLogic,
                      val documentLogic: DocumentLogic,
                      val insuranceLogic: InsuranceLogic) {

    @ApiOperation(nickname = "createBatchAndMessage", value = "create batch and message")
    @PostMapping("/{insuranceId}/{newMessageId}/{numericalRef}")
    suspend fun createBatchAndMessage(@PathVariable insuranceId: String,
                              @PathVariable newMessageId: String,
                              @PathVariable numericalRef: Long,
                              @RequestBody ids: MapOfIdsDto): MessageWithBatch? {
        val hcp = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
        val ins = insuranceLogic.getInsurance(insuranceId)

        val invoices = HashMap<String, List<Invoice>>()
        for ((key, value) in ids.mapOfIds) {
            invoices[key] = invoiceLogic.getInvoices(value).toList()
        }

        return if (hcp != null && ins != null) {
            efactLogic.prepareBatch(newMessageId, numericalRef, hcp, ins, false, invoices)
        }else null
    }
}

