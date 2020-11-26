/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.be.ehealth.logic.kmehr.v20161201

import ma.glasnost.orika.MapperFacade
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.taktik.commons.uti.UTI
import org.taktik.icure.be.drugs.logic.DrugsLogic
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.Utils
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.*
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.dt.v1.TextType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.id.v1.*
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.*
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.ObjectFactory
import org.taktik.icure.be.ehealth.logic.kmehr.Config
import org.taktik.icure.constants.ServiceStatus
import org.taktik.icure.entities.*
import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.Content
import org.taktik.icure.entities.embed.PlanOfAction
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.logic.*
import org.taktik.icure.logic.impl.filter.Filters
import org.taktik.icure.utils.FuzzyValues
import java.io.OutputStream
import java.math.BigDecimal
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.swing.text.rtf.RTFEditorKit
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.datatype.DatatypeConstants
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar
import kotlin.collections.ArrayList

open class KmehrExport {
    @Autowired var patientLogic: PatientLogic? = null
    @Autowired var codeLogic: CodeLogic? = null
    @Autowired var drugsLogic: DrugsLogic? = null
    @Autowired var healthElementLogic: HealthElementLogic? = null
    @Autowired var healthcarePartyLogic: HealthcarePartyLogic? = null
    @Autowired var contactLogic: ContactLogic? = null
    @Autowired var documentLogic: DocumentLogic? = null
    @Autowired var sessionLogic: SessionLogic? = null
    @Autowired var userLogic: UserLogic?= null
    @Autowired var filters: Filters? = null
    @Autowired var mapper: MapperFacade? = null

    val unitCodes = HashMap<String,Code>()

    internal val STANDARD = "20161201"
    @Value("\${icure.version}")
    internal val ICUREVERSION: String = "4.0.0"

    internal val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd")
    internal open val log = LogFactory.getLog(KmehrExport::class.java)

    fun createParty(ids : List<IDHCPARTY>, cds : List<CDHCPARTY>, name : String) : HcpartyType {
        return HcpartyType().apply { this.ids.addAll(ids); this.cds.addAll(cds); this.name = name }
    }

    fun createParty(m : HealthcareParty, cds : List<CDHCPARTY>? = listOf() ) : HcpartyType {
        return HcpartyType().apply {
            if(!m.nihii.isNullOrBlank()) {
                m.nihii?.let { nihii -> ids.add(IDHCPARTY().apply { s = IDHCPARTYschemes.ID_HCPARTY; sv = "1.0"; value = nihii }) }
            }
            if(!m.ssin.isNullOrBlank()) {
                m.ssin?.let { ssin -> ids.add(IDHCPARTY().apply { s = IDHCPARTYschemes.INSS; sv = "1.0"; value = ssin })}
            }
            cds?.let {this.cds.addAll(it)}
			this.cds.addAll(
					listOf(CDHCPARTY().apply { s(CDHCPARTYschemes.CD_HCPARTY); value = "persphysician" })
            )

            if (this.cds.filter { it.s == CDHCPARTYschemes.CD_HCPARTY }.any {it.value.startsWith("pers")}) {
                firstname = m.firstName
                familyname = m.lastName
            } else {
                name = m.name?.trim().let { when(it) {
                    null, "" -> listOfNotNull(m.firstName, m.lastName).joinToString(" ").trim()
                    else -> it
                }}
            }

            addresses.addAll(makeAddresses(m.addresses))
            telecoms.addAll(makeTelecoms(m.addresses))
        }
    }

    fun createSpecialistParty(m: HealthcareParty, cds: List<CDHCPARTY>? = listOf() ): HcpartyType {
        return HcpartyType().apply {
            cds?.let {this.cds.addAll(it)}
            this.cds.addAll(
                if (m.specialityCodes?.size ?: 0 > 0){
                    m.specialityCodes.map { CDHCPARTY().apply { s(CDHCPARTYschemes.CD_HCPARTY); value = it.code } }
                } else if (m.speciality?: "" != ""){
                    listOf(CDHCPARTY().apply { s(CDHCPARTYschemes.CD_HCPARTY); value = m.speciality })
                }
                else
                    listOf(CDHCPARTY().apply { s(CDHCPARTYschemes.CD_HCPARTY); value = "persphysician" }))
        }
    }

	private fun isNihiiValid(nihii: String) = nihii.length == 11 &&
            (
                    ((97 - nihii.substring(0, 6).toLong() % 97) == nihii.substring(6, 8).toLong()) ||
                    ((89 - nihii.substring(0, 6).toLong() % 89) == nihii.substring(6, 8).toLong())
            )

    fun makePatient(p : Patient, config: Config): PersonType {
        val ssin = p.ssin?.replace("[^0-9]".toRegex(), "")?.let { if (org.taktik.icure.utils.Math.isNissValid(it)) it else null }
        return makePerson(p, config).apply {
            ids.clear()
            ssin?.let { ssin -> ids.add(IDPATIENT().apply { s = IDPATIENTschemes.ID_PATIENT; sv = "1.0"; value = ssin }) }
            ids.add(IDPATIENT().apply {s= IDPATIENTschemes.LOCAL; sl= "MF-ID"; sv= config.soft?.version; value= p.id})
        }
    }

    fun makePerson(p : Patient, config: Config) : PersonType {
        return makePersonBase(p, config).apply {
            p.dateOfDeath?.let { if(it != 0) deathdate = Utils.makeDateTypeFromFuzzyLong(it.toLong()) }
            p.placeOfBirth?.let { birthlocation = AddressTypeBase().apply { city= it }}
            p.placeOfDeath?.let { deathlocation = AddressTypeBase().apply { city= it }}
            p.profession?.let { profession = ProfessionType().apply { text = TextType().apply { l= "fr"; value = it } } }
            usuallanguage= p.languages.firstOrNull()
            addresses.addAll(makeAddresses(p.addresses))
            telecoms.addAll(makeTelecoms(p.addresses))
            if(!p.nationality.isNullOrBlank()) {
                p.nationality?.let { nat -> nationality = PersonType.Nationality().apply { cd = CDCOUNTRY().apply { s(CDCOUNTRYschemes.CD_COUNTRY); value = nat } } }
            }
        }
    }

    fun makePersonBase(p : Patient, config: Config) : PersonType {
        val ssin = p.ssin?.replace("[^0-9]".toRegex(), "")?.let { if (org.taktik.icure.utils.Math.isNissValid(it)) it else null }

        return PersonType().apply {
            ssin?.let { ssin -> ids.add(IDPATIENT().apply { s = IDPATIENTschemes.ID_PATIENT; sv = "1.0"; value = ssin }) }
            p.id?.let { id -> ids.add(IDPATIENT().apply { s = IDPATIENTschemes.LOCAL; sv = config.soft?.version; sl = "${config.soft?.name}-Person-Id"; value = id }) }
            firstnames.add(p.firstName)
            familyname= p.lastName
            sex= SexType().apply {cd = CDSEX().apply { s= "CD-SEX"; sv= "1.0"; value = p.gender?.let { CDSEXvalues.fromValue(it.name) } ?: CDSEXvalues.UNKNOWN }}
            p.dateOfBirth?.let { birthdate = Utils.makeDateTypeFromFuzzyLong(it.toLong()) }
            recorddatetime = makeXGC(p.modified)
        }
    }

    open fun createItemWithContent(svc: Service, idx: Int, cdItem: String, contents: List<ContentType>, localIdName: String = "iCure-Service", language: String) : ItemType? {
        return ItemType().apply {
            ids.add(IDKMEHR().apply {s = IDKMEHRschemes.ID_KMEHR; sv = "1.0"; value = idx.toString()})
            ids.add(IDKMEHR().apply {s = IDKMEHRschemes.LOCAL; sl = localIdName; sv = ICUREVERSION; value = svc.id })
            cds.add(CDITEM().apply {s(CDITEMschemes.CD_ITEM); value = cdItem } )
			svc.tags.find { t -> t.type == "CD-LAB" }?.let { cds.add(CDITEM().apply {s(CDITEMschemes.CD_LAB); value = it.code } ) }

            this.contents.addAll(filterEmptyContent(contents))
            lifecycle = LifecycleType().apply {cd = CDLIFECYCLE().apply {s = "CD-LIFECYCLE"
                value = if (ServiceStatus.isIrrelevant(svc.status) || (svc.closingDate ?: 99999999 <= FuzzyValues.getCurrentFuzzyDate())) {
					CDLIFECYCLEvalues.INACTIVE
                } else {
                    svc.tags.find { t -> t.type == "CD-LIFECYCLE" }?.let {
                        try {
                            CDLIFECYCLEvalues.fromValue(it.code)

                        } catch (e: java.lang.IllegalArgumentException) {
                            CDLIFECYCLEvalues.CORRECTED
                        }
                    }
                            ?: if(cdItem == "medication") CDLIFECYCLEvalues.PRESCRIBED else CDLIFECYCLEvalues.ACTIVE
                }
			} }
            if(cdItem == "medication") {
                svc.tags.find { it.type == "CD-TEMPORALITY" && it.code != null }?.let {
                    temporality = TemporalityType().apply {
                        cd = CDTEMPORALITY().apply { s = "CD-TEMPORALITY"; value = CDTEMPORALITYvalues.fromValue(it.code.toLowerCase()) }
                    }
                }
                svc.content.entries.mapNotNull { it.value.medicationValue }.firstOrNull()?.let { med ->
                    KmehrPrescriptionHelper.inferPeriodFromRegimen(med.regimen, med.frequency)?.let {
                        frequency = KmehrPrescriptionHelper.mapPeriodToFrequency(it)
                    }
                    duration = KmehrPrescriptionHelper.toDurationType(med.duration)
                    med.regimen?.let { intakes ->
                        if (intakes.isNotEmpty()) {
                            regimen = ItemType.Regimen().apply {
                                for (intake in intakes) {
                                    // choice day specification
                                    intake.dayNumber?.let { dayNumber -> daynumbersAndQuantitiesAndDates.add(BigInteger.valueOf(dayNumber.toLong())) } ?: daynumbersAndQuantitiesAndDates.add(BigInteger.valueOf(1.toLong()))
                                    intake.date?.let { d -> daynumbersAndQuantitiesAndDates.add(Utils.makeXMLGregorianCalendarFromFuzzyLong(d)) }
                                    intake.weekday?.let { day ->
                                        daynumbersAndQuantitiesAndDates.add(ItemType.Regimen.Weekday().apply {
                                            day.weekday?.let { dayOfWeek ->
                                                cd = CDWEEKDAY().apply { s = "CD-WEEKDAY"; value = CDWEEKDAYvalues.fromValue(dayOfWeek.code) }
                                            }
                                            day.weekNumber?.let { n -> weeknumber = BigInteger.valueOf(n.toLong()) }
                                        })
                                    }
                                    // choice time of day
                                    daynumbersAndQuantitiesAndDates.add(KmehrPrescriptionHelper.toDaytime(intake))

                                    // mandatory quantity
                                    intake.administratedQuantity?.let { drugQuantity ->
                                        daynumbersAndQuantitiesAndDates.add(AdministrationquantityType().apply {
                                            decimal = drugQuantity.quantity?.let { BigDecimal(it) }
                                            drugQuantity.administrationUnit?.let { drugUnit ->
                                                unit = AdministrationunitType().apply {
                                                    cd = CDADMINISTRATIONUNIT().apply {
                                                        s = "CD-ADMINISTRATIONUNIT"
                                                        sv = "1.2"
                                                        value = drugUnit.code
                                                    }
                                                }
                                            }
                                        })
                                    }
                                }
                            }
                        }
                    }
                    if (this.regimen == null) {
                        (med.posology ?: med.posologyText)?.let {
                            this.posology = ItemType.Posology().apply { text = TextType().apply { l = language; value = it } }
                        }
                    }
                    (med.instructionForPatient)?.let {
                        this.instructionforpatient = TextType().apply { l = language; value = it }
                    }
                    med.renewal?.let {
                        renewal = RenewalType().apply {
                            it.decimal?.let { decimal = BigDecimal(it.toLong()) }
                            duration = KmehrPrescriptionHelper.toDurationType(it.duration)
                        }
                    }
                    med.drugRoute?.let { c ->
                        route = RouteType().apply { cd = CDDRUGROUTE().apply { s = "CD-DRUG-ROUTE"; value = c } }
                    }
                    med.temporality?.let{ c ->
                        temporality = TemporalityType().apply { cd = CDTEMPORALITY().apply { s = "CD-TEMPORALITY";
                            value = CDTEMPORALITYvalues.fromValue(c)
                        } }
                    }
                }
            }

            isIsrelevant = ServiceStatus.isRelevant(svc.status)
            beginmoment = (svc.content.entries.mapNotNull { it.value.medicationValue }.firstOrNull()?.beginMoment ?: svc.valueDate ?: svc.openingDate)?.let { if(it != 0L) Utils.makeMomentTypeDateFromFuzzyLong(it) else null }
            endmoment = (svc.closingDate ?: svc.content.entries.mapNotNull { it.value.medicationValue }.firstOrNull()?.endMoment)?.let { if(it != 0L) Utils.makeMomentTypeDateFromFuzzyLong(it) else null }
            recorddatetime = makeXGC(svc.modified ?: svc.created)
        }
    }

    open fun getOmissionOfMedicalDataWill(idx: Int): ItemType {
        return ItemType().apply {
            ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; sv = "1.0"; value = idx.toString()})
            cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value = CDITEMvalues.PATIENTWILL.value() } )
            contents.add(ContentType().apply { cds.add(CDCONTENT().apply { s(CDCONTENTschemes.CD_PATIENTWILL); value = CDPATIENTWILLvalues.OMISSIONOFMEDICALDATA.value() }) })
            lifecycle = LifecycleType().apply { cd = CDLIFECYCLE().apply { s = "CD-LIFECYCLE"; value = CDLIFECYCLEvalues.ACTIVE } }
            isIsrelevant = true
            beginmoment = Utils.makeMomentTypeDateFromFuzzyLong(FuzzyValues.getCurrentFuzzyDate())
            endmoment = Utils.makeMomentTypeDateFromFuzzyLong(FuzzyValues.getCurrentFuzzyDate())
            recorddatetime = makeXGC(Instant.now().toEpochMilli())
        }
    }

	private fun filterEmptyContent(contents: List<ContentType>) = contents.filter {
		it.isBoolean != null || it.cds?.size ?: 0 > 0 || it.bacteriology != null || it.compoundprescription != null ||
			it.location != null || it.lnks?.size ?: 0 > 0 || it.bacteriology != null || it.ecg != null || it.holter != null ||
			it.medication != null || it.compoundprescription != null || it.substanceproduct != null || it.medicinalproduct != null ||
			it.error != null || it.incapacity != null || it.insurance != null || it.person != null || it.hcparty != null ||
			it.date != null || it.time != null || it.yearmonth != null || it.year != null || it.texts?.size ?: 0 > 0 ||
			it.unsignedInt != null || it.decimal != null || it.cds?.size ?: 0 > 0 || it.ids?.size ?: 0 > 0 ||
			it.unit != null || it.minref != null || it.maxref != null || it.refscopes?.size ?: 0 > 0
	}

	open fun createItemWithContent(he : HealthElement, idx : Int, cdItem : String, contents : List<ContentType>) : ItemType? {
        return ItemType().apply {
            ids.add(IDKMEHR().apply {s = IDKMEHRschemes.ID_KMEHR; sv = "1.0"; value = idx.toString()})
            ids.add(IDKMEHR().apply {s = IDKMEHRschemes.LOCAL; sl = "iCure-HealthElement"; sv = ICUREVERSION; value = he.id })
            cds.add(CDITEM().apply {s(CDITEMschemes.CD_ITEM); value = cdItem } )

            this.contents.addAll(filterEmptyContent(contents))
            lifecycle = LifecycleType().apply {cd = CDLIFECYCLE().apply {s = "CD-LIFECYCLE"
                value = if (ServiceStatus.isIrrelevant(he.status) || (!ServiceStatus.isActive(he.status)) || (he.closingDate ?: 0 > FuzzyValues.getCurrentFuzzyDate()))
					CDLIFECYCLEvalues.INACTIVE
                else
                    he.tags.find { t -> t.type == "CD-LIFECYCLE" }?.let { CDLIFECYCLEvalues.fromValue(it.code) } ?: CDLIFECYCLEvalues.ACTIVE
			} }

            certainty = he.tags.find { t -> t.type == "CD-CERTAINTY" && !t.code.isNullOrBlank()}?.let {
                CertaintyType().apply {
                    cd = CDCERTAINTY().apply { s = "CD-CERTAINTY"; value = CDCERTAINTYvalues.fromValue(it.code) }
                }
            }
            isIsrelevant = ServiceStatus.isRelevant(he.status)
            beginmoment = (he.valueDate ?: he.openingDate).let { if(it != 0L) Utils.makeMomentTypeFromFuzzyLong(it) else null }
            endmoment = he.closingDate?.let { if(it != 0L) Utils.makeMomentTypeFromFuzzyLong(it) else null}
            recorddatetime = makeXGC(he.modified)
        }
    }

    fun makeTelecoms(addresses: Collection<Address>?): List<TelecomType> {
        return addresses?.filter { it.addressType != null }?.flatMapTo(ArrayList<TelecomType>()) { a ->
            a.telecoms?.filter {it.telecomNumber?.length?:0>0}?.map {
                TelecomType().apply {
                    cds.add(CDTELECOM().apply { s(CDTELECOMschemes.CD_ADDRESS); value = a.addressType?.name ?: "home"})
                    cds.add(CDTELECOM().apply { s(CDTELECOMschemes.CD_TELECOM); value = it.telecomType?.name ?: "phone"})
                    telecomnumber = it.telecomNumber
                }
            } ?: emptyList()
        } ?: emptyList()
    }

    fun makeDummyTelecom(): List<TelecomType>{
        var lst = ArrayList<TelecomType>()
        lst.add(TelecomType().apply {
            cds.add(CDTELECOM().apply { s(CDTELECOMschemes.CD_ADDRESS); value = "home"})
            cds.add(CDTELECOM().apply { s(CDTELECOMschemes.CD_TELECOM); value = "phone"})
            telecomnumber = "00.00.00"
        })
        return lst
    }

    fun makeAddresses(addresses: Collection<Address>?): List<AddressType> {
        return addresses?.filter { it.addressType != null && it.postalCode != null && it.street != null }?.mapTo(ArrayList<AddressType>()) { a ->
            AddressType().apply {
                cds.add(CDADDRESS().apply { s(CDADDRESSschemes.CD_ADDRESS); value = a.addressType!!.name })
				country = if (a.country?.length ?: 0 > 0) mapToCountryCode(a.country!!)?.let { natCode -> CountryType().apply { cd = CDCOUNTRY().apply { s(CDCOUNTRYschemes.CD_FED_COUNTRY); value = natCode }}} else CountryType().apply { cd = CDCOUNTRY().apply { s(CDCOUNTRYschemes.CD_FED_COUNTRY); value = "be" } }
                zip = a.postalCode ?: "0000"
                street = a.street ?: "unknown"
                if(!a.houseNumber.isNullOrBlank()){a.houseNumber?.let{ housenumber = a.houseNumber}}
                if(!a.postboxNumber.isNullOrBlank()){a.postboxNumber?.let{ postboxnumber = a.postboxNumber}}
                city = a.city ?: "unknown"

            }
        } ?: emptyList()
    }

    fun makeDummyAddress(): List<AddressType>{
        var lst = ArrayList<AddressType>()
        lst.add(AddressType().apply {
            cds.add(CDADDRESS().apply { s(CDADDRESSschemes.CD_ADDRESS); value = "work" })
            country = CountryType().apply { cd = CDCOUNTRY().apply { s(CDCOUNTRYschemes.CD_FED_COUNTRY); value = "be" } }
            zip = "0000"
            street = "unknown"
            city = "unknown"
        })
        return lst
    }

    fun  makeXGC(date: Long?, unsetMillis: Boolean = false): XMLGregorianCalendar? {
        return date?.let {
            DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar.getInstance().apply { time = Date(date) } as GregorianCalendar).apply {
                timezone = DatatypeConstants.FIELD_UNDEFINED
                if (unsetMillis) {
                    millisecond = DatatypeConstants.FIELD_UNDEFINED
                }
            }
        }
    }

    fun makeContent(language: String, content: Content): ContentType? {
        return (content.booleanValue ?: content.numberValue ?: content.stringValue ?: content.instantValue
        ?: content.measureValue ?: content.medicationValue ?: content.binaryValue ?: content.documentId).let {
            ContentType().apply {
                isBoolean = content.booleanValue
                content.numberValue?.let { decimal = BigDecimal.valueOf(it) }
                content.stringValue?.let {
                    if (content.binaryValue == null && content.documentId == null) {
                        texts.add(TextType().apply { l = language; value = content.stringValue })
                    }
                }
                Utils.makeXGC(content.instantValue?.toEpochMilli(), true)?.let { date = it; time = it; }
                content.measureValue?.let { mv ->
                    mv.unitCodes?.find { it.type == "CD-UNIT" }?.code?.let { unitCode ->
                        if (unitCode.isNotEmpty()) {
                            unit = UnitType().apply { cd = CDUNIT().apply { s(CDUNITschemes.CD_UNIT); value = unitCode } }
                        }
                    }
                    if (unit == null) {
                        mv.unit?.let { getCode(it)?.let { unit = UnitType().apply { cd = CDUNIT().apply { s(CDUNITschemes.CD_UNIT); value = it.code } } } }
                    }
                    mv.value?.let { decimal = BigDecimal.valueOf(it) }
                }
                content.medicationValue?.medicinalProduct?.let {
                    medicinalproduct = MedicinalProductType().apply {
                        intendedname = content.medicationValue?.medicinalProduct?.intendedname
                        intendedcds.add(CDDRUGCNK().apply { s(CDDRUGCNKschemes.CD_DRUG_CNK); /* TODO set versions in jaxb classes */ sv = "01-2016"; value = content.medicationValue?.medicinalProduct?.intendedcds?.find { it.type == "CD-DRUG-CNK" }?.code })
                    }
                }
                content.medicationValue?.substanceProduct?.let {
                    substanceproduct = ContentType.Substanceproduct().apply {
                        intendedname = content.medicationValue?.substanceProduct?.intendedname
                        intendedcd = CDINNCLUSTER().apply { s(CDINNCLUSTERschemes.CD_INNCLUSTER); /* TODO set versions in jaxb classes */ sv = "01-2016"; value = content.medicationValue?.substanceProduct?.intendedcds?.find { it.type == "CD-INNCLUSTER" }?.code }
                    }
                }
                content.medicationValue?.compoundPrescription?.let {
                    compoundprescription = CompoundprescriptionType().apply { this.content.add(ObjectFactory().createCompoundprescriptionTypeMagistraltext(TextType().apply { l = language; value = content.medicationValue?.compoundPrescription })) }
                }
                content.binaryValue?.let {
                    if (Arrays.equals(content.binaryValue.slice(0..4).toByteArray(), "{\\rtf".toByteArray())) {
                        texts.add(TextType().apply {
                            l = language; value = RTFEditorKit().let {
                            val document = it.createDefaultDocument()
                            it.read(content.binaryValue.inputStream(), document, 0)
                            document.getText(0, document.length) ?: ""
                        }
                        })
                    } else {
                        lnks.add(LnkType().apply { type = CDLNKvalues.MULTIMEDIA; mediatype = CDMEDIATYPEvalues.APPLICATION_PDF; value = content.binaryValue })
                    }
                }
                content.documentId?.let {
                    try {
                        documentLogic?.get(it)?.let { d -> d.attachment?.let { lnks.add(LnkType().apply { type = CDLNKvalues.MULTIMEDIA; mediatype = documentMediaType(d); value = it }) } }
                    } catch (e: Exception) {
                        log.warn("Document with id ${it} could not be loaded", e)
                    }
                }
            }
        }.let { if (it.isBoolean != null || it.date != null || it.time != null || it.lnks.size > 0 || it.compoundprescription != null || it.substanceproduct != null || it.medicinalproduct != null || it.cds.size > 0 || it.decimal != null || it.texts.size > 0) it else null }
    }

    protected fun documentMediaType(d: Document) =
            (listOf(d.mainUti) + d.otherUtis).map {
                UTI.get(it)?.mimeTypes?.firstOrNull()?.let {
                    try {
                        CDMEDIATYPEvalues.fromValue(it)
                    } catch (ignored: IllegalArgumentException) {
                        null
                    }
                }
            }.filterNotNull().firstOrNull()

    fun fillMedicationItem(svc : Service, item : ItemType, lang : String) {
        addServiceCodesAndTags(svc, item, true, listOf("CD-ATC"), null, listOf("CD-TRANSACTION", "CD-TRANSACTION-TYPE"))

        val c = svc.content[lang]?.let { if (it.medicationValue?.let { it.medicinalProduct ?: it.substanceProduct ?: it.compoundPrescription } != null) it else null }
                ?: svc.content.values.find { it.medicationValue?.let { it.medicinalProduct ?: it.substanceProduct ?: it.compoundPrescription } != null }

        c.let { cnt ->
            item.contents.add(0, ContentType().apply {texts.add(TextType().apply {l=lang; value= cnt?.medicationValue?.medicinalProduct?.intendedname?:cnt?.medicationValue?.substanceProduct?.intendedname?:cnt?.medicationValue?.compoundPrescription?:cnt?.stringValue?:""})})
            cnt?.medicationValue?.substanceProduct.let {sp->
                cnt?.medicationValue?.duration?.let { d ->
                    item.duration = DurationType().apply { decimal= BigDecimal.valueOf(d.value); unit = d.unit?.code?.let {
                        TimeunitType().apply { cd=CDTIMEUNIT().apply { s(CDTIMEUNITschemes.CD_TIMEUNIT); value=it } }
                    }}
                }
            }
            cnt?.medicationValue?.posology?.let {
                item.posology = ItemType.Posology().apply { text = TextType().apply { l = lang; value = it } }
            }
            if(item.posology == null) {
                cnt?.medicationValue?.posologyText?.let {
                    item.posology = ItemType.Posology().apply { text = TextType().apply { l = lang; value = it } }
                }
            }
            cnt?.medicationValue?.instructionForPatient?.let {
                item.instructionforpatient = TextType().apply { l = lang; value = it }
            }
        }
    }

    open fun addServiceCodesAndTags(svc: Service, item: ItemType, skipCdItem: Boolean = true, restrictedTypes: List<String>? = null, uniqueTypes: List<String>? = null, excludedTypes: List<String>? = listOf("CD-TRANSACTION", "CD-TRANSACTION-TYPE")) {
        ContentType().apply {
            svc.codes.forEach { c ->
                try {
                    val cdt = CDCONTENTschemes.fromValue(c.type)
                    if ((restrictedTypes == null || restrictedTypes.contains(c.type)) && (excludedTypes == null || !excludedTypes.contains(c.type))) {
                        if (uniqueTypes == null || !uniqueTypes.contains(c.type) || this.cds.find { cc -> cdt == cc.s } == null) {
                            this.cds.add(CDCONTENT().apply { s(cdt);  value = c.code })
                        } else if ((restrictedTypes == null || restrictedTypes.contains("LOCAL")) && (excludedTypes == null || !excludedTypes.contains("LOCAL"))) {
                            this.cds.add(CDCONTENT().apply { s(CDCONTENTschemes.LOCAL); sl = c.type; dn = c.type;  value = c.code })
                        }
                    }
                } catch (ignored : IllegalArgumentException) {
                    if ((restrictedTypes == null || restrictedTypes.contains("LOCAL")) && (excludedTypes == null || !excludedTypes.contains("LOCAL"))) {
                        this.cds.add(CDCONTENT().apply { s(CDCONTENTschemes.LOCAL); sl = c.type; dn = c.type;  value = c.code })
                    }
                }
            }

            for (c in svc.tags) {
                try {
                    val idt = CDITEMschemes.fromValue(c.type)
                    val prevIcc = item.cds.find { cc -> idt == cc.s }
                    if (prevIcc == null) {
                        item.cds.add(CDITEM().apply { s(idt);  value = c.code })
                    } else if (prevIcc.value != c.code) {
                        item.cds.add(CDITEM().apply { s(CDITEMschemes.LOCAL); sl = c.type; dn = c.type;  value = c.code })
                    }
                } catch (ignored: IllegalArgumentException) {
                    //noinspection GroovyUnusedCatchParameter
                    try {
                        val cdt = CDCONTENTschemes.fromValue(c.type)
                        if ((restrictedTypes == null || restrictedTypes.contains(c.type)) && (excludedTypes == null || !excludedTypes.contains(c.type))) {
                            val prevCc = this.cds.find { cc -> cdt == cc.s }
                            if (uniqueTypes == null || !uniqueTypes.contains(c.type) || prevCc == null) {
                                this.cds.add(CDCONTENT().apply { s(cdt);  value = c.code })
                            } else if (prevCc.value != c.code && ((restrictedTypes == null || restrictedTypes.contains("LOCAL")) && (excludedTypes == null || !excludedTypes.contains("LOCAL")))) {
                                this.cds.add(CDCONTENT().apply { s(CDCONTENTschemes.LOCAL); sl = c.type; dn = c.type;  value = c.code })
                            }
                        }
                    } catch (ignoredAsWell: IllegalArgumentException) {
                        if ((restrictedTypes == null || restrictedTypes.contains("LOCAL")) && (excludedTypes == null || !excludedTypes.contains("LOCAL"))) {
                            this.cds.add(CDCONTENT().apply { s(CDCONTENTschemes.LOCAL); sl = c.type; dn = c.type;  value = c.code })
                        }
                    }
                }
            }

            var lbl = svc.label
            if (lbl != null) {
                if (svc.content.values.find { it.medicationValue != null } != null) {
                    lbl += "{m}"
                } else if (svc.content.values.find { it.measureValue != null } != null) {
                    lbl += "{v}"
                } else if (svc.content.values.find { it.stringValue != null } != null) {
                    lbl += "{s}"
                } else if (svc.content.values.find { it.numberValue != null } != null) {
                    lbl += "{n}"
                } else if (svc.content.values.find { it.instantValue != null } != null) {
                    lbl += "{d}"
                } else if (svc.content.values.find { it.binaryValue != null || it.documentId != null } != null) {
                    lbl += "{x}"
                } else if (svc.content.values.find { it.booleanValue != null } != null) {
                    lbl += "{b}"
                }
                item.cds.add(CDITEM().apply { s(CDITEMschemes.LOCAL); sl = "iCure-Label"; dn = "iCure service label"; value = lbl })
            }

            if (this.cds.size > 0) {
                item.contents.add(this)
            }
        }
    }

    fun createFolder(sender: HealthcareParty, patient: Patient, cdTransaction: String, transactionType: CDTRANSACTIONschemes, dem: PlanOfAction, ssc: Form, text: String?, attachmentDocumentIds: List<String>, config: Config): FolderType {
        return FolderType().apply {
            ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; sv = "1.0"; value = 1.toString() })
            this.patient = makePerson(patient, config)
            transactions.add(TransactionType().apply {
                cds.add(CDTRANSACTION().apply { s(transactionType);  value = cdTransaction })
                author = AuthorType().apply { hcparties.add(createParty(sender, emptyList())) }
                ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; sv = "1.0"; value = "1" })
                ids.add(IDKMEHR().apply { s = IDKMEHRschemes.LOCAL; sl = "iCure-Item"; sv = config.soft?.version ?: "1.0"; value = ssc.id ?: dem.id ?: patient.id })
                recorddatetime = makeXGC(ssc.created ?: ((dem.openingDate ?: dem.valueDate)?.let { FuzzyValues.getDateTime(it) } ?: LocalDateTime.now()).atZone(ZoneId.systemDefault()).toEpochSecond()*1000)
                isIscomplete = true
                isIsvalidated = true

                if (text?.length ?: 0 >0) headingsAndItemsAndTexts.add(TextType().apply { l = "fr"; value = text })
                attachmentDocumentIds.forEach { id ->
                    val d = documentLogic?.get(id)
                    d?.attachment.let {
                        headingsAndItemsAndTexts.add(LnkType().apply {
                            type = CDLNKvalues.MULTIMEDIA; mediatype = documentMediaType(d!!); value = d.attachment
                        })
                    }
                }
            })
        }
    }

    fun initializeMessage(sender : HealthcareParty, config: Config) : Kmehrmessage {
        return Kmehrmessage().apply {
            header = HeaderType().apply {
                standard = StandardType().apply { cd = CDSTANDARD().apply { s = "CD-STANDARD"; value = STANDARD }
                    val filetype = if (config.format == Config.Format.PMF) {
                        CDMESSAGEvalues.GPPATIENTMIGRATION
                    } else if(config.format == Config.Format.SMF) {
                        CDMESSAGEvalues.GPSOFTWAREMIGRATION
                    } else { null }
                    filetype?.let {
                        specialisation = StandardType.Specialisation().apply { cd = CDMESSAGE().apply { s = "CD-MESSAGE"; value = filetype }; version = SMF_VERSION }
                    }
                }
                ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; sv = "1.0"; value = (sender.nihii ?: sender.id) + "." + (config._kmehrId ?: System.currentTimeMillis()) })
                makeXGC(Instant.now().toEpochMilli()).let {
                    date = it
                    time = it
                }
                this.sender = SenderType().apply {
                    hcparties.add(createParty(sender, emptyList()))
                    hcparties.add(HcpartyType().apply {
						ids.add(IDHCPARTY().apply { s = IDHCPARTYschemes.LOCAL; sl = config.soft?.name; sv = config.soft?.version; value = "${config.soft?.name}-${config.soft?.version}" })
						cds.add(CDHCPARTY().apply { s(CDHCPARTYschemes.CD_HCPARTY); value = "application" })
						name = config.soft?.name
					})
                }
            }
        }
    }

    fun mapToCountryCode(country: String?): String? {
        if (country == null) {return null }
        if (codeLogic!!.isValid(Code("CD-FED-COUNTRY", country.toLowerCase(), "1"))) {
            return country.toLowerCase()
        } else {
            try {
                return codeLogic!!.getCodeByLabel(country, "CD-FED-COUNTRY").code
            } catch (e:IllegalArgumentException) {
                return null
            }
        }
    }

    fun exportContactReportDynamic(patient: Patient, sender: HealthcareParty, recipient: Any?, dem: PlanOfAction, ssc: Form, text: String, attachmentDocIds: List<String>, config: Config, stream: OutputStream) {
        if (recipient is HealthcareParty) {
            exportContactReport(patient, sender, recipient, dem, ssc, text, attachmentDocIds, config, stream)
        } else if (recipient == null) {
            exportContactReport(patient, sender, null, dem, ssc, text, attachmentDocIds, config, stream)
        }  else {
            throw IllegalArgumentException("Recipient is not a doctor; a hospital or a generic recipient")
        }
    }

    fun exportContactReport(patient: Patient, sender: HealthcareParty, recipient: HealthcareParty?, dem: PlanOfAction, ssc: Form, text: String, attachmentDocIds: List<String>, config: Config, stream: OutputStream) {
        val message = initializeMessage(sender, config)

        message.header.recipients.add(RecipientType().apply {
            hcparties.add(recipient?.let { createParty(it, emptyList()) } ?: createParty(emptyList(), listOf(CDHCPARTY().apply { s(CDHCPARTYschemes.CD_APPLICATION) }), "gp-software-migration"))
        })

        val folder = createFolder (sender, patient, "contactreport", CDTRANSACTIONschemes.CD_TRANSACTION, dem, ssc, text, attachmentDocIds, config)
        message.folders.add(folder)

        val jaxbMarshaller = JAXBContext.newInstance(Kmehrmessage::class.java).createMarshaller()
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
        jaxbMarshaller.marshal(message, stream)
    }

    fun exportReportDynamic(patient: Patient, sender: HealthcareParty, recipient: Any?, dem: PlanOfAction, ssc: Form, text: String, attachmentDocIds: List<String>, config: Config, stream: OutputStream) {
        if (recipient is HealthcareParty) {
            exportReport(patient, sender, recipient, dem, ssc, text, attachmentDocIds, config, stream)
        } else if (recipient == null) {
            exportReport(patient, sender, null, dem, ssc, text, attachmentDocIds, config, stream)
        } else {
            throw IllegalArgumentException("Recipient is not a doctor; a hospital or a generic recipient")
        }
    }

    fun exportReport(patient : Patient, sender : HealthcareParty, recipient : HealthcareParty?, dem : PlanOfAction, ssc : Form, text : String, attachmentDocIds : List<String>, config: Config, stream : OutputStream) {
        val message = initializeMessage(sender, config)

        message.header.recipients.add(RecipientType().apply {
            hcparties.add(recipient?.let { createParty(it, emptyList()) } ?: createParty(emptyList(), listOf(CDHCPARTY().apply { s(CDHCPARTYschemes.CD_APPLICATION) }), "gp-software-migration"))
        })

        val folder = createFolder (sender, patient, "report", CDTRANSACTIONschemes.CD_TRANSACTION, dem, ssc, text, attachmentDocIds, config)
        message.folders.add(folder)

        val jaxbMarshaller = JAXBContext.newInstance(Kmehrmessage::class.java).createMarshaller()
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
        jaxbMarshaller.marshal(message, stream)

    }

    fun exportNoteDynamic(patient: Patient, sender: HealthcareParty, recipient: Any?, dem: PlanOfAction, ssc: Form, text: String, attachmentDocIds: List<String>, config: Config, stream: OutputStream) {
        if (recipient is HealthcareParty) {
            exportNote(patient, sender, recipient, dem, ssc, text, attachmentDocIds, config, stream)
        } else if (recipient == null) {
            exportNote(patient, sender, null, dem, ssc, text, attachmentDocIds, config, stream)
        } else {
            throw IllegalArgumentException("Recipient is not a doctor; a hospital or a generic recipient")
        }
    }

    fun exportNote(patient: Patient, sender: HealthcareParty, recipient: HealthcareParty?, dem: PlanOfAction, ssc: Form, text: String, attachmentDocIds: List<String>, config: Config, stream: OutputStream) {
        val message = initializeMessage(sender, config)

        message.header.recipients.add(RecipientType().apply {
            hcparties.add(recipient?.let { createParty(it, emptyList()) } ?: createParty(emptyList(), listOf(CDHCPARTY().apply { s(CDHCPARTYschemes.CD_APPLICATION) }), "gp-software-migration"))
        })

        val folder = createFolder(sender, patient, "note", CDTRANSACTIONschemes.CD_TRANSACTION, dem, ssc, text, attachmentDocIds, config)
        message.folders.add(folder)

        val jaxbMarshaller = JAXBContext.newInstance(Kmehrmessage::class.java).createMarshaller()
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
        jaxbMarshaller.marshal(message, stream)

    }

    fun exportPrescriptionDynamic(patient: Patient, sender: HealthcareParty, recipient: Any?, dem: PlanOfAction, ssc: Form, text: String, attachmentDocIds: List<String>, config: Config, stream: OutputStream) {
        if (recipient is HealthcareParty) {
            exportPrescription(patient, sender, recipient, dem, ssc, text, attachmentDocIds, config, stream)
        } else if (recipient == null) {
            exportPrescription(patient, sender, null, dem, ssc, text, attachmentDocIds, config, stream)
        } else {
            throw IllegalArgumentException("Recipient is not a doctor; a hospital or a generic recipient")
        }
    }

    fun exportPrescription(patient: Patient, sender: HealthcareParty, recipient: HealthcareParty?, dem: PlanOfAction, ssc: Form, text: String, attachmentDocIds: List<String>, config: Config, stream: OutputStream) {
        val message = initializeMessage(sender, config)

        message.header.recipients.add(RecipientType().apply {
            hcparties.add(recipient?.let { createParty(it, emptyList()) } ?: createParty(emptyList(), listOf(CDHCPARTY().apply { s(CDHCPARTYschemes.CD_APPLICATION) }), "gp-software-migration"))
        })

        val folder = createFolder(sender, patient, "prescription", CDTRANSACTIONschemes.CD_TRANSACTION, dem, ssc, text, attachmentDocIds, config)
        message.folders.add(folder)

        val jaxbMarshaller = JAXBContext.newInstance(Kmehrmessage::class.java).createMarshaller()
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
        jaxbMarshaller.marshal(message, stream)
    }

    protected fun CDITEM.s(scheme: CDITEMschemes) {
        s = scheme
        sv = scheme.version()?:"1.0"
    }

    protected fun CDTELECOM.s(scheme: CDTELECOMschemes) {
        s = scheme
        sv = scheme.version()?:"1.0"
    }

    protected fun CDADDRESS.s(scheme: CDADDRESSschemes) {
        s = scheme
        sv = scheme.version()?:"1.0"
    }

    protected fun CDCOUNTRY.s(scheme: CDCOUNTRYschemes) {
        s = scheme
        sv = scheme.version()?:"1.0"
    }

    protected fun CDCONTENT.s(scheme: CDCONTENTschemes) {
        s = scheme
        sv = scheme.version()?:"1.0"
    }

    protected fun CDHCPARTY.s(scheme: CDHCPARTYschemes) {
        s = scheme
        sv = scheme.version()?:"1.0"
    }

    protected fun CDTRANSACTION.s(scheme: CDTRANSACTIONschemes) {
        s = scheme
        sv = scheme.version()?:"1.0"
    }

    protected fun CDINNCLUSTER.s(scheme: CDINNCLUSTERschemes) {
        s = scheme
        sv = scheme.version()?:"1.0"
    }

    protected fun CDDRUGCNK.s(scheme: CDDRUGCNKschemes) {
        s = scheme
        sv = scheme.version()?:"1.0"
    }

    protected fun CDUNIT.s(scheme: CDUNITschemes) {
        s = scheme
        sv = scheme.version()?:"1.0"
    }

    protected fun CDTIMEUNIT.s(scheme: CDTIMEUNITschemes) {
        s = scheme
        sv = scheme.version()?:"1.0"
    }


    fun idKmehr(index: Int) = IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; sv = "1.0"; value = index.toString() }

	fun localIdKmehrElement(itemIndex: Int, config: Config): IDKMEHR {
		return localIdKmehr("Element", (itemIndex + 1).toString(), config)
    }

	fun localIdKmehr(itemType: String, id: String?, config: Config): IDKMEHR {
		return IDKMEHR().apply {
			s = IDKMEHRschemes.LOCAL
			sv = config.soft?.version
			sl = "${config.soft?.name}-$itemType-Id"
			value = id
		}
    }

	fun getCode(key:String) : Code? {
		synchronized(unitCodes) {
		if (unitCodes.size==0) {
				codeLogic!!.findCodesBy("CD-UNIT", null, null).forEach { unitCodes[it.id] = it }
		}}
		return unitCodes[key]
	}

	companion object {
		const val SMF_VERSION = "2.3"
	}

}
