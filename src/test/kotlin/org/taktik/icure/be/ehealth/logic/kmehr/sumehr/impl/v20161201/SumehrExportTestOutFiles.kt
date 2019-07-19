package org.taktik.icure.be.ehealth.logic.kmehr.sumehr.impl.v20161201

import org.mockito.Mockito
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.Gender
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
    val pat = Patient().apply {
        id = "idPatient";
        addresses = listOf(Address().apply {
            street = "streetPatient"
            houseNumber = "1D"
            postalCode = "1050"
            city = "Ixelles"
        })
    }

    /// Third parameter : sfks
    val sfks = listOf("sfks");

    /// Fourth parameter
    val sender = HealthcareParty().apply {
        nihii = "nihiiSender";
        id = "idSender";
        ssin = "ssinSender";
        specialityCodes = mutableListOf(CodeStub("type", "code", "version"))
        firstName = "firstNameSender";
        lastName = "lastNameSender";
        name = "nameSender";
        addresses = listOf(Address().apply {
            street = "streetSender"
            houseNumber = "3A"
            postalCode = "1000"
            city = "Bruxelles"
        })
        gender = Gender.fromCode("M");
        speciality = "perphysician"
    }

    /// Fifth parameter
    val recipient = HealthcareParty();

    /// Sixth parameter
    val language = "language";

    /// Seventh parameter
    val comment = "comment";

    /// Eighth parameter
    val excludedIds = listOf("")

    /// Ninth parameter
    val decryptor = Mockito.mock(AsyncDecrypt::class.java)

    // Execution
    sumehrExport.createSumehr(os1, pat, sfks, sender, recipient, language, comment, excludedIds, decryptor)

}