package org.taktik.icure.be.ehealth.logic.kmehr.incapacity.impl.v20170601

import java.time.Instant
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.Utils
import org.taktik.icure.be.ehealth.logic.kmehr.Config
import org.taktik.icure.be.ehealth.logic.kmehr.incapacity.IncapacityLogic
import org.taktik.icure.domain.be.kmehr.IncapacityExportInfo
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.services.external.http.websocket.AsyncProgress

@Service
class IncapacityLogicImpl(val incapacityExport: IncapacityExport) : IncapacityLogic {

	@Value("\${icure.version}")
	internal val ICUREVERSION: String = "4.0.0"

	override fun createIncapacityExport(
		patient: Patient,
		sender: HealthcareParty,
		language: String,
		exportInfo: IncapacityExportInfo,
		timeZone: String?,
		progressor: AsyncProgress?
	) =
		incapacityExport.exportIncapacity(
			patient, listOf(), sender, language, exportInfo, null, progressor,
			Config(
				_kmehrId = System.currentTimeMillis().toString(),
				date = Utils.makeXGC(Instant.now().toEpochMilli(), unsetMillis = false, setTimeZone = false, timeZone = timeZone ?: "Europe/Brussels")!!,
				time = Utils.makeXGC(Instant.now().toEpochMilli(), unsetMillis = true, setTimeZone = false, timeZone = timeZone ?: "Europe/Brussels")!!,
				soft = Config.Software(name = "iCure", version = ICUREVERSION),
				clinicalSummaryType = "",
				defaultLanguage = "en"
			)
		)
}
