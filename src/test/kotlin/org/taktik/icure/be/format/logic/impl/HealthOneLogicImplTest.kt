package org.taktik.icure.be.format.logic.impl;


import com.ibm.icu.impl.Assert.fail
import org.junit.*
import org.mockito.Matchers
import org.mockito.Mock
import org.taktik.icure.dao.ContactDAO
import org.taktik.icure.dao.impl.ContactDAOImpl
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.Document
import org.taktik.icure.logic.impl.ContactLogicImpl
import java.io.*

import java.util.*
import org.mockito.Mockito.*
import org.taktik.icure.entities.*
import org.taktik.icure.logic.ContactLogic
import org.taktik.icure.logic.DocumentLogic
import java.nio.charset.Charset
import org.junit.*
import org.slf4j.LoggerFactory
import org.taktik.icure.dao.DocumentDAO
import org.taktik.icure.logic.PatientLogic
import org.taktik.icure.logic.impl.DocumentLogicImpl
import java.lang.NullPointerException
import java.text.ParseException


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
        Assert.assertEquals(HealthOneLogicImpl.patientLogic==patientLogic,false)
        HealthOneLogicImpl.setPatientLogic(patientLogic);
        Assert.assertEquals(HealthOneLogicImpl.patientLogic,patientLogic)
    }

    @Test
    fun setDocumentLogic() {
        Assert.assertEquals(HealthOneLogicImpl.documentLogic==documentLogic,false)
        HealthOneLogicImpl.setDocumentLogic(documentLogic);
        Assert.assertEquals(HealthOneLogicImpl.documentLogic,documentLogic)
    }

    @Test
    fun getInfos() {

        // First parameter
        /// File 1
        val doc1 = Document();
        val mappings1 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339.lab")
        val bytes1 = mappings1.readBytes();
        doc1.setAttachment(bytes1);
        /// File 2
        val doc2 = Document();
        val mappings2 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/1100116471515_a1_LOL0327b54.LAB.txt")
        val bytes2 = mappings2.readBytes();
        doc2.setAttachment(bytes2);
        /// File 3 (report -L5)
        val doc3 = Document();
        val mappings3 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/st-jean-gaspar_MS-506")
        val bytes3 = mappings3.readBytes();
        doc3.setAttachment(bytes3);
        // File 4 (only one line protocol)
        val doc4 = Document();
        val mappings4 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/1100116471515_a1_LOL0327b54_2.LAB")
        val bytes4 = mappings4.readBytes();
        doc4.setAttachment(bytes4);
        // File 5 (ExtraPatientLine)
        val doc5 = Document();
        val mappings5 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/15692224004003_MS-642_WithS1Line.LAB")
        val bytes5 = mappings5.readBytes();
        doc5.setAttachment(bytes5);
        // File 6 - MS 642 (Partial result and missplaced NISS)
        val doc6 = Document();
        val mappings6 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/15692224004003_MS-642.LAB")
        val bytes6 = mappings6.readBytes();
        doc6.setAttachment(bytes6);
        // File 7 - MS 635 (2 services)
        val doc7 = Document();
        val mappings7 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/H1-190507162031-0001_MS-635.DAT")
        val bytes7 = mappings7.readBytes();
        doc7.setAttachment(bytes7);
        // File 8 - MS 339 (exception generated)
        val doc8 = Document();
        val mappings8 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339_3.txt")
        val bytes8 = mappings8.readBytes();
        doc8.setAttachment(bytes8);

        // Second parameter
        val full = true

        // Third parameter
        val language = "UTF-8"

        // Fourth parameter
        val enckeys = null


        // Execution
        val res1 = HealthOneLogicImpl.getInfos(doc1,full, language, enckeys)  // File 1
        val res2 = HealthOneLogicImpl.getInfos(doc2,full, language, enckeys)  // File 2
        val res3 = HealthOneLogicImpl.getInfos(doc3,full, language, enckeys)  // File 3
        val res4 = HealthOneLogicImpl.getInfos(doc4,full, language, enckeys)  // File 4
        val res5 = HealthOneLogicImpl.getInfos(doc5,full, language, enckeys)  // File 5
        val res6 = HealthOneLogicImpl.getInfos(doc6,full, language, enckeys)  // File 6
        val res7 = HealthOneLogicImpl.getInfos(doc7,full, language, enckeys)  // File 7

        // General Test
        /// File 1
        Assert.assertEquals(res1[0].documentId,"Doc");
        Assert.assertEquals(res1[0].protocol,"1903-19339");
        /// File 2
        Assert.assertEquals(res2[0].documentId,"Doc");
        Assert.assertEquals(res2[0].protocol,"9S231326");
        /// File 3
        Assert.assertEquals(res3[0].documentId,"Doc");
        Assert.assertEquals(res3[0].protocol,"18652739");

        // Test A1
        Assert.assertEquals(res1[0].labo,"CHR HAUTE SENNE"); //File 1 line 1
        Assert.assertEquals(res2[0].labo,"Labo Luc Olivier - Villers"); //File 2 line 1 - Format Labo Luc Olivier - Villers\Add1\Add2\Add3\ Ok Only the first parse is take to put the labo name

        // Test A2
        /// File 1 line 2
        Assert.assertEquals(res1[0].lastName,"NOM");
        Assert.assertEquals(res1[0].firstName,"PRENOM");
        Assert.assertEquals(res1[0].sex,"F");
        Assert.assertEquals(res1[0].dateOfBirth, 19500101L); // Case of 01011950 writing
        /// File 2 line 2 - Format NOM\PRENOM\F\010150\
        Assert.assertEquals(res2[0].lastName,"NOM");
        Assert.assertEquals(res2[0].firstName,"PRENOM");
        Assert.assertEquals(res2[0].sex,"F");
        /* Before code is modify
        Assert.assertEquals(res2[0].dateOfBirth, null); // Case of 010150 writing isn't detected// Date must have 8 digit and the format is ordered by shortDateFormat which is the ddMMyyyy format
       */
        Assert.assertEquals(res2[0].dateOfBirth, 19500101L); // Case of 010150 writing is detected after modification
       /// File 3 line 2 - Format NOM\PRENOM\Add1\F\01011950\Add1\Add2\ imply lastName and firstName OK but sex becomes "Add0" and dateOfBirth is null caused by "F" not convenient format for date
        Assert.assertEquals(res3[0].lastName,"NOM");
        Assert.assertEquals(res3[0].firstName,"PRENOM");
        Assert.assertEquals(res3[0].sex,"Add1");
        Assert.assertEquals(res3[0].dateOfBirth, null);
        // Test A3 (line 3) None of that informations is in the class ResultInfo

        // Test A4 Doctor's name isn't use in the class ResultInfo
        /// File 1 line 4 - Format Docteur Bidon\19032019\\C\
        Assert.assertEquals(res1[0].demandDate,1552950000000);
        Assert.assertEquals(res1[0].complete, true);
        /// File 2 line 4  - Format Docteur Bidon\27032018\Add1\C\
        Assert.assertEquals(res2[0].demandDate,1522101600000);
        Assert.assertEquals(res2[0].complete, true);
        /// File 6 line 4  - Format Docteur Bidon\26112018\1821\P\
        Assert.assertEquals(res6[0].demandDate,1557266400000);
        Assert.assertEquals(res6[0].complete, false);

        // Test A5
        Assert.assertEquals(res1[0].ssin,"50010100156"); // File 1 line 5
        Assert.assertEquals(res2[0].ssin,"Add1"); // File 2 line 5 - For SSIN it's always the fourth part caught
        Assert.assertEquals(res6[0].ssin,"3170"); // File 6 line 5 - For SSIN it's always the fourth part caught

        // Test lines L1 (services)
        /// File 1
        Assert.assertEquals(res1[0].services.size,31);
        Assert.assertEquals(res1[0].codes[0].type,"CD-TRANSACTION");
        Assert.assertEquals(res1[0].codes[0].code,"labresult");
        Assert.assertEquals(res1[0].codes[0].version,"1");
        /// File 2
        Assert.assertEquals(res2[0].services.size,4);
        // Test simple L1
        /// File 1 line 21 //L1\1903-19339\UREE\Urée\10-50\mg/dL\*\41\
        Assert.assertEquals(res1[0].services[13].label,"Urée");
        Assert.assertEquals(res1[0].services[13].content.get("UTF-8")?.measureValue?.min,10.0);
        Assert.assertEquals(res1[0].services[13].content.get("UTF-8")?.measureValue?.max,50.0);
        Assert.assertEquals(res1[0].services[13].content.get("UTF-8")?.measureValue?.unit,"mg/dL");
        Assert.assertEquals(res1[0].services[13].content.get("UTF-8")?.measureValue?.value,41.0);
        /// File 6 line ?? //L1\1903-19339\Activité estérasiqu\Urée\<25\U/µL\*\500\
        Assert.assertEquals(res6[0].services[6].label,"Activité estérasique");
        Assert.assertEquals(res6[0].services[6].content.get("UTF-8")?.measureValue?.max,25.0);
        Assert.assertEquals(res6[0].services[6].content.get("UTF-8")?.measureValue?.unit,"U/µL");
        Assert.assertEquals(res6[0].services[6].content.get("UTF-8")?.measureValue?.value,500.0);
        // Test L1 complex
        /// File 1 line 6-7
        ///L1\1903-19339\EX_H\Index d'hémolyse\0-15\mg/dL\\1\
        ///L1\1903-19339\EX_H\Index d'hémolyse\0-15\mg/dL\\\
        Assert.assertEquals(res1[0].services[1].label, "Index d'hémolyse")
        Assert.assertEquals(res1[0].services[1].content.get("UTF-8")?.measureValue?.min,0.0);
        Assert.assertEquals(res1[0].services[1].content.get("UTF-8")?.measureValue?.max,15.0);
        Assert.assertEquals(res1[0].services[1].content.get("UTF-8")?.measureValue?.unit,"mg/dL");
        Assert.assertEquals(res1[0].services[1].content.get("UTF-8")?.measureValue?.value,1.0);
        Assert.assertEquals(res1[0].services[1].content.get("UTF-8")?.measureValue?.comment,"");
        /// File 2 line 7-11
        ///L1\9S231326\INR\INR\2-3\\*\1,8\
        ///L1\9S231326\INR\\< 1,5     : Pas d'anticoagulation effective\\*\\
        ///L1\9S231326\INR\\2,0 - 3,0 : Indications g‚n‚rales\\*\\
        ///L1\9S231326\INR\\2,5 - 3,5 : Indications particuliŠres\\*\\
        ///L1\9S231326\INR\\> 5,0     : Surdosage AVK\\*\\
        Assert.assertEquals(res2[0].services[1].label, "INR")
        Assert.assertEquals(res2[0].services[1].content.get("UTF-8")?.measureValue?.min,2.0);
        Assert.assertEquals(res2[0].services[1].content.get("UTF-8")?.measureValue?.max,3.0);
        Assert.assertEquals(res2[0].services[1].content.get("UTF-8")?.measureValue?.value,1.8);
        Assert.assertEquals(res2[0].services[1].content.get("UTF-8")?.measureValue?.comment,"< 1,5     : Pas d'anticoagulation effective\n2,0 - 3,0 : Indications générales\n2,5 - 3,5 : Indications particulières\n> 5,0     : Surdosage AVK");
        /// File 2 line 7-11
        ///L1\9S231326\QUICK\Temps de Quick\70-100\%\*\36\
        ///L1\9S231326\QUICK\\13 - 30 : sous AVK\\*\\
        Assert.assertEquals(res2[0].services[2].label, "Temps de Quick")
        Assert.assertEquals(res2[0].services[2].content.get("UTF-8")?.measureValue?.min,70.0);
        Assert.assertEquals(res2[0].services[2].content.get("UTF-8")?.measureValue?.max,100.0);
        Assert.assertEquals(res2[0].services[2].content.get("UTF-8")?.measureValue?.value,36.0);
        Assert.assertEquals(res2[0].services[2].content.get("UTF-8")?.measureValue?.unit,"%");
        Assert.assertEquals(res2[0].services[2].content.get("UTF-8")?.measureValue?.comment,"13 - 30 : sous AVK");
        /// File 4
        Assert.assertEquals(res4[0].services.size,1);
        Assert.assertEquals(res4[0].services[0].label, "Temps de Quick")
        Assert.assertEquals(res4[0].services[0].content.get("UTF-8")?.measureValue?.min,70.0);
        Assert.assertEquals(res4[0].services[0].content.get("UTF-8")?.measureValue?.max,100.0);
        Assert.assertEquals(res4[0].services[0].content.get("UTF-8")?.measureValue?.value,36.0);
        Assert.assertEquals(res4[0].services[0].content.get("UTF-8")?.measureValue?.unit,"%");
        Assert.assertEquals(res4[0].services[0].content.get("UTF-8")?.measureValue?.comment,"13 - 30 : sous AVK");
        /// File 7
        Assert.assertEquals(res7.size,2);
        // Test lines L5 (services)
        /// File 3
        Assert.assertEquals(res3[0].services.size,1);
        Assert.assertEquals(res3[0].codes[0].type,"CD-TRANSACTION");
        Assert.assertEquals(res3[0].codes[0].code,"report");
        Assert.assertEquals(res3[0].codes[0].version,"1");
        Assert.assertEquals(res3[0].services[0].content.get("UTF-8")?.stringValue,"Clinique Saint Jean -\nBruxelles,\nle 09/04/2019\n"+
        "Réf.á: 7207797\nCher Confrère, chère Cons£ur,\nNous avons vu en consultation le 09/04/2019  Monsieur NOM PRENOM né le\n" +
        "01/01/1950.\nAnamnèseá:");

        // Test lines S1 (ExtraPatientLine)
        Assert.assertEquals(res3[0].services.size,1);

        try {
            val res8 = HealthOneLogicImpl.getInfos(doc8,full, language, enckeys)  // File 8
            println("Erreur non vue")
            //calculator.squareRoot(-10);
            //fail(&quot;Should throw exception when calculating square root of a negative number&quot;);
        }catch( e: ParseException){
            println("erreur vue")
            //Assert(e.getMessage().contains());
        }

    }


    @Test
    fun parseReportsAndLabs() {

        // First parameter
        val language = "UTF-8";

        // Second parameter
        val protocolIds = listOf("***");

        // Third parameter
        /// File 1
        val mappings1 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/1100116471515_a1_LOL0327b54.LAB.txt");
        val content1 = HealthOneLogicImpl.decodeRawData(mappings1.readBytes());
        val r1 = StringReader(content1);

        // Execution
        val res1 = HealthOneLogicImpl.parseReportsAndLabs(language, protocolIds, r1); // File 1

        // General Test
        /// labo
        Assert.assertEquals(res1[0].labo,"Labo Luc Olivier - Villers");
        /// resultReference
        Assert.assertEquals(res1[0].resultReference,"9S231326");
        /// fullLine
        Assert.assertEquals(res1[0].fullLine,"A1\\9S231326\\Labo Luc Olivier - Villers\\Add1\\Add2\\Add3\\");
        /// labosList
        Assert.assertEquals(res1[0].labosList.size,0);
        /// protoList
        Assert.assertEquals(res1[0].protoList.size,0);
        /// ril
        Assert.assertEquals(res1[0].ril.protocol,"9S231326");
        //Assert.assertEquals(res1[0].ril.demandDate,"2018-03-26T22:00:00Z");
        Assert.assertEquals(res1[0].ril.complete,true);
        /// pal
        Assert.assertEquals(res1[0].pal.protocol,"9S231326");
        Assert.assertEquals(res1[0].pal.address,"Rue factice");
        Assert.assertEquals(res1[0].pal.number,"1");
        Assert.assertEquals(res1[0].pal.zipCode,"5360");
        Assert.assertEquals(res1[0].pal.locality,"NATOYE");
        /// services
        Assert.assertEquals(res1[0].services.size, 4)
        /// File 1 line 7-11
        ///L1\9S231326\INR\INR\2-3\\*\1,8\
        ///L1\9S231326\INR\\< 1,5     : Pas d'anticoagulation effective\\*\\
        ///L1\9S231326\INR\\2,0 - 3,0 : Indications g‚n‚rales\\*\\
        ///L1\9S231326\INR\\2,5 - 3,5 : Indications particuliŠres\\*\\
        ///L1\9S231326\INR\\> 5,0     : Surdosage AVK\\*\\
        Assert.assertEquals(res1[0].services[1].label, "INR")
        Assert.assertEquals(res1[0].services[1].content.get("UTF-8")?.measureValue?.min,2.0);
        Assert.assertEquals(res1[0].services[1].content.get("UTF-8")?.measureValue?.max,3.0);
        Assert.assertEquals(res1[0].services[1].content.get("UTF-8")?.measureValue?.value,1.8);
        Assert.assertEquals(res1[0].services[1].content.get("UTF-8")?.measureValue?.comment,"< 1,5     : Pas d'anticoagulation effective\n2,0 - 3,0 : Indications générales\n2,5 - 3,5 : Indications particulières\n> 5,0     : Surdosage AVK");
        Assert.assertEquals(res1[0].services[1].valueDate,20180327L);
        /// File 1 line 12-13
        ///L1\9S231326\QUICK\Temps de Quick\70-100\%\*\36\
        ///L1\9S231326\QUICK\\13 - 30 : sous AVK\\*\\
        Assert.assertEquals(res1[0].services[2].label, "Temps de Quick")
        Assert.assertEquals(res1[0].services[2].content.get("UTF-8")?.measureValue?.min,70.0);
        Assert.assertEquals(res1[0].services[2].content.get("UTF-8")?.measureValue?.max,100.0);
        Assert.assertEquals(res1[0].services[2].content.get("UTF-8")?.measureValue?.value,36.0);
        Assert.assertEquals(res1[0].services[2].content.get("UTF-8")?.measureValue?.unit,"%");
        Assert.assertEquals(res1[0].services[2].content.get("UTF-8")?.measureValue?.comment,"13 - 30 : sous AVK");
        Assert.assertEquals(res1[0].services[2].valueDate,20180327L);
        /// resultLabResult
        Assert.assertEquals(res1[0].resultLabResult, true);
    }


    @Test
    fun doImport() {


        // First parameter
        val language = "UTF-8";

        // Second parameter
        val doc1 = Document();
        val mappings1 = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/format/logic/impl/19611222006001_MS-339.lab");
        val bytes1 = mappings1.readBytes();
        doc1.setAttachment(bytes1);

        // Third parameter
        val hcpId ="hcpId";

        // Fourth parameter
        val protocolIds = listOf("***");

        // Fifth parameter
        val formIds = listOf("111");

        // Sixth parameter
        val planOfActionId = "planOfActionId";

        // Seventh parameter
        val ctc = Contact();

        // Eighth parameter
        val enckeys = null;

        // Execution
        try {
            val res1 = HealthOneLogicImpl.doImport(language, doc1, hcpId, protocolIds, formIds, planOfActionId, ctc, enckeys);
        } catch( e: NullPointerException){

            HealthOneLogicImpl.setContactLogic(contactLogic);
            val res1 = HealthOneLogicImpl.doImport(language, doc1, hcpId, protocolIds, formIds, planOfActionId, ctc, enckeys);
        }

        HealthOneLogicImpl.setContactLogic(contactLogic);
        val res1 = HealthOneLogicImpl.doImport(language, doc1, hcpId, protocolIds, formIds, planOfActionId, ctc, enckeys);

        // Test
        Assert.assertEquals(res1.subContacts.firstOrNull()?.descr, "CHR HAUTE SENNE");
        Assert.assertEquals(res1.subContacts.firstOrNull()?.responsible, "hcpId");
        Assert.assertEquals(res1.subContacts.firstOrNull()?.protocol, "1903-19339");
        Assert.assertEquals(res1.subContacts.firstOrNull()?.planOfActionId, "planOfActionId");
        //Assert.assertEquals(res1.subContacts.firstOrNull()?.status, "");
        Assert.assertEquals(res1.subContacts.firstOrNull()?.formId, "111");
        Assert.assertEquals(res1.services.elementAtOrNull(26)?.label,"Urée");
        Assert.assertEquals(res1.services.elementAtOrNull(26)?.content?.get("UTF-8")?.measureValue?.min,10.0);
        Assert.assertEquals(res1.services.elementAtOrNull(26)?.content?.get("UTF-8")?.measureValue?.max,50.0);
        Assert.assertEquals(res1.services.elementAtOrNull(26)?.content?.get("UTF-8")?.measureValue?.unit,"mg/dL");
        Assert.assertEquals(res1.services.elementAtOrNull(26)?.content?.get("UTF-8")?.measureValue?.value,41.0);
        /*
			ssc.setStatus((ll.isResultLabResult() ? SubContact.STATUS_LABO_RESULT : SubContact.STATUS_PROTOCOL_RESULT) | SubContact.STATUS_UNREAD | (ll.ril != null && ll.ril.complete ? SubContact.STATUS_COMPLETE : 0));

			ctc.getServices().addAll(ll.getServices());
			ctc.getSubContacts().add(ssc);*/
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
        val res1 = HealthOneLogicImpl.canHandle(doc1,enckeys);
        val res2 = HealthOneLogicImpl.canHandle(doc2,enckeys);

        // Test
        Assert.assertEquals(res1, true); //File 1
        Assert.assertEquals(res2, false); //File 2

    }


}


