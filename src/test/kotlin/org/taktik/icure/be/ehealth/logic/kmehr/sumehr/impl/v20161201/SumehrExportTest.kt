package org.taktik.icure.be.ehealth.logic.kmehr.sumehr.impl.v20161201

import ma.glasnost.orika.MapperFacade
import org.bouncycastle.asn1.x500.style.RFC4519Style.c
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers.*
import org.mockito.Mockito
import org.taktik.icure.entities.*
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.ItemType
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.embed.Content
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.logic.impl.ContactLogicImpl
import org.taktik.icure.services.external.api.AsyncDecrypt
import org.taktik.icure.services.external.rest.v1.dto.CodeDto
import org.taktik.icure.services.external.rest.v1.dto.embed.ContentDto
import org.taktik.icure.services.external.rest.v1.dto.embed.ServiceDto
import org.taktik.icure.utils.FuzzyValues
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import org.taktik.icure.be.ehealth.logic.kmehr.v20161201.KmehrExport
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.*
import org.taktik.icure.be.format.logic.impl.HealthOneLogicImpl
import org.taktik.icure.entities.embed.PatientHealthCareParty
import org.taktik.icure.entities.embed.ReferralPeriod
import org.taktik.icure.logic.impl.HealthElementLogicImpl
import org.taktik.icure.logic.impl.HealthcarePartyLogicImpl
import org.taktik.icure.services.external.rest.v1.dto.HealthElementDto
import java.io.Serializable
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.OffsetDateTime.now

class SumehrExportTest {
    private val today = FuzzyValues.getFuzzyDate(LocalDateTime.now(), ChronoUnit.SECONDS)
    private val yesterday = FuzzyValues.getFuzzyDate(LocalDateTime.now().minusDays(1), ChronoUnit.SECONDS)
    private val oneWeekAgo = FuzzyValues.getFuzzyDate(LocalDateTime.now().minusWeeks(1), ChronoUnit.SECONDS)
    private val oneMonthAgo = FuzzyValues.getFuzzyDate(LocalDateTime.now().minusMonths(1), ChronoUnit.SECONDS)

    //The method tested needs a SumehrExport Class to run
    private val sumehrExport = SumehrExport()

    private val patient = Mockito.mock(Patient::class.java)
    private val contactLogic = Mockito.mock(ContactLogicImpl::class.java)
    private val decryptor = Mockito.mock(AsyncDecrypt::class.java)
    private val mapper = Mockito.mock(MapperFacade::class.java)
    private val healthElementLogic = Mockito.mock(HealthElementLogicImpl::class.java)
    private val healthcarePartyLogic = Mockito.mock(HealthcarePartyLogicImpl::class.java)

    private val validTags = setOf(CodeStub().apply { type = "CD-LIFECYCLE"; code = "active" }, CodeStub().apply { type = "CD-TESTINGITEM"; code = "inactive" })
    private val inactiveTags = setOf(CodeStub().apply { type = "CD-LIFECYCLE"; code = "inactive" })
    private val emptyTags = emptySet<CodeStub>()
    private val emptyTagsDto = emptySet<CodeDto>()

    private val validContent = mapOf(Pair("valid", Content().apply { booleanValue = true }))
    private val validContentDto = mapOf(Pair("valid", ContentDto().apply { booleanValue = true }))
    private val emptyContent = mapOf(Pair("empty", Content()))

    private val drugs = setOf(CodeStub().apply { type = "CD-DRUG-CNK"; code = "3434784" })
    private val drugsDto = setOf(CodeDto().apply { type = "CD-DRUG-CNK"; code = "3434784" })

    private val validService = Service().apply { this.id = "1"; this.endOfLife = null; this.status = 1; this.tags = validTags; this.content = validContent; this.openingDate = oneWeekAgo; this.closingDate = today }
    private val encryptedService = Service().apply { this.id = "2"; this.endOfLife = null; this.status = 2; this.tags = emptyTags; this.content = emptyContent; this.encryptedContent = "validContent"; this.codes = drugs; this.openingDate = oneWeekAgo }
    private val decryptedServiceDto = ServiceDto().apply { this.id = "2"; this.endOfLife = null; this.status = 2; this.tags = emptyTagsDto; this.content = validContentDto; this.codes = drugsDto; this.openingDate = oneWeekAgo }
    private val decryptedService = Service().apply { this.id = "2"; this.endOfLife = null; this.status = 2; this.tags = emptyTags; this.content = validContent; this.codes = drugs; this.openingDate = oneWeekAgo }
    private val lifeEndedService = Service().apply { this.id = "3"; this.endOfLife = Long.MAX_VALUE; this.status = 1; this.tags = validTags; this.content = validContent; this.openingDate = oneWeekAgo }
    private val wrongStatusService = Service().apply { this.id = "4"; this.endOfLife = null; this.status = 3; this.tags = validTags; this.content = validContent; this.openingDate = oneWeekAgo }
    private val inactiveService = Service().apply { this.id = "5"; this.endOfLife = null; this.status = 2; this.tags = inactiveTags; this.content = validContent; this.openingDate = oneWeekAgo }
    private val emptyService = Service().apply { this.id = "6"; this.endOfLife = null; this.status = 1; this.tags = validTags; this.content = emptyContent; this.openingDate = oneWeekAgo }
    private val oldService = Service().apply { this.id = "7"; this.endOfLife = null; this.status = 1; this.tags = validTags; this.content = validContent; this.openingDate = oneMonthAgo }
    private val closedService = Service().apply { this.id = "8"; this.endOfLife = null; this.status = 1; this.tags = validTags; this.content = validContent; this.openingDate = oneWeekAgo; this.closingDate = yesterday }
    private val services = listOf(validService, encryptedService, lifeEndedService, wrongStatusService, inactiveService, emptyService, oldService, closedService)

    private val emptyHealthElement = HealthElement()
    private val validHealthElementWithEmptyEncryptedSelf = HealthElement().apply {
        this.tags.add(CodeStub("CD-ITEM", "familyrisk", "1.3"));
        this.codes.add(CodeStub("ICPC", "CD-VACCINE", "11.65"));
        this.status = 3;
        this.closingDate = null;
        this.descr = "Notnull"
    }
    private val validHealthElement = HealthElement().apply {
        this.tags.add(CodeStub("CD-ITEM", "familyrisk", "1.3"));
        this.codes.add(CodeStub("ICPC", "CD-VACCINE", "11.65"));
        this.encryptedSelf = "encryptionKey";
        this.status = 3;
        this.closingDate = null;
        this.descr = "Notnull"
    }
    private val listOfHealthElement = listOf(validHealthElementWithEmptyEncryptedSelf, validHealthElement)

    @Before
    fun setUp() {
        Mockito.`when`(contactLogic.modifyContact(any(Contact::class.java)))
                .thenAnswer { it.getArgumentAt(0, Contact::class.java) }

        Mockito.`when`(contactLogic.getServices(any()))
                .thenAnswer { services }

        Mockito.`when`(decryptor.decrypt<ServiceDto>(any(), any()))
                .thenAnswer {
                    object : Future<List<ServiceDto>> {
                        override fun isDone(): Boolean = true
                        override fun cancel(mayInterruptIfRunning: Boolean): Boolean = false
                        override fun isCancelled(): Boolean = false
                        override fun get(): List<ServiceDto> = listOf(decryptedServiceDto)
                        override fun get(timeout: Long, unit: TimeUnit): List<ServiceDto> = listOf(decryptedServiceDto)
                    }
                }

        Mockito.`when`(mapper.map<Service, ServiceDto>(any(), eq(ServiceDto::class.java)))
                .thenAnswer { decryptedServiceDto }

        Mockito.`when`(mapper.map<ServiceDto, Service>(any(), eq(Service::class.java)))
                .thenAnswer { decryptedService }

        Mockito.`when`(healthElementLogic.findLatestByHCPartySecretPatientKeys(any(), any()))
                .thenAnswer { listOfHealthElement }

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

        Mockito.`when`(mapper.map<HealthElement, HealthElementDto>(any(), eq(HealthElementDto::class.java))).thenAnswer {
            HealthElementDto().apply {
                healthElementId = (it.getArgumentAt(0, HealthElement::class.java) as HealthElement).getHealthElementId();
                descr = (it.getArgumentAt(0, HealthElement::class.java) as HealthElement).descr;
                encryptedSelf = (it.getArgumentAt(0, HealthElement::class.java) as HealthElement).encryptedSelf;
                status = (it.getArgumentAt(0, HealthElement::class.java) as HealthElement).status;
                closingDate = (it.getArgumentAt(0, HealthElement::class.java) as HealthElement).closingDate;
                it.getArgumentAt(0, HealthElement::class.java).tags.forEach { c -> tags.add(CodeDto(c.type, c.code)); }
                it.getArgumentAt(0, HealthElement::class.java).codes.forEach { c -> codes.add(CodeDto(c.type, c.code)); }
            }
        }

        Mockito.`when`(mapper.map<HealthElementDto, HealthElement>(any(), eq(HealthElement::class.java))).thenAnswer {
            HealthElement().apply {
                healthElementId = (it.getArgumentAt(0, HealthElementDto::class.java) as HealthElementDto).getHealthElementId();
                descr = (it.getArgumentAt(0, HealthElementDto::class.java) as HealthElementDto).descr;
                encryptedSelf = (it.getArgumentAt(0, HealthElementDto::class.java) as HealthElementDto).encryptedSelf;
                status = (it.getArgumentAt(0, HealthElementDto::class.java) as HealthElementDto).status;
                closingDate = (it.getArgumentAt(0, HealthElementDto::class.java) as HealthElementDto).closingDate;
                it.getArgumentAt(0, HealthElementDto::class.java).tags.forEach { c -> tags.add(CodeStub(c.type, c.code,c.version)); }
                it.getArgumentAt(0, HealthElementDto::class.java).codes.forEach { c -> codes.add(CodeStub(c.type, c.code,c.version)); }
            }
        }

        Mockito.`when`(healthcarePartyLogic.getHealthcareParty(any())).thenAnswer {
            HealthcareParty()
        }

    }

    @Test
    fun getMd5() {
        //Arrange
        val hcPartyId = "1"
        val sfks = listOf("")
        val excludedIds = emptyList<String>()

        //Execution
        val md5 = sumehrExport.getMd5(hcPartyId, patient, sfks, excludedIds)

        //Tests
        assertNotNull(md5)
        assertFalse(md5.isBlank())
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

        //Execution
        val services = sumehrExport.getNonPassiveIrrelevantServices(hcPartyId, sfks, cdItems, excludedIds, decryptor)

        //Tests
        ///All services
        assertNotNull(services)
        assertEquals(4, services.size)
        assertNotNull(services?.firstOrNull())
        assertNotNull(services?.lastOrNull())

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
    fun getMedications() {
        //Arrange
        val hcPartyId = "1"
        val sfks = listOf("")
        val excludedIds = emptyList<String>()
        sumehrExport.contactLogic = this.contactLogic
        sumehrExport.mapper = this.mapper

        //Execute
        val medications = sumehrExport.getMedications(hcPartyId, sfks, excludedIds, decryptor)

        //Tests
        assertNotNull(medications)
        assertEquals(1, medications.count { m -> m.id.equals("2") })    // no drug duplicate
        assertTrue(medications.all { m -> m.closingDate == null || m.closingDate!!.let { today <= it } })
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
        val period1 = ReferralPeriod(date1,date2)
        val period2 = ReferralPeriod(date1,date3)
        val period3 = ReferralPeriod(date1,date4)
        val pHCP1 = PatientHealthCareParty().apply { referralPeriods.add(period1) }
        val pHCP2 = PatientHealthCareParty().apply { referralPeriods.add(period2) }
        val pHCP3 = PatientHealthCareParty().apply { referralPeriods.add(period3) }
        val pat1 = Patient().apply {
            patientHealthCareParties.add(pHCP1);
            patientHealthCareParties.add(pHCP2);
            patientHealthCareParties.add(pHCP3);
        }
        val pat2 = Patient().apply {
            patientHealthCareParties.add(pHCP1);
            patientHealthCareParties.add(pHCP2);
        }

        /// Second parameter
        val trn1 = ObjectFactory().createTransactionType();
        val trn2 = ObjectFactory().createTransactionType();
       /* val head1 = HeadingType()
        val head2 = HeadingType()
        trn1.headingsAndItemsAndTexts.add(head1)
        trn2.headingsAndItemsAndTexts.add(head2)*/

        // Execution
        sumehrExport.addGmdmanager(pat1, trn1)
        sumehrExport.addGmdmanager(pat2, trn2)

        // Tests
        Assert.assertEquals(trn1.headingsAndItemsAndTexts.size,1)
        val a1 = trn1.headingsAndItemsAndTexts.get(0) as HeadingType
        Assert.assertEquals(a1.headingsAndItemsAndTexts.size,1)
        val b1  = a1.headingsAndItemsAndTexts.get(0) as ItemType
        Assert.assertEquals(b1.ids.size,1)
        Assert.assertEquals(b1.ids[0].value,"1")
        Assert.assertEquals(b1.ids[0].s.value(),"ID-KMEHR")
        Assert.assertEquals(b1.ids[0].sv,"1.0")
        Assert.assertEquals(b1.cds.size,1)
        Assert.assertEquals(b1.cds[0].s.value(),"CD-ITEM")
        Assert.assertEquals(b1.cds[0].value,"gmdmanager")
        Assert.assertEquals(b1.contents.size,1)
        Assert.assertNotNull(b1.contents[0].hcparty)
        Assert.assertEquals(trn2.headingsAndItemsAndTexts.size,0)
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
        sumehrExport.addHealthCareElements(hcPartyId, sfks, trn1, excludedIds, decryptor1)
        sumehrExport.addHealthCareElements(hcPartyId, sfks, trn2, excludedIds, decryptor2)

        // Tests
        val a1: HeadingType = trn1.headingsAndItemsAndTexts.get(0) as HeadingType
        Assert.assertNotNull(trn1.headingsAndItemsAndTexts)
        Assert.assertEquals(trn1.headingsAndItemsAndTexts.size, 1)
        Assert.assertEquals(a1.headingsAndItemsAndTexts.size, 2)

        val a2: HeadingType = trn2.headingsAndItemsAndTexts.get(0) as HeadingType
        Assert.assertNotNull(trn1.headingsAndItemsAndTexts)
        Assert.assertEquals(trn1.headingsAndItemsAndTexts.size, 1)
        Assert.assertEquals(a2.headingsAndItemsAndTexts.size, 2)


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
        val code1 = CodeStub("CD-AUTONOMY", "CD-ITEM", "1")
        val code2 = CodeStub("ICPC", "CD-VACCINE", "1")
        val code3 = CodeStub("CD-ATC", "CD-MEDICATION", "2")
        val code4 = CodeStub("BE-THESAURUS", "CD-MEDICATION", "3.1")
        val code5 = CodeStub("NOTATYPE", "CD-MEDICATION", "3.1")
        eds1.codes.add(code1)
        eds1.codes.add(code2)
        eds2.codes.add(code1)
        eds3.codes.add(code1)
        eds4.codes.add(code3)
        eds5.codes.add(code4)
        eds6.codes.add(code5)


        // Execution
        sumehrExport.addHealthCareElement(trn1, eds1)
        sumehrExport.addHealthCareElement(trn2, eds2)
        sumehrExport.addHealthCareElement(trn3, eds3)
        sumehrExport.addHealthCareElement(trn4, eds4)
        sumehrExport.addHealthCareElement(trn5, eds5)
        sumehrExport.addHealthCareElement(trn6, eds6)

        // Tests
        val a1: HeadingType = trn1.headingsAndItemsAndTexts.get(0) as HeadingType
        val b1: ItemType = a1.headingsAndItemsAndTexts.get(0) as ItemType
        val c1 = b1.contents[0].cds[0]
        Assert.assertEquals(eds1.tags.firstOrNull()?.code, "problem")
        Assert.assertEquals(eds1.tags.firstOrNull()?.version, "1.11")
        Assert.assertEquals(eds1.codes.size, 1) // code1 (with "CD-AUTONOMY") is removed
        Assert.assertEquals(c1.value, "CD-VACCINE")
        Assert.assertEquals(c1.s.value(), "ICPC")
        Assert.assertEquals(c1.sv, "1")
        Assert.assertEquals(c1.sl, "ICPC")
        Assert.assertEquals(c1.dn, "ICPC")

        val a2: HeadingType = trn2.headingsAndItemsAndTexts.get(0) as HeadingType
        Assert.assertEquals(eds2.tags.firstOrNull()?.code, "allergy")
        Assert.assertEquals(eds2.tags.firstOrNull()?.version, "1")
        Assert.assertEquals(eds2.codes.size, 1)
        Assert.assertEquals(a2.headingsAndItemsAndTexts.size, 0)

        val a3: HeadingType = trn3.headingsAndItemsAndTexts.get(0) as HeadingType
        Assert.assertEquals(eds3.tags.firstOrNull()?.code, "problem")
        Assert.assertEquals(eds3.tags.firstOrNull()?.version, "1.11")
        Assert.assertEquals(eds3.codes.size, 0)
        Assert.assertEquals(a3.headingsAndItemsAndTexts.size, 0)

        val a4: HeadingType = trn4.headingsAndItemsAndTexts.get(0) as HeadingType
        val b4: ItemType = a4.headingsAndItemsAndTexts.get(0) as ItemType
        val c4 = b4.contents[0].cds[0]
        Assert.assertEquals(c4.value, "CD-MEDICATION")
        Assert.assertEquals(c4.s.value(), "CD-ATC")
        Assert.assertEquals(c4.sv, "1.0")
        Assert.assertEquals(c4.sl, "CD-ATC")
        Assert.assertEquals(c4.dn, "CD-ATC")

        val a5: HeadingType = trn5.headingsAndItemsAndTexts.get(0) as HeadingType
        val b5: ItemType = a5.headingsAndItemsAndTexts.get(0) as ItemType
        val c5 = b5.contents[0].cds[0]
        Assert.assertEquals(c5.value, "CD-MEDICATION")
        Assert.assertEquals(c5.s.value(), "CD-CLINICAL")
        Assert.assertEquals(c5.sv, "3.1")
        Assert.assertEquals(c5.sl, "CD-CLINICAL")
        Assert.assertEquals(c5.dn, "CD-CLINICAL")

        val a6: HeadingType = trn6.headingsAndItemsAndTexts.get(0) as HeadingType
        val b6: ItemType = a6.headingsAndItemsAndTexts.get(0) as ItemType
        Assert.assertEquals(b6.contents[0].cds.size, 0)
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
        Assert.assertFalse(test1)

        var test2 = false
        item2.contents[0].cds.forEach { c ->
            if (c.s.value().equals("LOCAL")) {
                test2 = true
            }
        }
        Assert.assertTrue(test2)
    }
}
