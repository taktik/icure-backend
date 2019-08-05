package org.taktik.icure.be.ehealth.logic.kmehr.sumehr.impl.v20161201

import ma.glasnost.orika.MapperFacade
import org.mockito.Matchers.any
import org.mockito.Matchers.eq
import org.mockito.Mockito
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.context.ApplicationContext
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENTschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.AddressTypeBase
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.embed.*
import org.taktik.icure.entities.embed.AddressType
import org.taktik.icure.entities.embed.ContractChangeType
import org.taktik.icure.entities.embed.Gender
import org.taktik.icure.entities.embed.HealthcarePartyStatus
import org.taktik.icure.entities.embed.ReferralPeriod
import org.taktik.icure.entities.embed.SuspensionReason
import org.taktik.icure.entities.embed.TelecomType
import org.taktik.icure.logic.HealthElementLogic
import org.taktik.icure.logic.HealthcarePartyLogic
import org.taktik.icure.logic.PatientLogic
import org.taktik.icure.logic.impl.CodeLogicImpl
import org.taktik.icure.logic.impl.ContactLogicImpl
import org.taktik.icure.logic.impl.filter.Filters
import org.taktik.icure.logic.impl.filter.service.ServiceByHcPartyTagCodeDateFilter
import org.taktik.icure.services.external.api.AsyncDecrypt
import org.taktik.icure.services.external.rest.v1.dto.CodeDto
import org.taktik.icure.services.external.rest.v1.dto.embed.*
import org.taktik.icure.utils.FuzzyValues
import java.io.File
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

private const val DIR_PATH = "src/test/resources/org/taktik/icure/be/ehealth/logic/kmehr/sumehr/impl/v20161201/out"

private val sumehrExport = SumehrExport()

private val applicationContext = Mockito.mock(ApplicationContext::class.java)
private val autowireCapableBeanFactory = Mockito.mock(AutowireCapableBeanFactory::class.java)
private val contactLogic = Mockito.mock(ContactLogicImpl::class.java)
private val codeLogic = Mockito.mock(CodeLogicImpl::class.java)
private val decryptor = Mockito.mock(AsyncDecrypt::class.java)
private val healthcarePartyLogic = Mockito.mock(HealthcarePartyLogic::class.java)
private val healthElementLogic = Mockito.mock(HealthElementLogic::class.java)
private val mapper = Mockito.mock(MapperFacade::class.java)
private val patientLogic = Mockito.mock(PatientLogic::class.java)

private const val language = "fr"

private const val adr = "adr"
private const val allergy = "allergy"
private const val socialrisk = "socialrisk"
private const val risk = "risk"
private const val patientwill = "patientwill"
private const val vaccine = "vaccine"
private const val medication = "medication"
private const val treatment = "treatment"
private const val healthissue = "healthissue"
private const val healthcareelement = "healthcareelement"

private val dateFormat = SimpleDateFormat("yyyyMMdd")
private val tomorrow = FuzzyValues.getFuzzyDate(LocalDateTime.now().plusDays(1), ChronoUnit.SECONDS)
private val today = FuzzyValues.getFuzzyDate(LocalDateTime.now(), ChronoUnit.SECONDS)
private val yesterday = FuzzyValues.getFuzzyDate(LocalDateTime.now().minusDays(1), ChronoUnit.SECONDS)
private val oneWeekAgo = FuzzyValues.getFuzzyDate(LocalDateTime.now().minusWeeks(1), ChronoUnit.SECONDS)
private val oneMonthAgo = FuzzyValues.getFuzzyDate(LocalDateTime.now().minusMonths(1), ChronoUnit.SECONDS)

private class MyContents {
    companion object {
        val neutralContent = mapOf(Pair(language, Content().apply {
            stringValue = "neutralContentStringValue"
        }))
        val medicationContent = mapOf(Pair(language, Content().apply {
            medicationValue = Medication().apply {
                substanceProduct = Substanceproduct().apply {
                    intendedcds = listOf(CodeStub("CD-INNCLUSTER", "1449834", "medication"))
                    intendedname = "paracetamol"
                }
            }
        }))
        val treatmentContent = mapOf(Pair(language, Content().apply {
            medicationValue = Medication().apply {
                medicinalProduct = Medicinalproduct().apply {
                    intendedcds = listOf(CodeStub("CD-DRUG-CNK", "1449834", "treatment"))
                    intendedname = "ibuprofen"
                }
            }
        }))
        val vaccineContent = mapOf(Pair(language, Content().apply {
            medicationValue = Medication().apply {
                medicinalProduct = Medicinalproduct().apply {
                    intendedcds = listOf(CodeStub("CD-DRUG-CNK", "1449834", "vaccine"))
                    intendedname = "gardasil"
                }
            }
        }))
        val medicationCodeContent = mapOf(Pair(language, Content().apply {
            medicationValue = Medication().apply {
            }
        }))
    }
}

private class MyCodes {
    companion object {
        val vaccineCode = CodeStub("CD-VACCINEINDICATION", "notEmpty", "1.0")
        val patientwillCode = CodeStub("CD-ITEM", "patientwill", "1.3")
        val ntbrCode = CodeStub(CDCONTENTschemes.CD_PATIENTWILL.value(), "ntbr", "4.1")
        val bloodtransfusionrefusalCode = CodeStub(CDCONTENTschemes.CD_PATIENTWILL.value(), "bloodtransfusionrefusal", "4.2")
        val intubationrefusalCode = CodeStub(CDCONTENTschemes.CD_PATIENTWILL.value(), "intubationrefusal", "4.3")
        val euthanasiarequestCode = CodeStub(CDCONTENTschemes.CD_PATIENTWILL.value(), "euthanasiarequest", "4.4")
        val vaccinationrefusalCode = CodeStub(CDCONTENTschemes.CD_PATIENTWILL.value(), "vaccinationrefusal", "4.5")
        val organdonationconsentCode = CodeStub(CDCONTENTschemes.CD_PATIENTWILL.value(), "organdonationconsent", "4.6")
        val datareuseforclinicalresearchconsentCode = CodeStub(CDCONTENTschemes.CD_PATIENTWILL.value(), "datareuseforclinicalresearchconsent", "4.6")
        val datareuseforclinicaltrialsconsentCode = CodeStub(CDCONTENTschemes.CD_PATIENTWILL.value(), "datareuseforclinicaltrialsconsent", "4.6")
        val clinicaltrialparticipationconsent = CodeStub(CDCONTENTschemes.CD_PATIENTWILL.value(), "clinicaltrialparticipationconsent", "4.6")
        val medicationCode = CodeStub("CD-DRUG-CNK", "medication", "version")
        val treatmentCode = CodeStub("CD-DRUG-CNK", "treatment", "version")
        val healthissueCode = CodeStub("type", healthissue, "version")
        val healthcareelementCode = CodeStub("type", healthcareelement, "version")
        val atcCode = CodeStub("CD-ATC", "code", "version")
        val clinicalCode = CodeStub("CD-CLINICAL", "code", "version")
    }
}

private class MyTags {
    companion object {
        val adrTag = CodeStub("CD-ITEM", adr, "1.0")  //Fixe : code
        val inactiveTag = CodeStub("CD-LIFECYCLE", "inactive", "1.0") //Fixe : type et code
        val allergyTag = CodeStub("CD-ITEM", allergy, "1.0") //Fixe : code
        val socialriskTag = CodeStub("CD-ITEM", socialrisk, "1.0") //Fixe : code
        val riskTag = CodeStub("CD-ITEM", risk, "1.0") //Fixe : code
        val patientwillTag = CodeStub("CD-ITEM", patientwill, "1.2")
        val vaccineTag = CodeStub("CD-ITEM", vaccine, "2.1")
        val medicationTag = CodeStub("CD-ITEM", medication, "5.8")
        val treatmentTag = CodeStub("CD-ITEM", treatment, "9.6")
        val healthissueTag = CodeStub("CD-ITEM", healthissue, "version")
        val healthcareelementTag = CodeStub("CD-ITEM", healthcareelement, "version")
        val activeTag = CodeStub("CD-LIFECYCLE", "active", "9.5")
    }
}

private val services = mutableListOf<Service>()

private class MyServices {
    companion object {
        private var newId = 1

        val validServiceADRAssessment = Service().apply {
            this.id = "validServiceADRAssessment"
            this.endOfLife = null
            this.comment = "It's the comment of validServiceADRAssessment"
            this.status = 0 // must be active => Assessment
            this.codes = mutableSetOf(MyCodes.atcCode)
            this.tags = mutableSetOf(MyTags.adrTag)
            this.content = MyContents.medicationCodeContent
            this.openingDate = oneWeekAgo
            this.closingDate = today
        }

        val validServiceADRHistory = Service().apply {
            this.id = "validServiceADRHistory"
            this.endOfLife = null
            this.status = 1 // must be inactive => History
            this.tags = mutableSetOf(MyTags.adrTag, MyTags.inactiveTag)
            this.codes = mutableSetOf(MyCodes.atcCode)
            this.label = medication
            this.content = MyContents.medicationCodeContent
            this.comment = "It's the comment of validServiceADRHistory"
            this.openingDate = oneWeekAgo
            this.closingDate = today
        }

        val validServiceEncryptedADR = Service().apply {
            this.id = "validServiceEncryptedADR"
            this.endOfLife = null
            this.status = 0
            this.tags = mutableSetOf(MyTags.adrTag)
            this.label = medication
            this.content = emptyMap<String, Content>()
            this.encryptedContent = MyContents.neutralContent.values.first().stringValue
            this.comment = "It's the comment of validServiceEncryptedADR"
            this.openingDate = oneWeekAgo
            this.closingDate = today
        }

        val validServiceAllergyAssessment = Service().apply {
            this.id = "validServiceAllergyAssessment"
            this.endOfLife = null
            this.status = 0 // must be active => Assessment
            this.tags = mutableSetOf(MyTags.allergyTag)
            this.codes = mutableSetOf(MyCodes.atcCode)
            this.label = medication
            this.content = MyContents.medicationCodeContent
            this.comment = "It's the comment of validServiceAllergyAssessment"
            this.openingDate = oneWeekAgo
            this.closingDate = today
        }

        val validServiceAllergyHistory = Service().apply {
            this.id = "validServiceAllergyHistory"
            this.endOfLife = null
            this.status = 1 // must be inactive => History
            this.tags = mutableSetOf(MyTags.allergyTag, MyTags.inactiveTag)
            this.codes = mutableSetOf(MyCodes.atcCode)
            this.label = medication
            this.content = MyContents.medicationCodeContent
            this.comment = "It's the comment of validServiceAllergyHistory"
            this.openingDate = oneWeekAgo
            this.closingDate = today
        }

        val validServiceEncryptedAllergy = Service().apply {
            this.id = "validServiceEncryptedAllergy"
            this.endOfLife = null
            this.status = 0
            this.tags = mutableSetOf(MyTags.allergyTag)
            this.label = medication
            this.content = emptyMap<String, Content>()
            this.encryptedContent = MyContents.neutralContent.values.first().stringValue
            this.comment = "It's the comment of validServiceEncryptedAllergy"
            this.openingDate = oneWeekAgo
            this.closingDate = today
        }

        val validServiceSocialriskAssessment = Service().apply {
            this.id = "validServiceSocialriskAssessment"
            this.endOfLife = null
            this.status = 0 // must be active => Assessment
            this.tags = mutableSetOf(MyTags.socialriskTag)
            this.codes = mutableSetOf(MyCodes.atcCode)
            this.label = medication
            this.content = MyContents.medicationCodeContent
            this.comment = "It's the comment of validServiceSocialriskAssessment"
            this.openingDate = oneWeekAgo
            this.closingDate = today
        }

        val validServiceSocialriskHistory = Service().apply {
            this.id = "validServiceSocialriskHistory"
            this.endOfLife = null
            this.status = 1 // must be inactive => History
            this.tags = mutableSetOf(MyTags.socialriskTag, MyTags.inactiveTag)
            this.codes = mutableSetOf(MyCodes.atcCode)
            this.label = medication
            this.content = MyContents.medicationCodeContent
            this.comment = "It's the comment of validServiceSocialriskHistory"
            this.openingDate = oneWeekAgo
            this.closingDate = today
        }

        val validServiceEncryptedSocialrisk = Service().apply {
            this.id = "validServiceEncryptedSocialrisk"
            this.endOfLife = null
            this.status = 0
            this.tags = mutableSetOf(MyTags.socialriskTag)
            this.label = medication
            this.content = emptyMap<String, Content>()
            this.encryptedContent = MyContents.neutralContent.values.first().stringValue
            this.comment = "It's the comment of validServiceEncryptedSocialrisk"
            this.openingDate = oneWeekAgo
            this.closingDate = today
        }

        val validServiceRiskAssessment = Service().apply {
            this.id = "validServiceRiskAssessment"
            this.endOfLife = null
            this.status = 0 // must be active => Assessment
            this.tags = mutableSetOf(MyTags.riskTag)
            this.codes = mutableSetOf(MyCodes.atcCode)
            this.label = medication
            this.content = MyContents.medicationCodeContent
            this.comment = "It's the comment of validServiceRiskAssessment"
            this.openingDate = oneWeekAgo
            this.closingDate = today
        }

        val validServiceRiskHistory = Service().apply {
            this.id = "validServiceRiskHistory"
            this.endOfLife = null
            this.status = 1 // must be inactive => History
            this.tags = mutableSetOf(MyTags.riskTag, MyTags.inactiveTag)
            this.codes = mutableSetOf(MyCodes.atcCode)
            this.label = medication
            this.content = MyContents.medicationCodeContent
            this.comment = "It's the comment of validServiceRiskHistory"
            this.openingDate = oneWeekAgo
            this.closingDate = today
        }

        val validServiceEncryptedRisk = Service().apply {
            this.id = "validServiceEncryptedRisk"
            this.endOfLife = null
            this.status = 0
            this.tags = mutableSetOf(MyTags.riskTag)
            this.label = medication
            this.content = emptyMap<String, Content>()
            this.encryptedContent = MyContents.neutralContent.values.first().stringValue
            this.comment = "It's the comment of validServiceEncryptedRisk"
            this.openingDate = oneWeekAgo
            this.closingDate = today
        }

        val patientwillNtbr = Service().apply {
            this.id = "patientwillNtbr"
            this.endOfLife = null
            this.status = 0 //must be active and relevant
            this.tags = mutableSetOf(MyTags.patientwillTag)
            this.codes = mutableSetOf(MyCodes.patientwillCode, MyCodes.ntbrCode)
            this.content = MyContents.neutralContent
            this.comment = "It's the comment of patientwillNtbr"
            this.openingDate = oneWeekAgo
            this.closingDate = today
        }

        val patientwillBloodtransfusionrefusal = Service().apply {
            this.id = "patientwillBloodtransfusionrefusal"
            this.endOfLife = null
            this.status = 0 //must be active and relevant
            this.tags = mutableSetOf(MyTags.patientwillTag)
            this.codes = mutableSetOf(MyCodes.patientwillCode, MyCodes.bloodtransfusionrefusalCode)
            this.content = MyContents.neutralContent
            this.comment = "It's the comment of patientwillBloodtransfusionrefusal"
            this.openingDate = oneWeekAgo
            this.closingDate = today
        }

        val patientwillIntubationrefusal = Service().apply {
            this.id = "patientwillBloodtransfusionrefusal"
            this.endOfLife = null
            this.status = 0 //must be active and relevant
            this.tags = mutableSetOf(MyTags.patientwillTag)
            this.codes = mutableSetOf(MyCodes.patientwillCode, MyCodes.intubationrefusalCode)
            this.content = MyContents.neutralContent
            this.comment = "It's the comment of patientwillIntubationrefusal"
            this.openingDate = oneWeekAgo
            this.closingDate = today
        }

        val patientwillEuthanasiarequest = Service().apply {
            this.id = "patientwillBloodtransfusionrefusal"
            this.endOfLife = null
            this.status = 0 //must be active and relevant
            this.tags = mutableSetOf(MyTags.patientwillTag)
            this.codes = mutableSetOf(MyCodes.patientwillCode, MyCodes.euthanasiarequestCode)
            this.content = MyContents.neutralContent
            this.comment = "It's the comment of patientwillEuthanasiarequest"
            this.openingDate = oneWeekAgo
            this.closingDate = today
        }

        val patientwillVaccinationrefusal = Service().apply {
            this.id = "patientwillVaccinationrefusal"
            this.endOfLife = null
            this.status = 0 //must be active and relevant
            this.tags = mutableSetOf(MyTags.patientwillTag)
            this.codes = mutableSetOf(MyCodes.patientwillCode, MyCodes.vaccinationrefusalCode)
            this.content = MyContents.neutralContent
            this.comment = "It's the comment of patientwillVaccinationrefusal"
            this.openingDate = oneWeekAgo
            this.closingDate = today
        }

        val vaccineValidService = Service().apply {
            this.id = "vaccineValidService"
            this.endOfLife = null
            this.status = 0 //must be active
            this.tags = mutableSetOf(MyTags.vaccineTag)
            this.codes = mutableSetOf(MyCodes.vaccineCode, MyCodes.atcCode)
            this.content = MyContents.vaccineContent
            this.comment = "It's the comment of vaccineValidService"
            this.openingDate = oneWeekAgo
            this.closingDate = today
        }
        val medicationValidService = Service().apply {
            this.id = "medicationValidService"
            this.endOfLife = null
            this.status = 0 //must be active
            this.tags = mutableSetOf(MyTags.medicationTag)
            this.codes = mutableSetOf(MyCodes.medicationCode, MyCodes.atcCode)
            this.content = MyContents.medicationContent
            this.comment = "It's the comment of medicationValidService"
            this.openingDate = oneWeekAgo
            this.closingDate = tomorrow
        }
        val treatmentValidService = Service().apply {
            this.id = "treatmentValidService"
            this.endOfLife = null
            this.status = 0 //must be active
            this.tags = mutableSetOf(MyTags.treatmentTag)
            this.codes = mutableSetOf(MyCodes.treatmentCode, MyCodes.atcCode)
            this.content = MyContents.treatmentContent
            this.comment = "It's the comment of treatmentValidService"
            this.openingDate = oneWeekAgo
            this.closingDate = tomorrow
        }
        val healthissueAssessmentService = Service().apply {
            this.id = "healthissueAssessmentService"
            this.endOfLife = null
            this.status = 0 // must be active => Assessment
            this.tags = mutableSetOf(MyTags.healthissueTag, MyTags.activeTag)
            this.codes = mutableSetOf(MyCodes.healthissueCode, MyCodes.atcCode)
            this.content = MyContents.medicationCodeContent
            this.comment = "It's the comment of healthissueAssessmentService"
            this.openingDate = oneWeekAgo
            this.closingDate = tomorrow
        }
        val healthissueHistoryService = Service().apply {
            this.id = "healthissueHistoryService"
            this.endOfLife = null
            this.status = 1 // must be inactive => History
            this.tags = mutableSetOf(MyTags.healthissueTag, MyTags.inactiveTag)
            this.codes = mutableSetOf(MyCodes.healthissueCode, MyCodes.atcCode)
            this.content = MyContents.medicationCodeContent
            this.comment = "It's the comment of healthissueHistoryService"
            this.openingDate = oneWeekAgo
            this.closingDate = tomorrow
        }
        val healthcareelementAssessmentService = Service().apply {
            this.id = "healthcareelementAssessmentService"
            this.endOfLife = null
            this.status = 0 // must be active => Assessment
            this.tags = mutableSetOf(MyTags.healthcareelementTag, MyTags.activeTag)
            this.codes = mutableSetOf(MyCodes.healthcareelementCode, MyCodes.atcCode)
            this.content = MyContents.medicationCodeContent
            this.comment = "It's the comment of healthcareelementAssessmentService"
            this.openingDate = oneWeekAgo
            this.closingDate = tomorrow
        }
        val healthcareelementHistoryService = Service().apply {
            this.id = "healthcareelementHistoryService"
            this.endOfLife = null
            this.status = 1 // must be inactive => History
            this.tags = mutableSetOf(MyTags.healthcareelementTag, MyTags.inactiveTag)
            this.codes = mutableSetOf(MyCodes.healthcareelementCode, MyCodes.atcCode)
            this.content = MyContents.medicationCodeContent
            this.comment = "It's the comment of healthcareelementHistoryService"
            this.openingDate = oneWeekAgo
            this.closingDate = tomorrow
        }
    }
}

private val filters = listOf(MyFilters.unionFilter, MyFilters.serviceFilter)

private class MyFilters {
    companion object {
        val unionFilter = Filters.UnionFilter<String, Service>()

        val serviceFilter = ServiceByHcPartyTagCodeDateFilter().apply {
            setContactLogic(contactLogic)
        }
    }
}

private val hcparties = mutableMapOf<String, HealthcareParty>()

private class MyHealthcareParties {
    companion object {
        val doctorGMD = HealthcareParty().apply {
            lastName = "doctorGMDlastname"
            firstName = "doctorGMDfirstname"
            speciality = "persphysician"
            userId = "1"
            nihii = "18000032004"
            ssin = "50010100156"
            addresses = listOf(Address().apply {
                addressType = AddressType.home
                street = "street"
                houseNumber = "3A"
                postalCode = "1000"
                city = "Bruxelles"
                telecoms = listOf(Telecom().apply {
                    telecomType = TelecomType.phone
                    telecomNumber = "0423456789"
                    telecomDescription = "personal phone"
                })
            })
            specialityCodes = listOf(CodeStub("CD-HCPARTY", "persphysician", "1"))
        }

        val referralGMD = HealthcareParty().apply {
            lastName = "referralGMDlastname"
            firstName = "referralGMDfirstname"
            speciality = "persphysician"
            userId = "2"
            nihii = "18000032004"
            ssin = "50010100156"
            addresses = listOf(Address().apply {
                addressType = AddressType.home
                street = "street"
                houseNumber = "3A"
                postalCode = "1000"
                city = "Bruxelles"
                telecoms = listOf(Telecom().apply {
                    telecomType = TelecomType.phone
                    telecomNumber = "0423456789"
                    telecomDescription = "personal phone"
                })
            })
            specialityCodes = listOf(CodeStub("CD-HCPARTY", "persphysician", "1"))
        }

        val medicalhouseGMD = HealthcareParty().apply {
            name = "medicalhouseGMDname"
            speciality = "orgpractice"
            userId = "3"
            nihii = "18000032004"
            ssin = "50010100156"
            addresses = listOf(Address().apply {
                addressType = AddressType.home
                street = "street"
                houseNumber = "3A"
                postalCode = "1000"
                city = "Bruxelles"
                telecoms = listOf(Telecom().apply {
                    telecomType = TelecomType.phone
                    telecomNumber = "0423456789"
                    telecomDescription = "personal phone"
                })
            })
            specialityCodes = listOf(CodeStub("CD-HCPARTY", "orgpractice", "1"))
        }

        val retirementhomeGMD = HealthcareParty().apply {
            name = "retirementhomeGMDname"
            speciality = "orgpublichealth"
            userId = "4"
            nihii = "18000032004"
            ssin = "50010100156"
            addresses = listOf(Address().apply {
                addressType = AddressType.home
                street = "street"
                houseNumber = "3A"
                postalCode = "1000"
                city = "Bruxelles"
                telecoms = listOf(Telecom().apply {
                    telecomType = TelecomType.phone
                    telecomNumber = "0423456789"
                    telecomDescription = "personal phone"
                })
            })
            specialityCodes = listOf(CodeStub("CD-HCPARTY", "orgpublichealth", "1"))
        }
        val hospitalGMD = HealthcareParty().apply {
            name = "hospitalGMDname"
            speciality = "orghospital"
            userId = "5"
            nihii = "18000032004"
            ssin = "50010100156"
            addresses = listOf(Address().apply {
                addressType = AddressType.home
                street = "street"
                houseNumber = "3A"
                postalCode = "1000"
                city = "Bruxelles"
                telecoms = listOf(Telecom().apply {
                    telecomType = TelecomType.phone
                    telecomNumber = "0423456789"
                    telecomDescription = "personal phone"
                })
            })
            specialityCodes = listOf(CodeStub("CD-HCPARTY", "orghospital", "1"))
        }

        val otherGMD = HealthcareParty().apply {
            speciality = "persphysician"
            userId = "6"
            nihii = "18000032004"
            ssin = "50010100156"
            addresses = listOf(Address().apply {
                addressType = AddressType.home
                street = "street"
                houseNumber = "3A"
                postalCode = "1000"
                city = "Bruxelles"
                telecoms = listOf(Telecom().apply {
                    telecomType = TelecomType.phone
                    telecomNumber = "0423456789"
                    telecomDescription = "personal phone"
                })
            })
            specialityCodes = listOf(CodeStub("CD-HCPARTY", "persphysician", "1"))
        }

        val referringphysicianGMD = HealthcareParty().apply {
            speciality = "persphysician"
            userId = "7"
            nihii = "18000032004"
            ssin = "50010100156"
            addresses = listOf(Address().apply {
                addressType = AddressType.home
                street = "street"
                houseNumber = "3A"
                postalCode = "1000"
                city = "Bruxelles"
                telecoms = listOf(Telecom().apply {
                    telecomType = TelecomType.phone
                    telecomNumber = "0423456789"
                    telecomDescription = "personal phone"
                })
            })
            specialityCodes = listOf(CodeStub("CD-HCPARTY", "persphysician", "1"))
        }
    }
}

private val patients = mutableMapOf<String, Patient>()

private class MyPatients {
    companion object {
        val minimalistPatient = Patient().apply {
            id = "316804da-9234-43d6-b18c-df0cccd46744"
            firstName = "Sargent"
            lastName = "Berie"
            ssin = "50010100156"
            gender = Gender.fromCode("M")
            dateOfBirth = 19500101
            languages = listOf("French")
        }

        val fullItemsPatient = Patient().apply {
            id = "idPatient"
            firstName = "firstNamePatient"
            lastName = "lastNamePatient"
            ssin = "50010100156"
            civility = "Mr"
            gender = Gender.fromCode("M")
            dateOfBirth = 19500101
            placeOfBirth = "Bruxelles"
            profession = "Cobaye"
            nationality = "Belge"
            addresses = listOf(Address().apply {
                addressType = AddressType.home
                street = "streetPatient"
                houseNumber = "1D"
                postalCode = "1050"
                city = "Ixelles"
                telecoms = listOf(Telecom().apply {
                    telecomType = TelecomType.phone
                    telecomNumber = "0423456789"
                    telecomDescription = "personal phone"
                })
            })
            languages = listOf("French")
            patientHealthCareParties = listOf(
                    PatientHealthCareParty().apply {
                        type = PatientHealthCarePartyType.doctor
                        this.isReferral = true
                        healthcarePartyId = "1"
                        referralPeriods.add(ReferralPeriod(Instant.ofEpochMilli(oneMonthAgo), Instant.ofEpochMilli(oneMonthAgo.plus(1L))))
                    },
                    PatientHealthCareParty().apply {
                        type = PatientHealthCarePartyType.referral
                        healthcarePartyId = "2"
                    },
                    PatientHealthCareParty().apply {
                        type = PatientHealthCarePartyType.medicalhouse
                        this.isReferral = true
                        healthcarePartyId = "3"
                        referralPeriods.add(ReferralPeriod(Instant.ofEpochMilli(oneMonthAgo.plus(1L)), Instant.ofEpochMilli(oneMonthAgo.plus(2L))))
                    },
                    PatientHealthCareParty().apply {
                        type = PatientHealthCarePartyType.retirementhome
                        this.isReferral = true
                        healthcarePartyId = "4"
                        referralPeriods.add(ReferralPeriod().apply {
                            this.startDate = Instant.ofEpochMilli(oneWeekAgo)
                        })
                    })
            partnerships = listOf(
                    Partnership().apply {
                        partnershipDescription = "Mother"
                        type = PartnershipType.mother
                        status = PartnershipStatus.active
                        partnerId = "Mother"
                    },
                    Partnership().apply {
                        partnershipDescription = "Spouse"
                        type = PartnershipType.spouse
                        status = PartnershipStatus.active
                        partnerId = "Spouse"
                    }
            )
        }

        val fullPatient = Patient().apply {
            active = true
            administrativeNote = "This patient is fake"
            addresses = listOf(Address().apply {
                addressType = AddressType.home
                city = "De Moeren"
                country = "Belgium"
                descr = "This address is fake"
                houseNumber = "15"
                postalCode = "8630"
                postboxNumber = "15"
                street = "Industriestraat"
                telecoms = listOf(Telecom().apply {
                    telecomDescription = "This phone number is fake"
                    telecomNumber = "0490175135"
                    telecomType = TelecomType.mobile
                })
            })
            this.alias = "Sarberie"
            this.author = "Dr Flamand"
            this.civility = "Mr"
            this.created = 20190101
            this.dateOfBirth = 19500101
            this.education = "Master en Cobayologie supérieure"
            this.externalId = "316804da-9234-43d6-b18c-df0cccd46744"
            this.financialInstitutionInformation = listOf(FinancialInstitutionInformation().apply {
                this.bankAccount = "553488836019"
                this.bic = "BPOTBEB1"
                this.key = "478"
                this.name = "MR SARGENT BERIE"
                this.preferredFiiForPartners = setOf("Foreign Institutional Investor")
                this.proxyBankAccount = "910638884355"
                this.proxyBic = "BPOTBEB"
            })
            this.firstName = "Sargent"
            this.gender = Gender.fromCode("M")
            this.id = "316804da-9234-43d6-b18c-df0cccd46744"
            this.insurabilities = listOf(Insurability().apply {
                this.ambulatory = true
                this.dental = true
                this.endDate = 20491231
                this.hospitalisation = true
                this.identificationNumber = "39672875"
                this.insuranceDescription = "All inclusive"
                this.insuranceId = "57827693"
                this.parameters = mapOf(Pair("Param", "Value"))
                this.startDate = 20000101
                this.titularyId = "35976872"
            })
            this.maidenName = "Beries"
            this.medicalHouseContracts = listOf(MedicalHouseContract().apply {
                this.changeType = ContractChangeType.suspension
                this.changedBy = "Damiane Drouin, Adviser"
                this.contractId = "92571275"
                this.endOfContract = 20290101
                this.endOfCoverage = 20283112
                this.endOfSuspension = tomorrow
                this.hcpId = "971c149d-62a4-4f0c-8aa9-9fbeca47465b"
                this.isForcedSuspension = true
                this.isGp = false
                this.isKine = true
                this.isNoGp = true
                this.isNoKine = false
                this.isNoNurse = false
                this.isNurse = true
                this.mmNihii = "18000131004"
                this.parentContractId = "57217592"
                this.startOfContract = 20183112
                this.startOfCoverage = 20190101
                this.startOfSuspension = yesterday
                this.suspensionReason = SuspensionReason.outsideOfCountry
                this.suspensionSource = "Drouin D., Adviser"
                this.unsubscriptionReasonId = 0
                this.validFrom = 20150101
                this.validTo = 20243112
            })
            this.languages = listOf("fr")
            this.lastName = "Berie"
            this.nationality = "be"
            this.parameters = mapOf(Pair("Param", listOf("Value1", "Value2")))
            this.partnerName = "Fayette Cadieux"
            this.partnerships = listOf(Partnership().apply {
                otherToMeRelationshipDescription = "Sœur"
                meToOtherRelationshipDescription = "Frère"
                partnershipDescription = "Jumeaux"
                partnerId = "793df193-6efb-4e63-b5b3-7bd5570f077a"
                status = PartnershipStatus.active
                type = PartnershipType.sister
            })
            this.patientHealthCareParties = listOf(PatientHealthCareParty().apply {
                this.healthcarePartyId = "3116d667-f3ba-4a9c-ab0a-313c9c3beeff"
                this.isReferral = true
                this.referralPeriods = sortedSetOf(
                        ReferralPeriod().apply {
                            this.comment = "First referral period"
                            this.endDate = dateFormat.parse("20141231").toInstant()
                            this.startDate = dateFormat.parse("20100101").toInstant()
                        },
                        ReferralPeriod().apply {
                            this.comment = "Second referral period"
                            this.endDate = dateFormat.parse("20191231").toInstant()
                            this.startDate = dateFormat.parse("20150101").toInstant()
                        },
                        ReferralPeriod().apply {
                            this.comment = "Third referral period"
                            this.endDate = dateFormat.parse("20241231").toInstant()
                            this.startDate = dateFormat.parse("20200101").toInstant()
                        }
                )
                this.sendFormats = mapOf(Pair(TelecomType.phone, "0484598271"))
                this.type = PatientHealthCarePartyType.doctor
            })
            this.profession = "Cobaye"
            this.patientProfessions = listOf(CodeStub("CD-PROFESSION", "Cobaye professionnel", "1.0"))
            this.personalStatus = PersonalStatus.married
            this.placeOfBirth = "Furnes"
            this.spouseName = "Fayette Cadieux"
            this.warning = "This patient is fake"
            this.ssin = "50010100156"
        }

        val motherPatient = Patient().apply {
            id = "motherPatientID"
            firstName = "motherPatientFirstName"
            lastName = "motherPatientLastName"
            ssin = "50010100156"
            gender = Gender.fromCode("F")
            dateOfBirth = 19500101
            dateOfDeath = 20500101
            placeOfBirth = "NAMUR"
            placeOfDeath = "LIEGE"
            profession = "Cobaye"
            languages = listOf("French")
            addresses = listOf(Address().apply {
                addressType = AddressType.home
                street = "streetMotherPatient"
                houseNumber = "1D"
                postalCode = "1050"
                city = "Ixelles"
                telecoms = listOf(Telecom().apply {
                    telecomType = TelecomType.phone
                    telecomNumber = "0423456789"
                    telecomDescription = "personal phone"
                })
            })
            nationality = "Belge"
        }

        val spousePatient = Patient().apply {
            id = "spousePatientID"
            firstName = "spousePatientFirstName"
            lastName = "spousePatientLastName"
            gender = Gender.fromCode("F")
            addresses = listOf(Address().apply {
                addressType = AddressType.home
                street = "streetSpousePatient"
                postalCode = "1050"
                city = "Ixelles"
                telecoms = listOf(Telecom().apply {
                    telecomType = TelecomType.phone
                    telecomNumber = "0423456789"
                    telecomDescription = "personal phone"
                })
            })
        }

        val sisterPatient = Patient().apply {
            id = "spousePatientID"
            firstName = "spousePatientFirstName"
            lastName = "spousePatientLastName"
            ssin = "50010100156"
            gender = Gender.fromCode("F")
            dateOfBirth = 19500101
            languages = listOf("French")
        }
    }
}

private val healthElements = mutableListOf<HealthElement>()

private class MyHealthElements {
    companion object {
        val historyHealthElementProblem = HealthElement().apply {
            healthElementId = "1"
            descr = "description of historyHealthElementProblem"
            status = 1
            openingDate = yesterday
            closingDate = FuzzyValues.getCurrentFuzzyDate()
            tags = mutableSetOf(MyTags.healthcareelementTag)
            codes = mutableSetOf(MyCodes.clinicalCode)
        }
        val assessmentHealthElementProblem = HealthElement().apply {
            healthElementId = "2"
            descr = "description of assessmentHealthElementProblem"
            status = 1
            openingDate = yesterday
            closingDate = null
            tags = mutableSetOf(MyTags.healthcareelementTag)
            codes = mutableSetOf(MyCodes.clinicalCode)
        }
        val historyHealthElementAllergy = HealthElement().apply {
            healthElementId = "3"
            descr = "description of historyHealthElementAllergy"
            status = 1
            openingDate = yesterday
            closingDate = tomorrow
            tags = mutableSetOf(MyTags.allergyTag)
            codes = mutableSetOf(MyCodes.clinicalCode)
        }
        val assessmentHealthElementAdr = HealthElement().apply {
            this.id = "4"
            this.openingDate = yesterday
            this.closingDate = null
            this.descr = "description of assessmentHealthElementAdr"
            this.note = "This is note for assessmentHealthElementAdr"
            this.status = 1
            this.tags = mutableSetOf(MyTags.adrTag)
            this.codes = mutableSetOf(MyCodes.clinicalCode)
        }
    }
}

fun main() {
    initializeSumehrExport()
    initializeMocks()

    generateMinimalist()
    generateFullPatientSumehr()
    generateFullSenderSumehr()
    generateFullRecipientSumehr()
    generateFullAdrItemSumehr()
    generateFullGmdManagerItemSumehr()
    generateFullContactPersonItemSumehr()
    generateEveryItemsSumehr()
    generateDecryptedSumehr()
}

private fun initializeSumehrExport() {
    sumehrExport.filters = Filters().apply { setApplicationContext(applicationContext) }
    sumehrExport.contactLogic = contactLogic
    sumehrExport.codeLogic = codeLogic
    sumehrExport.healthcarePartyLogic = healthcarePartyLogic
    sumehrExport.healthElementLogic = healthElementLogic
    sumehrExport.patientLogic = patientLogic
    sumehrExport.mapper = mapper
}

private fun initializeMocks() {
    Mockito.`when`(applicationContext.autowireCapableBeanFactory).thenAnswer {
        autowireCapableBeanFactory
    }

    val filtersIterator = filters.iterator()
    Mockito.`when`(autowireCapableBeanFactory.createBean(any(), any() ?: 0, any() ?: false)).thenAnswer {
        filtersIterator.next()
    }

    Mockito.`when`(contactLogic.getServices(any())).thenAnswer {
        val ids = it.getArgumentAt(0, HashSet::class.java) as HashSet<String>
        services.filter { service ->
            ids.contains(service.id)
        }
    }

    Mockito.`when`(contactLogic.findServicesByTag(any(), any(), any(), any(), any(), any())).thenAnswer {
        val tagType = it.getArgumentAt(2, String::class.java)
        val tagCode = it.getArgumentAt(3, String::class.java)

        services.filter { service ->
            service.tags.any { tag ->
                tag.type?.equals(tagType) ?: true &&
                        tag.code?.equals(tagCode) ?: true
            }
        }.map { service ->
            service.id
        }
    }

    Mockito.`when`(codeLogic.isValid(any() ?: Code(), any())).thenAnswer {
        val code = it.getArgumentAt(0, Code::class.java) as Code
        when (code.type) {
            "CD-FED-COUNTRY" -> when (code.code) {
                "be", "belgium", "belgique" -> true
                "fr", "france" -> true
                else -> false
            }
            else -> false
        }
    }

    Mockito.`when`(healthcarePartyLogic.getHealthcareParties(any())).thenAnswer {
        (it.getArgumentAt(0, List::class.java) as List<String>).mapNotNull {
            if (hcparties.containsKey(it)) {
                hcparties[it]
            } else {
                null
            }
        }
    }

    Mockito.`when`(healthcarePartyLogic.getHealthcareParty(any())).thenAnswer {
        hcparties[it.getArgumentAt(0, String::class.java) as String]
    }

    Mockito.`when`(healthElementLogic.findLatestByHCPartySecretPatientKeys(any(), any())).thenAnswer {
        healthElements
    }

    Mockito.`when`(patientLogic.getPatients(any())).thenAnswer {
        (it.getArgumentAt(0, List::class.java) as List<String>).mapNotNull {
            if (patients.containsKey(it)) {
                patients[it]
            } else {
                null
            }
        }
    }

    Mockito.`when`(decryptor.decrypt<ServiceDto>(any(), any())).thenAnswer {
        val encryptedServices = it.getArgumentAt(0, ArrayList::class.java) as ArrayList<ServiceDto>
        object : Future<List<ServiceDto>> {
            private val decryptedServices = encryptedServices.map { it.decrypt() }

            override fun isDone(): Boolean = true
            override fun cancel(mayInterruptIfRunning: Boolean): Boolean = false
            override fun isCancelled(): Boolean = false
            override fun get(): List<ServiceDto> = decryptedServices
            override fun get(timeout: Long, unit: TimeUnit): List<ServiceDto> = decryptedServices
        }
    }

    Mockito.`when`(mapper.map<Service, ServiceDto>(any(Service::class.java), eq(ServiceDto::class.java))).thenAnswer {
        val service = it.getArgumentAt(0, ArrayList::class.java) as Service
        service.map()
    }
}

private fun generateMinimalist() {
    services.clear()
    healthElements.clear()
    hcparties.clear()

    /// First parameter : os
    val os = File(DIR_PATH + "MinimalSumehr.xml").outputStream()

    /// Second parameter : pat
    val patient = MyPatients.minimalistPatient

    /// Third parameter : sfks
    val sfks = listOf("sfks")

    /// Fourth parameter
    val sender = HealthcareParty().apply {
        id = "8e716232-04ce-4262-8f71-3c51521fd740"
        nihii = "18000032004"
        ssin = "50010100156"
        firstName = "Orville"
        lastName = "Flamand"
        addresses = listOf(Address().apply {
            addressType = AddressType.home
            street = "Rue de Berloz"
            houseNumber = "267"
            postalCode = "4860"
            city = "Cornesse"
            telecoms = listOf(Telecom().apply {
                telecomNumber = "0474301934"
            })
        })
        speciality = "persphysician"
    }

    /// Fifth parameter
    val recipient = HealthcareParty().apply {
        speciality = "persphysician"
    }

    /// Seventh parameter
    val comment = "All data is fake"

    /// Eighth parameter
    val excludedIds = emptyList<String>()

    // Execution
    sumehrExport.createSumehr(os, patient, sfks, sender, recipient, language, comment, excludedIds, decryptor)
}

private fun generateEveryItemsSumehr() {
    services.clear()
    healthElements.clear()
    hcparties.clear()

    /// First parameter : os
    val os = File(DIR_PATH + "EveryItemsSumehr.xml").outputStream()

    /// Second parameter : pat
    val patient = MyPatients.fullItemsPatient

    /// Third parameter : sfks
    val sfks = listOf("sfks")

    /// Fourth parameter
    val sender = HealthcareParty().apply {
        nihii = "18000032004"
        id = "idSender"
        ssin = "50010100156"
        firstName = "firstNameSender"
        lastName = "lastNameSender"
        addresses = listOf(Address().apply {
            addressType = AddressType.home
            street = "streetSender"
            houseNumber = "3A"
            postalCode = "1000"
            city = "Bruxelles"
            telecoms = listOf(Telecom().apply {
                telecomType = TelecomType.phone
                telecomNumber = "0423456789"
                telecomDescription = "personal phone"
            })
        })
        gender = Gender.fromCode("M")
        speciality = "persphysician"
        specialityCodes = listOf(CodeStub("CD-HCPARTY", "persphysician", "1"))
    }

    /// Fifth parameter
    val recipient = HealthcareParty().apply {
        nihii = "18000032004"
        id = "idRecipient"
        ssin = "50010100156"
        name = "PMGRecipient"
        addresses = listOf(Address().apply {
            addressType = AddressType.home
            street = "streetRecipient"
            houseNumber = "3A"
            postalCode = "1000"
            city = "Bruxelles"
        })
        gender = Gender.fromCode("M")
        speciality = "persphysician"
    }

    /// Seventh parameter
    val comment = "It's the comment done in main"

    /// Eighth parameter
    val excludedIds = emptyList<String>()

    services.addAll(listOf(MyServices.validServiceADRAssessment, MyServices.validServiceADRHistory))
    services.addAll(listOf(MyServices.validServiceAllergyAssessment, MyServices.validServiceAllergyHistory))
    services.addAll(listOf(MyServices.validServiceSocialriskAssessment, MyServices.validServiceSocialriskHistory))
    services.addAll(listOf(MyServices.validServiceRiskAssessment, MyServices.validServiceRiskHistory))
    services.addAll(listOf(MyServices.patientwillNtbr, MyServices.patientwillBloodtransfusionrefusal, MyServices.patientwillIntubationrefusal, MyServices.patientwillEuthanasiarequest, MyServices.patientwillVaccinationrefusal))
    services.addAll(listOf(MyServices.vaccineValidService))
    services.addAll(listOf(MyServices.medicationValidService, MyServices.treatmentValidService))
    services.addAll(listOf(MyServices.healthissueAssessmentService, MyServices.healthissueHistoryService))
    services.addAll(listOf(MyServices.healthcareelementAssessmentService, MyServices.healthcareelementHistoryService))
    hcparties["1"] = MyHealthcareParties.doctorGMD
    hcparties["2"] = MyHealthcareParties.referralGMD
    hcparties["3"] = MyHealthcareParties.medicalhouseGMD
    hcparties["4"] = MyHealthcareParties.retirementhomeGMD
    patients["Mother"] = MyPatients.motherPatient
    patients["Spouse"] = MyPatients.spousePatient
    patients["Sister"] = MyPatients.sisterPatient
    healthElements.addAll(listOf(MyHealthElements.historyHealthElementProblem, MyHealthElements.assessmentHealthElementProblem, MyHealthElements.assessmentHealthElementAdr, MyHealthElements.historyHealthElementAllergy))

    // Execution
    sumehrExport.createSumehr(os, patient, sfks, sender, recipient, language, comment, excludedIds, decryptor)
}

private fun generateFullPatientSumehr() {
    services.clear()
    healthElements.clear()
    hcparties.clear()

    /// First parameter : os
    val os = File(DIR_PATH + "FullPatientSumehr.xml").outputStream()

    /// Second parameter : pat
    val patient = MyPatients.fullPatient

    /// Third parameter : sfks
    val sfks = listOf("sfks")

    /// Fourth parameter
    val sender = HealthcareParty().apply {
        id = "8e716232-04ce-4262-8f71-3c51521fd740"
        nihii = "18000032004"
        ssin = "50010100156"
        firstName = "Orville"
        lastName = "Flamand"
        addresses = listOf(Address().apply {
            addressType = AddressType.home
            street = "Rue de Berloz"
            houseNumber = "267"
            postalCode = "4860"
            city = "Cornesse"
            telecoms = listOf(Telecom().apply {
                telecomNumber = "0474301934"
            })
        })
        speciality = "persphysician"
    }

    /// Fifth parameter
    val recipient = HealthcareParty().apply {
        speciality = "persphysician"
    }

    /// Seventh parameter
    val comment = "All data is fake"

    /// Eighth parameter
    val excludedIds = emptyList<String>()

    // Execution
    sumehrExport.createSumehr(os, patient, sfks, sender, recipient, language, comment, excludedIds, decryptor)
}

private fun generateFullSenderSumehr() {
    services.clear()
    healthElements.clear()
    hcparties.clear()

    /// First parameter : os
    val os = File(DIR_PATH + "FullSenderSumehr.xml").outputStream()

    /// Second parameter : pat
    val patient = Patient().apply {
        id = "316804da-9234-43d6-b18c-df0cccd46744"
        firstName = "Sargent"
        lastName = "Berie"
        ssin = "50010100156"
        gender = Gender.fromCode("M")
        dateOfBirth = 19500101
        languages = listOf("French")
    }

    /// Third parameter : sfks
    val sfks = listOf("sfks")

    /// Fourth parameter
    val sender = HealthcareParty().apply {
        addresses = listOf(Address().apply {
            addressType = AddressType.home
            street = "Rue de Berloz"
            houseNumber = "267"
            postalCode = "4860"
            city = "Cornesse"
            telecoms = listOf(Telecom().apply {
                telecomDescription = "This phone number is fake"
                telecomNumber = "0474301934"
                telecomType = TelecomType.mobile
            })
        })
        bankAccount = "491665804694"
        bic = "BPOTBEB1"
        billingType = "virement"
        cbe = "cbeSender"
        companyName = "CHU Pepinster"
        contactPerson = "Olympia Poisson"
        contactPersonHcpId = "78918e6c-f1f4-4940-bd1b-bc517ee8304b"
        convention = 1
        financialInstitutionInformation = listOf(FinancialInstitutionInformation().apply {
            this.bankAccount = "453967676001"
            this.bic = "BPOTBEB1"
            this.key = "756"
            this.name = "MME OLYMPIA POISSON"
            this.preferredFiiForPartners = setOf("Foreign Institutional Investor")
            this.proxyBankAccount = "1000676769354"
            this.proxyBic = "BPOTBEB"
        })
        firstName = "Orville"
        flatRateTarifications
        id = "8e716232-04ce-4262-8f71-3c51521fd740"
        invoiceHeader = "CHU Pepinster, Unité d'Accueil"
        lastName = "Flamand"
        nihii = "18000032004"
        nihiiSpecCode = "004"
        notes = "This sender is fake"
        options = mapOf(Pair("optionsKey", "optionsValue"))
        parentId = "da3b518f-77b4-4ed4-a439-341497d35f77"
        proxyBankAccount = "496408566194"
        proxyBic = "BPOTBEB"
        sendFormats = mapOf(Pair(TelecomType.email, "orville.flamand@rhyta.com"))
        speciality = "persphysician"
        specialityCodes = listOf(CodeStub("CD-HCPARTY", "persphysician", "1.0"))
        ssin = "50010100156"
        statuses = listOf(HealthcarePartyStatus.accreditated)
        supervisorId = "9314b58f-b278-4aa5-bb0b-0030f1d2f05b"
        type = "Fake sender"
        userId = "43114031"
    }

    /// Fifth parameter
    val recipient = HealthcareParty().apply {
        speciality = "persphysician"
    }

    /// Seventh parameter
    val comment = "All data is fake"

    /// Eighth parameter
    val excludedIds = emptyList<String>()

    // Execution
    sumehrExport.createSumehr(os, patient, sfks, sender, recipient, language, comment, excludedIds, decryptor)
}

private fun generateFullRecipientSumehr() {
    services.clear()
    healthElements.clear()
    hcparties.clear()

    /// First parameter : os
    val os = File(DIR_PATH + "FullRecipientSumehr.xml").outputStream()

    /// Second parameter : pat
    val patient = Patient().apply {
        id = "316804da-9234-43d6-b18c-df0cccd46744"
        firstName = "Sargent"
        lastName = "Berie"
        ssin = "50010100156"
        gender = Gender.fromCode("M")
        dateOfBirth = 19500101
        languages = listOf("French")
    }

    /// Third parameter : sfks
    val sfks = listOf("sfks")

    /// Fourth parameter
    val sender = HealthcareParty().apply {
        id = "8e716232-04ce-4262-8f71-3c51521fd740"
        nihii = "18000032004"
        ssin = "50010100156"
        firstName = "Orville"
        lastName = "Flamand"
        addresses = listOf(Address().apply {
            addressType = AddressType.home
            street = "Rue de Berloz"
            houseNumber = "267"
            postalCode = "4860"
            city = "Cornesse"
            telecoms = listOf(Telecom().apply {
                telecomNumber = "0474301934"
            })
        })
        speciality = "persphysician"
    }

    /// Fifth parameter
    val recipient = HealthcareParty().apply {
        addresses = listOf(Address().apply {
            addressType = AddressType.work
            street = "Rue de Fromelenne"
            houseNumber = "337"
            postalCode = "9050"
            city = "Gentbrugge"
            telecoms = listOf(Telecom().apply {
                telecomDescription = "This phone number is fake"
                telecomNumber = "0471114951"
                telecomType = TelecomType.mobile
            })
        })
        bankAccount = "553065896726"
        bic = "BPOTBEB1"
        billingType = "virement"
        cbe = "cbeRecipient"
        civility = ""
        companyName = "CM Deserres"
        contactPerson = "Christelle Deserres"
        convention = 1
        contactPersonHcpId = "1cc58445-d3a6-4f02-bcd4-7f0a3fd84c4d"
        financialInstitutionInformation = listOf(FinancialInstitutionInformation().apply {
            this.bankAccount = "471616447946"
            this.bic = "BPOTBEB1"
            this.key = "824"
            this.name = "MME CHRISTELLE DESERRES"
            this.preferredFiiForPartners = setOf("Foreign Institutional Investor")
            this.proxyBankAccount = "649744616174"
            this.proxyBic = "BPOTBEB"
        })
        flatRateTarifications = listOf(FlatRateTarification().apply {
            code = "RAoS"
            flatRateType = FlatRateType.physician
            label = mapOf(Pair("en", "RAoS"), Pair("fr", "SRdA"))
            valorisations = setOf(Valorisation().apply {
                startOfValidity = 20000101
                endOfValidity = 20293112
                predicate = "FrtValorisationPredicate"
                totalAmount = 30.00
                reimbursement = 25.00
                patientIntervention = 3.90
                doctorSupplement = 1.10
                vat = 4.50
                label = mapOf(Pair("en", "Rheumatic Aortic Stenosis"), Pair("fr", "Sténose rhumatoïde de l'Aorte"))
            })
        })
        gender = Gender.unknown
        id = "fa018b68-458d-426a-a032-30a6df5c9e51"
        invoiceHeader = "CM Deserres, avec et sans RDV"
        languages = listOf("fr")
        name = "CM Deserres"
        nihii = "18000230004"
        nihiiSpecCode = "004"
        notes = "This recipient is fake"
        options = mapOf(Pair("optionsKey", "optionsValue"))
        parentId = "d68048cb-2a39-41bc-9627-e78b7b04f704"
        proxyBankAccount = "627698560355"
        proxyBic = "BPOTBEB"
        rev = "revRecipient"
        revHistory = mapOf(Pair("revHistoryKey", "revHistoryValue"))
        revisionsInfo = arrayOf(RevisionInfo().apply {
            rev = "revInfoRecipient"
            status = "revInfoStatus"
        })
        sendFormats = mapOf(Pair(TelecomType.email, "cm-deserres@dayrep.com"))
        speciality = "persphysician"
        specialityCodes = listOf(CodeStub("CD-HCPARTY", "persphysician", "1.0"))
        ssin = "50010100156"
        statuses = listOf(HealthcarePartyStatus.accreditated)
        type = "typeRecipient"
        userId = "b78ed49a-7dec-484e-8ae4-481948eb8725"
    }

    /// Seventh parameter
    val comment = "All data is fake"

    /// Eighth parameter
    val excludedIds = emptyList<String>()

    // Execution
    sumehrExport.createSumehr(os, patient, sfks, sender, recipient, language, comment, excludedIds, decryptor)
}

private fun generateDecryptedSumehr() {
    services.clear()
    healthElements.clear()
    hcparties.clear()

    /// First parameter : os
    val os = File(DIR_PATH + "DecryptedSumehr.xml").outputStream()

    /// Second parameter : pat
    val patient = MyPatients.minimalistPatient

    /// Third parameter : sfks
    val sfks = listOf("sfks")

    /// Fourth parameter
    val sender = HealthcareParty().apply {
        id = "8e716232-04ce-4262-8f71-3c51521fd740"
        nihii = "18000032004"
        ssin = "50010100156"
        firstName = "Orville"
        lastName = "Flamand"
        addresses = listOf(Address().apply {
            addressType = AddressType.home
            street = "Rue de Berloz"
            houseNumber = "267"
            postalCode = "4860"
            city = "Cornesse"
            telecoms = listOf(Telecom().apply {
                telecomNumber = "0474301934"
            })
        })
        speciality = "persphysician"
    }

    /// Fifth parameter
    val recipient = HealthcareParty().apply {
        speciality = "persphysician"
    }

    /// Seventh parameter
    val comment = "All data is fake"

    /// Eighth parameter
    val excludedIds = emptyList<String>()

    services.addAll(listOf(MyServices.validServiceEncryptedADR, MyServices.validServiceEncryptedAllergy, MyServices.validServiceEncryptedSocialrisk, MyServices.validServiceEncryptedRisk))

    // Execution
    sumehrExport.createSumehr(os, patient, sfks, sender, recipient, language, comment, excludedIds, decryptor)
}

private fun generateFullAdrItemSumehr() { // same structure as 'allergy', 'risk' and 'socialrisk'
    services.clear()
    healthElements.clear()
    hcparties.clear()

    /// First parameter : os
    val os = File(DIR_PATH + "FullAdrItemSumehr.xml").outputStream()

    /// Second parameter : pat
    val patient = MyPatients.minimalistPatient

    /// Third parameter : sfks
    val sfks = listOf("sfks")

    /// Fourth parameter
    val sender = HealthcareParty().apply {
        id = "8e716232-04ce-4262-8f71-3c51521fd740"
        nihii = "18000032004"
        ssin = "50010100156"
        firstName = "Orville"
        lastName = "Flamand"
        addresses = listOf(Address().apply {
            addressType = AddressType.home
            street = "Rue de Berloz"
            houseNumber = "267"
            postalCode = "4860"
            city = "Cornesse"
            telecoms = listOf(Telecom().apply {
                telecomNumber = "0474301934"
            })
        })
        speciality = "persphysician"
    }

    /// Fifth parameter
    val recipient = HealthcareParty().apply {
        speciality = "persphysician"
    }

    /// Seventh parameter
    val comment = "All the data is fake"

    /// Eighth parameter
    val excludedIds = emptyList<String>()

    services.addAll(listOf(MyServices.validServiceADRAssessment))
    healthElements.addAll(listOf(MyHealthElements.assessmentHealthElementAdr))

    // Execution
    sumehrExport.createSumehr(os, patient, sfks, sender, recipient, language, comment, excludedIds, decryptor)
}

private fun generateFullGmdManagerItemSumehr() {
    services.clear()
    healthElements.clear()
    hcparties.clear()

    /// First parameter : os
    val os = File(DIR_PATH + "FullGmdManagerItemSumehr.xml").outputStream()

    /// Second parameter : pat
    val patient = MyPatients.fullItemsPatient

    /// Third parameter : sfks
    val sfks = listOf("sfks")

    /// Fourth parameter
    val sender = HealthcareParty().apply {
        id = "8e716232-04ce-4262-8f71-3c51521fd740"
        nihii = "18000032004"
        ssin = "50010100156"
        firstName = "Orville"
        lastName = "Flamand"
        addresses = listOf(Address().apply {
            addressType = AddressType.home
            street = "Rue de Berloz"
            houseNumber = "267"
            postalCode = "4860"
            city = "Cornesse"
            telecoms = listOf(Telecom().apply {
                telecomNumber = "0474301934"
            })
        })
        speciality = "persphysician"
    }

    /// Fifth parameter
    val recipient = HealthcareParty().apply {
        speciality = "persphysician"
    }

    /// Seventh parameter
    val comment = "All the data is fake"

    /// Eighth parameter
    val excludedIds = emptyList<String>()

    hcparties["1"] = MyHealthcareParties.doctorGMD
    hcparties["2"] = MyHealthcareParties.referralGMD
    hcparties["3"] = MyHealthcareParties.medicalhouseGMD
    hcparties["4"] = MyHealthcareParties.retirementhomeGMD

    // Execution
    sumehrExport.createSumehr(os, patient, sfks, sender, recipient, language, comment, excludedIds, decryptor)
}

private fun generateFullContactPersonItemSumehr() {
    services.clear()
    healthElements.clear()
    hcparties.clear()

    /// First parameter : os
    val os = File(DIR_PATH + "FullContactPersonItemSumehr.xml").outputStream()

    /// Second parameter : pat
    val patient = MyPatients.fullItemsPatient

    /// Third parameter : sfks
    val sfks = listOf("sfks")

    /// Fourth parameter
    val sender = HealthcareParty().apply {
        id = "8e716232-04ce-4262-8f71-3c51521fd740"
        nihii = "18000032004"
        ssin = "50010100156"
        firstName = "Orville"
        lastName = "Flamand"
        addresses = listOf(Address().apply {
            addressType = AddressType.home
            street = "Rue de Berloz"
            houseNumber = "267"
            postalCode = "4860"
            city = "Cornesse"
            telecoms = listOf(Telecom().apply {
                telecomNumber = "0474301934"
            })
        })
        speciality = "persphysician"
    }

    /// Fifth parameter
    val recipient = HealthcareParty().apply {
        speciality = "persphysician"
    }

    /// Seventh parameter
    val comment = "All the data is fake"

    /// Eighth parameter
    val excludedIds = emptyList<String>()

    patients["Mother"] = MyPatients.motherPatient
    patients["Spouse"] = MyPatients.spousePatient
    patients["Sister"] = MyPatients.sisterPatient

    // Execution
    sumehrExport.createSumehr(os, patient, sfks, sender, recipient, language, comment, excludedIds, decryptor)
}

private fun Service.map(): ServiceDto {
    return ServiceDto().apply {
        this@apply.author = this@map.author
        this@apply.closingDate = this@map.closingDate
        this@apply.codes = this@map.codes.map { it.map() }.toSet()
        this@apply.comment = this@map.comment
        this@apply.content = this@map.content.map { entry -> Pair(entry.key, entry.value.map()) }.toMap()
        this@apply.contactId = this@map.contactId
        this@apply.created = this@map.created
        this@apply.encryptedSelf = this@map.encryptedSelf
        this@apply.encryptedContent = this@map.encryptedContent
        this@apply.encryptionKeys = this@map.encryptionKeys.map { entry -> Pair(entry.key, entry.value.map { it.map() }) }.toMap()
        this@apply.endOfLife = this@map.endOfLife
        this@apply.formId = this@map.formId
        this@apply.healthElementsIds = this@map.healthElementsIds?.toMutableSet()
        this@apply.id = this@map.id
        this@apply.index = this@map.index
        this@apply.invoicingCodes = this@map.invoicingCodes?.toMutableSet()
        this@apply.label = this@map.label
        this@apply.modified = this@map.modified
        this@apply.openingDate = this@map.openingDate
        this@apply.plansOfActionIds = this@map.plansOfActionIds?.toMutableSet()
        this@apply.responsible = this@map.responsible
        this@apply.secretForeignKeys = this@map.secretForeignKeys?.toMutableSet()
        this@apply.status = this@map.status
        this@apply.subContactIds = this@map.subContactIds?.toMutableSet()
        this@apply.tags = this@map.tags.map { it.map() }.toSet()
        this@apply.textIndexes = this@map.textIndexes.map { Pair(it.key, it.value) }.toMap()
        this@apply.valueDate = this@map.valueDate
    }
}

private fun ServiceDto.map(): Service {
    return Service().apply {
        this@apply.author = this@map.author
        this@apply.closingDate = this@map.closingDate
        this@apply.codes = this@map.codes.map { it.mapStub() }.toSet()
        this@apply.comment = this@map.comment
        this@apply.content = this@map.content.map { entry -> Pair(entry.key, entry.value.map()) }.toMap()
        this@apply.contactId = this@map.contactId
        this@apply.created = this@map.created
        this@apply.encryptedSelf = this@map.encryptedSelf
        this@apply.encryptedContent = this@map.encryptedContent
        this@apply.encryptionKeys = this@map.encryptionKeys.map { entry -> Pair(entry.key, entry.value.map { it.map() }.toSet()) }.toMap()
        this@apply.endOfLife = this@map.endOfLife
        this@apply.formId = this@map.formId
        this@apply.healthElementsIds = this@map.healthElementsIds?.toMutableSet()
        this@apply.id = this@map.id
        this@apply.index = this@map.index
        this@apply.invoicingCodes = this@map.invoicingCodes?.toMutableSet()
        this@apply.label = this@map.label
        this@apply.modified = this@map.modified
        this@apply.openingDate = this@map.openingDate
        this@apply.plansOfActionIds = this@map.plansOfActionIds?.toMutableSet()
        this@apply.responsible = this@map.responsible
        this@apply.secretForeignKeys = this@map.secretForeignKeys?.toMutableSet()
        this@apply.status = this@map.status
        this@apply.subContactIds = this@map.subContactIds?.toMutableSet()
        this@apply.tags = this@map.tags.map { it.mapStub() }.toSet()
        this@apply.textIndexes = this@map.textIndexes.map { Pair(it.key, it.value) }.toMap()
        this@apply.valueDate = this@map.valueDate
    }
}

private fun ServiceDto.copy(): ServiceDto {
    return ServiceDto().apply {
        this@apply.author = this@copy.author
        this@apply.closingDate = this@copy.closingDate
        this@apply.codes = this@copy.codes.map { it.copy() }.toSet()
        this@apply.comment = this@copy.comment
        this@apply.content = this@copy.content.map { entry -> Pair(entry.key, entry.value.copy()) }.toMap()
        this@apply.contactId = this@copy.contactId
        this@apply.created = this@copy.created
        this@apply.encryptedSelf = this@copy.encryptedSelf
        this@apply.encryptedContent = this@copy.encryptedContent
        this@apply.encryptionKeys = this@copy.encryptionKeys.map { entry -> Pair(entry.key, entry.value.map { it.copy() }) }.toMap()
        this@apply.endOfLife = this@copy.endOfLife
        this@apply.formId = this@copy.formId
        this@apply.healthElementsIds = this@copy.healthElementsIds?.toMutableSet()
        this@apply.id = this@copy.id
        this@apply.index = this@copy.index
        this@apply.invoicingCodes = this@copy.invoicingCodes?.toMutableSet()
        this@apply.label = this@copy.label
        this@apply.modified = this@copy.modified
        this@apply.openingDate = this@copy.openingDate
        this@apply.plansOfActionIds = this@copy.plansOfActionIds?.toMutableSet()
        this@apply.responsible = this@copy.responsible
        this@apply.secretForeignKeys = this@copy.secretForeignKeys?.toMutableSet()
        this@apply.status = this@copy.status
        this@apply.subContactIds = this@copy.subContactIds?.toMutableSet()
        this@apply.tags = this@copy.tags
        this@apply.textIndexes = this@copy.textIndexes.map { Pair(it.key, it.value) }.toMap()
        this@apply.valueDate = this@copy.valueDate
    }
}

private fun RegimenItem.AdministrationQuantity.map(): RegimenItemDto.AdministrationQuantity {
    return RegimenItemDto.AdministrationQuantity().apply {
        this@apply.administrationUnit = this@map.administrationUnit.map()
        this@apply.quantity = this@map.quantity
        this@apply.unit = this@map.unit
    }
}

private fun RegimenItemDto.AdministrationQuantity.map(): RegimenItem.AdministrationQuantity {
    return RegimenItem.AdministrationQuantity().apply {
        this@apply.administrationUnit = this@map.administrationUnit.map()
        this@apply.quantity = this@map.quantity
        this@apply.unit = this@map.unit
    }
}

private fun AgreementAppendix.map(): AgreementAppendixDto {
    return AgreementAppendixDto().apply {
        this@apply.docSeq = this@map.docSeq
        this@apply.documentId = this@map.documentId
        this@apply.path = this@map.path
        this@apply.verseSeq = this@map.verseSeq
    }
}

private fun AgreementAppendixDto.map(): AgreementAppendix {
    return AgreementAppendix().apply {
        this@apply.docSeq = this@map.docSeq
        this@apply.documentId = this@map.documentId
        this@apply.path = this@map.path
        this@apply.verseSeq = this@map.verseSeq
    }
}

private fun Code.map(): CodeDto {
    return CodeDto().apply {
        this@apply.code = this@map.code
        this@apply.data = this@map.data
        this@apply.flags = this@map.flags.map { it.map() }
        this@apply.label = this@map.label
        this@apply.level = this@map.level
        this@apply.links = this@map.links?.toList()
        this@apply.parent = this@map.parent
        this@apply.qualifiedLinks = this@map.qualifiedLinks.map { Pair(it.key, it.value?.toMutableList()) }.toMap()
    }
}

private fun CodeDto.copy(): CodeDto {
    return CodeDto().apply {
        this@apply.code = this@copy.code
        this@apply.data = this@copy.data
        this@apply.flags = this@copy.flags?.toList()
        this@apply.label = this@copy.label
        this@apply.level = this@copy.level
        this@apply.links = this@copy.links?.toList()
        this@apply.parent = this@copy.parent
        this@apply.qualifiedLinks = this@copy.qualifiedLinks.map { Pair(it.key, it.value?.toMutableList()) }.toMap()
    }
}

private fun org.taktik.icure.entities.base.CodeFlag.map(): CodeFlag {
    return when (this@map) {
        org.taktik.icure.entities.base.CodeFlag.male_only -> CodeFlag.male_only
        org.taktik.icure.entities.base.CodeFlag.female_only -> CodeFlag.female_only
        else -> null
    } as CodeFlag
}

private fun CodeDto.map(): Code {
    return Code().apply {
        this@apply.code = this@map.code
    }
}

private fun CodeDto.mapStub(): CodeStub {
    return CodeStub().apply {
        this@apply.code = this@mapStub.code
    }
}

private fun CodeStub.map(): CodeDto {
    return CodeDto().apply {
        this@apply.code = this@map.code
    }
}

private fun Content.map(): ContentDto {
    return ContentDto().apply {
        this@apply.binaryValue = this@map.binaryValue
        this@apply.booleanValue = this@map.booleanValue
        this@apply.documentId = this@map.documentId
        this@apply.fuzzyDateValue = this@map.fuzzyDateValue
        this@apply.instantValue = this@map.instantValue?.toEpochMilli()
        this@apply.measureValue = this@map.measureValue?.map()
        this@apply.medicationValue = this@map.medicationValue?.map()
        this@apply.numberValue = this@map.numberValue
        this@apply.stringValue = this@map.stringValue
    }
}

private fun ContentDto.map(): Content {
    return Content().apply {
        this@apply.binaryValue = this@map.binaryValue
        this@apply.booleanValue = this@map.booleanValue
        this@apply.documentId = this@map.documentId
        this@apply.fuzzyDateValue = this@map.fuzzyDateValue
        this@apply.instantValue = this@map.instantValue?.let { Instant.ofEpochMilli(it) }
        this@apply.measureValue = this@map.measureValue?.map()
        this@apply.medicationValue = this@map.medicationValue?.map()
        this@apply.numberValue = this@map.numberValue
        this@apply.stringValue = this@map.stringValue
    }
}

private fun ContentDto.copy(): ContentDto {
    return ContentDto().apply {
        this@apply.binaryValue = this@copy.binaryValue
        this@apply.booleanValue = this@copy.booleanValue
        this@apply.documentId = this@copy.documentId
        this@apply.fuzzyDateValue = this@copy.fuzzyDateValue
        this@apply.instantValue = this@copy.instantValue
        this@apply.measureValue = this@copy.measureValue.copy()
        this@apply.medicationValue = this@copy.medicationValue.copy()
        this@apply.numberValue = this@copy.numberValue
        this@apply.stringValue = this@copy.stringValue
    }
}

private fun DelegationDto.copy(): DelegationDto {
    return DelegationDto().apply {
        this@apply.delegatedTo = this@copy.delegatedTo
        this@apply.key = this@copy.key
        this@apply.owner = this@copy.owner
        this@apply.tag = this@copy.tag
    }
}

private fun Delegation.map(): DelegationDto {
    return DelegationDto().apply {
        this@apply.delegatedTo = this@map.delegatedTo
        this@apply.key = this@map.key
        this@apply.owner = this@map.owner
        this@apply.tag = this@map.tags.firstOrNull { !it.isNullOrBlank() } ?: ""
    }
}

private fun DelegationDto.map(): Delegation {
    return Delegation().apply {
        this@apply.delegatedTo = this@map.delegatedTo
        this@apply.key = this@map.key
        this@apply.owner = this@map.owner
        this@apply.tags = this@map.tag?.let { listOf(it) } ?: emptyList<String>()
    }
}

private fun Duration.map(): DurationDto {
    return DurationDto().apply {
        this@apply.unit = this@map.unit.map()
        this@apply.value = this@map.value
    }
}

private fun DurationDto.map(): Duration {
    return Duration().apply {
        this@apply.unit = this@map.unit.mapStub()
        this@apply.value = this@map.value
    }
}

private fun Measure.map(): MeasureDto {
    return MeasureDto().apply {
        this@apply.comment = this@map.comment
        this@apply.max = this@map.max
        this@apply.min = this@map.min
        this@apply.ref = this@map.ref
        this@apply.severity = this@map.severity
        this@apply.unit = this@map.unit
        this@apply.unitCodes = this@map.unitCodes.map { it.map() }.toSet()
        this@apply.value = this@map.value
    }
}

private fun MeasureDto.map(): Measure {
    return Measure().apply {
        this@apply.comment = this@map.comment
        this@apply.max = this@map.max
        this@apply.min = this@map.min
        this@apply.ref = this@map.ref
        this@apply.severity = this@map.severity
        this@apply.unit = this@map.unit
        this@apply.unitCodes = this@map.unitCodes.map { it.mapStub() }.toSet()
        this@apply.value = this@map.value
    }
}

private fun MeasureDto.copy(): MeasureDto {
    return MeasureDto().apply {
        this@apply.comment = this@copy.comment
        this@apply.max = this@copy.max
        this@apply.min = this@copy.min
        this@apply.ref = this@copy.ref
        this@apply.severity = this@copy.severity
        this@apply.unit = this@copy.unit
        this@apply.unitCodes = this@copy.unitCodes
        this@apply.value = this@copy.value
    }
}

private fun Medication.map(): MedicationDto {
    return MedicationDto().apply {
        this@apply.compoundPrescription = this@map.compoundPrescription
        this@apply.substanceProduct = this@map.substanceProduct?.map()
        this@apply.medicinalProduct = this@map.medicinalProduct?.map()
        this@apply.numberOfPackages = this@map.numberOfPackages
        this@apply.batch = this@map.batch
        this@apply.instructionForPatient = this@map.instructionForPatient
        this@apply.commentForDelivery = this@map.commentForDelivery
        this@apply.drugRoute = this@map.drugRoute
        this@apply.temporality = this@map.temporality
        this@apply.duration = this@map.duration?.map()
        this@apply.renewal = this@map.renewal.map()
        this@apply.beginMoment = this@map.beginMoment
        this@apply.endMoment = this@map.endMoment
        this@apply.knownUsage = this@map.knownUsage
        this@apply.frequency = this@map.frequency.map()
        this@apply.reimbursementReason = this@map.reimbursementReason.map()
        this@apply.substitutionAllowed = this@map.substitutionAllowed
        this@apply.regimen = this@map.regimen.map { it.map() }
        this@apply.posology = this@map.posology
        this@apply.options = this@map.options.map { Pair(it.key, it.value.map()) }.toMap()
        this@apply.agreements = this@map.agreements.map { Pair(it.key, it.value.map()) }.toMap()
        this@apply.medicationSchemeIdOnSafe = this@map.medicationSchemeIdOnSafe
        this@apply.medicationSchemeSafeVersion = this@map.medicationSchemeSafeVersion
        this@apply.medicationSchemeTimeStampOnSafe = this@map.medicationSchemeTimeStampOnSafe
        this@apply.medicationSchemeDocumentId = this@map.medicationSchemeDocumentId
        this@apply.safeIdName = this@map.safeIdName
        this@apply.idOnSafes = this@map.idOnSafes
        this@apply.timestampOnSafe = this@map.timestampOnSafe
        this@apply.changeValidated = this@map.changeValidated
        this@apply.newSafeMedication = this@map.newSafeMedication
        this@apply.medicationUse = this@map.medicationUse
        this@apply.beginCondition = this@map.beginCondition
        this@apply.endCondition = this@map.endCondition
        this@apply.origin = this@map.origin
        this@apply.medicationChanged = this@map.medicationChanged
        this@apply.posologyChanged = this@map.posologyChanged
        this@apply.prescriptionRID = this@map.prescriptionRID
    }
}

private fun MedicationDto.map(): Medication {
    return Medication().apply {
        this@apply.compoundPrescription = this@map.compoundPrescription
        this@apply.substanceProduct = this@map.substanceProduct?.map()
        this@apply.medicinalProduct = this@map.medicinalProduct?.map()
        this@apply.numberOfPackages = this@map.numberOfPackages
        this@apply.batch = this@map.batch
        this@apply.instructionForPatient = this@map.instructionForPatient
        this@apply.commentForDelivery = this@map.commentForDelivery
        this@apply.drugRoute = this@map.drugRoute
        this@apply.temporality = this@map.temporality
        this@apply.duration = this@map.duration?.map()
        this@apply.renewal = this@map.renewal.map()
        this@apply.beginMoment = this@map.beginMoment
        this@apply.endMoment = this@map.endMoment
        this@apply.knownUsage = this@map.knownUsage
        this@apply.frequency = this@map.frequency.map()
        this@apply.reimbursementReason = this@map.reimbursementReason.map()
        this@apply.substitutionAllowed = this@map.substitutionAllowed
        this@apply.regimen = this@map.regimen.map { it.map() }
        this@apply.posology = this@map.posology
        this@apply.options = this@map.options.map { Pair(it.key, it.value.map()) }.toMap()
        this@apply.agreements = this@map.agreements.map { Pair(it.key, it.value.map()) }.toMap()
        this@apply.medicationSchemeIdOnSafe = this@map.medicationSchemeIdOnSafe
        this@apply.medicationSchemeSafeVersion = this@map.medicationSchemeSafeVersion
        this@apply.medicationSchemeTimeStampOnSafe = this@map.medicationSchemeTimeStampOnSafe
        this@apply.medicationSchemeDocumentId = this@map.medicationSchemeDocumentId
        this@apply.safeIdName = this@map.safeIdName
        this@apply.idOnSafes = this@map.idOnSafes
        this@apply.timestampOnSafe = this@map.timestampOnSafe
        this@apply.changeValidated = this@map.changeValidated
        this@apply.newSafeMedication = this@map.newSafeMedication
        this@apply.medicationUse = this@map.medicationUse
        this@apply.beginCondition = this@map.beginCondition
        this@apply.endCondition = this@map.endCondition
        this@apply.origin = this@map.origin
        this@apply.medicationChanged = this@map.medicationChanged
        this@apply.posologyChanged = this@map.posologyChanged
        this@apply.prescriptionRID = this@map.prescriptionRID
    }
}

private fun MedicationDto.copy(): MedicationDto {
    return MedicationDto().apply {
        this@apply.agreements = this@copy.agreements
        this@apply.batch = this@copy.batch
        this@apply.beginCondition = this@copy.beginCondition
        this@apply.beginMoment = this@copy.beginMoment
        this@apply.changeValidated = this@copy.changeValidated
        this@apply.commentForDelivery = this@copy.commentForDelivery
        this@apply.compoundPrescription = this@copy.compoundPrescription
        this@apply.drugRoute = this@copy.drugRoute
        this@apply.duration = this@copy.duration
        this@apply.endCondition = this@copy.endCondition
        this@apply.endMoment = this@copy.endMoment
        this@apply.frequency = this@copy.frequency
        this@apply.idOnSafes = this@copy.idOnSafes
        this@apply.instructionForPatient = this@copy.instructionForPatient
        this@apply.knownUsage = this@copy.knownUsage
        this@apply.medicationChanged = this@copy.medicationChanged
        this@apply.medicationSchemeDocumentId = this@copy.medicationSchemeDocumentId
        this@apply.medicationSchemeIdOnSafe = this@copy.medicationSchemeIdOnSafe
        this@apply.medicationSchemeSafeVersion = this@copy.medicationSchemeSafeVersion
        this@apply.medicationSchemeTimeStampOnSafe = this@copy.medicationSchemeTimeStampOnSafe
        this@apply.medicationUse = this@copy.medicationUse
        this@apply.medicinalProduct = this@copy.medicinalProduct
        this@apply.newSafeMedication = this@copy.newSafeMedication
        this@apply.numberOfPackages = this@copy.numberOfPackages
        this@apply.options = this@copy.options
        this@apply.origin = this@copy.origin
        this@apply.posology = this@copy.posology
        this@apply.posologyChanged = this@copy.posologyChanged
        this@apply.prescriptionRID = this@copy.prescriptionRID
        this@apply.regimen = this@copy.regimen
        this@apply.reimbursementReason = this@copy.reimbursementReason
        this@apply.renewal = this@copy.renewal
        this@apply.safeIdName = this@copy.safeIdName
        this@apply.substanceProduct = this@copy.substanceProduct
        this@apply.substitutionAllowed = this@copy.substitutionAllowed
        this@apply.temporality = this@copy.temporality
        this@apply.timestampOnSafe = this@copy.timestampOnSafe
    }
}

private fun Medicinalproduct.map(): MedicinalproductDto {
    return MedicinalproductDto().apply {
        this@apply.deliveredcds = this@map.deliveredcds.map { it.map() }
        this@apply.deliveredname = this@map.deliveredname
        this@apply.intendedcds = this@map.intendedcds.map { it.map() }
        this@apply.intendedname = this@map.intendedname
    }
}

private fun MedicinalproductDto.map(): Medicinalproduct {
    return Medicinalproduct().apply {
        this@apply.deliveredcds = this@map.deliveredcds.map { it.mapStub() }
        this@apply.deliveredname = this@map.deliveredname
        this@apply.intendedcds = this@map.intendedcds.map { it.mapStub() }
        this@apply.intendedname = this@map.intendedname
    }
}

private fun ParagraphAgreement.map(): ParagraphAgreementDto {
    return ParagraphAgreementDto().apply {
        this@apply.agreementAppendices = this@map.agreementAppendices.map { it.map() }
        this@apply.cancelationDate = this@map.cancelationDate
        this@apply.careProviderReference = this@map.careProviderReference
        this@apply.coverageType = this@map.coverageType
        this@apply.decisionReference = this@map.decisionReference
        this@apply.documentId = this@map.documentId
        this@apply.end = this@map.end
        this@apply.ioRequestReference = this@map.ioRequestReference
        this@apply.paragraph = this@map.paragraph
        this@apply.quantityUnit = this@map.quantityUnit
        this@apply.quantityValue = this@map.quantityValue
        this@apply.refusalJustification = this@map.refusalJustification
        this@apply.responseType = this@map.responseType
        this@apply.start = this@map.start
        this@apply.strength = this@map.strength
        this@apply.strengthUnit = this@map.strengthUnit
        this@apply.timestamp = this@map.timestamp
        this@apply.unitNumber = this@map.unitNumber
        this@apply.verses = this@map.verses
    }
}

private fun ParagraphAgreementDto.map(): ParagraphAgreement {
    return ParagraphAgreement().apply {
        this@apply.agreementAppendices = this@map.agreementAppendices.map { it.map() }
        this@apply.cancelationDate = this@map.cancelationDate
        this@apply.careProviderReference = this@map.careProviderReference
        this@apply.coverageType = this@map.coverageType
        this@apply.decisionReference = this@map.decisionReference
        this@apply.documentId = this@map.documentId
        this@apply.end = this@map.end
        this@apply.ioRequestReference = this@map.ioRequestReference
        this@apply.paragraph = this@map.paragraph
        this@apply.quantityUnit = this@map.quantityUnit
        this@apply.quantityValue = this@map.quantityValue
        this@apply.refusalJustification = this@map.refusalJustification
        this@apply.responseType = this@map.responseType
        this@apply.start = this@map.start
        this@apply.strength = this@map.strength
        this@apply.strengthUnit = this@map.strengthUnit
        this@apply.timestamp = this@map.timestamp
        this@apply.unitNumber = this@map.unitNumber
        this@apply.verses = this@map.verses
    }
}

private fun RegimenItem.map(): RegimenItemDto {
    return RegimenItemDto().apply {
        this@apply.administratedQuantity = this@map.administratedQuantity.map()
        this@apply.date = this@map.date
        this@apply.dayNumber = this@map.dayNumber
        this@apply.dayPeriod = this@map.dayPeriod.map()
        this@apply.timeOfDay = this@map.timeOfDay
        this@apply.weekday = this@map.weekday.map()
    }
}

private fun RegimenItemDto.map(): RegimenItem {
    return RegimenItem().apply {
        this@apply.administratedQuantity = this@map.administratedQuantity.map()
        this@apply.date = this@map.date
        this@apply.dayNumber = this@map.dayNumber
        this@apply.dayPeriod = this@map.dayPeriod.map()
        this@apply.timeOfDay = this@map.timeOfDay
        this@apply.weekday = this@map.weekday.map()
    }
}

private fun Renewal.map(): RenewalDto {
    return RenewalDto().apply {
        this@apply.decimal = this@map.decimal
        this@apply.duration = this@map.duration.map()
    }
}

private fun RenewalDto.map(): Renewal {
    return Renewal().apply {
        this@apply.decimal = this@map.decimal
        this@apply.duration = this@map.duration.map()
    }
}

private fun Substanceproduct.map(): SubstanceproductDto {
    return SubstanceproductDto().apply {
        this@apply.deliveredcds = this@map.deliveredcds.map { it.map() }
        this@apply.deliveredname = this@map.deliveredname
        this@apply.intendedcds = this@map.intendedcds.map { it.map() }
        this@apply.intendedname = this@map.intendedname
    }
}

private fun SubstanceproductDto.map(): Substanceproduct {
    return Substanceproduct().apply {
        this@apply.deliveredcds = this@map.deliveredcds.map { it.mapStub() }
        this@apply.deliveredname = this@map.deliveredname
        this@apply.intendedcds = this@map.intendedcds.map { it.mapStub() }
        this@apply.intendedname = this@map.intendedname
    }
}

private fun RegimenItem.Weekday.map(): RegimenItemDto.Weekday {
    return RegimenItemDto.Weekday().apply {
        this@apply.weekNumber = this@map.weekNumber
        this@apply.weekday = this@map.weekday.map()
    }
}

private fun RegimenItemDto.Weekday.map(): RegimenItem.Weekday {
    return RegimenItem.Weekday().apply {
        this@apply.weekNumber = this@map.weekNumber
        this@apply.weekday = this@map.weekday.map()
    }
}

private fun ServiceDto.decrypt(): ServiceDto {
    return this@decrypt.copy().apply {
        this@apply.encryptedContent = null
        this@apply.encryptedSelf = null
        this@apply.content = mapOf(Pair("fr", ContentDto().apply {
            stringValue = when {
                this@decrypt.encryptedContent?.length ?: 0 > 0 -> this@decrypt.encryptedContent
                this@decrypt.encryptedSelf?.length ?: 0 > 0 -> this@decrypt.encryptedSelf
                else -> "UNKNOWN ENCRYPTED DATA"
            }
        }))
    }
}