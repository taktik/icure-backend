package org.taktik.icure.be.ehealth.logic.kmehr.smf.impl.v2_3g

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.*
import org.mockito.Matchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.Utils.Companion.makeXGC
import org.taktik.icure.be.ehealth.logic.kmehr.v20131001.KmehrExport
import org.taktik.icure.be.ehealth.logic.kmehr.v20131001.KmehrExport.Config
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.dto.mapping.ImportMapping
import org.taktik.icure.dto.result.ImportResult
import org.taktik.icure.entities.*
import org.taktik.icure.entities.base.Code
import org.taktik.icure.logic.*
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.time.Instant

class SoftwareMedicalFileImportExportTest {
    val contactLogic = mock(ContactLogic::class.java)
    val patientLogic = mock(PatientLogic::class.java)
    val healthElementLogic = mock(HealthElementLogic::class.java)
    //val healthcarePartyLogic = mock(HealthcarePartyLogic::class.java)
    //val userLogic = mock(UserLogic::class.java)
    val formTemplateLogic = mock(FormTemplateLogic::class.java)
    val insuranceLogic = mock(InsuranceLogic::class.java)
    val codeLogic = mock(CodeLogic::class.java)

    val documentLogic = mock(DocumentLogic::class.java)
    val formLogic = mock(FormLogic::class.java)
    val uuidGenerator = UUIDGenerator()
    val mapper = ObjectMapper()

    @InjectMocks
    val softwareMedicalFileExport : SoftwareMedicalFileExport = SoftwareMedicalFileExport()

    @Mock
    val userLogic = mock(UserLogic::class.java)

    @Mock
    val healthcarePartyLogic = mock(HealthcarePartyLogic::class.java)

    var contacts : MutableList<Contact> = mutableListOf()
    var hes : MutableList<HealthElement> = mutableListOf()

    val mappings = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/ehealth/logic/kmehr/smf/impl/smf.labels.json")
            .readBytes(10000).toString(Charsets.UTF_8)

    val testUser : User = User().apply {
        id = uuidGenerator.newGUID().toString();
        healthcarePartyId = uuidGenerator.newGUID().toString()
    }

    val testHcp : HealthcareParty = HealthcareParty().apply {
        id = uuidGenerator.newGUID().toString()
    }

    val config = KmehrExport.Config(_kmehrId = System.currentTimeMillis().toString(),
            date = makeXGC(Instant.now().toEpochMilli())!!,
            time = makeXGC(Instant.now().toEpochMilli(), true)!!,
            soft = KmehrExport.Config.Software(name = "iCure", version = "4.0.0"),
            clinicalSummaryType = "TODO",
            defaultLanguage = "en",
            exportAsPMF = false // no versioning in PMF
    )

    @Before
    fun setUp() {


        MockitoAnnotations.initMocks(this);

        // not used
        `when`(userLogic.getUser(any(String::class.java)))
                .thenAnswer {
                    testUser
                }

        `when`(healthcarePartyLogic.createHealthcareParty(any(HealthcareParty::class.java)))
                .thenAnswer { it.getArgumentAt(0, HealthcareParty::class.java) }

        `when`(healthcarePartyLogic.getHealthcareParty(any(String::class.java)))
                .thenAnswer { testHcp }

        `when`(patientLogic.createPatient(any(Patient::class.java)))
                .thenAnswer { it.getArgumentAt(0, Patient::class.java) }

        `when`(healthElementLogic.createHealthElement(any(HealthElement::class.java)))
                .thenAnswer {
                    val item = it.getArgumentAt(0, HealthElement::class.java)
                    hes.add(item)
                    item
                }


        `when`(contactLogic.createContact(any(Contact::class.java)))
                .thenAnswer {
                    val item = it.getArgumentAt(0, Contact::class.java)
                    contacts.add(item)
                    item
                }

        `when`(contactLogic.findByHCPartyPatient(anyString(), anyObject()))
                .thenAnswer { contacts }

        `when`(healthElementLogic.findByHCPartySecretPatientKeys(anyString(), anyObject()))
                .thenAnswer { hes }


        `when`(codeLogic.isValid(anyObject(), anyObject()))
                .thenAnswer { true }
    }

    fun clearDb() {
        contacts = mutableListOf()
        hes = mutableListOf()
    }

    @Test
    fun heHistory() {
        val res = SoftwareMedicalFileImport(patientLogic, healthcarePartyLogic, healthElementLogic, contactLogic, documentLogic, formLogic, formTemplateLogic, insuranceLogic, uuidGenerator)
                .importSMF(
                        this.javaClass.getResourceAsStream("he.history.xml"),
                        testUser,
                        "fr",
                        mapper.readValue(mappings, object : TypeReference<Map<String, List<ImportMapping>>>() {})
                )
        val res0 : ImportResult? = res.firstOrNull()
        Assert.assertNotNull("Patient must be assigned", res0?.patient)
        Assert.assertEquals("There is two HE", 2, res0?.hes?.size)
        Assert.assertTrue("Hes are two versions of the same He", res0!!.hes[0].healthElementId == res0!!.hes[1].healthElementId)

        var out : OutputStream = FileOutputStream("out.xml")
        softwareMedicalFileExport.exportSMF(out, res0.patient!!, emptyList(), testHcp, "fr", null, null, config)
        out.close()
        clearDb()

        var reimportedRes = SoftwareMedicalFileImport(patientLogic, healthcarePartyLogic, healthElementLogic, contactLogic, documentLogic, formLogic, formTemplateLogic, insuranceLogic, uuidGenerator)
                .importSMF(
                        FileInputStream("out.xml"),
                        testUser,
                        "fr",
                        mapper.readValue(mappings, object : TypeReference<Map<String, List<ImportMapping>>>() {})
                )
        var reimportedRes0 : ImportResult? = reimportedRes.firstOrNull()
        Assert.assertNotNull("Patient must be assigned", reimportedRes0?.patient)
        Assert.assertEquals("There is two HE", 2, reimportedRes0?.hes?.size)
        Assert.assertTrue("Hes are two versions of the same He", reimportedRes0!!.hes[0].healthElementId == reimportedRes0!!.hes[1].healthElementId)

    }

    @Test
    fun contactCountStability() {
        val res = SoftwareMedicalFileImport(patientLogic, healthcarePartyLogic, healthElementLogic, contactLogic, documentLogic, formLogic, formTemplateLogic, insuranceLogic, uuidGenerator)
                .importSMF(
                        this.javaClass.getResourceAsStream("he.history.xml"),
                        testUser,
                        "fr",
                        mapper.readValue(mappings, object : TypeReference<Map<String, List<ImportMapping>>>() {})
                )
        val res0 : ImportResult? = res.firstOrNull()
        Assert.assertNotNull("Patient must be assigned", res0?.patient)
        Assert.assertEquals("There is two HE", 2, res0?.hes?.size)
        Assert.assertEquals("There is two contacts + clinicalsummary", 3, res0?.ctcs?.size)
        Assert.assertTrue("Hes are two versions of the same He", res0!!.hes[0].healthElementId == res0!!.hes[1].healthElementId)

        var out : OutputStream = FileOutputStream("out.xml")
        softwareMedicalFileExport.exportSMF(out, res0.patient!!, emptyList(), testHcp, "fr", null, null, config)
        out.close()
        clearDb()

        var reimportedRes = SoftwareMedicalFileImport(patientLogic, healthcarePartyLogic, healthElementLogic, contactLogic, documentLogic, formLogic, formTemplateLogic, insuranceLogic, uuidGenerator)
                .importSMF(
                        FileInputStream("out.xml"),
                        testUser,
                        "fr",
                        mapper.readValue(mappings, object : TypeReference<Map<String, List<ImportMapping>>>() {})
                )
        var reimportedRes0 : ImportResult? = reimportedRes.firstOrNull()
        Assert.assertNotNull("Patient must be assigned", reimportedRes0?.patient)
        Assert.assertEquals("There is two HE", 2, reimportedRes0?.hes?.size)
        Assert.assertEquals("There is two contacts + clinicalsummary", 3, reimportedRes0?.ctcs?.size)
        println("There is two contacts + clinicalsummary : ${reimportedRes0?.ctcs?.size}")
        Assert.assertTrue("Hes are two versions of the same He", reimportedRes0!!.hes[0].healthElementId == reimportedRes0!!.hes[1].healthElementId)
        contacts.addAll(reimportedRes0!!.ctcs)

        // test if more contacts are created
        out = FileOutputStream("out.xml")
        softwareMedicalFileExport.exportSMF(out, res0.patient!!, emptyList(), testHcp, "fr", null, null, config)
        out.close()
        clearDb()

        reimportedRes = SoftwareMedicalFileImport(patientLogic, healthcarePartyLogic, healthElementLogic, contactLogic, documentLogic, formLogic, formTemplateLogic, insuranceLogic, uuidGenerator)
                .importSMF(
                        FileInputStream("out.xml"),
                        testUser,
                        "fr",
                        mapper.readValue(mappings, object : TypeReference<Map<String, List<ImportMapping>>>() {})
                )
        reimportedRes0 = reimportedRes.firstOrNull()
        Assert.assertNotNull("Patient must be assigned", reimportedRes0?.patient)
        Assert.assertEquals("There is two HE", 2, reimportedRes0?.hes?.size)
        Assert.assertEquals("There is two contacts + clinicalsummary", 3, reimportedRes0?.ctcs?.size)
        Assert.assertTrue("Hes are two versions of the same He", reimportedRes0!!.hes[0].healthElementId == reimportedRes0!!.hes[1].healthElementId)

    }

    @Test
    fun medicationHistory() {
        val res = SoftwareMedicalFileImport(patientLogic, healthcarePartyLogic, healthElementLogic, contactLogic, documentLogic, formLogic, formTemplateLogic, insuranceLogic, uuidGenerator)
                .importSMF(
                        this.javaClass.getResourceAsStream("medication.history.xml"),
                        testUser,
                        "fr",
                        mapper.readValue(mappings, object : TypeReference<Map<String, List<ImportMapping>>>() {})
                )
        val res0 : ImportResult? = res.firstOrNull()
        mutableSetOf("bla", "rah").filter{ it == "bla"}
        Assert.assertNotNull("Patient must be assigned", res0?.patient)
        val meds =res0?.ctcs?.map { c ->
            c.services.filter {
                it.tags.find{t -> t.type == "CD-ITEM" && t.code == "medication"} != null
            }
        }?.flatten()
        Assert.assertEquals("There is two medication", 2, meds?.size)
        Assert.assertEquals("medications are two version of the same medication", meds!![0].id, meds[1].id)


    }


}


// workarounds to make Mockito works with kotlin
// see https://stackoverflow.com/questions/30305217/is-it-possible-to-use-mockito-in-kotlin

private fun <T> anyObject(): T {
    return Mockito.anyObject<T>()
}

private fun <T> any(type: Class<T>): T = Matchers.any<T>(type)

