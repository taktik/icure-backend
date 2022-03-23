package org.taktik.icure.be.ehealth.logic.kmehr.incapacity.impl.v20170601

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import org.taktik.icure.asynclogic.*
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
                         retraction: Boolean,
                         dataset: String,
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
                serviceAuthors,
                dataset
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
            serviceAuthors: List<HealthcareParty>?,
            dataset: String
    ): FolderType {
        //creation of Patient
        val folder = FolderType().apply {
            ids.add(idKmehr(patientIndex))
            //TODO add CD-EMPLOYMENTSITUATION (not for iter 1)
            this.patient = makePatient(patient, config)
        }

        var itemsIdx = 1;

        folder.transactions.add(TransactionType().apply {
            ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; value = 1.toString() })
            cds.add(CDTRANSACTION().apply { s(CDTRANSACTIONschemes.CD_TRANSACTION); value = "notification"})
            //TODO add CD-TRANSACTION-TYPE --> incapacityextension (fA, fB, rC), incapacityrelapse
            cds.add(CDTRANSACTION().apply { s(CDTRANSACTIONschemes.CD_TRANSACTION_TYPE); value = "incapacity"})
            date = config.date
            time = config.time
            author = AuthorType().apply { hcparties.add(createParty(healthcareParty, emptyList())) }
            //TODO Start adding ITEMs here
            // IF NO RETRACTION
            // TODO: add filtering per dataset
            //  CD-ITEM incapacity
            //   ID-KMEHR = 1
            //   CD-INCAPACITY = work
            //   incapacityreason = MS-MULTEMEDIATTINCAPACITY|multemediattincapacity -> content.descr
            //   outofhomeallowed = MS-INCAPACITYOUTING|* -> content.descr
            //   beginmoment = MS-INCAPACITYFIELD|datebegin -> content.instant
            //   endmoment = MS-INCAPACITYFIELD|dateend -> content.instant
            val incapacityreason = incapacityServices?.find { it -> it.tags.contains(CodeStub(id="MS-MULTEMEDIATTINCAPACITY|multemediattincapacity|1", context=null, type="MS-MULTEMEDIATTINCAPACITY", code="multemediattincapacity", version="1", label=null)) }?.content?.get("descr")?.stringValue;
            val beginmoment = incapacityServices?.find { it -> it.tags.contains(CodeStub(id="MS-INCAPACITYFIELD|datebegin|1", context=null, type="MS-INCAPACITYFIELD", code="datebegin", version="1", label=null)) }?.content?.get("instant")?.fuzzyDateValue;
            val endmoment = incapacityServices?.find { it -> it.tags.contains(CodeStub(id="MS-INCAPACITYFIELD|dateend|1", context=null, type="MS-INCAPACITYFIELD", code="dateend", version="1", label=null)) }?.content?.get("instant")?.fuzzyDateValue;
            val outofhomeallowed = false //TODO get from services
            headingsAndItemsAndTexts.add(ItemType().apply {
                ids.add(idKmehr(itemsIdx++))
                cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value="incapacity" })
                contents.add(ContentType().apply {
                    incapacity = IncapacityType().apply {
                        cds.add(CDINCAPACITY().apply { value = CDINCAPACITYvalues.fromValue("work") })
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
            val diagnosisServices = incapacityServices.filter { it -> true }
            headingsAndItemsAndTexts.addAll(diagnosisServices.map{ svc ->
                ItemType().apply {
                    ids.add(idKmehr(itemsIdx++))
                    cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value="diagnosis" })
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
            //  IF RETRACTION
            //  CD-ITEM incapacity
            //  ID-KMEHR = 1
            //  content
            //    ID-KMEHR = incapacityId of related notification
            //    CD-LIFECYCLE = retracted
        })

        return folder
    }
}
