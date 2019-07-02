package org.taktik.icure.be.ehealth.logic.kmehr.sumehr.impl.v20161201

import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito
import org.taktik.icure.entities.Patient

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
}