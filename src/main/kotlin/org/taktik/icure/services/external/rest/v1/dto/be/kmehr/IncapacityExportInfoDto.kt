package org.taktik.icure.services.external.rest.v1.dto.be.kmehr

import java.io.Serializable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.v1.dto.HealthcarePartyDto
import org.taktik.icure.services.external.rest.v1.dto.embed.AddressDto
import org.taktik.icure.services.external.rest.v1.dto.embed.ServiceDto

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class IncapacityExportInfoDto(
	val recipient: HealthcarePartyDto? = null,
	val comment: String? = null,
	val incapacityId: String = "",
	val notificationDate: Long = 0,
	val retraction: Boolean = false,
	val dataset: String = "",
	val transactionType: String = "",
	val incapacityreason: String = "",
	val beginmoment: Long = 0,
	val endmoment: Long = 0,
	val outofhomeallowed: Boolean = false,
	val incapWork: Boolean = true,
	val incapSchool: Boolean = false,
	val incapSwim: Boolean = false,
	val incapSchoolsports: Boolean = false,
	val incapHeavyphysicalactivity: Boolean = false,
	val diagnoseServices: List<ServiceDto> = emptyList(),
	val jobstatus: String = "", //values of CD-EMPLOYMENTSITUATION independent, employer, ...
	val job: String = "", //freetext
	val occupationalDiseaseDeclDate: Long = 0,
	val accidentDate: Long = 0,
	val expectedbirthgivingDate: Long = 0, //MMEDIATT-ITEM expectedbirthgivingdate
	val maternityleaveBegin: Long = 0, //MMEDIATT-ITEM maternityleave
	val maternityleaveEnd: Long = 0, //MMEDIATT-ITEM maternityleave
	val hospitalisationBegin: Long = 0, //encounterdatetime
	val hospitalisationEnd: Long = 0, //dischargedatetime
	val hospital: HealthcarePartyDto? = null,
	val contactPersonTel: String = "",
	val recoveryAddress: AddressDto? = null,
	val foreignStayBegin: Long = 0, //MMEDIATT-ITEM foreignstay
	val foreignStayEnd: Long = 0,
) : Serializable
