package org.taktik.icure.be.ehealth.logic.kmehr.sumehr.impl.v20161201

import org.mockito.Mockito
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.embed.*
import org.taktik.icure.services.external.api.AsyncDecrypt
import java.io.File

private val sumehrExport = SumehrExport()

fun main() {
    // Arrange
    /// First parameter : OS
    val path1 = "src/test/resources/org/taktik/icure/be/ehealth/logic/kmehr/sumehr/impl/v20161201/outMinimalSumehr.xml"
    val file1 = File(path1)
    val os1 = file1.outputStream();

    /// Second parameter : pat
    val pat1 = Patient().apply {
        id = "PatientId"
        firstName = "PRENOM";
        lastName = "NOM";
        ssin = "50010100156";
        civility = "Mr";
        gender = Gender.fromCode("M");
        dateOfBirth = 19500101;
        placeOfBirth = "Bruxelles";
        profession = "Cobaye";
        nationality = "Belge"
        addresses = listOf(Address().apply {
            addressType = AddressType.home
            street = "streetPatient";
            houseNumber = "1D";
            postalCode = "1050";
            city = "Ixelles";
            telecoms = listOf(Telecom().apply {
                telecomType = TelecomType.phone;
                telecomNumber = "0423456789"
                telecomDescription = "personal phone";
            })
        })
        languages = listOf("French");
    }

    /// Third parameter : sfks
    val sfks = listOf("sfks");

    /// Fourth parameter
    val sender1 = HealthcareParty().apply {
        nihii = "nihiiSender";
        id = "idSender";
        ssin = "50010100156";
        specialityCodes = mutableListOf(CodeStub("type", "code", "version"))
        firstName = "firstNameSender";
        lastName = "lastNameSender";
        addresses = listOf(Address().apply {
            addressType = AddressType.home;
            street = "streetSender";
            houseNumber = "3A";
            postalCode = "1000";
            city = "Bruxelles";
            telecoms = listOf(Telecom().apply {
                telecomType = TelecomType.phone;
                telecomNumber = "0423456789";
                telecomDescription = "personal phone";
            })
        })
        gender = Gender.fromCode("M");
        speciality = "perphysician"
        specialityCodes = listOf(CodeStub("CD-HCPARTY","persphysician","1"))
    }

    /// Fifth parameter
    val recipient1 = HealthcareParty().apply {
        nihii = "nihiiRecipient";
        id = "idRecipient";
        ssin = "50010100156";
        specialityCodes = mutableListOf(CodeStub("type", "code", "version"))
        name = "PMGRecipient";
        addresses = listOf(Address().apply {
            addressType =AddressType.home;
            street = "streetRecipient";
            houseNumber = "3A";
            postalCode = "1000";
            city = "Bruxelles";
        })
        gender = Gender.fromCode("M");
        speciality = "perphysician"
    }

    /// Sixth parameter
    val language = "language";

    /// Seventh parameter
    val comment = "It's the comment done in main";

    /// Eighth parameter
    val excludedIds = listOf("")

    /// Ninth parameter
    val decryptor = Mockito.mock(AsyncDecrypt::class.java)

    /// tags
    val tagADR = CodeStub("type","adr","1")
    val tagAllergy = CodeStub("type","allergy","1")
    val tagSocialrisk = CodeStub("type","socialrisk","1")
    val tagRisk = CodeStub("type","risk","1")
    val tags = mutableSetOf(tagADR, tagAllergy, tagRisk, tagSocialrisk)

    /// Contents
    val medication = Medication().apply { medicinalProduct = Medicinalproduct().apply { intendedname = "medicationName" } }
    val medicationContent = mapOf(Pair("language", Content().apply { booleanValue = true }), Pair("medication", Content().apply { medicationValue = medication }))

    /// Services
    val validServiceADRAssessment = Service().apply {
        this.id = "1"; this.endOfLife = null;
        this.status = 0; // must be active => Assessment
        this.tags = mutableSetOf(tagADR);
        //this.codes = vaccineCodes;
        //this.label = medicationLabel;
        this.content = medicationContent;
        this.comment = "It's a comment";
        //this.openingDate = oneWeekAgo;
        //this.closingDate = today;
    }
    val validServiceADRHistory = Service().apply {
        this.id = "1"; this.endOfLife = null;
        this.status = 1; // must be inactive => History
        this.tags = mutableSetOf(tagADR, CodeStub("CD-LIFECYCLE","inactive","1"));
        //this.codes = vaccineCodes;
        //this.label = medicationLabel;
        this.content = medicationContent;
        this.comment = "comment";
        //this.openingDate = oneWeekAgo;
        //this.closingDate = today;
    }



    // Execution
    sumehrExport.createSumehr(os1, pat1, sfks, sender1, recipient1, language, comment, excludedIds, decryptor)

}