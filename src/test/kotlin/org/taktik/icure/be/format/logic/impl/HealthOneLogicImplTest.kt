package org.taktik.icure.be.format.logic.impl;


import org.junit.*
import org.mockito.Matchers
import org.mockito.Mockito.*
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.*
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.AddressType
import org.taktik.icure.entities.embed.Gender
import org.taktik.icure.logic.ContactLogic
import org.taktik.icure.logic.DocumentLogic
import org.taktik.icure.logic.PatientLogic

import java.io.*
import java.nio.charset.Charset
import java.time.LocalDateTime

import kotlin.io.outputStream


class HealthOneLogicImplTest {
    //The method tested needs a HealthOneLogicImpl Class to run
    val HealthOneLogicImpl = HealthOneLogicImpl();

    val contactLogic = mock(ContactLogic::class.java)
    val documentLogic = mock(DocumentLogic::class.java)
    val patientLogic = mock(PatientLogic::class.java)

    @Before
    fun setUp() {
        `when`(contactLogic.modifyContact(Matchers.any(Contact::class.java)))
                .thenAnswer { it.getArgumentAt(0, Contact::class.java) }

        `when`(documentLogic.modifyDocument(Matchers.any(Document::class.java)))
                .thenAnswer { it.getArgumentAt(0, Document::class.java) }

        `when`(patientLogic.modifyPatient(Matchers.any(Patient::class.java)))
                .thenAnswer { it.getArgumentAt(0, Patient::class.java) }
    }

    @Test
    fun setPatientLogic() {
        Assert.assertEquals(HealthOneLogicImpl.patientLogic == patientLogic, false)
        HealthOneLogicImpl.setPatientLogic(patientLogic);
        Assert.assertEquals(HealthOneLogicImpl.patientLogic, patientLogic)
    }

    @Test
    fun setDocumentLogic() {
        Assert.assertEquals(HealthOneLogicImpl.documentLogic == documentLogic, false)
        HealthOneLogicImpl.setDocumentLogic(documentLogic);
        Assert.assertEquals(HealthOneLogicImpl.documentLogic, documentLogic)
    }

    @Test
    fun readDate(){

        // Format ddmmyyyy
        val date1 = "01021950"
        val res1 = HealthOneLogicImpl.readDate(date1);
        val a = res1.toString();
        Assert.assertTrue((res1.toString()).equals("1950-02-01 00:00:00.0"))

        // Format ddmmyyyy
        val date2 = "010250"
        val res2 = HealthOneLogicImpl.readDate(date2);
        Assert.assertTrue((res2.toString()).equals("1950-02-01 00:00:00.0"))

        // Format ddmmyyyy
        val date3 = "01/02/1950"
        val res3 = HealthOneLogicImpl.readDate(date3);
        Assert.assertTrue((res3.toString()).equals("1950-02-01 00:00:00.0"))

        // Unaccepted format
        val date4 = "000"
        val res4 = HealthOneLogicImpl.readDate(date4);
        Assert.assertEquals(res4,null)

    }
}


