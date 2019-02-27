package org.taktik.icure.services.external.rest.v1.facade.be

import io.swagger.annotations.Api
import ma.glasnost.orika.MapperFacade
import org.springframework.stereotype.Component
import org.taktik.icure.entities.Invoice
import org.taktik.icure.exceptions.CreationException
import org.taktik.icure.logic.DocumentLogic
import org.taktik.icure.logic.EfactLogic
import org.taktik.icure.logic.HealthcarePartyLogic
import org.taktik.icure.logic.InsuranceLogic
import org.taktik.icure.logic.InvoiceLogic
import org.taktik.icure.logic.PatientLogic
import org.taktik.icure.logic.SessionLogic
import org.taktik.icure.services.external.rest.v1.dto.MapOfIdsDto
import org.taktik.icure.services.external.rest.v1.dto.be.efact.InvoicesBatch
import org.taktik.icure.services.external.rest.v1.dto.be.efact.MessageWithBatch
import org.taktik.icure.services.external.rest.v1.facade.OpenApiFacade
import java.io.IOException
import java.util.HashMap
import javax.security.auth.login.LoginException
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces


@Component
@Path("/be_efact")
@Api(tags = ["be_efact"])
@Consumes("application/json")
@Produces("application/json")
class EfactFacade(val mapper: MapperFacade, val efactLogic: EfactLogic, val sessionLogic: SessionLogic, val healthcarePartyLogic: HealthcarePartyLogic, val invoiceLogic: InvoiceLogic, val patientLogic: PatientLogic, val documentLogic: DocumentLogic, val insuranceLogic: InsuranceLogic) : OpenApiFacade {

    @Path("/{insuranceId}/{batchRef}/{numericalRef}")
    @POST

    @Throws(IOException::class, LoginException::class, CreationException::class)
    fun createBatchAndMessage(@PathParam("insuranceId") insuranceId: String, @PathParam("batchRef") batchRef: String, @PathParam("numericalRef") numericalRef: Long, ids: MapOfIdsDto): MessageWithBatch {
        val hcp = healthcarePartyLogic.getHealthcareParty(sessionLogic.currentSessionContext.user.healthcarePartyId)
        val ins = insuranceLogic.getInsurance(insuranceId)

        val invoices = HashMap<String, List<Invoice>>()
        for ((key, value) in ids.mapOfIds) {
            invoices[key] = invoiceLogic.getInvoices(value)
        }

        return efactLogic.prepareBatch(batchRef, numericalRef, hcp, ins, false, invoices)
    }
}

