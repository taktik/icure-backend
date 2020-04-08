package org.taktik.icure.be.ehealth.logic.kmehr.medex

import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import java.io.OutputStream

interface KmehrNoteLogic {
    fun createNote(
            output: OutputStream, id: String, author: HealthcareParty, date: Long, recipientNihii: String, recipientSsin: String, recipientFirstName: String, recipientLastName: String, patient: Patient, lang: String, transactionType: String, mimeType: String, document: ByteArray
    )
}
