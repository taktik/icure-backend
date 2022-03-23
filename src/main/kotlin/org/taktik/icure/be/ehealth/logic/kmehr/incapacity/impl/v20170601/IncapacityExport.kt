package org.taktik.icure.be.ehealth.logic.kmehr.incapacity.impl.v20170601

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import org.taktik.icure.asynclogic.*
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.Utils
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.cd.v1.*
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.dt.v1.TextType
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.schema.v1.*
import org.taktik.icure.be.ehealth.logic.kmehr.Config
import org.taktik.icure.be.ehealth.logic.kmehr.emitMessage
import org.taktik.icure.be.ehealth.logic.kmehr.v20170601.KmehrExport
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.services.external.api.AsyncDecrypt
import org.taktik.icure.services.external.http.websocket.AsyncProgress
import org.taktik.icure.services.external.rest.v1.mapper.embed.ServiceMapper
import java.time.Instant
import java.util.*

@Suppress("UNNECESSARY_SAFE_CALL")
@org.springframework.stereotype.Service
class IncapacityExport(patientLogic: PatientLogic,
                       codeLogic: CodeLogic,
                       healthElementLogic: HealthElementLogic,
                       healthcarePartyLogic: HealthcarePartyLogic,
                       contactLogic: ContactLogic,
                       documentLogic: DocumentLogic,
                       sessionLogic: AsyncSessionLogic,
                       userLogic: UserLogic,
                       filters: org.taktik.icure.asynclogic.impl.filter.Filters,
                       val serviceMapper: ServiceMapper
) : KmehrExport(patientLogic, codeLogic, healthElementLogic, healthcarePartyLogic, contactLogic, documentLogic, sessionLogic, userLogic, filters)  {
    fun exportIncapacity(patient: Patient,
                         sfks: List<String>,
                         sender: HealthcareParty,
                         language: String,
                         incapacityId: String,
                         services: List<Service>?,
                         serviceAuthors: List<HealthcareParty>?,
                         decryptor: AsyncDecrypt?,
                         progressor: AsyncProgress?,
                         config: Config = Config(_kmehrId = System.currentTimeMillis().toString(),
                                 date = Utils.makeXGC(Instant.now().toEpochMilli())!!,
                                 time = Utils.makeXGC(Instant.now().toEpochMilli(), true)!!,
                                 soft = Config.Software(name = "iCure", version = ICUREVERSION),
                                 clinicalSummaryType = "",
                                 defaultLanguage = "en"
                         )) = flow {
        config.defaultLanguage = if(sender.languages.firstOrNull() == "nl") "nl-BE" else if(sender.languages.firstOrNull() == "de") "de-BE" else "fr-BE"
        config.format = Config.Format.MULTEMEDIATT
        val message = initializeMessage(sender, config, incapacityId)


        val folder = makePatientFolder(
                1,
                patient,
                sender,
                config,
                language,
                services!!,
                serviceAuthors
        )
        emitMessage(message.apply { folders.add(folder) }).collect { emit(it) }
    }

    private suspend fun makePatientFolder(
            patientIndex: Int,
            patient: Patient,
            healthcareParty: HealthcareParty,
            config: Config,
            language: String,
            incapacityServices: List<Service>,
            serviceAuthors: List<HealthcareParty>?
    ): FolderType {
        //creation of Patient
        val folder = FolderType().apply {
            ids.add(idKmehr(patientIndex))
            this.patient = makePatient(patient, config)
        }

        return folder
    }
}
