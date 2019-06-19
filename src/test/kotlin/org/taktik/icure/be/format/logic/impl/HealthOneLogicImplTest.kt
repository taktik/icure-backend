package org.taktik.icure.be.format.logic.impl;


import net.sf.saxon.functions.True
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
import java.time.Instant
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
    fun getProtocolLine(){
        // Empty line
        val line1 = "L5"
        val res1 = HealthOneLogicImpl.getProtocolLine(line1)
        Assert.assertEquals(res1.protocol,null)
        Assert.assertEquals(res1.code,null)
        Assert.assertEquals(res1.text,null)

        val line2 = "L5\\\\\\\\\\"
        val res2 = HealthOneLogicImpl.getProtocolLine(line2)
        Assert.assertEquals(res2.protocol,"")
        Assert.assertEquals(res2.code,"")
        Assert.assertEquals(res2.text,"")

        // Complete line with text in eight position
        val line3 = "L5\\protocol\\SANG\\\\\\\\\\Text written by the doctor"
        val res3 = HealthOneLogicImpl.getProtocolLine(line3)
        Assert.assertEquals(res3.protocol,"protocol")
        Assert.assertEquals(res3.code,"SANG")
        Assert.assertEquals(res3.text, "Text written by the doctor")

        // Complete line with text in fourth position
        val line4 = "L5\\protocol\\SANG\\Text written by the doctor\\"
        val res4 = HealthOneLogicImpl.getProtocolLine(line4)
        Assert.assertEquals(res4.text,"Text written by the doctor")
    }

    @Test
    fun getResultsInfosLine(){
        // Empty line
        val line1 = "A4"
        val res1 = HealthOneLogicImpl.getResultsInfosLine(line1)
        Assert.assertEquals(res1.protocol,null)
        Assert.assertEquals(res1.complete,true)
        Assert.assertEquals(res1.demandDate,null)

        val line2 = "A4\\\\\\\\\\"
        val res2 = HealthOneLogicImpl.getResultsInfosLine(line2)
        Assert.assertEquals(res2.protocol,"")
        Assert.assertEquals(res2.complete,false)
        Assert.assertNotEquals(res2.demandDate,null)

        // Complete line with C
        val line3 = "A4\\protocol\\Docteur Bidon\\19032019\\\\C\\"
        val res3 = HealthOneLogicImpl.getResultsInfosLine(line3)
        Assert.assertEquals(res3.protocol,"protocol")
        Assert.assertEquals(res3.complete,true)
        Assert.assertEquals(res3.demandDate, Instant.ofEpochMilli(HealthOneLogicImpl.readDate("19032019")))

        // Complete line with P
        val line4 = "A4\\protocol\\Docteur Bidon\\19032019\\\\P\\"
        val res4 = HealthOneLogicImpl.getResultsInfosLine(line4)
        Assert.assertEquals(res4.complete,false)
    }

    @Test
    fun getPatientSSINLine(){
        // Empty line
        val line1 = "A5"
        val res1 = HealthOneLogicImpl.getPatientSSINLine(line1)
        Assert.assertEquals(res1.protocol,null)
        Assert.assertEquals(res1.ssin,null)
        val line2 = "A5\\\\\\\\\\"
        val res2 = HealthOneLogicImpl.getPatientSSINLine(line2)
        Assert.assertEquals(res2.protocol,null)
        Assert.assertEquals(res2.ssin,null)

        // SSIN in fourth position
        val line3 = "A5\\protocol\\\\50010100156\\\\"
        val res3 = HealthOneLogicImpl.getPatientSSINLine(line3)
        Assert.assertEquals(res3.protocol,"protocol")
        Assert.assertEquals(res3.ssin,"50010100156")

        // SSIN in fifth position
        val line4 = "A5\\protocol\\\\\\50010100156\\"
        val res4 = HealthOneLogicImpl.getPatientSSINLine(line4)
        Assert.assertEquals(res4.protocol,"protocol")
        Assert.assertEquals(res4.ssin,"50010100156")
    }

    @Test
    fun getPatientAddressLine(){
        val line1 = "A3\\protocol\\ Rue factice 8\\1050\\Ixelles\\"
        val res1 = HealthOneLogicImpl.getPatientAddressLine(line1)
        Assert.assertEquals(res1.protocol,"protocol")
        Assert.assertEquals(res1.address,"Rue factice")
        Assert.assertEquals(res1.number,"8")
        Assert.assertEquals(res1.zipCode,"1050")
        Assert.assertEquals(res1.locality,"Ixelles")

        val line2 = "A3"
        val res2 = HealthOneLogicImpl.getPatientAddressLine(line2)
        Assert.assertEquals(res2.protocol,null)
        Assert.assertEquals(res2.address,null)
        Assert.assertEquals(res2.number,null)
        Assert.assertEquals(res2.zipCode,null)
        Assert.assertEquals(res2.locality,null)

        val line3 = "A3\\protocol\\ 8Rue factice\\a1050\\Ixelles\\"
        val res3 = HealthOneLogicImpl.getPatientAddressLine(line3)
        Assert.assertEquals(res3.address," 8Rue factice") //Be careful the space at the beginning is important
        Assert.assertEquals(res3.number,null)
        Assert.assertEquals(res3.zipCode,null)

        val line4 = "A3\\protocol\\    8    Rue factice  \\   1050      \\Ixelles\\"
        val res4 = HealthOneLogicImpl.getPatientAddressLine(line4)
        Assert.assertEquals(res4.address,"Rue factice")
        Assert.assertEquals(res4.number,"8")
        Assert.assertEquals(res4.zipCode,"1050")
    }

    @Test
    fun doExport(){
        // First parameter
        val sender = HealthcareParty();
        val nihii = "Num_Nihii"
        sender.nihii = nihii
        val lastNameDoct = "Name_Docteur_Bidon"
        sender.lastName = lastNameDoct
        val firstNameDoct = "FirstName_Docteur_Bidon"
        sender.firstName = firstNameDoct

        // Second parameter
        val recipient = HealthcareParty();

        // Third parameter
        val patient = Patient();
        val ssin = "50010100156";
        patient.ssin = ssin;
        val firstNamePat = "PRENOM";
        patient.firstName = firstNamePat;
        val lastNamePat = "NOM";
        patient.lastName = lastNamePat;
        val gender = "M";
        patient.gender = Gender.fromCode(gender);
        val dateOfBirth = 19500101;
        patient.dateOfBirth = dateOfBirth;
        val ad = Address();
        val city = "IXELLES";
        ad.city = city;
        val postalCode = "1050";
        ad.postalCode = postalCode;
        val street = "Rue factice";
        ad.street = street;
        val addressType = AddressType.home;
        ad.addressType = addressType;
        patient.addresses = listOf(ad);

        // Fourth parameter
        val date = LocalDateTime.of(2019,6,18,0,0)

        // Fifth parameter
        val ref = "RefTest-1";

        // Sixth parameter
        val text = "This a comment written to create L5 line \n And this is to create a second L5 line";

        // Seventh parameter
        val file = File("src/test/resources/org/taktik/icure/be/format/logic/impl/sortie.lab")
        val out = file.outputStream();

        // Execution
        /// File 1
        val res1 = HealthOneLogicImpl.doExport(sender, recipient, patient, date, ref, text, out);
        val mappings1 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/sortie.lab");
        val br = mappings1.bufferedReader(Charset.forName("cp1252"))
        val res1return = HealthOneLogicImpl.extractResultInfos(br, "UTF8", "docId", true);

        // Test
        /// File 1
        Assert.assertEquals(res1return[0].ssin, ssin);
        Assert.assertEquals(res1return[0].lastName, lastNamePat);
        Assert.assertEquals(res1return[0].firstName, firstNamePat);
        Assert.assertEquals(res1return[0].dateOfBirth, dateOfBirth.toLong());
        Assert.assertEquals(res1return[0].sex, gender);
        Assert.assertEquals(res1return[0].protocol, ref);
        //assert.assertEquals(res1return[0].complete, true);
        //Assert.assertEquals(res1return[0].demandDate, 18062019L )
        Assert.assertEquals(res1return[0].labo, nihii+" "+lastNameDoct+" "+firstNameDoct)
        Assert.assertEquals(res1return[0].services.get(0).content.get("UTF8")?.stringValue, text)
        //assertEquals(res1return[0].services.get(0).valueDate, 18062019L)
    }

    @Test
    fun splitLine() {
        val line1 = "This\\line\\is\\split\\word\\by\\word"
        val res1 = HealthOneLogicImpl.splitLine(line1)
        Assert.assertEquals(res1.size, 7)
        Assert.assertEquals(res1[0], "This")
        Assert.assertEquals(res1[1], "line")
        Assert.assertEquals(res1[2], "is")
        Assert.assertEquals(res1[3], "split")
        Assert.assertEquals(res1[4], "word")
        Assert.assertEquals(res1[5], "by")
        Assert.assertEquals(res1[6], "word")

        val line2 = "123456  A7..." //"^\s*[0-9][0-9][0-9][0-9](\d+)\s+([A-Z][0-9])(.*)$"
        val res2 = HealthOneLogicImpl.splitLine(line2)
        Assert.assertEquals(res2.size, 3)
        Assert.assertEquals(res2[0], "A7")
        Assert.assertEquals(res2[1], "56")
        Assert.assertEquals(res2[2], "...")

    }

    @Test
    fun canHandle() {
        // First parameter
        /// File 1 - OK
        val doc1 = Document();
        val mappings1 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339.lab");
        val bytes1 = mappings1.readBytes();
        doc1.setAttachment(bytes1);
        /// File 2 - WithOut A1 line
        val doc2 = Document();
        val mappings2 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339_2_WithoutA1.lab");
        val bytes2 = mappings2.readBytes();
        doc2.setAttachment(bytes2);

        // Second parameter
        val enckeys = null;

        // Execution
        val res1 = HealthOneLogicImpl.canHandle(doc1, enckeys);
        val res2 = HealthOneLogicImpl.canHandle(doc2, enckeys);

        // Test
        Assert.assertEquals(res1, true); //File 1
        Assert.assertEquals(res2, false); //File 2
    }

    @Test
    fun readDate(){

        // Format ddmmyyyy
        val date1 = "01021950"
        val res1 = HealthOneLogicImpl.readDate(date1);
        val a = res1.toString();
        Assert.assertTrue((res1.toString()).equals("-628477200000"))

        // Format ddmmyyyy
        val date2 = "010250"
        val res2 = HealthOneLogicImpl.readDate(date2);
        Assert.assertTrue((res2.toString()).equals("-628477200000"))

        // Format ddmmyyyy
        val date3 = "01/02/1950"
        val res3 = HealthOneLogicImpl.readDate(date3);
        Assert.assertTrue((res3.toString()).equals("-628477200000"))

        // Unaccepted format
        val date4 = "000"
        try {
            val res4 = HealthOneLogicImpl.readDate(date4);
        }
        catch (e: NumberFormatException) {
            Assert.assertTrue(e.message!!.contains("Unreadable date: \"" + date4 + "\"") )
        }

    }
}


