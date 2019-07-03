package org.taktik.icure.be.ehealth.logic.kmehr.sumehr.impl.v20161201

import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito
import org.taktik.icure.entities.*
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.ItemType
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.be.ehealth.logic.kmehr.v20161201.KmehrExport

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
        val code1 = CodeStub("CD-AUTONOMY", "CD-ITEM", "1")
        val tag1 = CodeStub("CD-AUTONOMY", "CD-ITEM", "1")
        val code2 = CodeStub("CD-AUTONOMY", "CD-VACCINE", "1")
        val tag2 = CodeStub("CD-AUTONOMY", "CD-VACCINE", "1")
        val svc1 = Service()
        svc1.codes.add(code1)
        svc1.tags.add(tag1)
        svc1.codes.add(code2)
        svc1.tags.add(tag2)

        /// Second parameter
        val item1 = ItemType()
        val item2 = ItemType()

        /// Third parameter
        val skipCdItem = true;

        /// Fourth parameter
        val restrictedTypes1 = listOf("CD-AUTONOMY","LOCAL")

        /// Fifth parameter
        val uniqueTypes1 = listOf("CD-AUTONOMY")

        /// Sixth parameter
        val excludedTypes1 = null

        // Execution
        sumehrExport.addServiceCodesAndTags(svc1, item1, skipCdItem, restrictedTypes1, uniqueTypes1, excludedTypes1)
        KmehrExport().addServiceCodesAndTags(svc1, item2, skipCdItem, restrictedTypes1, uniqueTypes1, excludedTypes1)

        // Test
        var test1 = false
        item1.contents[0].cds.forEach{ c ->
            if( c.s.value().equals("LOCAL")){
                test1 = true
            }
        }
        Assert.assertFalse(test1)

        var test2 = false
        item2.contents[0].cds.forEach{ c ->
            if( c.s.value().equals("LOCAL")){
                test2 = true
            }
        }
        Assert.assertTrue(test2)
    }
}
