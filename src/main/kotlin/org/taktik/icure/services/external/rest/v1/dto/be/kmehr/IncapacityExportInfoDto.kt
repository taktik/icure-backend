package org.taktik.icure.services.external.rest.v1.dto.be.kmehr

import org.taktik.icure.services.external.rest.v1.dto.HealthcarePartyDto
import org.taktik.icure.services.external.rest.v1.dto.embed.AddressDto
import org.taktik.icure.services.external.rest.v1.dto.embed.ServiceDto
import java.io.Serializable

class IncapacityExportInfoDto : Serializable {
    var secretForeignKeys: List<String> = emptyList()
    var services: List<ServiceDto> = emptyList() //TODO move away of service usage
    var serviceAuthors: List<HealthcarePartyDto>? = null
    var recipient: HealthcarePartyDto? = null
    var comment: String? = null
    var incapacityId: String = ""
    var notificationDate: Long = 0
    var retraction: Boolean = false
    var dataset: String = ""
    var transactionType: String = ""
    var incapacityreason: String = ""
    var beginmoment: Long = 0
    var endmoment: Long = 0
    var outofhomeallowed: Boolean = false
    var incapWork: Boolean = true
    var incapSchool: Boolean = false
    var incapSwim: Boolean = false
    var incapSchoolsports: Boolean = false
    var incapHeavyphysicalactivity: Boolean = false
    var diagnoseServices: List<ServiceDto> = emptyList()
    var jobstatus: String = "" //independent, employer, ...
    var job: String = "" //freetext
    var occupationalDiseaseDeclDate: Long = 0
    var accidentDate: Long = 0
    var deliveryDate: Long = 0
    var hospitalisationBegin: Long = 0
    var hospitalisationEnd: Long = 0
    var hospital: HealthcarePartyDto? = null
    var recoveryAddress: AddressDto? = null
    var foreignStayRequestDate: Long = 0
}
