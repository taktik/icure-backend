package org.taktik.icure.be.ehealth.logic.kmehr.v20161201

import org.junit.Assert
import org.junit.Test
import org.taktik.icure.be.ehealth.dto.kmehr.v20110701.Utils.makeXGC
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.Utils
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDLIFECYCLEvalues
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDTEMPORALITYvalues
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDWEEKDAYvalues
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.dt.v1.TextType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.*
import org.taktik.icure.constants.ServiceStatus
import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.embed.*
import org.taktik.icure.utils.FuzzyValues
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class KmehrExportTest {

    @Test
    fun createItemWithContent() {
        // Arrange
        /// General
        val kmehrExport = KmehrExport()

        /// First parameter
        val svc1 = Service().apply {
            this.id = "idSvc1"
            tags = mutableSetOf((CodeStub("CD-LAB", "code", "version")))
            status = 2 //Irrelevant
            closingDate = FuzzyValues.getFuzzyDate(LocalDateTime.of(2950, 1, 1, 1, 1, 1, 1), ChronoUnit.NANOS)
            valueDate = FuzzyValues.getFuzzyDate(LocalDateTime.of(1900, 1, 1, 1, 1, 1, 1), ChronoUnit.NANOS)
            modified = 1L
        }
        val svc2 = Service().apply {
            // no tag with type == "CD-LAB"
            status = 0 //Relevant
            openingDate = FuzzyValues.getFuzzyDate(LocalDateTime.of(2950, 1, 1, 1, 1, 1, 1), ChronoUnit.NANOS)
            closingDate = FuzzyValues.getFuzzyDate(LocalDateTime.of(1000, 1, 1, 1, 1), ChronoUnit.NANOS)
        }
        val svc3 = Service().apply {
            // no tag with type == "CD-LAB"
            status = 0 //Relevant
            closingDate = FuzzyValues.getFuzzyDate(LocalDateTime.of(3000, 1, 1, 1, 1), ChronoUnit.NANOS)
            tags = mutableSetOf(CodeStub("CD-LIFECYCLE", "switched", "version"))
        }
        val svc4 = Service().apply {
            status = 0 //Relevant
            closingDate = FuzzyValues.getFuzzyDate(LocalDateTime.of(3000, 1, 1, 1, 1), ChronoUnit.NANOS)
            tags = mutableSetOf(
                    CodeStub("CD-TEMPORALITY", "chronic", "version")) // no tag with type == "CD-LAB" or "CD-LIFECYCLE"
            content.set("keyfirst", Content().apply {
                medicationValue = Medication().apply {
                    regimen = listOf(
                            RegimenItem().apply {
                                weekday = RegimenItem.Weekday().apply {
                                    weekday = Code("CD-WEEKDAY|sunday|version")
                                }
                                administratedQuantity = RegimenItem.AdministrationQuantity().apply {
                                    quantity = 1.5
                                    administrationUnit = Code("CD-ADMINISTRATIONUNIT|code|version")
                                }
                            }
                    )
                    duration = Duration().apply {
                        value = 7.0
                        unit = CodeStub("CD-TIMEUNITE", "code", "version")
                    }
                    renewal = Renewal().apply {
                        decimal = 1
                        duration = Duration().apply {
                            value = 3.5
                            unit = CodeStub("CD-TIMEUNIT","code","version")
                        }
                    }
                    drugRoute = "CD-DRUG-ROUTE"
                }
            })
        }

        /// Second parameter
        val idx = 1

        /// Third parameter
        val cdItem1 = ""
        val cdItem2 = "medication"

        /// Fourth parameter
        val contents = listOf(
                ContentType().apply {
                    bacteriology = TextType().apply {
                        value = "bacteriologyValue"
                    }
                },
                ContentType()
        )


        // Execution
        val res1 = kmehrExport.createItemWithContent(svc1, idx, cdItem1, contents)
        val res2 = kmehrExport.createItemWithContent(svc2, idx, cdItem1, contents, "Other Name")
        val res3 = kmehrExport.createItemWithContent(svc3, idx, cdItem1, contents)
        val res4 = kmehrExport.createItemWithContent(svc4, idx, cdItem1, contents)
        val res5 = kmehrExport.createItemWithContent(svc4, idx, cdItem2, contents)

        // Tests
        Assert.assertNotNull(res1)
        Assert.assertNotNull(res2)
        Assert.assertNotNull(res3)
        Assert.assertNotNull(res4)
        Assert.assertNotNull(res5)

        /// ids
        Assert.assertNotNull(res1?.ids)
        Assert.assertEquals(2, res1?.ids?.size)
        Assert.assertEquals(IDKMEHRschemes.ID_KMEHR, res1?.ids?.elementAt(0)?.s)
        Assert.assertEquals("1.0", res1?.ids?.elementAt(0)?.sv)
        Assert.assertEquals(idx.toString(), res1?.ids?.elementAt(0)?.value)
        Assert.assertEquals(IDKMEHRschemes.LOCAL, res1?.ids?.elementAt(1)?.s)
        Assert.assertEquals("iCure-Service", res1?.ids?.elementAt(1)?.sl)
        Assert.assertEquals(kmehrExport.ICUREVERSION, res1?.ids?.elementAt(1)?.sv)
        Assert.assertEquals(svc1.id, res1?.ids?.elementAt(1)?.value)
        Assert.assertEquals("Other Name", res2?.ids?.elementAt(1)?.sl)

        /// cds
        Assert.assertNotNull(res1?.cds)
        Assert.assertEquals(2, res1?.cds?.size)
        Assert.assertEquals(CDITEMschemes.CD_ITEM, res1?.cds?.elementAt(0)?.s)
        Assert.assertEquals(CDITEMschemes.CD_ITEM.version(), res1?.cds?.elementAt(0)?.sv)
        Assert.assertEquals(cdItem1, res1?.cds?.elementAt(0)?.value)
        Assert.assertEquals(CDITEMschemes.CD_LAB, res1?.cds?.elementAt(1)?.s)
        Assert.assertEquals(CDITEMschemes.CD_LAB.version(), res1?.cds?.elementAt(1)?.sv)
        Assert.assertEquals(svc1.tags.find { t -> t.type == "CD-LAB" }?.code, res1?.cds?.elementAt(1)?.value)
        Assert.assertEquals(1, res2?.cds?.size)

        /// contents
        Assert.assertEquals(1, res1?.contents?.size)

        /// lifecycle
        Assert.assertNotNull(res1?.lifecycle)
        Assert.assertNotNull(res1?.lifecycle?.cd)
        Assert.assertEquals("CD-LIFECYCLE", res1?.lifecycle?.cd?.s)
        Assert.assertEquals(CDLIFECYCLEvalues.INACTIVE, res1?.lifecycle?.cd?.value) // irrelevant service
        Assert.assertEquals(CDLIFECYCLEvalues.INACTIVE, res2?.lifecycle?.cd?.value) // closing date in past
        Assert.assertEquals(CDLIFECYCLEvalues.fromValue(svc3.tags.find { t -> t.type == "CD-LIFECYCLE" }?.code), res3?.lifecycle?.cd?.value) // relevant, closing date in future and a tag with type "CD-LIFECYCLE"
        Assert.assertEquals(CDLIFECYCLEvalues.ACTIVE, res4?.lifecycle?.cd?.value) // relevant, closing date in future, without any tag with type "CD-LIFECYCLE" and cdItem!="medication"
        Assert.assertEquals(CDLIFECYCLEvalues.PRESCRIBED, res5?.lifecycle?.cd?.value) // relevant, closing date in future, without any tag with type "CD-LIFECYCLE" and cdItem=="medication"

        /// if medication
        //// temporality
        Assert.assertNotNull(res5?.temporality)
        Assert.assertNotNull(res5?.temporality?.cd)
        Assert.assertEquals("CD-TEMPORALITY", res5?.temporality?.cd?.s)
        Assert.assertEquals(CDTEMPORALITYvalues.fromValue(svc4.tags.find { it.type == "CD-TEMPORALITY" }?.code).value(), res5?.temporality?.cd?.value?.value())
        //// content - frequency
        Assert.assertNotNull(res5?.frequency)
        //// content - duration
        Assert.assertNotNull(res5?.duration)
        //// content - regimen
        Assert.assertEquals(3, res5?.regimen?.daynumbersAndQuantitiesAndDates?.size)
        val a1 = res5?.regimen?.daynumbersAndQuantitiesAndDates?.get(0) as ItemType.Regimen.Weekday
        Assert.assertEquals("CD-WEEKDAY", a1?.cd?.s)
        Assert.assertEquals(CDWEEKDAYvalues.fromValue(svc4?.content?.getValue("keyfirst")?.medicationValue?.regimen?.elementAt(0)?.weekday?.weekday?.code), a1?.cd.value)
        Assert.assertNull(a1?.weeknumber)
        val a2 = res5?.regimen?.daynumbersAndQuantitiesAndDates?.elementAt(1) as ItemType.Regimen.Daytime
        val a3 = res5?.regimen?.daynumbersAndQuantitiesAndDates?.elementAt(2) as AdministrationquantityType
        Assert.assertEquals(BigDecimal(svc4?.content?.get("keyfirst")?.medicationValue?.regimen?.get(0)?.administratedQuantity?.quantity!!), a3?.decimal)
        Assert.assertEquals("CD-ADMINISTRATIONUNIT", a3?.unit?.cd?.s)
        Assert.assertEquals("1.2", a3?.unit?.cd?.sv)
        Assert.assertEquals(svc4?.content?.get("keyfirst")?.medicationValue?.regimen?.get(0)?.administratedQuantity?.administrationUnit?.code, a3?.unit?.cd?.value)
        //// content - renewal
        Assert.assertNotNull(res5?.renewal)
        Assert.assertEquals(BigDecimal(svc4?.content?.get("keyfirst")?.medicationValue?.renewal?.decimal?.toLong()!!),res5?.renewal?.decimal)
        Assert.assertNotNull(res5?.renewal?.duration)
        //// content - route
        Assert.assertNotNull(res5?.route)
        Assert.assertEquals("CD-DRUG-ROUTE",res5?.route?.cd?.s)
        Assert.assertEquals(svc4?.content?.get("keyfirst")?.medicationValue?.drugRoute,res5?.route?.cd?.value)

        /// isIsrelevant
        Assert.assertEquals(ServiceStatus.isRelevant(svc1.status), res1?.isIsrelevant)

        /// beginmoment
        Assert.assertEquals((svc1.valueDate
                ?: svc1.openingDate).let { Utils.makeMomentTypeDateFromFuzzyLong(it) }?.date, res1?.beginmoment?.date)
        Assert.assertEquals((svc2.valueDate
                ?: svc2.openingDate).let { Utils.makeMomentTypeDateFromFuzzyLong(it) }?.date, res2?.beginmoment?.date)

        /// endmoment
        Assert.assertEquals(svc1.closingDate?.let { Utils.makeMomentTypeDateFromFuzzyLong(it) }?.date, res1?.endmoment?.date)

        /// recorddatetime
        Assert.assertEquals(makeXGC(svc1.modified), res1?.recorddatetime)

    }
}