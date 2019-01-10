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
import org.taktik.commons.uti.UTI
import org.taktik.icure.be.drugs.logic.DrugsLogic
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.Utils
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.*
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.dt.v1.TextType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.id.v1.*
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.*
import org.taktik.icure.entities.Form
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.Content
import org.taktik.icure.entities.embed.PlanOfAction
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.logic.CodeLogic
import org.taktik.icure.logic.ContactLogic
import org.taktik.icure.logic.DocumentLogic
import org.taktik.icure.logic.HealthElementLogic
import org.taktik.icure.logic.HealthcarePartyLogic
import org.taktik.icure.logic.MainLogic
import org.taktik.icure.logic.PatientLogic
import org.taktik.icure.logic.SessionLogic
import org.taktik.icure.logic.UserLogic
import org.taktik.icure.logic.impl.filter.Filters
import org.taktik.icure.utils.FuzzyValues
import java.io.OutputStream
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.datatype.DatatypeConstants
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar

open class KmehrExport {
    @Autowired var patientLogic: PatientLogic? = null
    @Autowired var codeLogic: CodeLogic? = null
    @Autowired var drugsLogic: DrugsLogic? = null
    @Autowired var healthElementLogic: HealthElementLogic? = null
    @Autowired var healthcarePartyLogic: HealthcarePartyLogic? = null
    @Autowired var contactLogic: ContactLogic? = null
    @Autowired var documentLogic: DocumentLogic? = null
    @Autowired var mainLogic: MainLogic? = null
    @Autowired var sessionLogic: SessionLogic? = null
    @Autowired var userLogic: UserLogic?= null
    @Autowired var filters: Filters? = null
    @Autowired var mapper: MapperFacade? = null

    val unitCodes = HashMap<String,Code>()

    internal val STANDARD = "20161201"
    internal val ICUREVERSION = "4.0.0"
    internal val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd")
    internal open val log = LogFactory.getLog(KmehrExport::class.java)

    fun createParty(ids : List<IDHCPARTY>, cds : List<CDHCPARTY>, name : String) : HcpartyType {
        return HcpartyType().apply { this.ids.addAll(ids); this.cds.addAll(cds); this.name = name }
    }

    fun createPartyWithAddresses(m : HealthcareParty, cds : List<CDHCPARTY>? = listOf()) : HcpartyType {
        return createParty(m, cds).apply {
            addresses.addAll(makeAddresses(m.addresses))
            telecoms.addAll(makeTelecoms(m.addresses))
        }
    }

    fun createParty(m : HealthcareParty, cds : List<CDHCPARTY>? = listOf() ) : HcpartyType {
        return HcpartyType().apply {
            m.nihii?.let { nihii -> ids.add(IDHCPARTY().apply { s = IDHCPARTYschemes.ID_HCPARTY; sv = "1.0"; value = nihii }) }
            m.ssin?.let { ssin -> ids.add(IDHCPARTY().apply { s = IDHCPARTYschemes.INSS; sv = "1.0"; value = ssin }) }
            cds?.let {this.cds.addAll(it)}
			this.cds.addAll(
				if (m.specialityCodes?.size ?: 0 > 0)
					m.specialityCodes.map { CDHCPARTY().apply { s = CDHCPARTYschemes.CD_HCPARTY; sv = "1.0"; value = it.code } }
				else
					listOf(CDHCPARTY().apply { s = CDHCPARTYschemes.CD_HCPARTY; sv = "1.0"; value = "persphysician" }))

            firstname = m.firstName
            familyname = m.lastName
            addresses.addAll(makeAddresses(m.addresses))
            telecoms.addAll(makeTelecoms(m.addresses))
        }
    }

    fun makePatient(p : Patient, config: Config): PersonType {
        val ssin = p.ssin?.replace("[^0-9]".toRegex(), "")?.let { if (org.taktik.icure.utils.Math.isNissValid(it)) it else null }
        return makePerson(p, config).apply {
            ids.clear()
            ssin?.let { ssin -> ids.add(IDPATIENT().apply { s = IDPATIENTschemes.ID_PATIENT; sv = "1.0"; value = ssin }) }
            ids.add(IDPATIENT().apply {s= IDPATIENTschemes.LOCAL; sl= "MF-ID"; sv= config.soft.version; value= p.id})
        }
    }

    fun makePerson(p : Patient, config: Config) : PersonType {
        return makePersonBase(p, config).apply {
            p.dateOfDeath?.let { deathdate = Utils.makeDateTypeFromFuzzyLong(it.toLong()) }
            p.placeOfBirth?.let { birthlocation = AddressTypeBase().apply { city= it }}
            p.placeOfDeath?.let { deathlocation = AddressTypeBase().apply { city= it }}
            p.profession?.let { profession = ProfessionType().apply { text = TextType().apply { l= "fr"; value = it } } }
            usuallanguage= p.languages.firstOrNull()
            addresses.addAll(makeAddresses(p.addresses))
            telecoms.addAll(makeTelecoms(p.addresses))
            p.nationality?.let { nat -> nationality = PersonType.Nationality().apply { cd = CDCOUNTRY().apply { s= CDCOUNTRYschemes.CD_COUNTRY; sv= "1.0"; value = nat}}}
        }
    }

    fun makePersonBase(p : Patient, config: Config) : PersonType {
        val ssin = p.ssin?.replace("[^0-9]".toRegex(), "")?.let { if (org.taktik.icure.utils.Math.isNissValid(it)) it else null }

        return PersonType().apply {
            ssin?.let { ssin -> ids.add(IDPATIENT().apply { s = IDPATIENTschemes.ID_PATIENT; sv = "1.0"; value = ssin }) }
            p.id?.let { id -> ids.add(IDPATIENT().apply { s = IDPATIENTschemes.LOCAL; sv = config.soft.version; sl = "${config.soft.name}-Person-Id"; value = id }) }
            firstnames.add(p.firstName)
            familyname= p.lastName
            sex= SexType().apply {cd = CDSEX().apply { s= "CD-SEX"; sv= "1.0"; value = p.gender?.let { CDSEXvalues.fromValue(it.name) } ?: CDSEXvalues.UNKNOWN }}
            p.dateOfBirth?.let { birthdate = Utils.makeDateTypeFromFuzzyLong(it.toLong()) }
            recorddatetime = makeXGC(p.modified)
        }
    }

    open fun createItemWithContent(svc: Service, idx: Int, cdItem: String, contents: List<ContentType>, localIdName: String = "iCure-Service") : ItemType? {
        return ItemType().apply {
            ids.add(IDKMEHR().apply {s = IDKMEHRschemes.ID_KMEHR; sv = "1.0"; value = idx.toString()})
            ids.add(IDKMEHR().apply {s = IDKMEHRschemes.LOCAL; sl = localIdName; sv = ICUREVERSION; value = svc.id })
            cds.add(CDITEM().apply {s = CDITEMschemes.CD_ITEM; sv = "1.0"; value = cdItem } )

            this.contents.addAll(filterEmptyContent(contents))
            lifecycle = LifecycleType().apply {cd = CDLIFECYCLE().apply {s = "CD-LIFECYCLE"; sv = "1.0"
                value = if (((svc.status ?: 0) and 2) != 0 || (svc.closingDate ?: 0 > FuzzyValues.getCurrentFuzzyDate())) {
					CDLIFECYCLEvalues.INACTIVE
                } else {
                    svc.tags.find { t -> t.type == "CD-LIFECYCLE" }?.let { CDLIFECYCLEvalues.fromValue(it.code) }
                            ?: if(cdItem == "medication") CDLIFECYCLEvalues.PRESCRIBED else CDLIFECYCLEvalues.ACTIVE
                }
			} }
            if(cdItem == "medication") {
                svc.tags.find{ it.type == "CD-TEMPORALITY"}?.let {
                    temporality = TemporalityType().apply {
                        cd = CDTEMPORALITY().apply { s = "CD-TEMPORALITY"; sv = "1.0"; value = CDTEMPORALITYvalues.fromValue(it.code) }
                    }
                }
                //TODO: this code is not finished! Contains hard-coded test data
                regimen = ItemType.Regimen()
                frequency = FrequencyType().apply { periodicity = PeriodicityType().apply  { this.cd = CDPERIODICITY().apply { this.value = "D" } }}
                //svc.content.values.find { c -> c.medicationValue != null }?.let { cnt -> cnt.medicationValue?.let { m ->
                svc.content.values.find { it.medicationValue != null }?.let { it.medicationValue!!.regimen.map{
                            regimen.daynumbersAndQuantitiesAndDates.add(AdministrationquantityType().apply {
                                    this.decimal = BigDecimal(1); this.unit = AdministrationunitType().apply {
                                    this.cd = CDADMINISTRATIONUNIT().apply { this.value = "00005" }  }  })
                        }
                    }
            }


            isIsrelevant = ((svc.status?: 0) and 2) == 0
            beginmoment = (svc.valueDate ?: svc.openingDate).let { Utils.makeMomentTypeDateFromFuzzyLong(it) }
            endmoment = svc.closingDate?.let { Utils.makeMomentTypeDateFromFuzzyLong(it)}
            recorddatetime = makeXGC(svc.modified)
        }
    }

	private fun filterEmptyContent(contents: List<ContentType>) = contents.filterNotNull().filter {
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
            cds.add(CDITEM().apply {s = CDITEMschemes.CD_ITEM; sv = "1.0"; value = cdItem } )

            this.contents.addAll(filterEmptyContent(contents))
            lifecycle = LifecycleType().apply {cd = CDLIFECYCLE().apply {s = "CD-LIFECYCLE"; sv = "1.0"
                value = if (((he.status ?: 0) and 2) != 0 || (he.closingDate ?: 0 > FuzzyValues.getCurrentFuzzyDate()))
					CDLIFECYCLEvalues.INACTIVE
                else
                    he.tags.find { t -> t.type == "CD-LIFECYCLE" }?.let { CDLIFECYCLEvalues.fromValue(it.code) } ?: CDLIFECYCLEvalues.ACTIVE
			} }
            isIsrelevant = ((he.status?: 0) and 2) == 0
            beginmoment = (he.valueDate ?: he.openingDate).let { Utils.makeMomentTypeFromFuzzyLong(it) }
            endmoment = he.closingDate?.let { Utils.makeMomentTypeFromFuzzyLong(it)}
            recorddatetime = makeXGC(he.modified)
        }
    }

    fun makeTelecoms(addresses: Collection<Address>?): List<TelecomType> {
        return addresses?.filter { it.addressType != null }?.flatMapTo(ArrayList<TelecomType>(), { a ->
            a.telecoms?.filter {it.telecomNumber?.length?:0>0}?.map {
                TelecomType().apply {
                    cds.add(CDTELECOM().apply { s = CDTELECOMschemes.CD_ADDRESS; sv = "1.0"; value = a.addressType!!.name })
                    cds.add(CDTELECOM().apply { s = CDTELECOMschemes.CD_TELECOM; sv = "1.0"; value = it.telecomType!!.name })
                    telecomnumber = it.telecomNumber
                }
            } ?: emptyList()
        }) ?: emptyList()
    }

    fun makeAddresses(addresses: Collection<Address>?): List<AddressType> {
        return addresses?.filter { it.addressType != null && it.postalCode != null && it.street != null }?.mapTo(ArrayList<AddressType>(), { a ->
            AddressType().apply {
                cds.add(CDADDRESS().apply { s = CDADDRESSschemes.CD_ADDRESS; sv = "1.0"; value = a.addressType!!.name })
                country = if (a.country?.length ?: 0 > 0) CountryType().apply { cd = CDCOUNTRY().apply { s = CDCOUNTRYschemes.CD_COUNTRY; sv = "1.0"; value = a.country } } else CountryType().apply { cd = CDCOUNTRY().apply { s = CDCOUNTRYschemes.CD_COUNTRY; sv = "1.0"; value = "be" } }
                zip = a.postalCode
                street = a.street
                housenumber = a.houseNumber ?: ""
                city = a.city

            }
        }) ?: emptyList()
    }

    fun  makeXGC(date: Long?): XMLGregorianCalendar? {
        return date?.let {
            DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar.getInstance().apply { time = Date(date) } as GregorianCalendar).apply { timezone = DatatypeConstants.FIELD_UNDEFINED }
        }
    }

    fun makeContent(language : String, content : Content) : ContentType? {
        return (content.booleanValue ?: content.numberValue ?: content.stringValue ?: content.instantValue
                ?: content.measureValue ?: content.medicationValue ?: content.binaryValue ?: content.documentId).let {
            ContentType().apply {
                isBoolean = content.booleanValue
                content.numberValue?.let { decimal = BigDecimal(it) }
                content.stringValue?.let { texts.add(TextType().apply { l = language; value = content.stringValue }) }
                makeXGC(content.instantValue?.toEpochMilli())?.let { date = it; time = it; }
                content.measureValue?.let {
                    unit = UnitType().apply { cd = CDUNIT().apply { s = CDUNITschemes.CD_UNIT; sv = "1.0"; value = content.measureValue?.unitCodes?.find { it.type == "CD-UNIT" }?.code } }
                    content.measureValue?.value?.let { decimal = BigDecimal(it) }
                }
                content.medicationValue?.medicinalProduct?.let {
                    medicinalproduct = MedicinalProductType().apply {
                        intendedname = content.medicationValue?.medicinalProduct?.intendedname
                        intendedcds.add(CDDRUGCNK().apply { s = CDDRUGCNKschemes.CD_DRUG_CNK; sv = "01-2016"; value = content.medicationValue?.medicinalProduct?.intendedcds?.find { it.type == "CD-DRUG-CNK" }?.code })
                    }
                }
                content.medicationValue?.substanceProduct?.let {
                    substanceproduct = ContentType.Substanceproduct().apply {
                        intendedname = content.medicationValue?.substanceProduct?.intendedname
                        intendedcd = CDINNCLUSTER().apply { s = CDINNCLUSTERschemes.CD_INNCLUSTER; sv = "01-2016"; value = content.medicationValue?.substanceProduct?.intendedcds?.find { it.type == "CD-INNCLUSTER" }?.code }
                    }
                }
                content.medicationValue?.compoundPrescription?.let {
                    compoundprescription = CompoundprescriptionType().apply { this.content.add(TextType().apply { l = language; value = content.medicationValue?.compoundPrescription } ) }
                }
                content.binaryValue?.let {
                    lnks.add(LnkType().apply { type = CDLNKvalues.MULTIMEDIA; mediatype = CDMEDIATYPEvalues.APPLICATION_PDF; value = content.binaryValue })
                }
                content.documentId?.let {
                    documentLogic?.get(it)?.let { d -> d.attachment?.let { lnks.add(LnkType().apply { type = CDLNKvalues.MULTIMEDIA; mediatype = d.mainUti?.let { UTI.get(it)?.mimeTypes?.firstOrNull()?.let { CDMEDIATYPEvalues.fromValue(it) } }; value = it }) } }
                }
            }
        }
    }

    fun fillMedicationItem(svc : Service, item : ItemType, lang : String) {
        addServiceCodesAndTags(svc, item, true, listOf("CD-ATC"), null, listOf("CD-TRANSACTION", "CD-TRANSACTION-TYPE"))

        val c = svc.content[lang]?.let { if (it.medicationValue?.let { it.medicinalProduct ?: it.substanceProduct ?: it.compoundPrescription } != null) it else null }
                ?: svc.content.values.find { it.medicationValue?.let { it.medicinalProduct ?: it.substanceProduct ?: it.compoundPrescription } != null }

        c.let { cnt ->
            item.contents.add(0, ContentType().apply {texts.add(TextType().apply {l=lang; value= cnt?.medicationValue?.medicinalProduct?.intendedname?:cnt?.medicationValue?.substanceProduct?.intendedname?:cnt?.medicationValue?.compoundPrescription?:cnt?.stringValue?:""})})
            cnt?.medicationValue?.substanceProduct.let {sp->
                cnt?.medicationValue?.duration?.let { d ->
                    item.duration = DurationType().apply {decimal= BigDecimal(d.value); unit = d.unit?.code?.let {
                        TimeunitType().apply { cd= CDTIMEUNIT().apply { s= CDTIMEUNITschemes.CD_TIMEUNIT; sv="1.0"; value=it } }
                    }}
                }
            }
            cnt?.medicationValue?.posologyText?.let {
                item.posology = ItemType.Posology().apply { text = TextType().apply { l = lang; value = it } }
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
                            this.cds.add(CDCONTENT().apply { s(cdt); sv = "1.0"; value = c.code })
                        } else if ((restrictedTypes == null || restrictedTypes.contains("LOCAL")) && (excludedTypes == null || !excludedTypes.contains("LOCAL"))) {
                            this.cds.add(CDCONTENT().apply { s(CDCONTENTschemes.LOCAL); sl = c.type; dn = c.type; sv = "1.0"; value = c.code })
                        }
                    }
                } catch (ignored : IllegalArgumentException) {
                    if ((restrictedTypes == null || restrictedTypes.contains("LOCAL")) && (excludedTypes == null || !excludedTypes.contains("LOCAL"))) {
                        this.cds.add(CDCONTENT().apply { s(CDCONTENTschemes.LOCAL); sl = c.type; dn = c.type; sv = "1.0"; value = c.code })
                    }
                }
            }

            for (c in svc.tags) {
                try {
                    val idt = CDITEMschemes.fromValue(c.type)
                    val prevIcc = item.cds.find { cc -> idt == cc.s }
                    if (prevIcc == null) {
                        item.cds.add(CDITEM().apply { s(idt); sv = "1.0"; value = c.code })
                    } else if (prevIcc.value != c.code) {
                        item.cds.add(CDITEM().apply { s(CDITEMschemes.LOCAL); sl = c.type; dn = c.type; sv = "1.0"; value = c.code })
                    }
                } catch (ignored: IllegalArgumentException) {
                    //noinspection GroovyUnusedCatchParameter
                    try {
                        val cdt = CDCONTENTschemes.fromValue(c.type)
                        if ((restrictedTypes == null || restrictedTypes.contains(c.type)) && (excludedTypes == null || !excludedTypes.contains(c.type))) {
                            val prevCc = this.cds.find { cc -> cdt == cc.s }
                            if (uniqueTypes == null || !uniqueTypes.contains(c.type) || prevCc == null) {
                                this.cds.add(CDCONTENT().apply { s(cdt); sv = "1.0"; value = c.code })
                            } else if (prevCc.value != c.code && ((restrictedTypes == null || restrictedTypes.contains("LOCAL")) && (excludedTypes == null || !excludedTypes.contains("LOCAL")))) {
                                this.cds.add(CDCONTENT().apply { s(CDCONTENTschemes.LOCAL); sl = c.type; dn = c.type; sv = "1.0"; value = c.code })
                            }
                        }
                    } catch (ignoredAsWell: IllegalArgumentException) {
                        if ((restrictedTypes == null || restrictedTypes.contains("LOCAL")) && (excludedTypes == null || !excludedTypes.contains("LOCAL"))) {
                            this.cds.add(CDCONTENT().apply { s(CDCONTENTschemes.LOCAL); sl = c.type; dn = c.type; sv = "1.0"; value = c.code })
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
                item.cds.add(CDITEM().apply { s(CDITEMschemes.LOCAL); sl = "iCure-Label"; dn = "iCure service label";sv = "1.0"; value = lbl })
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
                cds.add(CDTRANSACTION().apply { s(transactionType); sv = "1.0"; value = cdTransaction })
                author = AuthorType().apply { hcparties.add(createParty(sender, emptyList())) }
                ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; sv = "1.0"; value = "1" })
                ids.add(IDKMEHR().apply { s = IDKMEHRschemes.LOCAL; sl = "iCure-Item"; sv = ICUREVERSION; value = ssc.id ?: dem.id ?: patient.id })
                recorddatetime = makeXGC(ssc.created ?: ((dem.openingDate ?: dem.valueDate)?.let { FuzzyValues.getDateTime(it) } ?: LocalDateTime.now()).atZone(ZoneId.systemDefault()).toEpochSecond()*1000)
                isIscomplete = true
                isIsvalidated = true

                if (text?.length ?: 0 >0) headingsAndItemsAndTexts.add(TextType().apply { l = "fr"; value = text })
                attachmentDocumentIds.forEach { id ->
                    val d = documentLogic?.get(id)
                    d?.attachment.let {
                        headingsAndItemsAndTexts.add(LnkType().apply {
                            type = CDLNKvalues.MULTIMEDIA; mediatype = d?.mainUti?.let { UTI.get(it)?.mimeTypes?.firstOrNull()?.let { CDMEDIATYPEvalues.fromValue(it) } }; value = d?.attachment
                        })
                    }
                }
            })
        }
    }

    fun initializeMessage(sender : HealthcareParty, config: Config) : Kmehrmessage {
        return Kmehrmessage().apply {
            header = HeaderType().apply {
                standard = StandardType().apply { cd = CDSTANDARD().apply { s = "CD-STANDARD";sv = "1.4"; value = STANDARD } }
                ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; sv = "1.0"; value = (sender.nihii ?: sender.id) + "." + System.currentTimeMillis() })
                makeXGC(Instant.now().toEpochMilli()).let {
                    date = it
                    time = it
                }
                this.sender = SenderType().apply {
                    hcparties.add(createParty(sender, emptyList()))
//                    hcparties.add(createParty(listOf(IDHCPARTY().apply { s = IDHCPARTYschemes.LOCAL; sl = "iCure"; sv = ICUREVERSION }),
//                            listOf(CDHCPARTY().apply { s = CDHCPARTYschemes.CD_APPLICATION; sv = "1.0" }), "iCure ${ICUREVERSION}"))
                }
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
            hcparties.add(recipient?.let { createParty(it, emptyList()) } ?: createParty(emptyList(), listOf(CDHCPARTY().apply { s = CDHCPARTYschemes.CD_APPLICATION; sv = "1.0" }), "gp-software-migration"))
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
            hcparties.add(recipient?.let { createParty(it, emptyList()) } ?: createParty(emptyList(), listOf(CDHCPARTY().apply { s = CDHCPARTYschemes.CD_APPLICATION; sv = "1.0" }), "gp-software-migration"))
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
            hcparties.add(recipient?.let { createParty(it, emptyList()) } ?: createParty(emptyList(), listOf(CDHCPARTY().apply { s = CDHCPARTYschemes.CD_APPLICATION; sv = "1.0" }), "gp-software-migration"))
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
            hcparties.add(recipient?.let { createParty(it, emptyList()) } ?: createParty(emptyList(), listOf(CDHCPARTY().apply { s = CDHCPARTYschemes.CD_APPLICATION; sv = "1.0" }), "gp-software-migration"))
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

    protected fun CDCONTENT.s(scheme: CDCONTENTschemes) {
        s = scheme
        sv = scheme.version()?:"1.0"
    }

    protected fun CDTRANSACTION.s(scheme: CDTRANSACTIONschemes) {
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
			sv = config.soft.version
			sl = "${config.soft.name}-$itemType-Id"
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
    data class Config(val _kmehrId: String, val date: XMLGregorianCalendar, val time: XMLGregorianCalendar, val soft: Software, var clinicalSummaryType: String, val defaultLanguage: String) {
        data class Software(val name : String, val version : String)
    }
}
