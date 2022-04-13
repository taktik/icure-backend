package org.taktik.icure.be.ehealth.logic.kmehr.incapacity.impl.v20170601

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.taktik.icure.asynclogic.*
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.cd.v1.CDLIFECYCLE
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.schema.v1.LifecycleType
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.schema.v1.AuthorType
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTION
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONschemes
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
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.services.external.api.AsyncDecrypt
import org.taktik.icure.services.external.http.websocket.AsyncProgress
import org.taktik.icure.services.external.rest.v1.dto.HealthcarePartyDto
import org.taktik.icure.services.external.rest.v1.dto.embed.AddressDto
import org.taktik.icure.services.external.rest.v1.dto.embed.ServiceDto
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
                         diagnoseServices: List<Service>,
                         jobstatus: String,
                         job: String,
                         occupationalDiseaseDeclDate: Long,
                         accidentDate: Long,
                         deliveryDate: Long,
                         hospitalisationBegin: Long,
                         hospitalisationEnd: Long,
                         hospital: HealthcareParty?,
                         recoveryAddress: Address?,
                         foreignStayRequestDate: Long,
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
                recipient,
                comment,
                incapacityId,
                notificationDate,
                retraction,
                dataset,
                transactionType,
                incapacityreason,
                beginmoment,
                endmoment,
                outofhomeallowed,
                incapWork,
                incapSchool,
                incapSwim,
                incapSchoolsports,
                incapHeavyphysicalactivity,
                diagnoseServices,
                jobstatus,
                job,
                occupationalDiseaseDeclDate,
                accidentDate,
                deliveryDate,
                hospitalisationBegin,
                hospitalisationEnd,
                hospital,
                recoveryAddress,
                foreignStayRequestDate
        )
        emitMessage(message.apply { folders.add(folder) }).collect { emit(it) }
    }

    private suspend fun makePatientFolder(
            patientIndex: Int,
            patient: Patient,
            sender: HealthcareParty,
            config: Config,
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
            diagnoseServices: List<Service>,
            jobstatus: String,
            job: String,
            occupationalDiseaseDeclDate: Long,
            accidentDate: Long,
            deliveryDate: Long,
            hospitalisationBegin: Long,
            hospitalisationEnd: Long,
            hospital: HealthcareParty?,
            recoveryAddress: Address?,
            foreignStayRequestDate: Long
    ): FolderType {
        //creation of Patient
        val folder = FolderType().apply {
            ids.add(idKmehr(patientIndex))
            //TODO add CD-EMPLOYMENTSITUATION (not for iter 1)
            this.patient = makePatient(patient, config)
        }

        var itemsIdx = 1;

        if(retraction){
            folder.transactions.add(TransactionType().apply {
                ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; value = 1.toString() })
                cds.add(CDTRANSACTION().apply { s(CDTRANSACTIONschemes.CD_TRANSACTION); value = "notification" })
                cds.add(CDTRANSACTION().apply { s(CDTRANSACTIONschemes.CD_TRANSACTION_TYPE); value = "incapacity" })
                date = config.date
                time = config.time
                author = AuthorType().apply { hcparties.add(createParty(sender, emptyList())) }
                isIscomplete = true
                isIsvalidated = true
                headingsAndItemsAndTexts.add(ItemType().apply {
                    ids.add(idKmehr(1))
                    cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value = "incapacity" })
                    contents.add(ContentType().apply {
                        ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; value = incapacityId })
                    })
                    lifecycle = LifecycleType().apply {cd = CDLIFECYCLE().apply {s = "CD-LIFECYCLE"; value = CDLIFECYCLEvalues.RETRACTED}}
                })
            })
        } else {
            folder.transactions.add(TransactionType().apply {
                ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; value = 1.toString() })
                cds.add(CDTRANSACTION().apply { s(CDTRANSACTIONschemes.CD_TRANSACTION); value = "notification" })

                cds.add(CDTRANSACTION().apply { s(CDTRANSACTIONschemes.CD_TRANSACTION_TYPE); value = transactionType })
                date = config.date
                time = config.time
                author = AuthorType().apply { hcparties.add(createParty(sender, emptyList())) }
                isIscomplete = true
                isIsvalidated = true
                //TODO Start adding ITEMs here
                // TODO: add filtering per dataset
                //  CD-ITEM incapacity
                //   ID-KMEHR = 1
                //   CD-INCAPACITY = work, school, swim, schoolsports, heavyphysicalactivity
                //   incapacityreason = MS-MULTEMEDIATTINCAPACITY|multemediattincapacity -> content.descr
                //   outofhomeallowed = MS-INCAPACITYOUTING|* -> content.descr
                //   beginmoment = MS-INCAPACITYFIELD|datebegin -> content.instant
                //   endmoment = MS-INCAPACITYFIELD|dateend -> content.instant

                headingsAndItemsAndTexts.add(ItemType().apply {
                    ids.add(idKmehr(itemsIdx++))
                    cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value = "incapacity" })
                    contents.add(ContentType().apply {
                        incapacity = IncapacityType().apply {
                            //TODO: make type variable: work, school, swim, schoolsports or heavyphysicalactivity : multiple possible
                            if(incapWork) cds.add(CDINCAPACITY().apply { value = CDINCAPACITYvalues.fromValue("work") })
                            if(incapSchool) cds.add(CDINCAPACITY().apply { value = CDINCAPACITYvalues.fromValue("school") })
                            if(incapSwim) cds.add(CDINCAPACITY().apply { value = CDINCAPACITYvalues.fromValue("swim") })
                            if(incapSchoolsports) cds.add(CDINCAPACITY().apply { value = CDINCAPACITYvalues.fromValue("schoolsports") })
                            if(incapHeavyphysicalactivity) cds.add(CDINCAPACITY().apply { value = CDINCAPACITYvalues.fromValue("heavyphysicalactivity") })
                            this.incapacityreason = IncapacityreasonType().apply {
                                this.cd = CDINCAPACITYREASON().apply { value = CDINCAPACITYREASONvalues.fromValue(incapacityreason) }
                            }
                            this.isOutofhomeallowed = outofhomeallowed
                        }
                    })
                    contents.add(ContentType().apply {
                        this.date = null //TODO this is creationdate.
                    })
                    this.beginmoment = Utils.makeDateTypeFromFuzzyLong(beginmoment);
                    this.endmoment = Utils.makeDateTypeFromFuzzyLong(endmoment);
                })
                //TODO:
                //  CD-ITEM diagnosis
                //   ID-KMEHR = idx
                //   content
                //     ICD = MS-INCAPACITYFIELD|diagnosis, ICD|*|10
                //     ICPC = MS-INCAPACITYFIELD|diagnosis, ICPC|*|2
                //     ...
                //     text = griep ...
                //  CD-ITEM diagnosis
                //   ID-KMEHR = idx
                //   content
                //     ICD = MS-INCAPACITYFIELD|diagnosis, ICD|*|10
                //     ICPC = MS-INCAPACITYFIELD|diagnosis, ICPC|*|2
                //     ...
                //     text = griep ...
                val diagnosisServices = diagnoseServices.filter{ it.tags.any { tag -> tag.id == "MS-INCAPACITYFIELD|diagnosis|1" } }
                var addPrincipalCode = diagnoseServices.size > 1
                headingsAndItemsAndTexts.addAll(diagnosisServices.map { svc ->
                    ItemType().apply {
                        ids.add(idKmehr(itemsIdx++))
                        cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value = "diagnosis" })
                        if(addPrincipalCode){
                            cds.add(CDITEM().apply { s(CDITEMschemes.LOCAL);  sl = "MMEDIATT-ITEM"; value = "principal"})
                            addPrincipalCode = false
                        }
                        //svc.codes has all the content
                        contents.add(ContentType().apply {
                            cds.addAll(svc.codes.map { cd ->
                                CDCONTENT().apply { s(if (cd.type == "ICD") CDCONTENTschemes.ICD else (if (cd.type == "ICPC") CDCONTENTschemes.ICPC else CDCONTENTschemes.CD_CLINICAL)); value = cd.code }
                            })
                            val descr_fr = svc.content?.get("descr_fr")?.stringValue;
                            val descr_nl = svc.content?.get("descr_nl")?.stringValue;
                            val descr = svc.content?.get("descr")?.stringValue;
                            texts.add(TextType().apply {
                                this.l = "nl" //TODO make dynamic
                                this.value = descr_fr ?: descr_nl ?: descr //mostly 2 languages are present so always returns french
                            })
                        })
                    }
                })
                //TODO:
                //  CD-ITEM contactperson
                //    ID-KMEHR = idx
                //    CD-CONTACT-PERSON => mother, ...
                //    person (firstame, id, address, telecom, ...)
                //  CD-ITEM encountertype (has linked CD-ITEM: encounterdatetime, dischargedatetime
                //   ID-KMEHR = idx
                //   content CD-ENCOUNTER = hospital
                //  CD-ITEM encounterdatetime
                //   ID-KMEHR = idx
                //  CD-ITEM dischargedatetime
                //   ID-KMEHR = idx
            })
        }

        return folder
    }
}
