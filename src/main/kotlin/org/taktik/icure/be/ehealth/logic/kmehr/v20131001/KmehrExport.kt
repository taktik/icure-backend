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

package org.taktik.icure.be.ehealth.logic.kmehr.v20131001

import ma.glasnost.orika.MapperFacade
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.taktik.commons.uti.UTI
import org.taktik.icure.be.drugs.logic.DrugsLogic
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.Utils
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.Form
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.Content
import org.taktik.icure.entities.embed.PlanOfAction
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.logic.*
import org.taktik.icure.logic.impl.filter.Filters
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.cd.v1.*
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.cd.v1.CDCOUNTRYschemes.CD_FED_COUNTRY
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTYschemes.CD_HCPARTY
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.cd.v1.CDLIFECYCLEvalues.*
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.dt.v1.TextType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTY
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes.ID_HCPARTY
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes.INSS
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENT
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENTschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENTschemes.ID_PATIENT
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENTschemes.LOCAL
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.*
import org.taktik.icure.utils.FuzzyValues
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.swing.text.rtf.RTFEditorKit
import javax.xml.datatype.XMLGregorianCalendar
import kotlin.collections.HashMap

open class KmehrExport {
    @Autowired var patientLogic: PatientLogic? = null
    @Autowired var codeLogic: CodeLogic? = null
    @Autowired var drugsLogic: DrugsLogic? = null
    @Autowired var healthElementLogic: HealthElementLogic? = null
    @Autowired var healthcarePartyLogic: HealthcarePartyLogic? = null
    @Autowired var contactLogic: ContactLogic? = null
    @Autowired var documentLogic: DocumentLogic? = null
    @Autowired var mainLogic: MainLogic? = null
    @Autowired var formLogic: FormLogic? = null
    @Autowired var formTemplateLogic: FormTemplateLogic? = null
    @Autowired var sessionLogic: SessionLogic? = null
    @Autowired var filters: Filters? = null
    @Autowired var mapper: MapperFacade? = null
    @Autowired var userLogic: UserLogic?= null
    @Autowired var insuranceLogic: InsuranceLogic?= null

	val unitCodes = HashMap<String,Code>()

    internal val STANDARD = "20131001"
    internal val ICUREVERSION = "4.0.0" // TODO fetch actual version here or delete and fetch elsewhere
    internal val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd")
    internal open val log = LogFactory.getLog(KmehrExport::class.java)

    fun createParty(ids : List<IDHCPARTY>, cds : List<CDHCPARTY>, name : String) : HcpartyType  {
        return HcpartyType().apply { this.ids.addAll(ids); this.cds.addAll(cds); this.name = name }
    }

    fun createPartyWithAddresses(m : HealthcareParty, cds : List<CDHCPARTY>? = listOf()) : HcpartyType {
		return createParty(m, cds).apply {
			addresses.addAll(makeAddresses(m.addresses))
			telecoms.addAll(makeTelecoms(m.addresses))
		}
    }

	fun createParty(m: HealthcareParty, cds: List<CDHCPARTY>? = listOf()): HcpartyType {
		val hcp = HcpartyType().apply {
			m.nihii?.let { nihii -> if (isNihiiValid(nihii)) ids.add(IDHCPARTY().apply { s = ID_HCPARTY; sv = "1.0"; value = nihii }) }
			m.ssin?.let { ssin -> ids.add(IDHCPARTY().apply { s = INSS; sv = "1.0"; value = ssin }) }
			cds?.let { this.cds.addAll(it) }
			this.cds.addAll(if (m.specialityCodes?.size ?: 0 > 0)
				m.specialityCodes.map { CDHCPARTY().apply { s = CD_HCPARTY; sv = "1.6"; value = it.code } }
			else
				listOf(CDHCPARTY().apply { s = CD_HCPARTY; sv = "1.6"; value = "persphysician" }))

			firstname = m.firstName
			familyname = m.lastName

		}
		return hcp
	}

	private fun isNihiiValid(nihii: String) = nihii.length == 11 && ((97 - nihii.substring(0, 6).toLong()) % 97 == nihii.substring(6, 8).toLong())

	fun makePatient(p : Patient, config: Config): PersonType {
		val ssin = p.ssin?.replace("[^0-9]".toRegex(), "")?.let { if (org.taktik.icure.utils.Math.isNissValid(it)) it else null }
		return makePerson(p, config).apply {
			ids.clear()
			ssin?.let { ssin -> ids.add(IDPATIENT().apply { s = ID_PATIENT; sv = "1.0"; value = ssin }) }
			ids.add(IDPATIENT().apply {s= IDPATIENTschemes.LOCAL; sl= "MF-ID"; sv= config.soft.version; value= p.id})
		}
	}

	fun makePerson(p : Patient, config: Config) : PersonType {
        return makePersonBase(p, config).apply {
            p.dateOfDeath?.let {
                if(it == 0) {
                    deathdate = null
                } else {
                    deathdate = Utils.makeDateTypeFromFuzzyLong(it.toLong())
                }
            }
            p.placeOfBirth?.let { birthlocation = AddressTypeBase().apply { city= it }}
            p.placeOfDeath?.let { deathlocation = AddressTypeBase().apply { city= it }}
            p.profession?.let { profession = ProfessionType().apply() { text = TextType().apply { l= "fr"; value = it } } }
            usuallanguage= p.languages.firstOrNull()
            addresses.addAll(makeAddresses(p.addresses))
            telecoms.addAll(makeTelecoms(p.addresses))
            p.nationality?.let { nat -> mapToCountryCode(nat)?.let { natCode -> nationality = PersonType.Nationality().apply { cd = CDCOUNTRY().apply { s= CD_FED_COUNTRY; sv= "1.0"; value = natCode }}}}
        }
    }

    fun makePersonBase(p : Patient, config: Config) : PersonType {
		val ssin = p.ssin?.replace("[^0-9]".toRegex(), "")?.let { if (org.taktik.icure.utils.Math.isNissValid(it)) it else null }
        return PersonType().apply {
			p.id?.let { id -> ids.add(IDPATIENT().apply { s = LOCAL; sv = config.soft.version; sl = "${config.soft.name}-Person-Id"; value = id }) }
			ssin?.let { ssin -> ids.add(IDPATIENT().apply { s = IDPATIENTschemes.INSS; sv = "1.0"; value = ssin }) }
            firstnames.add(p.firstName)
            familyname= p.lastName
            sex= SexType().apply {cd = CDSEX().apply { s= "CD-SEX"; sv= "1.0"; value = p.gender?.let { CDSEXvalues.fromValue(it.name) } ?: CDSEXvalues.UNKNOWN}}
            p.dateOfBirth?.let { birthdate = Utils.makeDateTypeFromFuzzyLong(it.toLong()) }
            recorddatetime = Utils.makeXGC(p.modified, true)
        }
    }

    open fun createItemWithContent(svc: Service, idx: Int, cdItem: String, contents: List<ContentType>, localIdName: String = "iCure-Service") : ItemType {
        return ItemType().apply {
            ids.add(IDKMEHR().apply {s = IDKMEHRschemes.ID_KMEHR; sv = "1.0"; value = idx.toString()})
            ids.add(IDKMEHR().apply {s = IDKMEHRschemes.LOCAL; sl = localIdName; sv = ICUREVERSION; value = svc.id })
            cds.add(CDITEM().apply {s = CDITEMschemes.CD_ITEM; sv = "1.6"; value = cdItem } )

            this.contents.addAll(filterEmptyContent(contents))
            lifecycle = LifecycleType().apply {cd = CDLIFECYCLE().apply {s = "CD-LIFECYCLE"; sv = "1.6"
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
                        cd = CDTEMPORALITY().apply { s = "CD-TEMPORALITY"; sv = "1.0"; value = CDTEMPORALITYvalues.fromValue(it.code.toLowerCase()) }
                    }
                }
            }
            isIsrelevant = ((svc.status?: 0) and 2) == 0
            beginmoment = (svc.valueDate ?: svc.openingDate).let { Utils.makeMomentTypeFromFuzzyLong(it) }
            endmoment = svc.closingDate?.let { Utils.makeMomentTypeFromFuzzyLong(it)}
            recorddatetime = Utils.makeXGC(svc.modified, true)
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

	open fun  createItemWithContent(he : HealthElement, idx : Int, cdItem : String, contents : List<ContentType>, localIdName: String = "iCure-Healthelement") : ItemType {
        return ItemType().apply {
            ids.add(IDKMEHR().apply {s = IDKMEHRschemes.ID_KMEHR; sv = "1.0"; value = idx.toString()})
            ids.add(IDKMEHR().apply {s = IDKMEHRschemes.LOCAL; sl = localIdName; sv = ICUREVERSION; value = he.healthElementId })
            ids.add(IDKMEHR().apply {s = IDKMEHRschemes.LOCAL; sl = "icure-id"; sv = ICUREVERSION; value = he.id })
            cds.add(CDITEM().apply {s = CDITEMschemes.CD_ITEM; sv = "1.6"; value = cdItem } )

            this.contents.addAll(filterEmptyContent(contents))
            lifecycle = LifecycleType().apply {cd = CDLIFECYCLE().apply {s = "CD-LIFECYCLE"; sv = "1.6"
                value = if (((he.status ?: 0) and 2) != 0 || (he.closingDate ?: 0 > FuzzyValues.getCurrentFuzzyDate()))
                CDLIFECYCLEvalues.INACTIVE
                else
                he.tags.find { t -> t.type == "CD-LIFECYCLE" }?.let { CDLIFECYCLEvalues.fromValue(it.code)} ?: ACTIVE } }
            //isIsrelevant = ((he.status?: 0) and 2) == 0 // FIXME: two way to store the relevant status
            isIsrelevant = if(lifecycle.cd.value == ACTIVE) true else he.isRelevant // in *MF, all active elements are relevant
            beginmoment = (he.valueDate ?: he.openingDate).let { Utils.makeMomentTypeFromFuzzyLong(it) }
            endmoment = he.closingDate?.let {
                if(it == 0L) {
                    null
                } else {
                    Utils.makeMomentTypeFromFuzzyLong(it)
                }
            }
            recorddatetime = Utils.makeXGC(he.modified, true)
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

				country = if (a.country?.length ?: 0 > 0) mapToCountryCode(a.country!!)?.let { natCode -> CountryType().apply { cd = CDCOUNTRY().apply { s = CD_FED_COUNTRY; sv = "1.0"; value = natCode }}} else CountryType().apply { cd = CDCOUNTRY().apply { s = CD_FED_COUNTRY; sv = "1.0"; value = "be" } }
                zip = a.postalCode
                street = a.street
                housenumber = a.houseNumber ?: ""
				postboxnumber = a.postboxNumber
                city = a.city

            }
        }) ?: emptyList()
    }

    fun makeContent(language : String, content : Content) : ContentType? {
        return (content.booleanValue ?: content.numberValue ?: content.stringValue ?: content.instantValue
                ?: content.measureValue ?: content.medicationValue ?: content.binaryValue ?: content.documentId).let {
            ContentType().apply {
				isBoolean = content.booleanValue
                content.numberValue?.let { decimal = BigDecimal.valueOf(it) }
                content.stringValue?.let { if (content.binaryValue==null && content.documentId==null) { texts.add(TextType().apply { l = language; value = content.stringValue }) } }
				Utils.makeXGC(content.instantValue?.toEpochMilli(), true)?.let { date = it; time = it; }
                content.measureValue?.let { mv ->
                    mv.unitCodes?.find { it.type == "CD-UNIT" }?.code?.let { unitCode -> if (unitCode.isNotEmpty()) {unit = UnitType().apply { cd = CDUNIT().apply { s = CDUNITschemes.CD_UNIT; sv = "1.4"; value = unitCode } } } }
					if (unit == null) {
						mv.unit?.let { getCode(it)?.let {unit = UnitType().apply { cd = CDUNIT().apply { s = CDUNITschemes.CD_UNIT; sv = "1.4"; value = it.code }}}}
					}
                    mv.value?.let { decimal = BigDecimal.valueOf(it) }
                }
                content.medicationValue?.medicinalProduct?.let {
                    medicinalproduct = ContentType.Medicinalproduct().apply {
                        intendedname = content.medicationValue?.medicinalProduct?.intendedname
                        intendedcds.add(CDDRUGCNK().apply { s = CDDRUGCNKschemes.CD_DRUG_CNK; sv = "01-2016"; value = content.medicationValue?.medicinalProduct?.intendedcds?.find { it.type == "CD-DRUG-CNK" }?.code })
                    }
                }
                content.medicationValue?.substanceProduct?.let {
                    substanceproduct = ContentType.Substanceproduct().apply {
                        intendedname = content.medicationValue?.substanceProduct?.intendedname
                        intendedcd = CDINNCLUSTER().apply { s = "CD-INNCLUSTER"; sv = "01-2016"; value = content.medicationValue?.substanceProduct?.intendedcds?.find { it.type == "CD-INNCLUSTER" }?.code }
                    }
                }
                content.medicationValue?.compoundPrescription?.let {
                    if (it != "" ) {
                        compoundprescription = CompoundprescriptionType().apply {
                            l = language
                            this.content.add(content.medicationValue?.compoundPrescription)
                        }
                    }
                }
                content.binaryValue?.let {
					if (Arrays.equals(content.binaryValue.slice(0..4).toByteArray(), "{\\rtf".toByteArray())) {
						texts.add(TextType().apply { l = language; value = RTFEditorKit().let {
                            val document = it.createDefaultDocument()
                            it.read(content.binaryValue.inputStream(), document, 0)
                            document.getText(0, document.length) ?: ""
                        }})
					} else {
						lnks.add(LnkType().apply { type = CDLNKvalues.MULTIMEDIA; mediatype = CDMEDIATYPEvalues.APPLICATION_PDF; value = content.binaryValue })
					}
                }
                content.documentId?.let {
                    try {
						documentLogic?.get(it)?.let { d -> d.attachment?.let { lnks.add(LnkType().apply { type = CDLNKvalues.MULTIMEDIA; mediatype = documentMediaType(d); value = it }) } }
					} catch (e : Exception) {
						log.warn("Document with id ${it} could not be loaded",e)
					}
                }
            }
        }.let { if (it.isBoolean != null || it.date != null || it.time != null || it.lnks.size > 0 || it.compoundprescription != null || it.substanceproduct != null || it.medicinalproduct != null || it.cds.size > 0 || it.decimal!=null || it.texts.size>0) it else null }
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
                        TimeunitType().apply { cd=CDTIMEUNIT().apply { s=CDTIMEUNITschemes.CD_TIMEUNIT; sv="2.1"; value=it } }
                    }}
                }
            }
            cnt?.medicationValue?.getPosologyText()?.let {
                item.posology = ItemType.Posology().apply { text = TextType().apply { l = lang; value = it } }
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
                        item.cds.add(CDITEM().apply { s(idt); sv = "1.6"; value = c.code })
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
                cds.add(CDTRANSACTION().apply { s(transactionType); sv = "1.5"; value = cdTransaction })
                author = AuthorType().apply { hcparties.add(createPartyWithAddresses(sender, emptyList())) }
                ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; sv = "1.0"; value = "1" })
                ids.add(IDKMEHR().apply { s = IDKMEHRschemes.LOCAL; sl = "iCure-Item"; sv = ICUREVERSION; value = ssc.id ?: dem.id ?: patient.id })
                recorddatetime = Utils.makeXGC(ssc.created ?: ((dem.openingDate ?: dem.valueDate)?.let { FuzzyValues.getDateTime(it) } ?: LocalDateTime.now()).atZone(ZoneId.systemDefault()).toEpochSecond()*1000, true)
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

    fun initializeMessage(sender : HealthcareParty, config : Config) : Kmehrmessage {
        return Kmehrmessage().apply {
            header = HeaderType().apply {
                standard = StandardType().apply {
					cd = CDSTANDARD().apply { s = "CD-STANDARD"; sv = "1.8"; value = STANDARD }
                    val filetype = if(config.exportAsPMF) {
                        CDMESSAGEvalues.GPPATIENTMIGRATION
                    } else {
                        CDMESSAGEvalues.GPSOFTWAREMIGRATION
                    }
					specialisation = StandardType.Specialisation().apply { cd = CDMESSAGE().apply { s = "CD-MESSAGE"; sv = "1.1"; value = filetype } ; version = SMF_VERSION }
				}
                ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; sv = "1.0"; value = (sender.nihii ?: sender.id) + "." + config._kmehrId })
				date = config.date
                time = config.time
                this.sender = SenderType().apply {
					hcparties.add(HcpartyType().apply {
						ids.add(IDHCPARTY().apply { s = IDHCPARTYschemes.LOCAL; sl = config.soft.name; sv = config.soft.version; value = "${config.soft.name}-${config.soft.version}" })
						cds.add(CDHCPARTY().apply { s = CD_HCPARTY ; sv = "1.6"; value = "application" })
						name = config.soft.name
					})
                    hcparties.add(createParty(sender, emptyList()))
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

    protected fun CDITEM.s(scheme: CDITEMschemes) {
        s = scheme
        sv = "1.0"
    }

    protected fun CDCONTENT.s(scheme: CDCONTENTschemes) {
        s = scheme
        sv = "1.0"
    }

    protected fun CDTRANSACTION.s(scheme: CDTRANSACTIONschemes) {
        s = scheme
        sv = "1.0"
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
	data class Config(val _kmehrId: String, val date: XMLGregorianCalendar, val time: XMLGregorianCalendar, val soft: Software, var clinicalSummaryType: String, val defaultLanguage: String, val exportAsPMF: Boolean) {
		data class Software(val name : String, val version : String)
	}
}
