package org.taktik.icure.services.external.rest.v1.controllers.be

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import ma.glasnost.orika.MapperFacade
import org.springframework.web.bind.annotation.*
import org.taktik.icure.entities.Invoice
import org.taktik.icure.logic.*
import org.taktik.icure.services.external.rest.v1.dto.MapOfIdsDto
import org.taktik.icure.services.external.rest.v1.dto.be.efact.MessageWithBatch
import java.util.*


@RestController
@RequestMapping("/rest/v1/be_efact")
@Api(tags = ["be_efact"])
class EfactController(val mapper: MapperFacade,
                      val efactLogic: EfactLogic,
                      val sessionLogic: SessionLogic,
                      val healthcarePartyLogic: HealthcarePartyLogic,
                      val invoiceLogic: InvoiceLogic,
                      val patientLogic: PatientLogic,
                      val documentLogic: DocumentLogic,
                      val insuranceLogic: InsuranceLogic) {

    @ApiOperation(nickname = "createBatchAndMessage", value = "create batch and message")
    @PostMapping("/{insuranceId}/{newMessageId}/{numericalRef}")
    fun createBatchAndMessage(@PathVariable insuranceId: String,
                              @PathVariable newMessageId: String,
                              @PathVariable numericalRef: Long,
                              @RequestBody ids: MapOfIdsDto): MessageWithBatch? {
        val hcp = healthcarePartyLogic.getHealthcareParty(sessionLogic.currentSessionContext.user.healthcarePartyId)
        val ins = insuranceLogic.getInsurance(insuranceId)

        val invoices = HashMap<String, List<Invoice>>()
        for ((key, value) in ids.mapOfIds) {
            invoices[key] = invoiceLogic.getInvoices(value)
        }

        return efactLogic.prepareBatch(newMessageId, numericalRef, hcp, ins, false, invoices)
    }
}

