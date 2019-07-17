package org.taktik.icure.be.format.logic.impl;


import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.AddressType
import org.taktik.icure.entities.embed.Gender
import org.taktik.icure.logic.ContactLogic
import org.taktik.icure.logic.DocumentLogic
import org.taktik.icure.logic.PatientLogic
import org.taktik.icure.utils.FuzzyValues
import java.io.File
import java.io.StringReader
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit


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
    fun setContactLogic() {
        Assert.assertEquals(HealthOneLogicImpl.contactLogic == contactLogic, false)
        HealthOneLogicImpl.setContactLogic(contactLogic);
        Assert.assertEquals(HealthOneLogicImpl.contactLogic, contactLogic)
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
    fun doImport() {
        // First parameter
        val language = "UTF-8";

        // Second parameter
        /// File 1
        val doc1 = Document();
        /// File 2
        val doc2 = Document();
        val mappings2 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/FichierVide.txt");
        val bytes2 = mappings2.readBytes();
        doc2.setAttachment(bytes2);

        // Third parameter
        val hcpId = "hcpId";

        // Fourth parameter
        val protocolIds1 = listOf("***");

        // Fifth parameter
        val formIds = listOf("111");

        // Sixth parameter
        val planOfActionId = "planOfActionId";

        // Seventh parameter
        val ctc = Contact();

        // Eighth parameter
        val enckeys = null;

        /// Doc hasn't a content
        try {
            val res1 = HealthOneLogicImpl.doImport(language, doc1, hcpId, protocolIds1, formIds, planOfActionId, ctc, enckeys);
            Assert.fail()
        } catch (e: UnsupportedCharsetException) {
        }
        // Modify Contact is impossible (in this case because ContactLogic isn't initialized
        HealthOneLogicImpl.setContactLogic(contactLogic);
        /*try {
            val res2 = HealthOneLogicImpl.doImport(language, doc2, hcpId, protocolIds1, formIds, planOfActionId, ctc, enckeys);
            Assert.fail()
        } catch (e: IllegalStateException) {
        }
        */
        // Expected result
        val res3 = HealthOneLogicImpl.doImport(language, doc2, hcpId, protocolIds1, formIds, planOfActionId, ctc, enckeys);
        Assert.assertNotNull(res3)
    }

    @Test
    fun parseReportsAndLabs() {
        // First parameter
        val language = "UTF-8";

        // Second parameter
        val protocolIds1 = listOf("***");
        val protocolIds2 = listOf("*");

        // Third parameter
        /// File 1
        val mappings1 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/FichierVide.txt");
        val content1 = HealthOneLogicImpl.decodeRawData(mappings1.readBytes());
        val r1 = StringReader(content1);
        /// File 2
        val mappings2 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339_2_WithoutA1.LAB");
        val content2 = HealthOneLogicImpl.decodeRawData(mappings2.readBytes());
        val r2 = StringReader(content2);
        /// File 3
        val mappings3 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339_4.LAB");
        val content3 = HealthOneLogicImpl.decodeRawData(mappings3.readBytes());
        val r3 = StringReader(content3);
        /// File 4
        val mappings4 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339_5.LAB");
        val content4 = HealthOneLogicImpl.decodeRawData(mappings4.readBytes());
        val r4 = StringReader(content4);
        /// File 5
        val mappings5 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339_6-A1LineOnly.LAB");
        val content5 = HealthOneLogicImpl.decodeRawData(mappings5.readBytes());
        val r5 = StringReader(content5)
        /// File 6
        val mappings6 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339_7_BadL1Line.txt");
        val content6 = HealthOneLogicImpl.decodeRawData(mappings6.readBytes());
        val r6 = StringReader(content6);
        /// File 7
        val mappings7 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339_8.txt");
        val content7 = HealthOneLogicImpl.decodeRawData(mappings7.readBytes());
        val r7 = StringReader(content7);
        /// File 8
        val mappings8 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339_9.txt");
        val content8 = HealthOneLogicImpl.decodeRawData(mappings8.readBytes());
        val r8 = StringReader(content8);
        /// File 9
        val mappings9 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/st-jean-gaspar_MS-506");
        val content9 = HealthOneLogicImpl.decodeRawData(mappings9.readBytes());
        val r9 = StringReader(content9);
        /// File 10
        val mappings10 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/st-jean-gaspar_MS-506_2");
        val content10 = HealthOneLogicImpl.decodeRawData(mappings10.readBytes());
        val r10 = StringReader(content10);

        // Execution
        val res1 = HealthOneLogicImpl.parseReportsAndLabs(language, protocolIds1, r1); // File 1
        val res2 = HealthOneLogicImpl.parseReportsAndLabs(language, protocolIds1, r2); // File 2
        val res3 = HealthOneLogicImpl.parseReportsAndLabs(language, protocolIds1, r3); // File 3
        val res4 = HealthOneLogicImpl.parseReportsAndLabs(language, protocolIds1, r4); // File 4
        val res5 = HealthOneLogicImpl.parseReportsAndLabs(language, protocolIds2, r5); // File 5
        val res6 = HealthOneLogicImpl.parseReportsAndLabs(language, protocolIds1, r6); // File 6
        val res7 = HealthOneLogicImpl.parseReportsAndLabs(language, protocolIds1, r7); // File 7
        val res8 = HealthOneLogicImpl.parseReportsAndLabs(language, protocolIds1, r8); // File 8
        val res9 = HealthOneLogicImpl.parseReportsAndLabs(language, protocolIds1, r9); // File 9
        val res10 = HealthOneLogicImpl.parseReportsAndLabs(language, protocolIds1, r10); // File 10

        //Tests
        /// Empty file
        Assert.assertEquals(res1.size, 0)

        /// File where the first line isn't LaboLine
        Assert.assertEquals(res2.size, 0)

        /// File where A2 and A3 Lines is before A1
        Assert.assertEquals(res3.size, 1)
        Assert.assertNull(res3[0].pal)
        Assert.assertNotNull(res3[0].ril)

        /// File contains two A1 Lines
        Assert.assertEquals(res4.size, 2)
        Assert.assertNull(res4[0].pal)
        Assert.assertNull(res4[0].ril)

        /// File contains one A1 Line and parseReportsAndLabs is called with protocolIds2
        Assert.assertEquals(res5.size, 0)

        /// File contains one A1,A2,A3,A4 Line and one invalid L1Line !!!!! HOW? TODO
        /*Assert.assertEquals(res6.size, 1)
        Assert.assertEquals(res6[0].services.size, 0)
        Assert.assertNotNull(res6[0].pal)
        Assert.assertNotNull(res6[0].ril)*/


        /// File contains one A1,A2,A3,A4 Line and one L1Line
        Assert.assertEquals(res7.size, 1)
        Assert.assertEquals(res7[0].services.size, 1)

        /// File contains one A1,A2,A3,A4 Line and two L1Line with different analysisCode
        Assert.assertEquals(res8.size, 1)
        Assert.assertEquals(res8[0].services.size, 2)

        /// File contains one A1,A2,A3,A4 Line and one invalid L5Line !!!!!!! HOW? TODO
        /*Assert.assertEquals(res6.size,1)
        Assert.assertEquals(res6[0].services.size,0)*/

        /// File contains one A1,A2,A3,A4 Line and less than 20 L5Line
        Assert.assertEquals(res9.size, 1)
        Assert.assertEquals(res9[0].services.size, 1)

        /// File contains one A1,A2,A3,A4 Line and more than 20 L5Line
        Assert.assertEquals(res10.size, 1)
        Assert.assertEquals(res10[0].services.size, 2)
    }

    @Test
    fun createServices() {
        // First parameter
        val laboLine = "A1\\protocol\\Labo\\"
        val ll1 = HealthOneLogicImpl.getLaboLine(laboLine)
        val ll2 = HealthOneLogicImpl.getLaboLine(laboLine)
        val resultsInfosLine = "A4\\protocol\\Docteur Bidon\\19032019\\\\C\\"
        val ril = HealthOneLogicImpl.getResultsInfosLine(resultsInfosLine)
        ll1.setRil(ril)
        ll2.setRil(ril)
        val laboResultLine1 = "L1\\protocol\\BLOOD\\Red corpuscule\\2-4\\g\\+\\6.0"
        val lrl1 = HealthOneLogicImpl.getLaboResultLine(laboResultLine1, ll1)
        val laboResultLine2 = "L1\\protocol\\UREA\\Urea\\1-2\\mL\\+\\0.5"
        val lrl2 = HealthOneLogicImpl.getLaboResultLine(laboResultLine2, ll1)
        val protoLine1 = "L2\\protocol\\BLOOD\\Dear colleague,"
        val proto1 = HealthOneLogicImpl.getProtocolLine(protoLine1)
        // Second parameter
        val language = "language"
        // Third parameter
        val position = 1L

        // Test with empty labolist and empty protoList
        HealthOneLogicImpl.createServices(ll1, language, position)
        Assert.assertEquals(ll1.services.size, 0)

        // Test with labolist and empty protoList
        ll1.labosList.add(lrl1)
        HealthOneLogicImpl.createServices(ll1, language, position)
        Assert.assertEquals(ll1.services.size, 1)

        // Test with empty labolist and protoList
        ll2.protoList.add(proto1)
        HealthOneLogicImpl.createServices(ll2, language, position)
        Assert.assertEquals(ll2.services.size, 1)

        // Test with labolist and  protoList
        ll1.services.clear()
        ll1.labosList.add(lrl1)
        ll1.protoList.add(proto1)
        HealthOneLogicImpl.createServices(ll1, language, position)
        Assert.assertEquals(ll1.services.size, 2)

    }

    @Test
    fun importProtocol() {
        // First parameter
        val language = "language"
        // Second parameter
        val protoLine1 = "L2\\protocol\\BLOOD\\Dear colleague,"
        val protoLine2 = "L2\\protocol\\BLOOD\\I'm doing a test"
        val proto1 = HealthOneLogicImpl.getProtocolLine(protoLine1)
        val proto2 = HealthOneLogicImpl.getProtocolLine(protoLine2)
        val protoList = listOf(proto1, proto2)
        // Third parameter
        val position = 1L
        // Fourth parameter
        val resultsInfosLine = "A4\\protocol\\Docteur Bidon\\19032019\\\\C\\"
        val ril = HealthOneLogicImpl.getResultsInfosLine(resultsInfosLine)

        // Execution
        val res1 = HealthOneLogicImpl.importProtocol(language, protoList, position, ril)

        // Tests
        Assert.assertNotNull(res1.id)
        Assert.assertEquals(res1.content?.get(language)?.stringValue, "Dear colleague,\nI'm doing a test")
        Assert.assertEquals(res1.label, "Protocol")
        Assert.assertEquals(res1.index, position)
        Assert.assertEquals(res1.valueDate, FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(ril.demandDate, ZoneId.systemDefault()), ChronoUnit.DAYS))

    }

    @Test
    fun importLaboResult() {
        // First parameter
        val language = "language"
        // Second parameter
        val laboLine = "A1\\protocol\\Labo\\"
        val ll = HealthOneLogicImpl.getLaboLine(laboLine)
        val laboResultLine1 = "L1\\protocol\\BLOOD\\Red corpuscule\\\\\\\\"
        val lrl1 = HealthOneLogicImpl.getLaboResultLine(laboResultLine1, ll)
        val d = 6.0
        val laboResultLine2 = "L1\\protocol\\BLOOD\\Red corpuscule\\2-4\\g\\+\\" + d
        val lrl2 = HealthOneLogicImpl.getLaboResultLine(laboResultLine2, ll)
        val labResults1 = listOf(lrl2)
        val labResults2 = listOf(lrl1, lrl2)
        val labResults3 = listOf(lrl2, lrl1, lrl1)
        val labResults4 = listOf(lrl2, lrl1, lrl2)
        // Third parameter
        val position = 1L
        // Fourth parameter
        val resultsInfosLine = "A4\\protocol\\Docteur Bidon\\19032019\\\\C\\"
        val ril = HealthOneLogicImpl.getResultsInfosLine(resultsInfosLine)

        // Execution
        val res1 = HealthOneLogicImpl.importLaboResult(language, labResults1, position, ril)
        val res2 = HealthOneLogicImpl.importLaboResult(language, labResults2, position, ril)
        val res3 = HealthOneLogicImpl.importLaboResult(language, labResults3, position, ril)
        val res4 = HealthOneLogicImpl.importLaboResult(language, labResults4, position, ril)

        // Tests
        /// If there is only one LaboResultLine
        Assert.assertEquals(res1.size, 1)
        /// If there is more than 1 and the return of the first lrl'value by tryToGetValueAsNumber is null
        Assert.assertNotNull(res2.lastOrNull()?.id)
        Assert.assertEquals(res2.lastOrNull()?.content?.get(language)?.stringValue, "\n" + d)
        Assert.assertEquals(res2.lastOrNull()?.label, lrl1.analysisType)
        Assert.assertEquals(res2.lastOrNull()?.index, position)
        Assert.assertEquals(res2.lastOrNull()?.valueDate, FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(ril.demandDate, ZoneId.systemDefault()), ChronoUnit.DAYS))
        /// If there is more than 1 and the return of the first lrl'value by tryToGetValueAsNumber isn't null
        Assert.assertEquals(res3.lastOrNull()?.content?.get(language)?.measureValue?.comment, "")
        /// If there is more than 1 and the return of the first lrl'value by tryToGetValueAsNumber isn't null +
        Assert.assertEquals(res4.lastOrNull()?.content?.get(language)?.measureValue?.comment, "\n" + d)
    }

    @Test
    fun addLaboResult() {
        // First parameter
        val laboLine = "A1\\protocol\\Labo\\"
        val ll = HealthOneLogicImpl.getLaboLine(laboLine)
        val laboResultLine1 = "L1\\protocol\\BLOOD\\Red corpuscule\\\\\\\\"
        val lrl1 = HealthOneLogicImpl.getLaboResultLine(laboResultLine1, ll)
        val d = 6.0
        val laboResultLine2 = "L1\\protocol\\BLOOD\\Red corpuscule\\2-4\\g\\+\\" + d
        val lrl2 = HealthOneLogicImpl.getLaboResultLine(laboResultLine2, ll)

        // Second parameter
        val language = "language"
        // Third parameter
        val position = 1L
        // Fourth parameter
        val resultsInfosLine = "A4\\protocol\\Docteur Bidon\\19032019\\\\C\\"
        val ril = HealthOneLogicImpl.getResultsInfosLine(resultsInfosLine)
        // Fifth parameter
        val comment = "comment"

        // Execution
        val res1 = HealthOneLogicImpl.addLaboResult(lrl1, language, position, ril, comment)
        val res2 = HealthOneLogicImpl.addLaboResult(lrl2, language, position, ril, comment)

        // Tests
        ///
        Assert.assertNotNull(res1.lastOrNull()?.content?.get(language)?.stringValue)
        ///
        Assert.assertNull(res2.lastOrNull()?.content?.get(language)?.stringValue)
    }

    @Test
    fun importPlainStringLaboResult() {
        // First parameter
        val language = "language"
        // Second parameter
        val laboLine = "A1\\protocol\\Labo\\"
        val ll = HealthOneLogicImpl.getLaboLine(laboLine)
        val laboResultLine1 = "L1\\protocol\\BLOOD\\Red corpuscule\\\\\\\\"
        val lrl1 = HealthOneLogicImpl.getLaboResultLine(laboResultLine1, ll)
        val laboResultLine2 = "L1\\protocol\\BLOOD\\Red corpuscule\\2-4\\g\\+\\6"
        val lrl2 = HealthOneLogicImpl.getLaboResultLine(laboResultLine2, ll)
        // Third parameter
        val position = 1L
        // Fourth parameter
        val resultsInfosLine = "A4\\protocol\\Docteur Bidon\\19032019\\\\C\\"
        val ril = HealthOneLogicImpl.getResultsInfosLine(resultsInfosLine)

        // Execution
        val res1 = HealthOneLogicImpl.importPlainStringLaboResult(language, lrl1, position, ril)
        val res2 = HealthOneLogicImpl.importPlainStringLaboResult(language, lrl2, position, ril)

        // Tests
        ///
        Assert.assertEquals(res1.codes.size, 0)
        Assert.assertNotNull(res1.id)
        Assert.assertEquals(res1.content.get(language)?.stringValue, " ")
        Assert.assertEquals(res1.label, lrl1.analysisType)
        Assert.assertEquals(res1.index, position)
        Assert.assertEquals(res1.valueDate, FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(ril.demandDate, ZoneId.systemDefault()), ChronoUnit.DAYS))
        ///
        Assert.assertEquals(res2.codes.size, 1)
        Assert.assertNotNull(res2.id)
        Assert.assertEquals(res2.content.get(language)?.stringValue, "6 g (2-4 ) (+ )")
        Assert.assertEquals(res2.label, lrl1.analysisType)
        Assert.assertEquals(res2.index, position)
        Assert.assertEquals(res2.valueDate, FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(ril.demandDate, ZoneId.systemDefault()), ChronoUnit.DAYS))

    }

    @Test
    fun importNumericLaboResult() {
        // First parameter
        val language = "language"
        // Second parameter
        val d = 1.0
        // Third parameter
        val laboLine = "A1\\protocol\\Labo\\"
        val ll = HealthOneLogicImpl.getLaboLine(laboLine)
        val laboResultLine1 = "L1\\protocol\\BLOOD\\Red corpuscule\\2-4\\g\\+\\6"
        val lrl1 = HealthOneLogicImpl.getLaboResultLine(laboResultLine1, ll)
        val laboResultLine2 = "L1\\protocol\\BLOOD\\Red corpuscule\\\\g\\\\6"
        val lrl2 = HealthOneLogicImpl.getLaboResultLine(laboResultLine2, ll)
        val laboResultLine3 = "L1\\protocol\\BLOOD\\Red corpuscule\\2-4 g\\\\\\\\\\"
        val lrl3 = HealthOneLogicImpl.getLaboResultLine(laboResultLine3, ll)
        // Fourth parameter
        val position = 1L
        // Fifth parameter
        val resultsInfosLine = "A4\\protocol\\Docteur Bidon\\19032019\\\\C\\"
        val ril = HealthOneLogicImpl.getResultsInfosLine(resultsInfosLine)
        // Sixth parameter
        val comment1 = null
        val comment2 = "comment"

        // Execution
        val res1 = HealthOneLogicImpl.importNumericLaboResult(language, d, lrl2, position, ril, comment1)
        val res2 = HealthOneLogicImpl.importNumericLaboResult(language, d, lrl1, position, ril, comment2)
        val res3 = HealthOneLogicImpl.importNumericLaboResult(language, d, lrl3, position, ril, comment2)

        // Tests
        /// The process goes in none if block
        Assert.assertNotNull(res1.id)
        Assert.assertEquals(res1.label, lrl2.analysisType)
        Assert.assertEquals(res1.index, position)
        Assert.assertEquals(res1.valueDate, FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(ril.demandDate, ZoneId.systemDefault()), ChronoUnit.DAYS))
        Assert.assertEquals(res1.content.get(language)?.measureValue?.value, d)
        Assert.assertEquals(res1.content.get(language)?.measureValue?.comment, null)
        Assert.assertEquals(res1.content.get(language)?.measureValue?.unit, lrl2.unit)
        Assert.assertEquals(res1.content.get(language)?.measureValue?.min, null)
        Assert.assertEquals(res1.content.get(language)?.measureValue?.max, null)
        Assert.assertEquals(res1.content.get(language)?.measureValue?.severity, null)
        Assert.assertEquals(res1.content.get(language)?.measureValue?.severityCode, null)
        Assert.assertEquals(res1.codes.size, 0)

        /// All the ifs at the first level are accepted but not the one at second level
        Assert.assertNotNull(res2.id)
        Assert.assertEquals(res2.label, lrl2.analysisType)
        Assert.assertEquals(res2.index, position)
        Assert.assertEquals(res2.valueDate, FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(ril.demandDate, ZoneId.systemDefault()), ChronoUnit.DAYS))
        Assert.assertEquals(res2.content.get(language)?.measureValue?.value, d)
        Assert.assertEquals(res2.content.get(language)?.measureValue?.comment, comment2)
        Assert.assertEquals(res2.content.get(language)?.measureValue?.unit, lrl2.unit)
        Assert.assertEquals(res2.content.get(language)?.measureValue?.min, 2.0)
        Assert.assertEquals(res2.content.get(language)?.measureValue?.max, 4.0)
        Assert.assertEquals(res2.content.get(language)?.measureValue?.severity, 1)
        Assert.assertEquals(res2.content.get(language)?.measureValue?.severityCode, "+")
        Assert.assertEquals(res2.codes.size, 1)

        ///
        Assert.assertNotNull(res3.id)
        Assert.assertEquals(res3.label, lrl2.analysisType)
        Assert.assertEquals(res3.index, position)
        Assert.assertEquals(res3.valueDate, FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(ril.demandDate, ZoneId.systemDefault()), ChronoUnit.DAYS))
        Assert.assertEquals(res3.content.get(language)?.measureValue?.value, d)
        Assert.assertEquals(res3.content.get(language)?.measureValue?.comment, comment2)
        Assert.assertEquals(res3.content.get(language)?.measureValue?.unit, "g")
        Assert.assertEquals(res3.content.get(language)?.measureValue?.min, 2.0)
        Assert.assertEquals(res3.content.get(language)?.measureValue?.max, 4.0)
        Assert.assertEquals(res3.content.get(language)?.measureValue?.severity, null)
        Assert.assertEquals(res3.content.get(language)?.measureValue?.severityCode, null)
        Assert.assertEquals(res3.codes.size, 0)

    }

    @Test
    fun tryToGetValueAsNumber() {
        // Integer
        val values1 = "1"
        val res1 = HealthOneLogicImpl.tryToGetValueAsNumber(values1)
        Assert.assertTrue(res1 == 1.0)

        // Zero
        val values2 = "0"
        val res2 = HealthOneLogicImpl.tryToGetValueAsNumber(values2)
        Assert.assertTrue(res2 == 0.0)

        // Negative integer
        val values3 = "-1"
        val res3 = HealthOneLogicImpl.tryToGetValueAsNumber(values3)
        Assert.assertTrue(res3 == -1.0)

        // Double with comma
        val values4 = "1,0"
        val res4 = HealthOneLogicImpl.tryToGetValueAsNumber(values4)
        Assert.assertTrue(res4 == 1.0)

        // Double with point
        val values5 = "1.0"
        val res5 = HealthOneLogicImpl.tryToGetValueAsNumber(values5)
        Assert.assertTrue(res5 == 1.0)

        // Negative double with point
        val values6 = "-1.0"
        val res6 = HealthOneLogicImpl.tryToGetValueAsNumber(values6)
        Assert.assertTrue(res6 == -1.0)

        // Negative double with point
        val values7 = "-1.0000000000000000000000"
        val res7 = HealthOneLogicImpl.tryToGetValueAsNumber(values7)
        Assert.assertTrue(res7 == -1.0)

        // Not a double
        val values8 = "a"
        val res8 = HealthOneLogicImpl.tryToGetValueAsNumber(values8)
        Assert.assertEquals(res8, null)
    }

    @Test
    fun tryToGetReferenceValues() {
        // betweenReference
        val refValues1 = "1-2"
        val res1 = HealthOneLogicImpl.tryToGetReferenceValues(refValues1)
        Assert.assertTrue(res1.minValue == 1.0)
        Assert.assertTrue(res1.maxValue == 2.0)

        // same with group 3
        val refValues2 = "1-2     mg"
        val res2 = HealthOneLogicImpl.tryToGetReferenceValues(refValues2)
        Assert.assertTrue(res2.minValue == 1.0)
        Assert.assertTrue(res2.maxValue == 2.0)
        Assert.assertEquals(res2.unit, "mg")

        // same with group 4
        val refValues3 = "1-2    8mg"
        val res3 = HealthOneLogicImpl.tryToGetReferenceValues(refValues3)
        Assert.assertTrue(res3.minValue == 1.0)
        Assert.assertTrue(res3.maxValue == 2.0)
        Assert.assertEquals(res3.unit, "8mg")

        // lessThanReference
        val refValues4 = "<2"
        val res4 = HealthOneLogicImpl.tryToGetReferenceValues(refValues4)
        Assert.assertTrue(res4.maxValue == 2.0)

        // lessThanReference with group 3
        val refValues5 = "<2   L"
        val res5 = HealthOneLogicImpl.tryToGetReferenceValues(refValues5)
        Assert.assertTrue(res5.maxValue == 2.0)
        Assert.assertEquals(res5.unit, "L")

        // lessThanReference with group 4
        val refValues6 = "<2   8L"
        val res6 = HealthOneLogicImpl.tryToGetReferenceValues(refValues6)
        Assert.assertTrue(res6.maxValue == 2.0)
        Assert.assertEquals(res6.unit, "8L")

        // greaterThanReference
        val refValues7 = ">2"
        val res7 = HealthOneLogicImpl.tryToGetReferenceValues(refValues7)
        Assert.assertTrue(res7.minValue == 2.0)

        // greaterThanReference with group 3
        val refValues8 = ">2   e10mg"
        val res8 = HealthOneLogicImpl.tryToGetReferenceValues(refValues8)
        Assert.assertTrue(res8.minValue == 2.0)
        Assert.assertEquals(res8.unit, "e10mg")

        // greaterThanReference with group 4
        val refValues9 = ">2   10mg"
        val res9 = HealthOneLogicImpl.tryToGetReferenceValues(refValues9)
        Assert.assertTrue(res9.minValue == 2.0)
        Assert.assertEquals(res9.unit, "10mg")

        // refValues not matches
        val refValues10 = "([-,-:-, a"
        val res10 = HealthOneLogicImpl.tryToGetReferenceValues(refValues10)
        Assert.assertEquals(res10, null)

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
        val res2 = HealthOneLogicImpl.extractResultInfos(bufferedreader2, language, null, full)

        Assert.assertTrue(res1.size == res2.size)
        for ((index, ResultInfo) in res1.withIndex()) {
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
            for ((codeIndex, code) in ResultInfo.codes.withIndex()) {
                Assert.assertEquals(code, res2[index].codes[codeIndex])
            }
            for ((serviceIndex, service) in ResultInfo.services.withIndex()) {
                val a = service.index
                val b = res2[index].services[serviceIndex].index
                Assert.assertEquals(service.index, res2[index].services[serviceIndex].index)
            }
        }

    }

    @Test
    fun extractResultInfos() {
        // First parameter
        /// File 1
        val mappings1 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/FichierVide.txt")
        val bufferedreader1 = mappings1.bufferedReader(Charset.forName("cp1252"));
        /// File 2
        val mappings2 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339_2_WithoutA1.LAB")
        val bufferedreader2 = mappings2.bufferedReader(Charset.forName("cp850"));
        /// File 3
        val mappings3 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339_6-A1LineOnly.LAB")
        val bufferedreader3 = mappings3.bufferedReader(Charset.forName("cp850"));
        // File 4
        val mappings4 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339_4.LAB")
        val bufferedreader4 = mappings4.bufferedReader(Charset.forName("cp850"));
        // File 5
        val mappings5 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339_10_A1+A2WithoutDateOfBirth.LAB")
        val bufferedreader5 = mappings5.bufferedReader(Charset.forName("cp850"));
        // File 6
        val mappings6 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339_11_A1+A2WithDateOfBirth.LAB")
        val bufferedreader6 = mappings6.bufferedReader(Charset.forName("cp850"));
        // File 7
        val mappings7 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339_12_A1+A2+S4.LAB")
        val bufferedreader7 = mappings7.bufferedReader(Charset.forName("cp850"));
        // File 8
        val mappings8 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339_13_A1+A2+S4.LAB")
        val bufferedreader8 = mappings8.bufferedReader(Charset.forName("cp850"));
        // File 9
        val mappings9 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339_14.LAB")
        val bufferedreader9 = mappings9.bufferedReader(Charset.forName("cp850"));
        // File 10
        val mappings10 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339_15.LAB")
        val bufferedreader10 = mappings10.bufferedReader(Charset.forName("cp850"));
        // File 11
        val mappings11 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339_16.LAB")
        val bufferedreader11 = mappings11.bufferedReader(Charset.forName("cp850"));
        // File 12
        val mappings12 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339_9.txt")
        val bufferedreader12 = mappings12.bufferedReader(Charset.forName("cp850"));
        // File 13
        val mappings13 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339_5.LAB")
        val bufferedreader13 = mappings13.bufferedReader(Charset.forName("cp850"));
        // File 14
        val mappings14 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/st-jean-gaspar_MS-506")
        val bufferedreader14 = mappings14.bufferedReader(Charset.forName("cp850"));
        // File 15
        val mappings15 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/st-jean-gaspar_MS-506_2")
        val bufferedreader15 = mappings15.bufferedReader(Charset.forName("cp850"));
        // File 16
        val mappings16 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/15692224004003_MS-642.LAB")
        val bufferedreader16 = mappings16.bufferedReader(Charset.forName("cp1252"));

        // Second parameter
        val language = "UTF-8"

        // Third parameter
        val docID = "docID"

        // Fourth parameter
        val full = true

        // Executions
        val res1 = HealthOneLogicImpl.extractResultInfos(bufferedreader1, language, docID, full)
        val res2 = HealthOneLogicImpl.extractResultInfos(bufferedreader2, language, docID, full)
        val res3 = HealthOneLogicImpl.extractResultInfos(bufferedreader3, language, docID, full)
        val res4 = HealthOneLogicImpl.extractResultInfos(bufferedreader4, language, docID, full)
        val res5 = HealthOneLogicImpl.extractResultInfos(bufferedreader5, language, docID, full)
        val res6 = HealthOneLogicImpl.extractResultInfos(bufferedreader6, language, docID, full)
        val res7 = HealthOneLogicImpl.extractResultInfos(bufferedreader7, language, docID, full)
        val res8 = HealthOneLogicImpl.extractResultInfos(bufferedreader8, language, docID, full)
        val res9 = HealthOneLogicImpl.extractResultInfos(bufferedreader9, language, docID, full)
        val res10 = HealthOneLogicImpl.extractResultInfos(bufferedreader10, language, docID, full)
        val res11 = HealthOneLogicImpl.extractResultInfos(bufferedreader11, language, docID, full)
        val res12 = HealthOneLogicImpl.extractResultInfos(bufferedreader12, language, docID, full)
        val res13 = HealthOneLogicImpl.extractResultInfos(bufferedreader13, language, docID, full)
        val res14 = HealthOneLogicImpl.extractResultInfos(bufferedreader14, language, docID, full)
        val res15 = HealthOneLogicImpl.extractResultInfos(bufferedreader15, language, docID, full)
        val res16 = HealthOneLogicImpl.extractResultInfos(bufferedreader16, language, docID, full)

        // Tests
        /// Empty File
        Assert.assertEquals(res1.size, 0);
        /// File without A1 (LaboLine) Line
        Assert.assertEquals(res2.size, 0);
        /// File with only A1 Line
        Assert.assertEquals(res3.size, 1);
        Assert.assertEquals(res3[0].services.size, 0);
        Assert.assertNull(res3[0].complete);
        Assert.assertNull(res3[0].demandDate);
        /// File with two A1 Line
        Assert.assertEquals(res13.size, 2);
        /// File A2 then A3 then A1 then A4 Line
        Assert.assertEquals(res4.size, 1);
        Assert.assertTrue(res4[0].complete);
        Assert.assertEquals(res4[0].demandDate, Instant.ofEpochMilli(HealthOneLogicImpl.readDate("19032019")).toEpochMilli())
        Assert.assertEquals(res4[0].services.size, 0);
        /// File with A1 and A2 (without date of birth) lines
        Assert.assertEquals(res5[0].lastName, "NOM")
        Assert.assertEquals(res5[0].firstName, "PRENOM")
        Assert.assertNull(res5[0].dateOfBirth)
        Assert.assertEquals(res5[0].protocol, "1903-19339")
        Assert.assertEquals(res5[0].sex, "F")
        Assert.assertEquals(res5[0].documentId, docID)
        /// File with A1 and A2 (with date of birth) lines
        Assert.assertEquals(res6[0].lastName, "NOM")
        Assert.assertEquals(res6[0].firstName, "PRENOM")
        Assert.assertEquals(res6[0].dateOfBirth, FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(Timestamp(HealthOneLogicImpl.readDate("01011950")).getTime()), ZoneId.systemDefault()), ChronoUnit.DAYS))
        Assert.assertEquals(res6[0].protocol, "1903-19339")
        Assert.assertEquals(res6[0].sex, "F")
        Assert.assertEquals(res6[0].documentId, docID)
        /// File with A1, A2 and enpty S4.* lines
        Assert.assertEquals(res7[0].dateOfBirth, FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(Timestamp(HealthOneLogicImpl.readDate("01011950")).getTime()), ZoneId.systemDefault()), ChronoUnit.DAYS))
        Assert.assertEquals(res7[0].sex, "F")
        /// File with A1, A2 and S4.*(with date of birth and sex) lines
        Assert.assertEquals(res8[0].dateOfBirth, FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(Timestamp(HealthOneLogicImpl.readDate("02021950")).getTime()), ZoneId.systemDefault()), ChronoUnit.DAYS))
        Assert.assertEquals(res8[0].sex, "M")
        /// File with A1,A2,S4.* and A4 lines
        /// with null r
        /// File with A1,A2,S4.* and A4 lines
        Assert.assertTrue(res9[0].complete);
        Assert.assertEquals(res9[0].demandDate, Instant.ofEpochMilli(HealthOneLogicImpl.readDate("19032019")).toEpochMilli())
        Assert.assertEquals(res9[0].services.size, 0);
        /// File with A1,A2,S4.*,A4 and L1 lines
        Assert.assertTrue(res10[0].complete);
        Assert.assertEquals(res10[0].demandDate, Instant.ofEpochMilli(HealthOneLogicImpl.readDate("19032019")).toEpochMilli())
        Assert.assertEquals(res10[0].services[0].valueDate, FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(HealthOneLogicImpl.readDate("19032019")), ZoneId.systemDefault()), ChronoUnit.DAYS));
        Assert.assertEquals(res10[0].codes.size, 1)
        Assert.assertEquals(res10[0].codes[0].type, "CD-TRANSACTION")
        Assert.assertEquals(res10[0].codes[0].code, "labresult")
        Assert.assertEquals(res10[0].codes[0].version, "1")
        /// File with A1,A2,S4.*,A4 and A5 lines
        /// with null r
        /// File with A1,A2,S4.*,A4 and A5 lines
        Assert.assertEquals(res11[0].ssin, "50010100156")
        /// File with A1,A2,A3,A4 and two L1 lines
        Assert.assertEquals(res12[0].services.size, 2);
        /// File contains one A1,A2,A3,A4 Line and less than 20 L5Line
        Assert.assertEquals(res14.size, 1)
        Assert.assertEquals(res14[0].services.size, 1)
        /// File contains one A1,A2,A3,A4 Line and more than 20 L5Line
        Assert.assertEquals(res15.size, 1)
        Assert.assertEquals(res15[0].services.size, 2)
        /// File contains 2 line L1 with the same code but 2 differents types
        Assert.assertEquals(res16.size, 1)
        Assert.assertEquals(res16[0].services.size, 2)
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
        Assert.assertEquals(res1.resultReference, null)
        Assert.assertEquals(res1.labo, null)
        Assert.assertEquals(res1.fullLine, line1)

        val line2 = "A1\\\\\\\\\\\\"
        val res2 = HealthOneLogicImpl.getLaboLine(line2)
        Assert.assertEquals(res2.resultReference, "")
        Assert.assertEquals(res2.labo, "")
        Assert.assertEquals(res2.fullLine, line2)

        // Complete Line with "V"
        val line3 = "A1\\protocol\\LaboName"
        val res3 = HealthOneLogicImpl.getLaboLine(line3)
        Assert.assertEquals(res3.resultReference, "protocol")
        Assert.assertEquals(res3.labo, "LaboName")
        Assert.assertEquals(res3.fullLine, line3)
    }

    @Test
    fun getPatientLine() {
        // Empty line
        val line1 = "A2"
        val res1 = HealthOneLogicImpl.getPatientLine(line1)
        Assert.assertEquals(res1.protocol, null)
        Assert.assertEquals(res1.firstName, null)
        Assert.assertEquals(res1.lastName, null)
        Assert.assertEquals(res1.sex, null)
        Assert.assertEquals(res1.dn, null)

        val line2 = "A2\\\\\\\\\\\\"
        val res2 = HealthOneLogicImpl.getPatientLine(line2)
        Assert.assertEquals(res2.protocol, "")
        Assert.assertEquals(res2.firstName, "")
        Assert.assertEquals(res2.lastName, "")
        Assert.assertEquals(res2.sex, "")
        Assert.assertEquals(res2.dn, null)

        // Complete Line with "V"
        val line3 = "A2\\protocol\\NOM\\PRENOM\\V\\01011950\\"
        val res3 = HealthOneLogicImpl.getPatientLine(line3)
        Assert.assertEquals(res3.protocol, "protocol")
        Assert.assertEquals(res3.firstName, "PRENOM")
        Assert.assertEquals(res3.lastName, "NOM")
        Assert.assertEquals(res3.sex, "F")
        Assert.assertEquals(res3.dn, Timestamp(HealthOneLogicImpl.readDate("01011950")))

        // Complete Line with "A" and unaccepted date
        val line4 = "A2\\protocol\\NOM\\PRENOM\\A\\010\\"
        val res4 = HealthOneLogicImpl.getPatientLine(line4)
        Assert.assertEquals(res4.sex, "A")
        Assert.assertEquals(res4.dn, null)
    }

    @Test
    fun getExtraPatientLine() {
        // Empty line
        val line1 = "S4.*"
        val res1 = HealthOneLogicImpl.getExtraPatientLine(line1)
        Assert.assertEquals(res1.protocol, null)
        Assert.assertEquals(res1.sex, null)
        Assert.assertEquals(res1.dn, null)

        val line2 = "S4.*\\\\\\\\\\"
        val res2 = HealthOneLogicImpl.getExtraPatientLine(line2)
        Assert.assertEquals(res2.protocol, "")
        Assert.assertEquals(res2.sex, "")
        Assert.assertEquals(res2.dn, null)

        // Complete Line with "V"
        val line3 = "S4.*\\protocol\\01011950\\V\\"
        val res3 = HealthOneLogicImpl.getExtraPatientLine(line3)
        Assert.assertEquals(res3.protocol, "protocol")
        Assert.assertEquals(res3.sex, "F")
        Assert.assertEquals(res3.dn, Timestamp(HealthOneLogicImpl.readDate("01011950")))

        // Complete Line with "A" and unaccepted date
        val line4 = "S4.*\\protocol\\010\\A\\"
        val res4 = HealthOneLogicImpl.getExtraPatientLine(line4)
        Assert.assertEquals(res4.sex, "A")
        Assert.assertEquals(res4.dn, null)
    }

    @Test
    fun getLaboResultLine() {
        // Empty line
        val line1 = "L1"
        val laboLine = "A1\\protocol\\Labo\\"
        val ll = HealthOneLogicImpl.getLaboLine(laboLine)
        val res1 = HealthOneLogicImpl.getLaboResultLine(line1, ll)
        Assert.assertNotNull(res1)
        Assert.assertEquals(res1.protocol, "")
        Assert.assertEquals(res1.analysisCode, "")
        Assert.assertEquals(res1.analysisType, "untitled")
        Assert.assertEquals(res1.referenceValues, "")
        Assert.assertEquals(res1.unit, "")
        Assert.assertEquals(res1.severity, "")
        Assert.assertEquals(res1.value, "")

        val line2 = "L1\\\\\\\\\\\\\\"
        val res2 = HealthOneLogicImpl.getLaboResultLine(line2, ll)
        Assert.assertEquals(res2.protocol, "")
        Assert.assertEquals(res2.analysisCode, "")
        Assert.assertEquals(res2.analysisType, "untitled")
        Assert.assertEquals(res2.referenceValues, "")
        Assert.assertEquals(res2.unit, "")
        Assert.assertEquals(res2.severity, "")
        Assert.assertEquals(res2.value, "")

        // Complete line with R1.*
        val line4 = "R1.*\\protocol\\BLOOD\\Red corpuscule\\2\\4\\g\\6"
        val res4 = HealthOneLogicImpl.getLaboResultLine(line4, ll)
        Assert.assertEquals(res4.protocol, "protocol")
        Assert.assertEquals(res4.analysisCode, "BLOOD")
        Assert.assertEquals(res4.analysisType, "Red corpuscule")
        Assert.assertEquals(res4.referenceValues, "2 - 4")
        Assert.assertEquals(res4.unit, "g")
        Assert.assertEquals(res4.severity, "")
        Assert.assertEquals(res4.value, "6")

        // Complete line with L1
        val line3 = "L1\\protocol\\BLOOD\\Red corpuscule\\2-4\\g\\+\\6"
        val res3 = HealthOneLogicImpl.getLaboResultLine(line3, ll)
        Assert.assertEquals(res3.protocol, "protocol")
        Assert.assertEquals(res3.analysisCode, "BLOOD")
        Assert.assertEquals(res3.analysisType, "Red corpuscule")
        Assert.assertEquals(res3.referenceValues, "2-4")
        Assert.assertEquals(res3.unit, "g")
        Assert.assertEquals(res3.severity, "+")
        Assert.assertEquals(res3.value, "6")

        ll.labosList.add(res3)
        val line5 = "L1\\protocol\\BLOOD\\\\Text written by the lab"
        val res5 = HealthOneLogicImpl.getLaboResultLine(line5, ll)
        Assert.assertEquals(res5.protocol, "protocol")
        Assert.assertEquals(res5.analysisCode, "BLOOD")
        Assert.assertEquals(res5.analysisType, "Red corpuscule")
        Assert.assertEquals(res5.value, "Text written by the lab")
    }

    @Test
    fun getProtocolLine() {
        // Empty line
        val line1 = "L5"
        val res1 = HealthOneLogicImpl.getProtocolLine(line1)
        Assert.assertEquals(res1.protocol, null)
        Assert.assertEquals(res1.code, null)
        Assert.assertEquals(res1.text, null)

        val line2 = "L5\\\\\\\\\\"
        val res2 = HealthOneLogicImpl.getProtocolLine(line2)
        Assert.assertEquals(res2.protocol, "")
        Assert.assertEquals(res2.code, "")
        Assert.assertEquals(res2.text, "")

        // Complete line with text in eight position
        val line3 = "L5\\protocol\\SANG\\\\\\\\\\Text written by the doctor"
        val res3 = HealthOneLogicImpl.getProtocolLine(line3)
        Assert.assertEquals(res3.protocol, "protocol")
        Assert.assertEquals(res3.code, "SANG")
        Assert.assertEquals(res3.text, "Text written by the doctor")

        // Complete line with text in fourth position
        val line4 = "L5\\protocol\\SANG\\Text written by the doctor\\"
        val res4 = HealthOneLogicImpl.getProtocolLine(line4)
        Assert.assertEquals(res4.text, "Text written by the doctor")

        // Null line
        val line5 = null
        val res5 = HealthOneLogicImpl.getProtocolLine(line5)
        Assert.assertNull(res5)
    }

    @Test
    fun getResultsInfosLine() {
        // Empty line
        val line1 = "A4"
        val res1 = HealthOneLogicImpl.getResultsInfosLine(line1)
        Assert.assertEquals(res1.protocol, null)
        Assert.assertEquals(res1.complete, true)
        Assert.assertEquals(res1.demandDate, null)

        val line2 = "A4\\\\\\\\\\"
        val res2 = HealthOneLogicImpl.getResultsInfosLine(line2)
        Assert.assertEquals(res2.protocol, "")
        Assert.assertEquals(res2.complete, false)
        Assert.assertNotEquals(res2.demandDate, null)

        // Complete line with C
        val line3 = "A4\\protocol\\Docteur Bidon\\19032019\\\\C\\"
        val res3 = HealthOneLogicImpl.getResultsInfosLine(line3)
        Assert.assertEquals(res3.protocol, "protocol")
        Assert.assertEquals(res3.complete, true)
        Assert.assertEquals(res3.demandDate, Instant.ofEpochMilli(HealthOneLogicImpl.readDate("19032019")))

        // Complete line with P
        val line4 = "A4\\protocol\\Docteur Bidon\\19032019\\\\P\\"
        val res4 = HealthOneLogicImpl.getResultsInfosLine(line4)
        Assert.assertEquals(res4.complete, false)

        // Null line
        val line5 = null
        val res5 = HealthOneLogicImpl.getResultsInfosLine(line5)
        Assert.assertNull(res5)
    }

    @Test
    fun getPatientSSINLine() {
        // Empty line
        val line1 = "A5"
        val res1 = HealthOneLogicImpl.getPatientSSINLine(line1)
        Assert.assertEquals(res1.protocol, null)
        Assert.assertEquals(res1.ssin, null)
        val line2 = "A5\\\\\\\\\\"
        val res2 = HealthOneLogicImpl.getPatientSSINLine(line2)
        Assert.assertEquals(res2.protocol, "")
        Assert.assertEquals(res2.ssin, null)

        // SSIN in fourth position
        val line3 = "A5\\protocol\\\\50010100156\\\\"
        val res3 = HealthOneLogicImpl.getPatientSSINLine(line3)
        Assert.assertEquals(res3.protocol, "protocol")
        Assert.assertEquals(res3.ssin, "50010100156")

        // SSIN in fifth position
        val line4 = "A5\\protocol\\\\\\50010100156\\"
        val res4 = HealthOneLogicImpl.getPatientSSINLine(line4)
        Assert.assertEquals(res4.protocol, "protocol")
        Assert.assertEquals(res4.ssin, "50010100156")

        // Null line
        val line5 = null
        val res5 = HealthOneLogicImpl.getPatientSSINLine(line5)
        Assert.assertNull(res5)
    }

    @Test
    fun getPatientAddressLine() {
        val line1 = "A3\\protocol\\ Rue factice 8\\1050\\Ixelles\\"
        val res1 = HealthOneLogicImpl.getPatientAddressLine(line1)
        Assert.assertEquals(res1.protocol, "protocol")
        Assert.assertEquals(res1.address, "Rue factice")
        Assert.assertEquals(res1.number, "8")
        Assert.assertEquals(res1.zipCode, "1050")
        Assert.assertEquals(res1.locality, "Ixelles")

        val line2 = "A3"
        val res2 = HealthOneLogicImpl.getPatientAddressLine(line2)
        Assert.assertEquals(res2.protocol, null)
        Assert.assertEquals(res2.address, null)
        Assert.assertEquals(res2.number, null)
        Assert.assertEquals(res2.zipCode, null)
        Assert.assertEquals(res2.locality, null)

        val line3 = "A3\\protocol\\ 8Rue factice\\a1050\\Ixelles\\"
        val res3 = HealthOneLogicImpl.getPatientAddressLine(line3)
        Assert.assertEquals(res3.address, " 8Rue factice") //Be careful the space at the beginning is important
        Assert.assertEquals(res3.number, null)
        Assert.assertEquals(res3.zipCode, null)

        val line4 = "A3\\protocol\\    8    Rue factice  \\   1050      \\Ixelles\\"
        val res4 = HealthOneLogicImpl.getPatientAddressLine(line4)
        Assert.assertEquals(res4.address, "Rue factice")
        Assert.assertEquals(res4.number, "8")
        Assert.assertEquals(res4.zipCode, "1050")
    }

    @Test
    fun doExport() {
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
        val date = LocalDateTime.of(2019, 6, 18, 0, 0)

        // Fifth parameter
        val ref = "RefTest-1";

        // Sixth parameter
        val text = "This a comment written to create L5 line \n And this is to create a second L5 line";

        // Seventh parameter
        val path = "src/test/resources/org/taktik/icure/be/format/logic/impl/outDoExport.lab"
        val file = File(path)
        val out = file.outputStream();

        // Execution
        /// File 1
        val res1 = HealthOneLogicImpl.doExport(sender, recipient, patient, date, ref, text, out);
        val mappings1 = file.inputStream();
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
        Assert.assertEquals(res1return[0].labo, nihii + " " + lastNameDoct + " " + firstNameDoct)
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
    fun readDate() {

        // Format ddmmyyyy
        val date1 = "01021950"
        val res1 = HealthOneLogicImpl.readDate(date1);
        val a = res1.toString();
        Assert.assertTrue((res1.toString()).equals("-628477200000"))

        // Format ddmmyy
        val date2 = "010250"
        val res2 = HealthOneLogicImpl.readDate(date2);
        Assert.assertTrue((res2.toString()).equals("-628477200000"))

        // Format dd/mm/yyyy
        val date3 = "01/02/1950"
        val res3 = HealthOneLogicImpl.readDate(date3);
        Assert.assertTrue((res3.toString()).equals("-628477200000"))

        // Unaccepted format
        val date4 = "000"
        try {
            val res4 = HealthOneLogicImpl.readDate(date4);
        } catch (e: NumberFormatException) {
            Assert.assertTrue(e.message!!.contains("Unreadable date: \"" + date4 + "\""))
        }

    }
}
