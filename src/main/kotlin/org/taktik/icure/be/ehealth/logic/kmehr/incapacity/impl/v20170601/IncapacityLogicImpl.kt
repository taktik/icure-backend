package org.taktik.icure.be.ehealth.logic.kmehr.incapacity.impl.v20170601

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.stereotype.Service
import org.taktik.icure.be.ehealth.logic.kmehr.incapacity.IncapacityLogic
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.services.external.api.AsyncDecrypt
import org.taktik.icure.services.external.http.websocket.AsyncProgress

@Service
class IncapacityLogicImpl(val incapacityExport: IncapacityExport): IncapacityLogic {
    override fun createIncapacityExport(patient: Patient, sfks: List<String>, sender: HealthcareParty, language: String, incapacityId: String, decryptor: AsyncDecrypt?, progressor: AsyncProgress?): Flow<DataBuffer> {
        TODO("Not yet implemented")
    }

    override fun createIncapacityExport(patient: Patient, sender: HealthcareParty, language: String, incapacityId: String, services: List<org.taktik.icure.entities.embed.Service>, serviceAuthors: List<HealthcareParty>?, timeZone: String?, progressor: AsyncProgress?): Flow<DataBuffer> {
        TODO("Not yet implemented")
    }
}
