package org.taktik.icure.be.ehealth.logic.kmehr.smf.impl.v2_3g

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.dto.mapping.ImportMapping
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.User
import org.taktik.icure.logic.*


class SoftwareMedicalFileImportTest {
    val contactLogic = mock(ContactLogic::class.java)
    val patientLogic = mock(PatientLogic::class.java)
    val healthElementLogic = mock(HealthElementLogic::class.java)
    val healthcarePartyLogic = mock(HealthcarePartyLogic::class.java)
    val formTemplateLogic = mock(FormTemplateLogic::class.java)
    val insuranceLogic = mock(InsuranceLogic::class.java)

    val documentLogic = mock(DocumentLogic::class.java)
    val formLogic = mock(FormLogic::class.java)
    val uuidGenerator = UUIDGenerator()
    val mapper = ObjectMapper()

    @Before
    fun setUp() {
        `when`(healthcarePartyLogic.createHealthcareParty(Matchers.any(HealthcareParty::class.java)))
            .thenAnswer { it.getArgumentAt(0, HealthcareParty::class.java) }

        `when`(patientLogic.createPatient(Matchers.any(Patient::class.java)))
            .thenAnswer { it.getArgumentAt(0, Patient::class.java) }
    }

    @Test
    fun importSMF() {
        val mappings = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/ehealth/logic/kmehr/smf/impl/smf.labels.json")
            .readBytes(10000).toString(Charsets.UTF_8)
        val res = SoftwareMedicalFileImport(patientLogic, healthcarePartyLogic, healthElementLogic, contactLogic, documentLogic, formLogic, formTemplateLogic, insuranceLogic, uuidGenerator)
            .importSMF(
                this.javaClass.getResourceAsStream("Test.xml"),
                User().apply {
                    id = uuidGenerator.newGUID().toString();
                    healthcarePartyId = uuidGenerator.newGUID().toString()
                },
                "fr",
                mapper.readValue(mappings, object : TypeReference<Map<String, List<ImportMapping>>>() {})
                      )
        Assert.assertNotNull("Patient must be assigned", res.firstOrNull()?.patient)
    }
}
