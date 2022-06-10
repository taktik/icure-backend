package org.taktik.icure.domain.be.kmehr

import java.io.Serializable
import com.github.pozo.KotlinBuilder
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.Service

@KotlinBuilder
data class IncapacityExportInfo(
	val recipient: HealthcareParty? = null,
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
	val diagnoseServices: List<Service> = emptyList(),
	val jobstatus: String = "", //values of CD-EMPLOYMENTSITUATION independent, employer, ...
	val job: String = "", //freetext
	val occupationalDiseaseDeclDate: Long = 0,
	val accidentDate: Long = 0,
	val expectedbirthgivingDate: Long = 0, //MMEDIATT-ITEM expectedbirthgivingdate
	val maternityleaveBegin: Long = 0, //MMEDIATT-ITEM maternityleave
	val maternityleaveEnd: Long = 0, //MMEDIATT-ITEM maternityleave
	val hospitalisationBegin: Long = 0, //encounterdatetime
	val hospitalisationEnd: Long = 0, //dischargedatetime
	val hospital: HealthcareParty? = null,
	val contactPersonTel: String = "",
	val recoveryAddress: Address? = null,
	val foreignStayBegin: Long = 0, //MMEDIATT-ITEM foreignstay
	val foreignStayEnd: Long = 0,
) : Serializable
