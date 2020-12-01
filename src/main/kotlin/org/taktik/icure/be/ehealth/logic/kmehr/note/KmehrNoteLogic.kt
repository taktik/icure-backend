package org.taktik.icure.be.ehealth.logic.kmehr.medex

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient

interface KmehrNoteLogic {
    suspend fun createNote(
            id: String,
            author: HealthcareParty,
            date: Long,
            recipientNihii: String,
            recipientSsin: String,
            recipientFirstName: String,
            recipientLastName: String,
            patient: Patient,
            lang: String,
            transactionType: String,
            mimeType: String,
            document: ByteArray
    ) : Flow<DataBuffer>
}
