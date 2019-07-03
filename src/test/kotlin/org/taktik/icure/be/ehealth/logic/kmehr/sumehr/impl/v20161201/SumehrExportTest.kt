package org.taktik.icure.be.ehealth.logic.kmehr.sumehr.impl.v20161201

import ma.glasnost.orika.MapperFacade
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
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import org.taktik.icure.be.ehealth.logic.kmehr.v20161201.KmehrExport
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.*
import java.time.OffsetDateTime.now

class SumehrExportTest {
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

    private val validService = Service().apply { this.id = "1"; this.endOfLife = null; this.status = 1; this.tags = validTags; this.content = validContent }
    private val encryptedService = Service().apply { this.id = "2"; this.endOfLife = null; this.status = 2; this.tags = emptyTags; this.content = emptyContent; this.encryptedContent = "validContent" }
    private val serviceDto = ServiceDto().apply { this.id = "2"; this.endOfLife = null; this.status = 2; this.tags = emptyTagsDto; this.content = validContentDto }
    private val decryptedService = Service().apply { this.id = "2"; this.endOfLife = null; this.status = 2; this.tags = emptyTags; this.content = validContent }
    private val lifeEndedService = Service().apply { this.id = "3"; this.endOfLife = Long.MAX_VALUE; this.status = 1; this.tags = validTags; this.content = validContent }
    private val wrongStatusService = Service().apply { this.id = "4"; this.endOfLife = null; this.status = 3; this.tags = validTags; this.content = validContent }
    private val inactiveService = Service().apply { this.id = "5"; this.endOfLife = null;this.status = 2;this.tags = inactiveTags;this.content = validContent }
    private val emptyService = Service().apply { this.id = "6"; this.endOfLife = null;this.status = 1;this.tags = validTags;this.content = emptyContent }
    private val services = listOf(validService, encryptedService, lifeEndedService, wrongStatusService, inactiveService, emptyService)

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
                        override fun get(): List<ServiceDto> = listOf(serviceDto)
                        override fun get(timeout: Long, unit: TimeUnit): List<ServiceDto> = listOf(serviceDto)
                    }
                }

        Mockito.`when`(mapper.map<Service, ServiceDto>(any(), eq(ServiceDto::class.java)))
                .thenAnswer { serviceDto }

        Mockito.`when`(mapper.map<ServiceDto, Service>(any(), eq(Service::class.java)))
                .thenAnswer { decryptedService }
    }

    @Test
    fun getMd5() {
        //Arrange
        val hcPartyId = "1"
        val sfks = listOf("", "")
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
        val sfks = listOf("", "")
        val cdItems = listOf("medication")
        val excludedIds = emptyList<String>()
        sumehrExport.contactLogic = this.contactLogic
        sumehrExport.mapper = this.mapper

        //Execution
        val services = sumehrExport.getNonPassiveIrrelevantServices(hcPartyId, sfks, cdItems, excludedIds, decryptor)

        //Tests
        ///All services
        assertNotNull(services)
        assertEquals(2, services.size)
        assertNotNull(services?.firstOrNull())
        assertNotNull(services?.lastOrNull())

        ///Normal service
        val service1 = services.first()
        assertNull(service1.endOfLife)
        assertEquals(1, service1.status)
        assertEquals("active", service1.tags?.firstOrNull { it.type == "CD-LIFECYCLE" }?.code ?: "active")
        assertNotNull(service1.content)
        assertNotNull(service1.content.values.firstOrNull()?.booleanValue)

        ///Decrypted service
        val service2 = services.last()
        assertNull(service2.endOfLife)
        assertEquals(2, service2.status)
        assertEquals(0, service2.tags.size)
        assertNotNull(service2.content)
        assertNotNull(service2.content.values.firstOrNull()?.booleanValue)
    }

    @Test
    fun addHealthCareElement() {
        // Arrange
        /// First parameter
        val trn1 = ObjectFactory().createTransactionType()
        val trn2 = ObjectFactory().createTransactionType()
        val trn3 = ObjectFactory().createTransactionType()

        /// Second parameter
        val eds1 = HealthElement() //closingDate == null
        val eds2 = HealthElement() //closingDate !=null
        val eds3 = HealthElement()
        eds2.closingDate = now().toEpochSecond()
        val tag1 = CodeStub("CD-ITEM", "familyrisk", "1")
        val tag2 = CodeStub("CD-ITEM", "allergy", "1")
        eds1.tags.add(tag1)
        eds2.tags.add(tag2)
        eds3.tags.add(tag2)
        val code1 = CodeStub("CD-AUTONOMY", "CD-ITEM", "1")
        val code2 = CodeStub("ICPC", "CD-VACCINE", "1")
        eds1.codes.add(code1)
        eds1.codes.add(code2)
        eds2.codes.add(code1)
        eds2.codes.add(code2)


        /// Execution
        sumehrExport.addHealthCareElement(trn1,eds1)
        sumehrExport.addHealthCareElement(trn2,eds2)
        sumehrExport.addHealthCareElement(trn3,eds3)

        /// Tests
        Assert.assertEquals(eds1.tags.firstOrNull()?.code,"problem")
        Assert.assertEquals(eds1.tags.firstOrNull()?.version,"1.11")
        Assert.assertEquals(eds1.codes.size,1)


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
        val restrictedTypes1 = listOf("CD-AUTONOMY","LOCAL")

        /// Fifth parameter
        val uniqueTypes1 = listOf("CD-AUTONOMY")

        /// Sixth parameter
        val excludedTypes1 = null

        // Execution
        sumehrExport.addServiceCodesAndTags(svc1, item1, skipCdItem, restrictedTypes1, uniqueTypes1, excludedTypes1)
        KmehrExport().addServiceCodesAndTags(svc1, item2, skipCdItem, restrictedTypes1, uniqueTypes1, excludedTypes1)

        // Test
        var test1 = false
        item1.contents[0].cds.forEach{ c ->
            if( c.s.value().equals("LOCAL")){
                test1 = true
            }
        }
        Assert.assertFalse(test1)

        var test2 = false
        item2.contents[0].cds.forEach{ c ->
            if( c.s.value().equals("LOCAL")){
                test2 = true
            }
        }
        Assert.assertTrue(test2)
    }
}
