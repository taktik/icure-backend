package org.taktik.icure.be.ehealth.logic.kmehr.sumehr.impl.v20161201

import ma.glasnost.orika.MapperFacade
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers.*
import org.mockito.Mockito
import org.taktik.icure.entities.Patient
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
        assertTrue(medications.all { m -> m.closingDate == null || m.closingDate!!.let { today <= it }})
    }

    @Test
    fun addServiceCodesAndTags() {
        // Arrange
        /// First parameter
        val svc = Service()
        val code = CodeStub("CD-TRANSACTION", "allergy", "1")
        svc.codes.add(code)

        /// Second parameter
        val item = ItemType()

        /// Third parameter
        val skipCdItem = true;

        /// Fourth parameter
        val restrictedTypes = listOf("allergy")

        /// Fifth parameter
        val uniqueTypes = listOf("string")

        /// Sixth parameter
        val excludedTypes = null

        // Execution
        sumehrExport.addServiceCodesAndTags(svc, item, skipCdItem, restrictedTypes, uniqueTypes, excludedTypes)
        //super.addServiceCodesAndTags(svc, item, skipCdItem, restrictedTypes, uniqueTypes, (excludedTypes ?: emptyList()) + listOf("LOCAL", "RELEVANCE", "SUMEHR", "SOAP", "CD-TRANSACTION", "CD-TRANSACTION-TYPE"))
    }
}
