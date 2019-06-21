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
import java.sql.Timestamp
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
    fun tryToGetValueAsNumber() {
        // Integer
        val values1 = "1"
        val res1 = HealthOneLogicImpl.tryToGetValueAsNumber(values1)
        Assert.assertTrue(res1==1.0)

        // Zero
        val values2 = "0"
        val res2 = HealthOneLogicImpl.tryToGetValueAsNumber(values2)
        Assert.assertTrue(res2==0.0)

        // Negative integer
        val values3 = "-1"
        val res3 = HealthOneLogicImpl.tryToGetValueAsNumber(values3)
        Assert.assertTrue(res3==-1.0)

        // Double with comma
        val values4 = "1,0"
        val res4 = HealthOneLogicImpl.tryToGetValueAsNumber(values4)
        Assert.assertTrue(res4==1.0)

        // Double with point
        val values5 = "1.0"
        val res5 = HealthOneLogicImpl.tryToGetValueAsNumber(values5)
        Assert.assertTrue(res5==1.0)

        // Negative double with point
        val values6 = "-1.0"
        val res6 = HealthOneLogicImpl.tryToGetValueAsNumber(values6)
        Assert.assertTrue(res6==-1.0)

        // Negative double with point
        val values7 = "-1.0000000000000000000000"
        val res7 = HealthOneLogicImpl.tryToGetValueAsNumber(values7)
        Assert.assertTrue(res7==-1.0)

        // Not a double
        val values8 = "a"
        val res8 = HealthOneLogicImpl.tryToGetValueAsNumber(values8)
        Assert.assertEquals(res8,null)
    }

    @Test
    fun tryToGetReferenceValues() {
        // betweenReference
        val refValues1 = "1-2"
        val res1 = HealthOneLogicImpl.tryToGetReferenceValues(refValues1)
        Assert.assertTrue(res1.minValue==1.0)
        Assert.assertTrue(res1.maxValue==2.0)

        // same with group 3
        val refValues2 = "1-2     mg"
        val res2 = HealthOneLogicImpl.tryToGetReferenceValues(refValues2)
        Assert.assertTrue(res2.minValue==1.0)
        Assert.assertTrue(res2.maxValue==2.0)
        Assert.assertEquals(res2.unit,"mg")

        // lessThanReference
        val refValues3 = "<2"
        val res3 = HealthOneLogicImpl.tryToGetReferenceValues(refValues3)
        Assert.assertTrue(res3.maxValue==2.0)

        // lessThanReference with group 3
        val refValues4 = "<2   L"
        val res4 = HealthOneLogicImpl.tryToGetReferenceValues(refValues4)
        Assert.assertTrue(res4.maxValue==2.0)
        Assert.assertEquals(res4.unit,null) // Must be modificated when the code will be correct

        // greaterThanReference
        val refValues5 = ">2"
        val res5 = HealthOneLogicImpl.tryToGetReferenceValues(refValues5)
        Assert.assertTrue(res5.minValue==2.0)

        // greaterThanReference with group 3
        val refValues6 = ">2   e10mg"
        val res6 = HealthOneLogicImpl.tryToGetReferenceValues(refValues6)
        Assert.assertTrue(res6.minValue==2.0)
        Assert.assertEquals(res6.unit,null) // Must be modificated when the code will be correct

        // refValues not matches
        val refValues7 = "([-,-:-, a"
        val res7 = HealthOneLogicImpl.tryToGetReferenceValues(refValues7)
        Assert.assertEquals(res7,null)

    }

    @Test
    fun getInfos() {
        // First parameter
        val doc1 = Document();
        val mappings1 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339.lab")
        val bytes1 = mappings1.readBytes();
        doc1.setAttachment(bytes1);

        // Second parameter
        val full = true

        // Third parameter
        val language = "UTF8"

        // Fourth parameter
        val enckeys = null

        // Execution
        val res1 = HealthOneLogicImpl.getInfos(doc1, full, language, enckeys)
        val mappings2 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339.lab")
        val bufferedreader2 = mappings2.bufferedReader(Charset.forName("cp1252"));
        val res2 = HealthOneLogicImpl.extractResultInfos(bufferedreader2,language,null,full)

        Assert.assertTrue(res1.size == res2.size)
        for((index,ResultInfo) in res1.withIndex()){
            Assert.assertTrue(ResultInfo.ssin == res2[index].ssin)
            Assert.assertTrue(ResultInfo.lastName == res2[index].lastName)
            Assert.assertTrue(ResultInfo.firstName == res2[index].firstName)
            Assert.assertTrue(ResultInfo.dateOfBirth == res2[index].dateOfBirth)
            Assert.assertTrue(ResultInfo.sex == res2[index].sex)
            Assert.assertTrue(ResultInfo.documentId == res2[index].documentId)
            Assert.assertTrue(ResultInfo.protocol == res2[index].protocol)
            Assert.assertTrue(ResultInfo.complete == res2[index].complete)
            Assert.assertTrue(ResultInfo.demandDate == res2[index].demandDate)
            Assert.assertTrue(ResultInfo.labo == res2[index].labo)
            Assert.assertTrue(ResultInfo.engine == res2[index].engine)
            for((codeIndex,code) in ResultInfo.codes.withIndex()){
                Assert.assertEquals(code,res2[index].codes[codeIndex])
            }
            for((serviceIndex,service) in ResultInfo.services.withIndex()){
                val a = service.index
                val b = res2[index].services[serviceIndex].index
                Assert.assertEquals(service.index,res2[index].services[serviceIndex].index)
            }
        }

    }

    @Test
    fun extractResultInfos() {
        // First parameter
        /// File 1
        val mappings1 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339.lab")
        val bufferedreader1 = mappings1.bufferedReader(Charset.forName("cp1252"));
        /// File 2
        val mappings2 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/1100116471515_a1_LOL0327b54.LAB.txt")
        val bufferedreader2 = mappings2.bufferedReader(Charset.forName("cp850"));
        /// File 3 (report -L5)
        val mappings3 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/st-jean-gaspar_MS-506")
        val bufferedreader3 = mappings3.bufferedReader(Charset.forName("cp850"));
        // File 4 (only one line protocol)
        val mappings4 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/1100116471515_a1_LOL0327b54_2.LAB")
        val bufferedreader4 = mappings4.bufferedReader(Charset.forName("cp850"));
        // File 5
        val mappings5 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-84_2.LAB")
        val bufferedreader5 = mappings5.bufferedReader(Charset.forName("cp850"));
        // File 6 - MS 642 (Partial result and missplaced NISS)
        val mappings6 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/15692224004003_MS-642.LAB")
        val bufferedreader6 = mappings6.bufferedReader(Charset.forName("cp1252"));
        // File 7 - MS 635 (2 services)
        val mappings7 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/H1-190507162031-0001_MS-635.DAT")
        val bufferedreader7 = mappings7.bufferedReader(Charset.forName("cp850"));
        // File 8 - MS 339 (exception generated)
        val mappings8 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339_3.txt")
        val bufferedreader8 = mappings8.bufferedReader(Charset.forName("cp850"));

        // Second parameter
        val language = "UTF-8"

        // Third parameter
        val docID = "docID"

        // Fourth parameter
        val full = true

        // Executions
        val res1 = HealthOneLogicImpl.extractResultInfos(bufferedreader1,language,docID,full)
        val res2 = HealthOneLogicImpl.extractResultInfos(bufferedreader2,language,docID,full)
        val res3 = HealthOneLogicImpl.extractResultInfos(bufferedreader3,language,docID,full)
        val res4 = HealthOneLogicImpl.extractResultInfos(bufferedreader4,language,docID,full)
        val res5 = HealthOneLogicImpl.extractResultInfos(bufferedreader5,language,docID,full)
        val res6 = HealthOneLogicImpl.extractResultInfos(bufferedreader6,language,docID,full)
        val res7 = HealthOneLogicImpl.extractResultInfos(bufferedreader7,language,docID,full)
        val res8 = HealthOneLogicImpl.extractResultInfos(bufferedreader8,language,docID,full)

        // General Test
        /// File 1
        Assert.assertEquals(res1[0].documentId, docID);
        Assert.assertEquals(res1[0].protocol, "1903-19339");
        /// File 2
        Assert.assertEquals(res2[0].documentId, docID);
        Assert.assertEquals(res2[0].protocol, "9S231326");
        /// File 3
        Assert.assertEquals(res3[0].documentId, docID);
        Assert.assertEquals(res3[0].protocol, "18652739");

        // Test A1
        Assert.assertEquals(res1[0].labo, "CHR HAUTE SENNE"); //File 1 line 1
        Assert.assertEquals(res2[0].labo, "Labo Luc Olivier - Villers"); //File 2 line 1 - Format Labo Luc Olivier - Villers\Add1\Add2\Add3\ Ok Only the first parse is take to put the labo name

        // Test A2
        /// File 1 line 2
        Assert.assertEquals(res1[0].lastName, "NOM");
        Assert.assertEquals(res1[0].firstName, "PRENOM");
        Assert.assertEquals(res1[0].sex, "F");
        Assert.assertEquals(res1[0].dateOfBirth, 19500101L); // Case of 01011950 writing
        /// File 2 line 2 - Format NOM\PRENOM\F\010150\
        Assert.assertEquals(res2[0].lastName, "NOM");
        Assert.assertEquals(res2[0].firstName, "PRENOM");
        Assert.assertEquals(res2[0].sex, "F");
        /* Before code is modify
        Assert.assertEquals(res2[0].dateOfBirth, null); // Case of 010150 writing isn't detected// Date must have 8 digit and the format is ordered by shortDateFormat which is the ddMMyyyy format
       */
        Assert.assertEquals(res2[0].dateOfBirth, 19500101L); // Case of 010150 writing
        /// File 3 line 2 - Format NOM\PRENOM\Add1\F\01011950\Add1\Add2\ imply lastName and firstName OK but sex becomes "Add0" and dateOfBirth is null caused by "F" not convenient format for date
        Assert.assertEquals(res3[0].lastName, "NOM");
        Assert.assertEquals(res3[0].firstName, "PRENOM");
        Assert.assertEquals(res3[0].sex, "Add1");
        Assert.assertEquals(res3[0].dateOfBirth, null);
        /// File 5
        Assert.assertEquals(res5[0].dateOfBirth, 19500101L); // Case of 01/01/1950 writing

        // Test A3 (line 3) None of that informations is in the class ResultInfo

        // Test A4 Doctor's name isn't use in the class ResultInfo
        /// File 1 line 4 - Format Docteur Bidon\19032019\\C\
        Assert.assertEquals(res1[0].demandDate, 1552950000000);
        Assert.assertEquals(res1[0].complete, true);
        /// File 2 line 4  - Format Docteur Bidon\27032018\Add1\C\
        Assert.assertEquals(res2[0].demandDate, 1522101600000);
        Assert.assertEquals(res2[0].complete, true);
        /// File 6 line 4  - Format Docteur Bidon\26112018\1821\P\
        Assert.assertEquals(res6[0].demandDate, 1557266400000);
        Assert.assertEquals(res6[0].complete, false);

        // Test A5
        Assert.assertEquals(res1[0].ssin, null); // File 1 line 5
        Assert.assertEquals(res2[0].ssin, null); // File 2 line 5 - For SSIN it's always the fourth part caught
        Assert.assertEquals(res6[0].ssin, "50010100156"); // File 6 line 5 - For SSIN it's always the fourth part caught

        // Test lines L1 (services)
        /// File 1
        Assert.assertEquals(res1[0].services.size, 32);
        Assert.assertEquals(res1[0].codes[0].type, "CD-TRANSACTION");
        Assert.assertEquals(res1[0].codes[0].code, "labresult");
        Assert.assertEquals(res1[0].codes[0].version, "1");
        /// File 2
        Assert.assertEquals(res2[0].services.size, 4);
        // Test simple L1
        /// File 1 line 21 //L1\1903-19339\UREE\Urée\10-50\mg/dL\*\41\
        Assert.assertEquals(res1[0].services[13].label, "Urée");
        Assert.assertEquals(res1[0].services[13].content.get("UTF-8")?.measureValue?.min, 10.0);
        Assert.assertEquals(res1[0].services[13].content.get("UTF-8")?.measureValue?.max, 50.0);
        Assert.assertEquals(res1[0].services[13].content.get("UTF-8")?.measureValue?.unit, "mg/dL");
        Assert.assertEquals(res1[0].services[13].content.get("UTF-8")?.measureValue?.value, 41.0);
        /// File 6 line ?? //L1\1903-19339\Activité estérasiqu\Urée\<25\U/µL\*\500\
        Assert.assertEquals(res6[0].services[6].label, "Activité estérasique");
        Assert.assertEquals(res6[0].services[6].content.get("UTF-8")?.measureValue?.max, 25.0);
        Assert.assertEquals(res6[0].services[6].content.get("UTF-8")?.measureValue?.unit, "U/µL");
        Assert.assertEquals(res6[0].services[6].content.get("UTF-8")?.measureValue?.value, 500.0);
        // Test L1 complex
        /// File 1 line 6-7
        ///L1\1903-19339\EX_H\Index d'hémolyse\0-15\mg/dL\\1\
        ///L1\1903-19339\EX_H\Index d'hémolyse\0-15\mg/dL\\\
        Assert.assertEquals(res1[0].services[1].label, "Index d'hémolyse")
        Assert.assertEquals(res1[0].services[1].content.get("UTF-8")?.measureValue?.min, 0.0);
        Assert.assertEquals(res1[0].services[1].content.get("UTF-8")?.measureValue?.max, 15.0);
        Assert.assertEquals(res1[0].services[1].content.get("UTF-8")?.measureValue?.unit, "mg/dL");
        Assert.assertEquals(res1[0].services[1].content.get("UTF-8")?.measureValue?.value, 1.0);
        Assert.assertEquals(res1[0].services[1].content.get("UTF-8")?.measureValue?.comment, "");
        /// File 2 line 7-11
        ///L1\9S231326\INR\INR\2-3\\*\1,8\
        ///L1\9S231326\INR\\< 1,5     : Pas d'anticoagulation effective\\*\\
        ///L1\9S231326\INR\\2,0 - 3,0 : Indications g‚n‚rales\\*\\
        ///L1\9S231326\INR\\2,5 - 3,5 : Indications particuliŠres\\*\\
        ///L1\9S231326\INR\\> 5,0     : Surdosage AVK\\*\\
        Assert.assertEquals(res2[0].services[1].label, "INR")
        Assert.assertEquals(res2[0].services[1].content.get("UTF-8")?.measureValue?.min, 2.0);
        Assert.assertEquals(res2[0].services[1].content.get("UTF-8")?.measureValue?.max, 3.0);
        Assert.assertEquals(res2[0].services[1].content.get("UTF-8")?.measureValue?.value, 1.8);
        Assert.assertEquals(res2[0].services[1].content.get("UTF-8")?.measureValue?.comment, "< 1,5     : Pas d'anticoagulation effective\n2,0 - 3,0 : Indications générales\n2,5 - 3,5 : Indications particulières\n> 5,0     : Surdosage AVK");
        /// File 2 line 7-11
        ///L1\9S231326\QUICK\Temps de Quick\70-100\%\*\36\
        ///L1\9S231326\QUICK\\13 - 30 : sous AVK\\*\\
        Assert.assertEquals(res2[0].services[2].label, "Temps de Quick")
        Assert.assertEquals(res2[0].services[2].content.get("UTF-8")?.measureValue?.min, 70.0);
        Assert.assertEquals(res2[0].services[2].content.get("UTF-8")?.measureValue?.max, 100.0);
        Assert.assertEquals(res2[0].services[2].content.get("UTF-8")?.measureValue?.value, 36.0);
        Assert.assertEquals(res2[0].services[2].content.get("UTF-8")?.measureValue?.unit, "%");
        Assert.assertEquals(res2[0].services[2].content.get("UTF-8")?.measureValue?.comment, "13 - 30 : sous AVK");
        /// File 4
        Assert.assertEquals(res4[0].services.size, 1);
        Assert.assertEquals(res4[0].services[0].label, "Temps de Quick")
        Assert.assertEquals(res4[0].services[0].content.get("UTF-8")?.measureValue?.min, 70.0);
        Assert.assertEquals(res4[0].services[0].content.get("UTF-8")?.measureValue?.max, 100.0);
        Assert.assertEquals(res4[0].services[0].content.get("UTF-8")?.measureValue?.value, 36.0);
        Assert.assertEquals(res4[0].services[0].content.get("UTF-8")?.measureValue?.unit, "%");
        Assert.assertEquals(res4[0].services[0].content.get("UTF-8")?.measureValue?.comment, "13 - 30 : sous AVK");
        /// File 7
        Assert.assertEquals(res7.size, 2);
        // Test lines L5 (services)
        /// File 3
        Assert.assertEquals(res3[0].services.size, 1);
        Assert.assertEquals(res3[0].codes[0].type, "CD-TRANSACTION");
        Assert.assertEquals(res3[0].codes[0].code, "report");
        Assert.assertEquals(res3[0].codes[0].version, "1");
        Assert.assertEquals(res3[0].services[0].content.get("UTF-8")?.stringValue, "Clinique Saint Jean -\nBruxelles,\nle 09/04/2019\n" +
                "Réf.á: 7207797\nCher Confrère, chère Cons£ur,\nNous avons vu en consultation le 09/04/2019  Monsieur NOM PRENOM né le\n" +
                "01/01/1950.\nAnamnèseá:");

        // Test lines S1 (ExtraPatientLine)
        Assert.assertEquals(res3[0].services.size, 1);
/*
        try {
            val res8 = HealthOneLogicImpl.getInfos(doc8, full, language, enckeys)  // File 8
            //println("Erreur non vue")
            //calculator.squareRoot(-10);
            //fail(&quot;Should throw exception when calculating square root of a negative number&quot;);
        } catch (e: ParseException) {
            //println("erreur vue")
            //Assert(e.getMessage().contains());
        }*/
    }

    @Test
    fun isPatientLine() {
        // Empty line
        val line1 = ""
        val res1 = HealthOneLogicImpl.isPatientLine(line1)
        Assert.assertFalse(res1)

        // A2 line
        val line2 = "A2\\text"
        val res2 = HealthOneLogicImpl.isPatientLine(line2)
        Assert.assertTrue(res2)

        // S2.* line
        val line3 = "12 S2.*\\text"
        val res3 = HealthOneLogicImpl.isPatientLine(line3)
        Assert.assertTrue(res3)

        // Other beginning line
        val line4 = "25\\text"
        val res4 = HealthOneLogicImpl.isPatientLine(line4)
        Assert.assertFalse(res4)
    }

    @Test
    fun isExtraPatientLine() {
        // Empty line
        val line1 = ""
        val res1 = HealthOneLogicImpl.isExtraPatientLine(line1)
        Assert.assertFalse(res1)

        // S4.* line
        val line2 = "12 S4.*\\text"
        val res2 = HealthOneLogicImpl.isExtraPatientLine(line2)
        Assert.assertTrue(res2)

        // Other beginning line
        val line3 = "25\\text"
        val res3 = HealthOneLogicImpl.isExtraPatientLine(line3)
        Assert.assertFalse(res3)
    }

    @Test
    fun isPatientAddressLine() {
        // Empty line
        val line1 = ""
        val res1 = HealthOneLogicImpl.isPatientAddressLine(line1)
        Assert.assertFalse(res1)

        // A3 line
        val line2 = "A3\\text"
        val res2 = HealthOneLogicImpl.isPatientAddressLine(line2)
        Assert.assertTrue(res2)

        // S3.* line
        val line3 = "12 S3.*\\text"
        val res3 = HealthOneLogicImpl.isPatientAddressLine(line3)
        Assert.assertTrue(res3)

        // Other beginning line
        val line4 = "25\\text"
        val res4 = HealthOneLogicImpl.isPatientAddressLine(line4)
        Assert.assertFalse(res4)
    }

    @Test
    fun isResultsInfosLine() {
        // Empty line
        val line1 = ""
        val res1 = HealthOneLogicImpl.isResultsInfosLine(line1)
        Assert.assertFalse(res1)

        // A4 line
        val line2 = "A4\\text"
        val res2 = HealthOneLogicImpl.isResultsInfosLine(line2)
        Assert.assertTrue(res2)

        // S5.* line
        val line3 = "12 S5.*\\text"
        val res3 = HealthOneLogicImpl.isResultsInfosLine(line3)
        Assert.assertTrue(res3)

        // Other beginning line
        val line4 = "25\\text"
        val res4 = HealthOneLogicImpl.isResultsInfosLine(line4)
        Assert.assertFalse(res4)
    }

    @Test
    fun isPatientSSINLine() {
        // Empty line
        val line1 = ""
        val res1 = HealthOneLogicImpl.isPatientSSINLine(line1)
        Assert.assertFalse(res1)

        // A5 line
        val line2 = "A5\\text"
        val res2 = HealthOneLogicImpl.isPatientSSINLine(line2)
        Assert.assertTrue(res2)

        // Other beginning line
        val line3 = "25\\text"
        val res3 = HealthOneLogicImpl.isPatientSSINLine(line3)
        Assert.assertFalse(res3)
    }

    @Test
    fun isLaboLine() {
        // Empty line
        val line1 = ""
        val res1 = HealthOneLogicImpl.isLaboLine(line1)
        Assert.assertFalse(res1)

        // A1 line
        val line2 = "A1\\text"
        val res2 = HealthOneLogicImpl.isLaboLine(line2)
        Assert.assertTrue(res2)

        // S1.* line
        val line3 = "12 S1.*\\text"
        val res3 = HealthOneLogicImpl.isLaboLine(line3)
        Assert.assertTrue(res3)

        // Other beginning line
        val line4 = "25\\text"
        val res4 = HealthOneLogicImpl.isLaboLine(line4)
        Assert.assertFalse(res4)
    }

    @Test
    fun isLaboResultLine() {
        // Empty line
        val line1 = ""
        val res1 = HealthOneLogicImpl.isLaboResultLine(line1)
        Assert.assertFalse(res1)

        // L1 line
        val line2 = "L1\\text"
        val res2 = HealthOneLogicImpl.isLaboResultLine(line2)
        Assert.assertTrue(res2)

        // R1.* line
        val line3 = "12 R1.*\\text"
        val res3 = HealthOneLogicImpl.isLaboResultLine(line3)
        Assert.assertTrue(res3)

        // Other beginning line
        val line4 = "25\\text"
        val res4 = HealthOneLogicImpl.isLaboResultLine(line4)
        Assert.assertFalse(res4)
    }

    @Test
    fun isProtocolLine() {
        // Empty line
        val line1 = ""
        val res1 = HealthOneLogicImpl.isProtocolLine(line1)
        Assert.assertFalse(res1)

        // L5 line
        val line2 = "L5\\text"
        val res2 = HealthOneLogicImpl.isProtocolLine(line2)
        Assert.assertTrue(res2)

        // L2 line
        val line3 = "L2\\text"
        val res3 = HealthOneLogicImpl.isProtocolLine(line3)
        Assert.assertTrue(res3)

        // Other beginning line
        val line4 = "25\\text"
        val res4 = HealthOneLogicImpl.isProtocolLine(line4)
        Assert.assertFalse(res4)
    }

    @Test
    fun getLaboLine() {
        // Empty line
        val line1 = "A1"
        val res1 = HealthOneLogicImpl.getLaboLine(line1)
        Assert.assertEquals(res1.resultReference,null)
        Assert.assertEquals(res1.labo,null)
        Assert.assertEquals(res1.fullLine,line1)

        val line2 = "A1\\\\\\\\\\\\"
        val res2 = HealthOneLogicImpl.getLaboLine(line2)
        Assert.assertEquals(res2.resultReference,"")
        Assert.assertEquals(res2.labo,"")
        Assert.assertEquals(res2.fullLine,line2)

        // Complete Line with "V"
        val line3 = "A1\\protocol\\LaboName"
        val res3 = HealthOneLogicImpl.getLaboLine(line3)
        Assert.assertEquals(res3.resultReference,"protocol")
        Assert.assertEquals(res3.labo,"LaboName")
        Assert.assertEquals(res3.fullLine,line3)
    }

    @Test
    fun getPatientLine() {
        // Empty line
        val line1 = "A2"
        val res1 = HealthOneLogicImpl.getPatientLine(line1)
        Assert.assertEquals(res1.protocol,null)
        Assert.assertEquals(res1.firstName,null)
        Assert.assertEquals(res1.lastName,null)
        Assert.assertEquals(res1.sex,null)
        Assert.assertEquals(res1.dn,null)

        val line2 = "A2\\\\\\\\\\\\"
        val res2 = HealthOneLogicImpl.getPatientLine(line2)
        Assert.assertEquals(res2.protocol,"")
        Assert.assertEquals(res2.firstName,"")
        Assert.assertEquals(res2.lastName,"")
        Assert.assertEquals(res2.sex,"")
        Assert.assertEquals(res2.dn,null)

        // Complete Line with "V"
        val line3 = "A2\\protocol\\NOM\\PRENOM\\V\\01011950\\"
        val res3 = HealthOneLogicImpl.getPatientLine(line3)
        Assert.assertEquals(res3.protocol,"protocol")
        Assert.assertEquals(res3.firstName,"PRENOM")
        Assert.assertEquals(res3.lastName,"NOM")
        Assert.assertEquals(res3.sex,"F")
        Assert.assertEquals(res3.dn,Timestamp(HealthOneLogicImpl.readDate("01011950")))

        // Complete Line with "A" and unaccepted date
        val line4 = "A2\\protocol\\NOM\\PRENOM\\A\\010\\"
        val res4 = HealthOneLogicImpl.getPatientLine(line4)
        Assert.assertEquals(res4.sex,"A")
        Assert.assertEquals(res4.dn,null)
    }

    @Test
    fun getExtraPatientLine() {
        // Empty line
        val line1 = "S4.*"
        val res1 = HealthOneLogicImpl.getExtraPatientLine(line1)
        Assert.assertEquals(res1.protocol,null)
        Assert.assertEquals(res1.sex,null)
        Assert.assertEquals(res1.dn,null)

        val line2 = "S4.*\\\\\\\\\\"
        val res2 = HealthOneLogicImpl.getExtraPatientLine(line2)
        Assert.assertEquals(res2.protocol,"")
        Assert.assertEquals(res2.sex,"")
        Assert.assertEquals(res2.dn,null)

        // Complete Line with "V"
        val line3 = "S4.*\\protocol\\01011950\\V\\"
        val res3 = HealthOneLogicImpl.getExtraPatientLine(line3)
        Assert.assertEquals(res3.protocol,"protocol")
        Assert.assertEquals(res3.sex,"F")
        Assert.assertEquals(res3.dn,Timestamp(HealthOneLogicImpl.readDate("01011950")))

        // Complete Line with "A" and unaccepted date
        val line4 = "S4.*\\protocol\\010\\A\\"
        val res4 = HealthOneLogicImpl.getExtraPatientLine(line4)
        Assert.assertEquals(res4.sex,"A")
        Assert.assertEquals(res4.dn,null)
    }

    @Test
    fun getLaboResultLine(){
        // Empty line
        val line1 = "L1"
        val laboline = "A1\\protocol\\Labo\\"
        val ll = HealthOneLogicImpl.getLaboLine(laboline)
        val res1 = HealthOneLogicImpl.getLaboResultLine(line1, ll)
        Assert.assertEquals(res1,null)

        val line2 = "L1\\\\\\\\\\\\\\\\"
        val res2 = HealthOneLogicImpl.getLaboResultLine(line2,ll)
        Assert.assertEquals(res2.protocol,"")
        Assert.assertEquals(res2.analysisCode,"")
        Assert.assertEquals(res2.analysisType,"Note")
        Assert.assertEquals(res2.referenceValues,"")
        Assert.assertEquals(res2.unit,"")
        Assert.assertEquals(res2.severity,"")
        Assert.assertEquals(res2.value,"")

        // Complete line with R1.*
        val line4 = "R1.*\\protocol\\BLOOD\\Red corpuscule\\2\\4\\g\\6"
        val res4 = HealthOneLogicImpl.getLaboResultLine(line4,ll)
        Assert.assertEquals(res4.protocol,"protocol")
        Assert.assertEquals(res4.analysisCode,"BLOOD")
        Assert.assertEquals(res4.analysisType,"Red corpuscule")
        Assert.assertEquals(res4.referenceValues,"2 - 4")
        Assert.assertEquals(res4.unit,"g")
        Assert.assertEquals(res4.severity,"")
        Assert.assertEquals(res4.value,"6")

        // Complete line with L1
        val line3 = "L1\\protocol\\BLOOD\\Red corpuscule\\2-4\\g\\+\\6"
        val res3 = HealthOneLogicImpl.getLaboResultLine(line3,ll)
        Assert.assertEquals(res3.protocol,"protocol")
        Assert.assertEquals(res3.analysisCode,"BLOOD")
        Assert.assertEquals(res3.analysisType,"Red corpuscule")
        Assert.assertEquals(res3.referenceValues,"2-4")
        Assert.assertEquals(res3.unit,"g")
        Assert.assertEquals(res3.severity,"+")
        Assert.assertEquals(res3.value,"6")

        ll.labosList.add(res3)
        val line5 = "L1\\protocol\\BLOOD\\\\Text written by the lab"
        val res5 = HealthOneLogicImpl.getLaboResultLine(line5,ll)
        Assert.assertEquals(res5.protocol,"protocol")
        Assert.assertEquals(res5.analysisCode,"BLOOD")
        Assert.assertEquals(res5.analysisType,"Red corpuscule")
        Assert.assertEquals(res5.value,"Text written by the lab")
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
        Assert.assertEquals(res2.protocol,"")
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
