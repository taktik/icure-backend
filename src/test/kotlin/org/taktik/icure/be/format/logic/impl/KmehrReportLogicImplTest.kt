package org.taktik.icure.be.format.logic.impl;


import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.Patient
import org.taktik.icure.logic.ContactLogic
import org.taktik.icure.logic.DocumentLogic
import org.taktik.icure.logic.PatientLogic
import java.nio.charset.UnsupportedCharsetException


class KmehrReportLogicImplTest {
    //The method tested needs a HealthOneLogicImpl Class to run
    val kmehrReportLogic = KmehrReportLogicImpl();

    val contactLogic = mock(ContactLogic::class.java)
    val documentLogic = mock(DocumentLogic::class.java)
    val patientLogic = mock(PatientLogic::class.java)

    @Before
    fun setUp() {
        `when`(contactLogic.modifyContact(Matchers.any(Contact::class.java)))
                .thenAnswer { it.getArgumentAt(0, Contact::class.java) }

        `when`(documentLogic.modifyDocument(Matchers.any(Document::class.java)))
                .thenAnswer { it.getArgumentAt(0, Document::class.java) }

        `when`(documentLogic.createDocument(Matchers.any(Document::class.java), Matchers.any(String::class.java)))
                .thenAnswer { it.getArgumentAt(0, Document::class.java) }

        `when`(patientLogic.modifyPatient(Matchers.any(Patient::class.java)))
                .thenAnswer { it.getArgumentAt(0, Patient::class.java) }

        kmehrReportLogic.contactLogic = contactLogic;
        kmehrReportLogic.documentLogic = documentLogic;
    }

    @Test
    fun doImport() {
        val doc = Document();
        val labResource = this::class.java.getResource("kmehr_note_1554212544883.xml")
        labResource?.let { doc.attachment = it.readBytes() }

        try {
            if (kmehrReportLogic.canHandle(doc, null)) {
                val res = kmehrReportLogic.doImport("fr", doc, "hcpId", listOf("1"), listOf("111"), "planOfActionId", Contact().apply {
                    author = "1111"
                    responsible = "1111"
                }, null)
            }
        } catch (e: UnsupportedCharsetException) { }
    }

}
