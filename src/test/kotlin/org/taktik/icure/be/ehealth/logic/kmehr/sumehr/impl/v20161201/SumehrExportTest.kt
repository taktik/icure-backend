package org.taktik.icure.be.ehealth.logic.kmehr.sumehr.impl.v20161201

import ma.glasnost.orika.MapperFacade
import org.apache.commons.codec.digest.DigestUtils
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers.any
import org.mockito.Matchers.eq
import org.mockito.Mockito
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.Utils.makeXGC
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENTschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.dt.v1.TextType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.*
import org.taktik.icure.be.ehealth.dto.kmehr.v20170901.Utils
import org.taktik.icure.be.ehealth.logic.kmehr.Config
import org.taktik.icure.be.ehealth.logic.kmehr.v20161201.KmehrExport
import org.taktik.icure.entities.*
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.base.ICureDocument
import org.taktik.icure.entities.embed.*
import org.taktik.icure.logic.impl.ContactLogicImpl
import org.taktik.icure.logic.impl.HealthElementLogicImpl
import org.taktik.icure.logic.impl.HealthcarePartyLogicImpl
import org.taktik.icure.logic.impl.PatientLogicImpl
import org.taktik.icure.services.external.api.AsyncDecrypt
import org.taktik.icure.services.external.rest.v1.dto.CodeDto
import org.taktik.icure.services.external.rest.v1.dto.HealthElementDto
import org.taktik.icure.services.external.rest.v1.dto.embed.ContentDto
import org.taktik.icure.services.external.rest.v1.dto.embed.ServiceDto
import org.taktik.icure.utils.FuzzyValues
import java.io.File
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime.now
import java.time.temporal.ChronoUnit
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class SumehrExportTest {
    private val today = FuzzyValues.getFuzzyDate(LocalDateTime.now(), ChronoUnit.SECONDS)
    private val yesterday = FuzzyValues.getFuzzyDate(LocalDateTime.now().minusDays(1), ChronoUnit.SECONDS)
    private val oneWeekAgo = FuzzyValues.getFuzzyDate(LocalDateTime.now().minusWeeks(1), ChronoUnit.SECONDS)
    private val oneMonthAgo = FuzzyValues.getFuzzyDate(LocalDateTime.now().minusMonths(1), ChronoUnit.SECONDS)

    //The method tested needs a SumehrExport Class to run
    private val sumehrExport = SumehrExport()

    private val decryptor = Mockito.mock(AsyncDecrypt::class.java)
    private val mapper = Mockito.mock(MapperFacade::class.java)
    private val config = Config(_kmehrId = System.currentTimeMillis().toString(),
            date = makeXGC(Instant.now().toEpochMilli())!!,
            time = makeXGC(Instant.now().toEpochMilli(), true)!!,
            soft = Config.Software(name = "iCure", version = sumehrExport.ICUREVERSION),
            clinicalSummaryType = "",
            defaultLanguage = "en"
    )

    private val contactLogic = Mockito.mock(ContactLogicImpl::class.java)
    private val healthElementLogic = Mockito.mock(HealthElementLogicImpl::class.java)
    private val healthcarePartyLogic = Mockito.mock(HealthcarePartyLogicImpl::class.java)
    private val patientLogic = Mockito.mock(PatientLogicImpl::class.java)

    private val validTags = setOf(CodeStub().apply { type = "CD-LIFECYCLE"; code = "active" }, CodeStub().apply { type = "CD-TESTINGITEM"; code = "inactive" })
    private val inactiveTags = setOf(CodeStub().apply { type = "CD-LIFECYCLE"; code = "inactive" })
    private val emptyTags = emptySet<CodeStub>()
    private val secretTags = setOf(CodeStub().apply { type = "org.taktik.icure.entities.embed.Confidentiality"; code = "secret" })
    private val emptyTagsDto = emptySet<CodeDto>()
    private val secretTagsDto = setOf(CodeDto().apply { type = "org.taktik.icure.entities.embed.Confidentiality"; code = "secret" })

    private val medication = Medication().apply { medicinalProduct = Medicinalproduct().apply { intendedname = "medicationName" } }

    private val validContent = mapOf(Pair("valid", Content().apply { booleanValue = true }), Pair("medication", Content().apply { medicationValue = medication }))
    private val validContentDto = mapOf(Pair("valid", ContentDto().apply { booleanValue = true }))
    private val emptyContent = mapOf(Pair("empty", Content()))
    private val stringValueContent = mapOf(Pair("stringValue", Content().apply { stringValue = "stringValue" }))
    private val medicationValueContent = mapOf(Pair("medicationValue", Content().apply { medicationValue = medication }))
    private val doubleContent = mapOf(Pair("stringValue", Content().apply { stringValue = "stringValue" }), Pair("medicationValue", Content().apply { medicationValue = medication }))

    private val patientwillCodes = setOf(CodeStub("CD-PATIENTWILL", "organdonationconsent", "15.7"))
    private val drugsCodes = setOf(CodeStub("CD-DRUG-CNK", "3434784", "15.7"))
    private val drugsCodesDto = setOf(CodeDto("CD-DRUG-CNK", "3434784", "15.7"))
    private val vaccineCodes = setOf(CodeStub("CD-VACCINEINDICATION", "maskedfromsummary", "15.7"))
    private val autonomyCode = CodeStub("CD-AUTONOMY", "CD-ITEM", "1")
    private val icpcCode = CodeStub("ICPC", "CD-VACCINE", "1")
    private val atcCode = CodeStub("CD-ATC", "CD-MEDICATION", "2")
    private val beThesaurusCode = CodeStub("BE-THESAURUS", "CD-MEDICATION", "3.1")
    private val notATypeCode = CodeStub("NOTATYPE", "CD-MEDICATION", "3.1")
    private val codes = setOf(atcCode, beThesaurusCode, notATypeCode)


    private val medicationLabel = "medication"
    private val treatmentLabel = "treatment"
    private val vaccineLabel = "vaccine"

    private val validService = Service().apply { this.id = "1"; this.endOfLife = null; this.status = 1; this.tags = validTags; this.codes = vaccineCodes; this.label = medicationLabel; this.content = validContent; this.comment = "comment"; this.openingDate = oneWeekAgo; this.closingDate = today }
    private val encryptedService = Service().apply { this.id = "2"; this.endOfLife = null; this.status = 2; this.tags = secretTags; this.codes = drugsCodes; this.label = medicationLabel; this.content = emptyContent; this.encryptedContent = "validContent"; this.openingDate = oneWeekAgo }
    private val decryptedServiceDto = ServiceDto().apply { this.id = "2"; this.endOfLife = null; this.status = 2; this.tags = secretTagsDto; this.codes = drugsCodesDto; this.label = medicationLabel; this.content = validContentDto; this.openingDate = oneWeekAgo }
    private val decryptedService = Service().apply { this.id = "2"; this.endOfLife = null; this.status = 2; this.tags = secretTags; this.codes = drugsCodes; this.label = medicationLabel; this.content = validContent; this.openingDate = oneWeekAgo }
    private val lifeEndedService = Service().apply { this.id = "3"; this.endOfLife = Long.MAX_VALUE; this.status = 1; this.tags = validTags; this.codes = vaccineCodes; this.content = validContent; this.openingDate = oneWeekAgo }
    private val wrongStatusService = Service().apply { this.id = "4"; this.endOfLife = null; this.status = 3; this.tags = validTags; this.content = validContent; this.openingDate = oneWeekAgo }
    private val inactiveService = Service().apply { this.id = "5"; this.endOfLife = null; this.status = 2; this.tags = inactiveTags; this.content = validContent; this.openingDate = oneWeekAgo }
    private val emptyService = Service().apply { this.id = "6"; this.endOfLife = null; this.status = 2; this.tags = validTags; this.content = emptyContent; this.openingDate = oneWeekAgo }
    private val oldService = Service().apply { this.id = "7"; this.endOfLife = null; this.status = 2; this.tags = validTags; this.codes = vaccineCodes; this.content = validContent; this.openingDate = oneMonthAgo }
    private val closedService = Service().apply { this.id = "8"; this.endOfLife = null; this.status = 2; this.tags = validTags; this.content = validContent; this.openingDate = oneWeekAgo; this.closingDate = yesterday }
    private val medicationService = Service().apply { this.id = "medication"; this.endOfLife = null; this.status = 0; this.tags = validTags; this.label = medicationLabel; this.content = validContent; this.comment = "comment"; this.openingDate = oneWeekAgo; this.closingDate = today }
    private val treatmentService = Service().apply { this.id = "treatment"; this.endOfLife = null; this.status = 0; this.tags = validTags; this.label = treatmentLabel; this.content = validContent; this.comment = "comment"; this.openingDate = oneWeekAgo; this.closingDate = today }
    private val vaccineService = Service().apply { this.id = "vaccine"; this.endOfLife = null; this.status = 1; this.tags = validTags; this.codes = vaccineCodes; this.label = vaccineLabel; this.content = validContent; this.comment = "comment"; this.openingDate = oneWeekAgo; this.closingDate = today }

    private val services = mutableListOf<List<Service>>()

    private val patient = Patient().apply { this.id = "0dce1288"; this.partnerships = listOf(Partnership().apply { partnerId = "2ed64d50"; otherToMeRelationshipDescription = "father" }, Partnership().apply { partnerId = "excluded"; otherToMeRelationshipDescription = "uncle" }) }
    private val patientContact = Patient().apply { this.id = "2ed64d50"; this.partnerships = emptyList<Partnership>() }
    private val contactPatient = Patient().apply { this.id = "ad977492"; this.partnerships = listOf(Partnership().apply { partnerId = "0dce1288"; otherToMeRelationshipDescription = "brother" }) }
    private val unknownPatient = Patient().apply { this.id = "f9512b4f"; this.partnerships = emptyList<Partnership>() }
    private val excludedPatient = Patient().apply { this.id = "excluded"; this.partnerships = emptyList<Partnership>() }
    private val patients = mutableListOf<Patient>()

    private val emptyHealthElement = HealthElement()
    private val validHealthElementWithEmptyEncryptedSelf = HealthElement().apply {
        this.tags.add(CodeStub("CD-ITEM", "familyrisk", "1.3"))
        this.codes.add(CodeStub("ICPC", "CD-VACCINE", "11.65"))
        this.status = 0
        this.closingDate = null
        this.descr = "Notnull"
    }
    private val validHealthElement = HealthElement().apply {
        this.tags.add(CodeStub("CD-ITEM", "familyrisk", "1.3"))
        this.codes.add(CodeStub("ICPC", "CD-VACCINE", "11.65"))
        this.encryptedSelf = "encryptionKey"
        this.status = 0
        this.closingDate = null
        this.descr = "Notnull"
    }
    private val listOfHealthElement = mutableListOf(validHealthElementWithEmptyEncryptedSelf, validHealthElement)

    private val healthcareParties = mutableListOf(HealthcareParty())

    private var callNumber = 0
    private fun resetServices() {
        services.clear()
        callNumber = 0
    }

    @Before
    fun setUp() {
        Mockito.`when`(contactLogic.modifyContact(any(Contact::class.java))).thenAnswer { it.getArgumentAt(0, Contact::class.java) }

        Mockito.`when`(contactLogic.getServices(any())).thenAnswer {
            if (services.isEmpty())
                emptyList()
            else
                services[callNumber++ % services.size]
        }

        Mockito.`when`(decryptor.decrypt<ServiceDto>(any(), any())).thenAnswer {
            object : Future<List<ServiceDto>> {
                override fun isDone(): Boolean = true
                override fun cancel(mayInterruptIfRunning: Boolean): Boolean = false
                override fun isCancelled(): Boolean = false
                override fun get(): List<ServiceDto> = listOf(decryptedServiceDto)
                override fun get(timeout: Long, unit: TimeUnit): List<ServiceDto> = listOf(decryptedServiceDto)
            }
        }

        Mockito.`when`(decryptor.decrypt<HealthElementDto>(any(), any()))
                .thenAnswer {
                    object : Future<List<HealthElementDto>> {
                        override fun isDone(): Boolean = true
                        override fun cancel(mayInterruptIfRunning: Boolean): Boolean = false
                        override fun isCancelled(): Boolean = false
                        override fun get(): List<HealthElementDto> = it.getArgumentAt(0, ArrayList::class.java) as ArrayList<HealthElementDto>
                        override fun get(timeout: Long, unit: TimeUnit): List<HealthElementDto> = it.getArgumentAt(0, ArrayList::class.java) as ArrayList<HealthElementDto>
                    }
                }

        Mockito.`when`(healthElementLogic.findLatestByHCPartySecretPatientKeys(any(), any()))
                .thenAnswer { listOfHealthElement }

        Mockito.`when`(healthcarePartyLogic.getHealthcareParty(any()))
                .thenAnswer { HealthcareParty() }

        Mockito.`when`(healthcarePartyLogic.getHealthcareParties(any()))
                .thenAnswer { healthcareParties.filter { hcp -> (it.getArgumentAt(0, List::class.java) as List<String>).contains(hcp.id) } }

        Mockito.`when`(mapper.map<Service, ServiceDto>(any(), eq(ServiceDto::class.java)))
                .thenAnswer { decryptedServiceDto }

        Mockito.`when`(mapper.map<ServiceDto, Service>(any(), eq(Service::class.java)))
                .thenAnswer { decryptedService }

        Mockito.`when`(mapper.map<HealthElement, HealthElementDto>(any(), eq(HealthElementDto::class.java))).thenAnswer {
            HealthElementDto().apply {
                healthElementId = (it.getArgumentAt(0, HealthElement::class.java) as HealthElement).healthElementId
                descr = (it.getArgumentAt(0, HealthElement::class.java) as HealthElement).descr
                encryptedSelf = (it.getArgumentAt(0, HealthElement::class.java) as HealthElement).encryptedSelf
                status = (it.getArgumentAt(0, HealthElement::class.java) as HealthElement).status
                closingDate = (it.getArgumentAt(0, HealthElement::class.java) as HealthElement).closingDate
                it.getArgumentAt(0, HealthElement::class.java).tags.forEach { c -> tags.add(CodeDto(c.type, c.code)); }
                it.getArgumentAt(0, HealthElement::class.java).codes.forEach { c -> codes.add(CodeDto(c.type, c.code)); }
            }
        }

        Mockito.`when`(mapper.map<HealthElementDto, HealthElement>(any(), eq(HealthElement::class.java))).thenAnswer {
            HealthElement().apply {
                healthElementId = (it.getArgumentAt(0, HealthElementDto::class.java) as HealthElementDto).healthElementId
                descr = (it.getArgumentAt(0, HealthElementDto::class.java) as HealthElementDto).descr
                encryptedSelf = (it.getArgumentAt(0, HealthElementDto::class.java) as HealthElementDto).encryptedSelf
                status = (it.getArgumentAt(0, HealthElementDto::class.java) as HealthElementDto).status
                closingDate = (it.getArgumentAt(0, HealthElementDto::class.java) as HealthElementDto).closingDate
                it.getArgumentAt(0, HealthElementDto::class.java).tags.forEach { c -> tags.add(CodeStub(c.type, c.code, c.version)); }
                it.getArgumentAt(0, HealthElementDto::class.java).codes.forEach { c -> codes.add(CodeStub(c.type, c.code, c.version)); }
            }
        }

        Mockito.`when`(patientLogic.getPatients(any())).thenAnswer {
            val arg = it.getArgumentAt(0, ArrayList::class.java) as ArrayList<String>
            patients.filter { patient ->
                arg.contains(patient.id)
            }
        }
    }

    @Test
    fun getMd5() {
        //Arrange
        sumehrExport.contactLogic = this.contactLogic
        sumehrExport.mapper = this.mapper
        sumehrExport.healthElementLogic = this.healthElementLogic
        this.resetServices()
        this.services.add(listOf(validService))
        this.listOfHealthElement.clear()
        val hEle = validHealthElement
        hEle.modified = 1L
        this.listOfHealthElement.addAll(listOf(hEle))
        /// First parameter
        val hcPartyId = "1"

        /// Second parameter
        val sfks = listOf("")

        /// Third parameter
        val excludedIds = listOf("")

        //Execution
        val md5 = sumehrExport.getMd5(hcPartyId, patient, sfks, excludedIds, false)

        //Tests
        assertNotNull(md5)
        assertFalse(md5.isBlank())
        assertEquals(md5, DigestUtils.md5Hex(hEle.modified.toString() + "," + "116eec6358284f12a6a05ff491cf65a6" + "," + "null" + "," + "null" + "," + "null" + "," + "null"))
    }

    @Test
    fun createSumehr() {
        // Arrange
        /// First parameter
        val path1 = "/tmp/outCreateSumehr1.xml"
        val file1 = File(path1)
        val os1 = file1.outputStream()

        /// Second parameter
        val pat = Patient().apply {
            id = "idPatient"
            addresses = listOf(Address().apply {
                street = "streetPatient"
                houseNumber = "1D"
                postalCode = "1050"
                city = "Ixelles"
            })
        }

        /// Third parameter
        val sfks = listOf("sfks")

        /// Fourth parameter
        val sender = HealthcareParty().apply {
            nihii = "nihiiSender"
            id = "idSender"
            ssin = "ssinSender"
            specialityCodes = mutableListOf(CodeStub("type", "code", "version"))
            firstName = "firstNameSender"
            lastName = "lastNameSender"
            name = "nameSender"
            addresses = listOf(Address().apply {
                street = "streetSender"
                houseNumber = "3A"
                postalCode = "1000"
                city = "Bruxelles"
            })
        }

        /// Fifth parameter
        val recipient = HealthcareParty()

        /// Sixth parameter
        val language = "language"

        /// Seventh parameter
        val comment = "comment"

        /// Eighth parameter
        val excludedIds = listOf("excludedId")

        // Execution
        sumehrExport.createSumehr(os1, pat, sfks, sender, recipient, language, comment, excludedIds, false, decryptor, null, null,
                Config(_kmehrId = System.currentTimeMillis().toString(),
                date = makeXGC(Instant.now().toEpochMilli())!!,
                time = Utils.makeXGC(Instant.now().toEpochMilli(), true)!!,
                soft = Config.Software(name = "iCure", version = "1.0"),
                clinicalSummaryType = "",
                defaultLanguage = "en"
        ))

        // Tests
        assertNotNull(file1)
        val mappings1 = file1.inputStream()
        val bufferedReader1 = mappings1.bufferedReader(Charset.forName("cp1252"))
        val file1Line1 = bufferedReader1.readLine()
        assertTrue(file1Line1.startsWith("<?xml"))
    }

    @Test
    fun fillPatientFolder() {
        // Arrange
        val folder = FolderType()
        val sfks = listOf("")
        val sender = HealthcareParty().apply { id = "48cf7938" }
        val excludedIds = emptyList<String>()
        sumehrExport.contactLogic = this.contactLogic
        sumehrExport.mapper = this.mapper
        this.resetServices()
        this.services.add(listOf(Service().apply { id = "adr"; endOfLife = null; status = 0; tags = validTags; label = medicationLabel; content = validContent; openingDate = oneWeekAgo; closingDate = today }))
        this.services.add(listOf(Service().apply { id = "allergy"; endOfLife = null; status = 0; tags = validTags; label = medicationLabel; content = validContent; openingDate = oneWeekAgo; closingDate = today }))
        this.services.add(listOf(Service().apply { id = "socialrisk"; endOfLife = null; status = 0; tags = validTags; label = medicationLabel; content = validContent; openingDate = oneWeekAgo; closingDate = today }))
        this.services.add(listOf(Service().apply { id = "risk"; endOfLife = null; status = 0; tags = validTags; label = medicationLabel; content = validContent; openingDate = oneWeekAgo; closingDate = today }))
        this.services.add(listOf(Service().apply { id = "patientwill"; endOfLife = null; status = 0; tags = validTags; codes = patientwillCodes; label = medicationLabel; content = validContent; openingDate = oneWeekAgo; closingDate = today }))
        this.services.add(listOf(Service().apply { id = "vaccine"; endOfLife = null; status = 0; tags = validTags; codes = vaccineCodes; label = medicationLabel; content = validContent; openingDate = oneWeekAgo; closingDate = today }))
        this.services.add(listOf(Service().apply { id = "medication"; endOfLife = null; status = 0; tags = validTags; codes = drugsCodes; label = medicationLabel; content = validContent; openingDate = oneWeekAgo; closingDate = today }))
        this.services.add(listOf(Service().apply { id = "treatment"; endOfLife = null; status = 0; tags = validTags; label = medicationLabel; content = validContent; openingDate = oneWeekAgo; closingDate = today }))
        this.services.add(listOf(Service().apply { id = "healthissue"; endOfLife = null; status = 0; tags = validTags; label = medicationLabel; content = validContent; openingDate = oneWeekAgo; closingDate = today }))
        this.services.add(listOf(Service().apply { id = "healthcareelement"; endOfLife = null; status = 0; tags = validTags; label = medicationLabel; content = validContent; openingDate = oneWeekAgo; closingDate = today }))

        // Execute
        sumehrExport.fillPatientFolder(folder, patient, sfks, sender, "fr", config, "comment", excludedIds, false, decryptor, null, null)

        // Tests
        assertNotNull(folder)
        assertNotNull(folder.transactions)
        assertEquals(1, folder.transactions.size)
        val transaction = folder.transactions[0]
        assertNotNull(transaction)
        assertNotNull(transaction.headingsAndItemsAndTexts)
        assertEquals(2, transaction.headingsAndItemsAndTexts.size)
        transaction.headingsAndItemsAndTexts.forEach { assertNotNull(it) }

        assertTrue(transaction.headingsAndItemsAndTexts[0] is HeadingType)
        val heading = transaction.headingsAndItemsAndTexts[0] as HeadingType
        assertNotNull(heading.headingsAndItemsAndTexts)
        assertEquals(10, heading.headingsAndItemsAndTexts.size)
        val items = heading.headingsAndItemsAndTexts.map {
            assertNotNull(it)
            assertTrue(it is ItemType)
            it as ItemType
        }

        val getId = fun(item: ItemType) = item.ids[1].value
        val getCd = fun(item: ItemType) = item.cds[0].value
        assertEquals("adr", getId(items[0]))
        assertEquals("allergy", getId(items[1]))
        assertEquals("socialrisk", getId(items[2]))
        assertEquals("risk", getId(items[3]))
        assertEquals("patientwill", getId(items[4]))
        assertEquals("vaccine", getId(items[5]))
        assertEquals("medication", getId(items[6]))
        assertEquals("treatment", getId(items[7]))
        assertEquals("healthissue", getId(items[8]))
        assertEquals("healthcareelement", getId(items[9]))
        assertEquals("problem", getCd(items[8]))
        assertEquals("problem", getCd(items[9]))

        assertTrue(transaction.headingsAndItemsAndTexts[1] is TextType)
        val comment = transaction.headingsAndItemsAndTexts[1] as TextType
        assertEquals("comment", comment.value)
    }

    @Test
    fun getAllServices() {
        // Arrange
        val hcPartyId = "1"
        val sfks = listOf("")
        val excludedIds = emptyList<String>()
        sumehrExport.contactLogic = this.contactLogic
        sumehrExport.mapper = this.mapper
        this.resetServices()
        this.services.add(listOf(validService, encryptedService, inactiveService))
        this.services.add(listOf(medicationService, closedService))
        this.services.add(listOf(treatmentService, closedService))
        this.services.add(listOf(vaccineService, inactiveService))

        // Execute
        val services = sumehrExport.getAllServices(setOf(hcPartyId), sfks, excludedIds, false, decryptor)

        // Tests
        assertNotNull(services)
        assertEquals(5, services.size)
        assertTrue(services.none { it.id == inactiveService.id })
        assertTrue(services.none { it.id == closedService.id })
    }

    @Test
    fun getNonPassiveIrrelevantServices() {
        //Arrange
        val hcPartyId = "1"
        val sfks = listOf("")
        val cdItems = listOf("medication")
        val excludedIds = emptyList<String>()
        sumehrExport.contactLogic = this.contactLogic
        sumehrExport.mapper = this.mapper
        this.resetServices()
        this.services.add(listOf(validService, encryptedService, lifeEndedService, wrongStatusService, inactiveService, emptyService, oldService, closedService))

        //Execution
        val services = sumehrExport.getActiveServices(setOf(hcPartyId), sfks, cdItems, excludedIds, false, decryptor, null)

        //Tests
        ///All services
        assertNotNull(services)
        assertEquals(4, services.size)
        assertNotNull(services.firstOrNull())
        assertNotNull(services.lastOrNull())

        ///Normal service
        val service1 = services.elementAt(0)
        assertNull(service1.endOfLife)
        assertEquals(1, service1.status)    // status is irrelevant
        assertTrue(service1.tags.none { it.type == "CD-LIFECYCLE" && it.code == "inactive" })  // service is active
        assertNotNull(service1.content)
        assertNotNull(service1.content.values.firstOrNull()?.booleanValue)  // service content has value

        ///Decrypted service
        val service2 = services.elementAt(1)
        assertNull(service2.endOfLife)
        assertEquals(2, service2.status)    // status is inactive
        assertNotNull(service2.content)
        assertNotNull(service2.content.values.firstOrNull()?.booleanValue)  // service content has value
    }

    @Test
    fun getNonConfidentialItems() {
        //Arrange
        val article = Article().apply {
            this.id = "1"
            this.tags = setOf(
                    CodeStub().apply { this.type = "org.taktik.icure.entities.embed.Confidentiality"; this.code = "notsecret" }
            )
            this.codes = setOf(
                    CodeStub().apply { this.type = "org.taktik.icure.entities.embed.Visibility"; this.code = "visibleinsummary" }
            )
        }

        val message = Message().apply {
            this.id = "2"
            this.tags = setOf(
                    CodeStub().apply { this.type = "org.taktik.icure.entities.embed.Confidentiality"; this.code = "secret" }
            )
            this.codes = setOf(
                    CodeStub().apply { this.type = "org.taktik.icure.entities.embed.Visibility"; this.code = "visibleinsummary" }
            )
        }

        val receipt = Receipt().apply {
            this.id = "3"
            this.tags = setOf(
                    CodeStub().apply { this.type = "org.jdumur.tests.Confidentiality"; this.code = "secret" }
            )
            this.codes = setOf(
                    CodeStub().apply { this.type = "org.taktik.icure.entities.embed.Visibility"; this.code = "maskedfromsummary" }
            )
        }

        val patient = Patient().apply {
            this.id = "4"
            this.tags = setOf(
                    CodeStub().apply { this.type = "org.taktik.icure.entities.embed.Confidentiality"; this.code = "secret" }
            )
            this.codes = setOf(
                    CodeStub().apply { this.type = "org.taktik.icure.entities.embed.Visibility"; this.code = "maskedfromsummary" }
            )
        }

        val contact = Contact().apply {
            this.id = "5"
            this.tags = setOf(
                    CodeStub().apply { this.type = "org.jdumur.tests.Confidentiality"; this.code = "secret" }
            )
            this.codes = setOf(
                    CodeStub().apply { this.type = "org.jdumur.tests.Visibility"; this.code = "maskedfromsummary" }
            )
        }

        val document = Document().apply {
            this.id = "6"
            this.tags = setOf(
                    CodeStub().apply { this.type = "org.taktik.icure.entities.embed.Confidentiality"; this.code = "notsecret" },
                    CodeStub().apply { this.type = "org.jdumur.tests.Confidentiality"; this.code = "secret" }
            )
            this.codes = setOf(
                    CodeStub().apply { this.type = "org.taktik.icure.entities.embed.Visibility"; this.code = "visibleinsummary" },
                    CodeStub().apply { this.type = "org.jdumur.tests.Visibility"; this.code = "maskedfromsummary" }
            )
        }

        val invoice = Invoice().apply {
            this.id = "7"
            this.tags = emptySet<CodeStub>()
            this.codes = emptySet<CodeStub>()
        }

        //Execute
        val items = mutableListOf<ICureDocument>()
        items.addAll(sumehrExport.getNonConfidentialItems(listOf(article, contact, document, invoice)))
        items.addAll(sumehrExport.getNonConfidentialItems(listOf(message, receipt, patient)))

        //Tests
        assertNotNull(items)
        assertEquals(4, items.size)
        assertTrue(items.contains(article))     //
        assertFalse(items.contains(message))    // secret
        assertFalse(items.contains(receipt))    //        masked
        assertFalse(items.contains(patient))    // secret masked
        assertTrue(items.contains(contact))     //
        assertTrue(items.contains(document))    //
        assertTrue(items.contains(invoice))     //
    }

    @Test
    fun hasOmissionOfMedicalDataItem()
    {
        // Arrange
        val transaction1 = TransactionType()
        val transaction2 = TransactionType().apply { sumehrExport.getAssessment(this).headingsAndItemsAndTexts.add(sumehrExport.getOmissionOfMedicalDataWill(1)) }

        // Execution
        val firstIsMarked = sumehrExport.hasOmissionOfMedicalDataItem(transaction1)
        val secondIsMarked = sumehrExport.hasOmissionOfMedicalDataItem(transaction2)

        // Tests
        assertFalse(firstIsMarked)
        assertTrue(secondIsMarked)
    }

    @Test
    fun addOmissionOfMedicalDataItem()
    {
        // Arrange
        val transaction0 = TransactionType()    // no oomd  ->   1 oomd (effective addition)
        val transaction1 = TransactionType()    // no oomd  ->  no oomd (no non-expected addition)
        val transaction2 = TransactionType()    //  1 oomd  ->   1 oomd (no deletion)
                .apply { sumehrExport.getAssessment(this).headingsAndItemsAndTexts.add(sumehrExport.getOmissionOfMedicalDataWill(1)) }
        val transaction3 = TransactionType()    // no oomd  ->   1 oomd (effective conditional addition)
        val transaction4 = TransactionType()    //  1 oomd  ->   1 oomd (no duplicate)
                .apply { sumehrExport.getAssessment(this).headingsAndItemsAndTexts.add(sumehrExport.getOmissionOfMedicalDataWill(1)) }
        val transaction5 = TransactionType()    // no oomd  ->   1 oomd (effective overwritten condition addition)
        val transaction6 = TransactionType()    // no oomd  ->  no oomd (no overwritten condition non-expected addition)

        val filledList = listOf(Service())
        val emptyList = emptyList<Service>()

        fun <T: ICureDocument> predicate(a: List<T>, b: List<T>) = a.size == b.size

        // Execution
        sumehrExport.addOmissionOfMedicalDataItem(transaction0)                 // effective addition
        sumehrExport.addOmissionOfMedicalDataItem(transaction1, filledList, filledList)   // no non-expected addition
        sumehrExport.addOmissionOfMedicalDataItem(transaction2, filledList, filledList)   // no deletion
        sumehrExport.addOmissionOfMedicalDataItem(transaction3, filledList,  emptyList)   // effective conditional addition
        sumehrExport.addOmissionOfMedicalDataItem(transaction4, filledList,  emptyList)   // no duplicate
        sumehrExport.addOmissionOfMedicalDataItem(transaction5, filledList, filledList, ::predicate)    // effective overwritten condition addition
        sumehrExport.addOmissionOfMedicalDataItem(transaction6, filledList,  emptyList, ::predicate)    // no overwritten condition non-expected addition

        // Tests
        assertTrue(sumehrExport.hasOmissionOfMedicalDataItem(transaction0))     // effective addition
        assertFalse(sumehrExport.hasOmissionOfMedicalDataItem(transaction1))    // no non-expected addition
        assertTrue(sumehrExport.hasOmissionOfMedicalDataItem(transaction2))     // no deletion
        assertTrue(sumehrExport.hasOmissionOfMedicalDataItem(transaction3))     // effective conditional addition
        assertTrue(sumehrExport.hasOmissionOfMedicalDataItem(transaction4))     // no duplicate
        assertTrue(sumehrExport.hasOmissionOfMedicalDataItem(transaction5))     // effective overwritten condition addition
        assertFalse(sumehrExport.hasOmissionOfMedicalDataItem(transaction6))    // no overwritten condition non-expected addition
    }

    @Test
    fun getHealthElements() {
        // Arrange
        sumehrExport.contactLogic = this.contactLogic
        sumehrExport.healthElementLogic = this.healthElementLogic
        this.listOfHealthElement.clear()
        val filteredHealthElement1 = HealthElement().apply {
            this.id = "excluded"
            this.descr = "NotINBOX"
            this.status = 0 // active and relevant
        }
        val filteredHealthElement2 = HealthElement().apply {
            this.healthElementId = "Id2"
            this.descr = "INBOX"
        }
        val filteredHealthElement3 = HealthElement().apply {
            this.healthElementId = "Id3"
            this.descr = "NotINBOX"
            this.status = 2 // (active and) irrelevant
            this.closingDate = 1L
        }
        val filteredHealthElement4 = HealthElement().apply {
            this.healthElementId = "Id4"
            this.descr = "NotINBOX"
            this.status = 3 // inactive and irrelevant
        }
        val keptHealthElement1 = HealthElement().apply {
            this.healthElementId = "Id5"
            this.descr = "NotINBOX"
            this.status = 1 // (inactive and) relevant
        }
        val keptHealthElement2 = HealthElement().apply {
            this.healthElementId = "Id6"
            this.descr = "NotINBOX"
            this.status = 2 // (active and) irrelevant
        }

        this.listOfHealthElement.addAll(listOf(filteredHealthElement1, filteredHealthElement2, filteredHealthElement3, filteredHealthElement4, keptHealthElement1, keptHealthElement2))

        /// First parameter
        val hcPartyId = "1"

        /// Second parameter
        val sfks = listOf("")

        /// Third parameter
        val excludedIds = listOf("excluded")

        // Execute
        val res1 = sumehrExport.getHealthElements(setOf(hcPartyId), sfks, excludedIds, false, mutableSetOf())

        // Tests
        val size = healthElementLogic.findLatestByHCPartySecretPatientKeys(hcPartyId, sfks).size
        assertNotNull(res1)
        assertEquals(res1.size, size - 4)
    }

    @Test
    fun getMedications() {
        //Arrange
        val hcPartyId = "1"
        val sfks = listOf("")
        val excludedIds = emptyList<String>()
        sumehrExport.contactLogic = this.contactLogic
        sumehrExport.mapper = this.mapper
        this.resetServices()
        this.services.add(listOf(validService, encryptedService, oldService, closedService, medicationService, treatmentService))

        //Execute
        val medications = sumehrExport.getMedications(setOf(hcPartyId), sfks, excludedIds, false, decryptor, null)

        //Tests
        assertNotNull(medications)
        assertEquals(1, medications.count { m -> m.id.equals("2") })    // no drug duplicate
        assertTrue(medications.all { m -> m.closingDate == null || m.closingDate!!.let { today <= it } })
    }

    @Test
    fun getVaccines() {
        // Arrange
        sumehrExport.contactLogic = this.contactLogic
        sumehrExport.mapper = this.mapper
        this.resetServices()
        this.services.add(listOf(validService, oldService, closedService, vaccineService))

        /// First parameter
        val hcPartyId = "1"

        /// Second parameter
        val sfks = listOf("")

        /// Third parameter
        val excludedIds = emptyList<String>()

        // Execution
        val cdItems = listOf("vaccine")
        val services1 = sumehrExport.getActiveServices(setOf(hcPartyId), sfks, cdItems, excludedIds, false, decryptor, null)
        val services2 = sumehrExport.getVaccines(setOf(hcPartyId), sfks, excludedIds, false, decryptor, null)

        // Tests
        val filteredService1 = services1.filter { it.codes.any { c -> c.type == "CD-VACCINEINDICATION" && c.code?.length ?: 0 > 0 } }
        assertEquals(filteredService1, services2)
    }

    @Test
    fun getAssessment() {
        // Arrange
        val transaction = TransactionType()

        // Execute
        val assessment1 = sumehrExport.getAssessment(transaction)
        val assessment2 = sumehrExport.getAssessment(transaction)

        // Tests
        assertNotNull(assessment1)
        assertNotNull(assessment2)
        assertEquals(assessment1, assessment2)
    }

    @Test
    fun getHistory() {
        // Arrange
        val transaction = TransactionType()

        // Execute
        val history1 = sumehrExport.getHistory(transaction)
        val history2 = sumehrExport.getHistory(transaction)

        // Tests
        assertNotNull(history1)
        assertNotNull(history2)
        assertEquals(history1, history2)
    }

    @Test
    fun addNonPassiveIrrelevantServicesAsCD() {
        // Arrange
        val hcPartyId = "1"
        val sfks = listOf("")
        val transaction = TransactionType()
        val cdItem = "patientwill"
        val type = CDCONTENTschemes.CD_PATIENTWILL
        val values = listOf("euthanasiarequest", "organdonationconsent", "datareuseforclinicalresearchconsent")
        val excludedIds = emptyList<String>()
        sumehrExport.contactLogic = this.contactLogic

        this.resetServices()
        services.add(listOf(
                Service().apply {
                    id = "1"; status = 0; tags = validTags; content = validContent; openingDate = oneWeekAgo; closingDate = today
                    codes = setOf(CodeStub().apply { this.type = type.value(); this.code = "euthanasiarequest" })
                },
                Service().apply {
                    id = "2"; status = 0; tags = validTags; content = validContent; openingDate = oneWeekAgo; closingDate = today
                    codes = emptySet<CodeStub>()
                },
                Service().apply {
                    id = "3"; status = 0; tags = validTags; content = validContent; openingDate = oneWeekAgo; closingDate = today
                    codes = setOf(
                            CodeStub().apply { this.type = type.value(); this.code = "some code" },
                            CodeStub().apply { this.type = "some type"; this.code = "organdonationconsent" }
                    )
                }
        ))
        val language = "fr"

        // Execute
        sumehrExport.addActiveServicesAsCD(setOf(hcPartyId), sfks, transaction, cdItem, type, values, excludedIds, false, decryptor, null, language)

        // Tests
        assertNotNull(transaction)
        val assessment = sumehrExport.getAssessment(transaction)
        assertNotNull(assessment)
        assertEquals(1, assessment.headingsAndItemsAndTexts.size)

        val euthanasia = assessment.headingsAndItemsAndTexts[0] as ItemType
        assertNotNull(euthanasia)
        assertNotNull(euthanasia.contents)
        assertEquals(1, euthanasia.contents.size)
        assertNotNull(euthanasia.contents[0])
        assertNotNull(euthanasia.contents[0].cds)
        assertEquals(1, euthanasia.contents[0].cds.size)
        assertNotNull(euthanasia.contents[0].cds[0])
        assertEquals("euthanasiarequest", euthanasia.contents[0].cds[0].value)
    }

    @Test
    fun createVaccineItem() {
        // Arrange
        /// First parameter
        val content1 = Content().apply {
            booleanValue = true
            binaryValue = "binaryValue".toByteArray()
            documentId = "documentId"
            measureValue = Measure().apply {
                value = 1.0
                min = 1.1
                max = 1.2
                ref = 1.3
                severity = 1
                severityCode = "severityCode"
                unit = "unit"
                unitCodes = setOf(CodeStub("type", "code", "version"))
                comment = "comment"
            }
            numberValue = 1.4
            instantValue = Instant.ofEpochMilli(0L)
            stringValue = "stringValue"
            medicationValue = Medication().apply {
                compoundPrescription = "compoundPrescription"
                substanceProduct = Substanceproduct().apply {
                    intendedname = "intendedname"
                }
                medicinalProduct = Medicinalproduct().apply {
                    intendedname = "intendedname"
                }
            }
        }
        val content2 = Content().apply {
            medicationValue = Medication().apply {
                compoundPrescription = "compoundPrescription"
                substanceProduct = Substanceproduct().apply {
                    intendedname = "intendedname"
                }
                medicinalProduct = Medicinalproduct().apply {
                    intendedname = "intendedname"
                }
            }
        }
        val svc1 = Service()
        svc1.content["1"] = content1
        svc1.tags.add(CodeStub("Type", "Code", "Version"))
        svc1.codes.add(CodeStub("Type", "Code", "Version"))
        val svc2 = Service()
        svc2.content["1"] = content1
        svc2.content["2"] = content2

        /// Second parameter
        val itemIndex = 0
        val language = "fr"

        // Execute
        val res1 = sumehrExport.createVaccineItem(svc1, itemIndex, language)
        val res2 = sumehrExport.createVaccineItem(svc2, itemIndex, language)

        // Tests
        assertNotNull(res1)
        assertEquals(res1?.contents?.size, 1)
        assertNull(res1?.contents?.firstOrNull()?.isBoolean)
        assertNull(res1?.contents?.firstOrNull()?.date)
        assertNull(res1?.contents?.firstOrNull()?.time)
        assertNull(res1?.contents?.firstOrNull()?.decimal)
        assertEquals(res1?.contents?.firstOrNull()?.lnks?.size, 0)
        assertEquals(res1?.contents?.firstOrNull()?.cds?.size, 0)
        assertEquals(res1?.contents?.firstOrNull()?.texts?.size, 0)
        assertNotNull(res2)
        assertEquals(res2?.contents?.size, 2)
    }

    @Test
    fun createItemWithContent() {
        // Arrange
        /// First parameter
        val svc1 = Service()
        val svc2 = Service()
        svc2.status = 4
        val svc3 = Service()
        svc3.status = 0
        svc3.tags.add(CodeStub("CD-LIFECYCLE", "notpresent", "1,2"))
        /// OR First parameter
        val he1 = HealthElement()
        val he2 = HealthElement()
        he2.status = 4
        val he3 = Service()
        he3.status = 0
        he3.tags.add(CodeStub("CD-LIFECYCLE", "notpresent", "1,2"))

        /// Second parameter
        val idx = 0

        /// Third parameter
        val cdItem = "cdItem"

        /// Fourth parameter
        val contents = listOf(ContentType())

        val language = "fr"
        // Execute
        /// createItemWithContent with Service parameter
        val res1S = sumehrExport.createItemWithContent(svc1, idx, cdItem, contents, language = language)
        val res2S = sumehrExport.createItemWithContent(svc2, idx, cdItem, contents, language = language)
        val res3S = sumehrExport.createItemWithContent(svc3, idx, cdItem, contents, language = language)
        /// createItemWithContent with HealthElement parameter
        val res1H = sumehrExport.createItemWithContent(he1, idx, cdItem, contents)
        val res2H = sumehrExport.createItemWithContent(he2, idx, cdItem, contents)
        val res3H = sumehrExport.createItemWithContent(he3, idx, cdItem, contents, language = language)

        // Tests
        assertNotNull(res1S)
        assertNull(res2S)
        assertNull(res3S)
        assertNotNull(res1H)
        assertNull(res2H)
        assertNull(res3H)
    }

    @Test
    fun addContactPeople() {
        // Arrange
        val transaction = TransactionType()
        val excludedIds = listOf("excluded")
        sumehrExport.patientLogic = this.patientLogic
        patients.clear()
        patients.addAll(listOf(patient, patientContact, contactPatient, unknownPatient, excludedPatient))

        // Execute
        sumehrExport.addContactPeople(patient, transaction, config, excludedIds)

        // Tests
        assertNotNull(transaction)
        assertNotNull(transaction.headingsAndItemsAndTexts)
        assertEquals(1, transaction.headingsAndItemsAndTexts.size)
        assertNotNull(transaction.headingsAndItemsAndTexts[0])
        assertTrue(transaction.headingsAndItemsAndTexts[0] is HeadingType)
        val heading = transaction.headingsAndItemsAndTexts[0] as HeadingType
        assertEquals(1, heading.headingsAndItemsAndTexts.size)
        assertNotNull(heading.headingsAndItemsAndTexts[0])
        assertTrue(heading.headingsAndItemsAndTexts[0] is ItemType)
        val item = heading.headingsAndItemsAndTexts[0] as ItemType
        assertNotNull(item.cds)
        assertEquals(2, item.cds.size)
        assertEquals("contactperson", item.cds[0].value)
        assertEquals("father", item.cds[1].value)
        assertNotNull(item.contents)
        assertEquals(1, item.contents.size)
        val content = item.contents[0]
        assertNotNull(content.person)
        assertNotNull(content.person.ids)
        assertEquals(1, content.person.ids.size)
        val id = content.person.ids[0]
        assertNotNull(id)
        assertEquals("2ed64d50", id.value)
    }

    @Test
    fun addPatientHealthcareParties() {
        // Arrange
        sumehrExport.healthcarePartyLogic = this.healthcarePartyLogic
        val healthcareParty1 = HealthcareParty().apply {
            specialityCodes = listOf(CodeStub("Type", "Notpers", "1.0"), CodeStub("Type", "pers", "1.0"))
            name = "healthcareParty1"
        }
        val healthcareParty2 = HealthcareParty().apply {
            id = "healthcareParty2Id"
            specialityCodes = listOf(CodeStub("Type", "autre", "1.0"))
            name = "healthcareParty2"
        }
        val healthcareParty3 = HealthcareParty().apply {
            id = "healthcareParty3Id"
            specialityCodes = listOf(CodeStub("Type", "pers", "1.0"))
            name = "healthcareParty3"
        }
        val healthcareParty4 = HealthcareParty().apply {
            id = "excluded"
            specialityCodes = listOf(CodeStub("Type", "pers", "1.0"))
            name = "healthcareParty4"
        }
        this.healthcareParties.clear()
        this.healthcareParties.addAll(listOf(healthcareParty1, healthcareParty2, healthcareParty3, healthcareParty4))

        /// First parameter
        val pat1 = Patient().apply {
            patientHealthCareParties.add(PatientHealthCareParty().apply {
                healthcarePartyId = null
            })
            patientHealthCareParties.add(PatientHealthCareParty().apply {
                healthcarePartyId = "healthcareParty2Id"
            })
            patientHealthCareParties.add(PatientHealthCareParty().apply {
                healthcarePartyId = "healthcareParty3Id"
            })
            patientHealthCareParties.add(PatientHealthCareParty().apply {
                healthcarePartyId = "excluded"
            })
        }
        val pat1PatientHealthCarePartiesSize = pat1.patientHealthCareParties.size

        /// Second parameter
        val trn1 = ObjectFactory().createTransactionType()

        /// Third parameter
        val config = this.config

        /// Fourth parameter
        val excludedIds = listOf("excluded")

        // Execution
        val itemsSize = if (trn1.headingsAndItemsAndTexts.size == 0) {
            0
        } else {
            (trn1.headingsAndItemsAndTexts[0] as HeadingType).headingsAndItemsAndTexts.size
        }
        sumehrExport.addPatientHealthcareParties(pat1, trn1, config, excludedIds)

        // Tests
        assertEquals(1, trn1.headingsAndItemsAndTexts.size)
        val a1 = trn1.headingsAndItemsAndTexts[0] as HeadingType
        assertEquals(1, a1.headingsAndItemsAndTexts.size)
        val b1 = a1.headingsAndItemsAndTexts[0] as ItemType
        assertEquals(1, b1.ids.size)
        assertEquals((itemsSize + 1).toString(), b1.ids[0].value)
        assertEquals("ID-KMEHR", b1.ids[0].s.value())
        assertEquals("1.0", b1.ids[0].sv)
        assertEquals(1, b1.cds.size)
        assertEquals("CD-ITEM", b1.cds[0].s.value())
        assertEquals("contacthcparty", b1.cds[0].value)
        assertEquals(1, b1.contents.size)
        assertNotNull(b1.contents[0].hcparty)
    }

    @Test
    fun addGmdmanager() {
        // Arrange
        sumehrExport.healthElementLogic = this.healthElementLogic
        sumehrExport.healthcarePartyLogic = this.healthcarePartyLogic

        /// First parameter
        val format = SimpleDateFormat("dd/MM/yyyy")
        val date1 = format.parse("02/01/2000").toInstant()
        val date2 = format.parse("02/01/2001").toInstant()
        val date3 = format.parse("02/01/3000").toInstant()
        val date4 = null
        val period1 = ReferralPeriod(date1, date2)
        val period2 = ReferralPeriod(date1, date3)
        val period3 = ReferralPeriod(date1, date4)
        val pHCP1 = PatientHealthCareParty().apply { referralPeriods.add(period1) }
        val pHCP2 = PatientHealthCareParty().apply { referralPeriods.add(period2) }
        val pHCP3 = PatientHealthCareParty().apply { referralPeriods.add(period3) }
        val pat1 = Patient().apply {
            patientHealthCareParties.add(pHCP1)
            patientHealthCareParties.add(pHCP2)
            patientHealthCareParties.add(pHCP3)
        }
        val pat2 = Patient().apply {
            patientHealthCareParties.add(pHCP1)
            patientHealthCareParties.add(pHCP2)
        }

        /// Second parameter
        val trn1 = ObjectFactory().createTransactionType()
        val trn2 = ObjectFactory().createTransactionType()


        // Execution
        sumehrExport.addGmdmanager(pat1, trn1)
        sumehrExport.addGmdmanager(pat2, trn2)

        // Tests
        assertEquals(trn1.headingsAndItemsAndTexts.size, 1)
        val a1 = trn1.headingsAndItemsAndTexts[0] as HeadingType
        assertEquals(a1.headingsAndItemsAndTexts.size, 1)
        val b1 = a1.headingsAndItemsAndTexts[0] as ItemType
        assertEquals(b1.ids.size, 1)
        assertEquals(b1.ids[0].value, "1")
        assertEquals(b1.ids[0].s.value(), "ID-KMEHR")
        assertEquals(b1.ids[0].sv, "1.0")
        assertEquals(b1.cds.size, 1)
        assertEquals(b1.cds[0].s.value(), "CD-ITEM")
        assertEquals(b1.cds[0].value, "gmdmanager")
        assertEquals(b1.contents.size, 1)
        assertNotNull(b1.contents[0].hcparty)
        assertEquals(trn2.headingsAndItemsAndTexts.size, 0)
    }

    @Test
    fun addMedications() {
        // Arrange
        val hcPartyId = "1"
        val sfks = listOf("")
        val transaction = TransactionType()
        val excludedIds = emptyList<String>()
        sumehrExport.contactLogic = this.contactLogic
        sumehrExport.mapper = this.mapper
        this.resetServices()
        this.services.add(listOf(validService, encryptedService, lifeEndedService, oldService))
        val language = "fr"

        // Execute
        sumehrExport.addMedications(setOf(hcPartyId), sfks, transaction, excludedIds, false, decryptor, null, null, language)

        // Tests
        assertNotNull(transaction)
        assertNotNull(transaction.headingsAndItemsAndTexts)
        assertEquals(1, transaction.headingsAndItemsAndTexts.size)
        val element = transaction.headingsAndItemsAndTexts[0]
        assertNotNull(element)
        assertTrue(element is HeadingType)
        val heading = element as HeadingType
        val items = heading.headingsAndItemsAndTexts
                .filter { it is ItemType }
                .map { it as ItemType }
                .filter { it.contents.all { it.cds.all { it.value != "omissionofmedicaldata" } } }
        assertEquals(3, items.size)
        for (item in items) {
            assertNotNull(item)
            assertNotNull(item.contents)
            assertEquals(3, item.contents.size)
            assertFalse(item.contents.any { it == null })
        }
    }

    @Test
    fun addVaccines() {
        // Arrange
        sumehrExport.healthElementLogic = this.healthElementLogic
        sumehrExport.contactLogic = this.contactLogic
        sumehrExport.mapper = this.mapper
        this.resetServices()
        this.services.add(listOf(validService, lifeEndedService, oldService))

        /// First parameter
        val hcPartyId = ""

        /// Second parameter
        val sfks = listOf("")

        /// Third parameter
        val trn1 = ObjectFactory().createTransactionType()

        /// Fourth parameter
        val excludedIds = listOf("")

        /// Fifth parameter
        val decryptor1 = decryptor
        val language = "fr"

        // Execution
        sumehrExport.addVaccines(setOf(hcPartyId), sfks, trn1, excludedIds, false, decryptor1, null, null, language)
        //sumehrExport.addVaccines(hcPartyId, sfks, trn2, excludedIds, decryptor2)

        // Tests
        assertEquals(trn1.headingsAndItemsAndTexts.size, 1)
        val a1 = trn1.headingsAndItemsAndTexts[0] as HeadingType
        assertEquals(a1.headingsAndItemsAndTexts.size, 2)
    }

    @Test
    fun addHealthCareElements() {
        // Arrange
        sumehrExport.healthElementLogic = this.healthElementLogic
        sumehrExport.mapper = this.mapper

        /// First parameter
        val hcPartyId = ""

        /// Second parameter
        val sfks = listOf("")

        /// Third parameter
        val trn1 = ObjectFactory().createTransactionType()
        val trn2 = ObjectFactory().createTransactionType()

        /// Fourth parameter
        val excludedIds = listOf("")

        /// Fifth parameter
        val decryptor1 = null
        val decryptor2 = decryptor

        // Execution
        sumehrExport.addHealthCareElements(setOf(hcPartyId), sfks, trn1, excludedIds, false, decryptor1, null)
        sumehrExport.addHealthCareElements(setOf(hcPartyId), sfks, trn2, excludedIds, false, decryptor2, null)

        // Tests
        assertNotNull(trn1.headingsAndItemsAndTexts)
        assertEquals(trn1.headingsAndItemsAndTexts.size, 1)
        val a1: HeadingType = trn1.headingsAndItemsAndTexts[0] as HeadingType
        assertEquals(a1.headingsAndItemsAndTexts.size, 2)

        assertNotNull(trn1.headingsAndItemsAndTexts)
        assertEquals(trn1.headingsAndItemsAndTexts.size, 1)
        val a2: HeadingType = trn2.headingsAndItemsAndTexts[0] as HeadingType
        assertEquals(a2.headingsAndItemsAndTexts.size, 2)
    }

    @Test
    fun addHealthCareElement() {
        // Arrange
        /// First parameter
        val trn1 = ObjectFactory().createTransactionType()
        val trn2 = ObjectFactory().createTransactionType()
        val trn3 = ObjectFactory().createTransactionType()
        val trn4 = ObjectFactory().createTransactionType()
        val trn5 = ObjectFactory().createTransactionType()
        val trn6 = ObjectFactory().createTransactionType()

        /// Second parameter
        val eds1 = HealthElement() //closingDate == null
        val eds2 = HealthElement() //closingDate !=null
        val eds3 = HealthElement()
        val eds4 = HealthElement()
        val eds5 = HealthElement()
        val eds6 = HealthElement()
        eds2.closingDate = now().toEpochSecond()
        val tag1 = CodeStub("CD-ITEM", "familyrisk", "1")
        val tag2 = CodeStub("CD-ITEM", "allergy", "1")
        eds1.tags.add(tag1)
        eds2.tags.add(tag2)
        eds3.tags.add(tag1)
        eds4.tags.add(tag2)
        eds5.tags.add(tag2)
        eds6.tags.add(tag2)
        eds1.codes.addAll(setOf(autonomyCode, icpcCode))
        eds2.codes.add(autonomyCode)
        eds3.codes.add(autonomyCode)
        eds4.codes.add(atcCode)
        eds5.codes.add(beThesaurusCode)
        eds6.codes.add(notATypeCode)

        // Execution
        sumehrExport.addHealthCareElement(trn1, eds1)
        sumehrExport.addHealthCareElement(trn2, eds2)
        sumehrExport.addHealthCareElement(trn3, eds3)
        sumehrExport.addHealthCareElement(trn4, eds4)
        sumehrExport.addHealthCareElement(trn5, eds5)
        sumehrExport.addHealthCareElement(trn6, eds6)

        // Tests
        val a1: HeadingType = trn1.headingsAndItemsAndTexts[0] as HeadingType
        val b1: ItemType = a1.headingsAndItemsAndTexts[0] as ItemType
        val c1 = b1.contents[0].cds[1]
        assertEquals(eds1.tags.firstOrNull()?.code, "problem")
        assertEquals(eds1.tags.firstOrNull()?.version, "1.11")
        assertEquals(c1.value, "CD-VACCINE")
        assertEquals(c1.s.value(), "ICPC")
        assertEquals(c1.sv, "1")
        assertEquals(c1.sl, "ICPC")
        assertEquals(c1.dn, "ICPC")

        val a2: HeadingType = trn2.headingsAndItemsAndTexts[0] as HeadingType
        assertEquals(eds2.tags.firstOrNull()?.code, "allergy")
        assertEquals(eds2.tags.firstOrNull()?.version, "1")
        assertEquals(eds2.codes.size, 1)
        assertEquals(a2.headingsAndItemsAndTexts.size, 1)

        val a3: HeadingType = trn3.headingsAndItemsAndTexts[0] as HeadingType
        assertEquals(eds3.tags.firstOrNull()?.code, "problem")
        assertEquals(eds3.tags.firstOrNull()?.version, "1.11")
        assertEquals(eds3.codes.size, 1)
        assertEquals(a3.headingsAndItemsAndTexts.size, 1)

        val a4: HeadingType = trn4.headingsAndItemsAndTexts[0] as HeadingType
        val b4: ItemType = a4.headingsAndItemsAndTexts[0] as ItemType
        val c4 = b4.contents[0].cds[0]
        assertEquals(c4.value, "CD-MEDICATION")
        assertEquals(c4.s.value(), "CD-ATC")
        assertEquals(c4.sv, "1.0")
        assertEquals(c4.sl, "CD-ATC")
        assertEquals(c4.dn, "CD-ATC")

        val a5: HeadingType = trn5.headingsAndItemsAndTexts[0] as HeadingType
        val b5: ItemType = a5.headingsAndItemsAndTexts[0] as ItemType
        val c5 = b5.contents[0].cds[0]
        assertEquals(c5.value, "CD-MEDICATION")
        assertEquals(c5.s.value(), "CD-CLINICAL")
        assertEquals(c5.sv, "3.1")
        assertEquals(c5.sl, "CD-CLINICAL")
        assertEquals(c5.dn, "CD-CLINICAL")

        val a6: HeadingType = trn6.headingsAndItemsAndTexts[0] as HeadingType
        val b6: ItemType = a6.headingsAndItemsAndTexts[0] as ItemType
        assertEquals(b6.contents[0].cds.size, 0)
    }

    @Test
    fun addServiceCodesAndTags() {
        // Arrange
        /// First parameter
        val code1 = autonomyCode
        val tag1 = CodeStub("CD-AUTONOMY", "CD-ITEM", "1")
        val code2 = CodeStub("CD-AUTONOMY", "CD-VACCINE", "1")
        val tag2 = CodeStub("CD-AUTONOMY", "CD-VACCINE", "1")
        val svc1 = Service()
        svc1.codes.addAll(setOf(code1, code2))
        svc1.tags.addAll(setOf(tag1, tag2))

        /// Second parameter
        val item1 = ItemType()
        val item2 = ItemType()

        /// Third parameter
        val skipCdItem = true

        /// Fourth parameter
        val restrictedTypes1 = listOf("CD-AUTONOMY", "LOCAL")

        /// Fifth parameter
        val uniqueTypes1 = listOf("CD-AUTONOMY")

        /// Sixth parameter
        val excludedTypes1 = null

        // Execution
        sumehrExport.addServiceCodesAndTags(svc1, item1, skipCdItem, restrictedTypes1, uniqueTypes1, excludedTypes1)
        KmehrExport().addServiceCodesAndTags(svc1, item2, skipCdItem, restrictedTypes1, uniqueTypes1, excludedTypes1)

        // Test
        var test1 = false
        item1.contents[0].cds.forEach { c ->
            if (c.s.value().equals("LOCAL")) {
                test1 = true
            }
        }
        assertFalse(test1)

        var test2 = false
        item2.contents[0].cds.forEach { c ->
            if (c.s.value().equals("LOCAL")) {
                test2 = true
            }
        }
        assertTrue(test2)
    }

    @Test
    fun createParty() {
        // Arrange
        val hcpartyPers = HealthcareParty().apply {
            firstName = "Christelle"
            lastName = "Langlais"
            name = "Cabinet Langlais"
            speciality = "persdentist"
        }
        val hcpartyOrg = HealthcareParty().apply {
            firstName = "CHU Langlais"
            lastName = null
            name = null
            specialityCodes = listOf(CodeStub("hcparty", "orghospital", "1.0"), CodeStub("hcparty", "orglaboratory", "1.0"))
        }
        val hcpartyDept = HealthcareParty().apply {
            firstName = "CHU Langlais"
            lastName = "Département Radiothérapie"
            name = ""
            speciality = "deptradiotherapy"
        }
        val hcparty = HealthcareParty().apply {
            firstName = "Christelle"
            lastName = "Langlais"
            name = "Cabinet Langlais"
        }

        // Act
        val hcpartyTypePers = sumehrExport.createParty(hcpartyPers)
        val hcpartyTypeOrg = sumehrExport.createParty(hcpartyOrg)
        val hcpartyTypeDept = sumehrExport.createParty(hcpartyDept)
        val hcpartyType = sumehrExport.createParty(hcparty)

        // Asserts
        assertNotNull(hcpartyTypePers)
        assertEquals("Christelle", hcpartyTypePers.firstname ?: "")
        assertEquals("Langlais", hcpartyTypePers.familyname ?: "")
        assertNull(hcpartyTypePers.name)

        assertNotNull(hcpartyTypeOrg)
        assertNull(hcpartyTypeOrg.firstname)
        assertNull(hcpartyTypeOrg.familyname)
        assertEquals("CHU Langlais", hcpartyTypeOrg.name ?: "")

        assertNotNull(hcpartyTypeDept)
        assertNull(hcpartyTypeDept.firstname)
        assertNull(hcpartyTypeDept.familyname)
        assertEquals("CHU Langlais Département Radiothérapie", hcpartyTypeDept.name ?: "")

        assertNotNull(hcpartyType)
        assertEquals("Christelle", hcpartyType.firstname ?: "")
        assertEquals("Langlais", hcpartyType.familyname ?: "")
        assertNull(hcpartyType.name)
    }
}
