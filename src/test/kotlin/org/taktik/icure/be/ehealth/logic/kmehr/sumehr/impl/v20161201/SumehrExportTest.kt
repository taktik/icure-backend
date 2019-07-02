package org.taktik.icure.be.ehealth.logic.kmehr.sumehr.impl.v20161201

import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito
import org.taktik.icure.entities.Patient
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.ItemType
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.embed.Service

class SumehrExportTest {
    //The method tested needs a SumehrExport Class to run
    private val sumehrExport = SumehrExport()

    private val patient = Mockito.mock(Patient::class.java)

    @Test
    fun getMd5() {
        //Arrange
        val hcPartyId = "1"
        val sfks = listOf("", "")
        val excludedIds = emptyList<String>()

        //Execution
        val md5 = sumehrExport.getMd5(hcPartyId, patient, sfks, excludedIds)

        //Tests
        assertNotNull(md5)
        assertFalse(md5.isBlank())
    }

    @Test
    fun addServiceCodesAndTags() {
        // Arrange
        /// First parameter
        val svc = Service()
        val code = CodeStub("CD-TRANSACTION", "allergy", "1")
        svc.codes.add(code)

        /// Second parameter
        val item = ItemType()

        /// Third parameter
        val skipCdItem = true;

        /// Fourth parameter
        val restrictedTypes = listOf("allergy")

        /// Fifth parameter
        val uniqueTypes = listOf("string")

        /// Sixth parameter
        val excludedTypes = null

        // Execution
        sumehrExport.addServiceCodesAndTags(svc, item, skipCdItem, restrictedTypes, uniqueTypes, excludedTypes)
        //super.addServiceCodesAndTags(svc, item, skipCdItem, restrictedTypes, uniqueTypes, (excludedTypes ?: emptyList()) + listOf("LOCAL", "RELEVANCE", "SUMEHR", "SOAP", "CD-TRANSACTION", "CD-TRANSACTION-TYPE"))
    }
}
