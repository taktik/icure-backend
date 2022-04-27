package org.taktik.icure.be.ehealth.logic.kmehr.incapacity.impl.v20170601

import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.stereotype.Service
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.Utils
import org.taktik.icure.be.ehealth.logic.kmehr.Config
import org.taktik.icure.be.ehealth.logic.kmehr.incapacity.IncapacityLogic
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.Partnership
import org.taktik.icure.services.external.api.AsyncDecrypt
import org.taktik.icure.services.external.http.websocket.AsyncProgress
import java.time.Instant

@Service
class IncapacityLogicImpl(val incapacityExport: IncapacityExport): IncapacityLogic {

    @Value("\${icure.version}")
    internal val ICUREVERSION: String = "4.0.0"

    override fun createIncapacityExport(patient: Patient,
                                        sender: HealthcareParty,
                                        language: String,
                                        recipient: HealthcareParty?,
                                        comment: String?,
                                        incapacityId: String,
                                        notificationDate: Long,
                                        retraction: Boolean,
                                        dataset: String,
                                        transactionType: String,
                                        incapacityreason: String,
                                        beginmoment: Long,
                                        endmoment: Long,
                                        outofhomeallowed: Boolean,
                                        incapWork: Boolean,
                                        incapSchool: Boolean,
                                        incapSwim: Boolean,
                                        incapSchoolsports: Boolean,
                                        incapHeavyphysicalactivity: Boolean,
                                        diagnoseServices: List<org.taktik.icure.entities.embed.Service>,
                                        jobstatus: String,
                                        job: String,
                                        occupationalDiseaseDeclDate: Long,
                                        accidentDate: Long,
                                        expectedbirthgivingDate: Long,
                                        maternityleaveBegin: Long,
                                        maternityleaveEnd: Long,
                                        hospitalisationBegin: Long,
                                        hospitalisationEnd: Long,
                                        hospital: HealthcareParty?,
                                        contactPersonTel: String,
                                        recoveryAddress: Address?,
                                        foreignStayBegin: Long,
                                        foreignStayEnd: Long,
                                        timeZone: String?, progressor: AsyncProgress?
    ) =
            incapacityExport.exportIncapacity(patient, listOf(), sender, language, recipient, comment, incapacityId, notificationDate, retraction, dataset, transactionType, incapacityreason, beginmoment, endmoment, outofhomeallowed,
                    incapWork, incapSchool, incapSwim, incapSchoolsports, incapHeavyphysicalactivity, diagnoseServices, jobstatus, job, occupationalDiseaseDeclDate, accidentDate, expectedbirthgivingDate, maternityleaveBegin, maternityleaveEnd,
                    hospitalisationBegin, hospitalisationEnd, hospital, contactPersonTel, recoveryAddress, foreignStayBegin, foreignStayEnd, null, progressor, Config(_kmehrId = System.currentTimeMillis().toString(),
                    date = Utils.makeXGC(Instant.now().toEpochMilli(), unsetMillis = false, setTimeZone = false, timeZone = timeZone ?: "Europe/Brussels")!!,
                    time = Utils.makeXGC(Instant.now().toEpochMilli(), unsetMillis = true, setTimeZone = false, timeZone = timeZone ?: "Europe/Brussels")!!,
                    soft = Config.Software(name = "iCure", version = ICUREVERSION),
                    clinicalSummaryType = "",
                    defaultLanguage = "en"
            ))

}
