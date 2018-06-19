package org.taktik.icure.db

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import groovy.sql.Sql
import org.ektorp.CouchDbConnector
import org.ektorp.DocumentNotFoundException
import org.ektorp.Page
import org.ektorp.PageRequest
import org.ektorp.ViewQuery
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.security.core.Authentication
import org.taktik.icure.entities.*
import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.*
import org.taktik.icure.logic.*
import org.taktik.icure.security.UserDetails
import org.taktik.icure.utils.FuzzyValues

import java.text.ParseException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class OsoftSyncImporter extends Importer {
    ContactLogic contactLogic
    HealthElementLogic healthElementLogic
	HealthcarePartyLogic healthcarePartyLogic
    InsuranceLogic insuranceLogic
    FormLogic formLogic
    DocumentLogic documentLogic
    UserLogic userLogic
    FormTemplateLogic formTemplateLogic
	SessionLogic sessionLogic

    private File blobsBase
    static my_database_driver = "com.mysql.jdbc.Driver"
    static docTypesMap = ['BILAN DE CONSULTATION': 'CS', 'DOCAPICRYPT': 'Docapic', 'LETTRE': 'Lettre',
                          'CRO'                  : 'Cro', 'HOSPITALISATION': 'Hosp.', 'CERTIFICAT': 'Certif', 'RECU': 'Re\u00e7u']

    static void arrayCopy(byte[] src, byte[] dst, int offSrc, int offDst, int length) {
        for (int i = 0; i < length; i++) {
            dst[offDst + i] = src[offSrc + i]
        }
    }

    static String t(String val) {
        StringBuffer buf = new StringBuffer()

        for (int c in val.getChars()) {
            if (c >= 0 && c < 128) {
                buf.append((char) c)
            } else {
                switch (c) {
                    case 161:
                        buf.append("\u00b0")
                        break
                    case 230:
                        buf.append("\u00c8")
                        break
                    case 232:
                    case 402:
                        buf.append("\u00c9")
                        break
					case 233:
                    case 381:
                        buf.append("\u00e9")
                        break
                    case 65533:
                        buf.append("\u00e8")
                        break
                    case 8216:
                        buf.append("\u00eb")
                        break
                    case 8482:
                        buf.append("\u00f4")
                        break
                    case 8240:
                        buf.append("\u00e2")
                        break
                    case 8226:
                        buf.append("\u00ef")
                        break
                    case 710:
                        buf.append("\u00e0")
                        break
                    case 213:
                        buf.append("\u2019")
                        break
                    case 382:
                        buf.append("\u00fb")
                        break
                    case 8221:
                        buf.append("\u00ee")
                        break
					case 201:
						buf.append("\u00c9")
						break
                    case 203:
                        buf.append("\u00c2")
                        break
                    case 240:
                        buf.append("\u00d4")
                        break
					case 199:
                    case 8218:
                        buf.append("\u00c7")
                        break
                    default:
                        println "Unknown char: ${c}"
                }
            }
        }
        return buf
    }

    static byte[] getOsoftBody(byte[] val) {
        byte[] magic = new byte[4]
        arrayCopy(val, magic, 0, 0, 4)
        byte[] bodyLength = new byte[2]
        arrayCopy(val, bodyLength, 76, 0, 2)
        int length = (bodyLength[0] & 0xFF) * 256 + (bodyLength[1] & 0xFF)
        byte[] result = new byte[length]
        arrayCopy(val, result, 82, 0, length)
        return result
    }

    def language = 'fr'

    static loopOnDocumentsInBatch(CouchDbConnector db, ViewQuery q, Class<StoredDocument> docClazz, Closure closure) {
        println("Looping on ${docClazz.simpleName}")
        PageRequest pageRequest = PageRequest.firstPage(1000)

        while (true) {
            print '.'
            Page<? extends StoredICureDocument> res = db.queryForPage(q, pageRequest, docClazz)
            res.rows.each { d ->
                if (closure(d)) {
                    db.update(d)
                }
            }
            if (!res.hasNext) {
                break
            }
            pageRequest = res.getNextPageRequest()
        }
        println("")
    }


    @SuppressWarnings(["SqlNoDataSourceInspection", "SqlDialectInspection"])
    void doScan(Sql sql, User user, doc, extraLogs, List<User> users, parties, formTemplates) {
        def dbOwnerId = user.healthcarePartyId
        def userId = user.id

        Map<String, Patient> curPatients = [:]
        loopOnDocumentsInBatch(couchdbPatient, new ViewQuery(includeDocs: true).dbPath(couchdbPatient.path()).designDocId("_design/Patient").viewName("all"), Patient.class) { Patient p ->
            if (p.lastName && p.firstName) {
                curPatients[('' + p.lastName + p.firstName + p.dateOfBirth).toLowerCase().replaceAll("[ \t]", "")] = p
            }
            if (p.externalId) {
                curPatients[p.externalId] = p
            }
            return false
        }

        Map<String, HealthcareParty> curDoctors = [:]
        loopOnDocumentsInBatch(couchdbBase, new ViewQuery(includeDocs: true).dbPath(couchdbPatient.path()).designDocId("_design/HealthcareParty").viewName("all"), HealthcareParty.class) { HealthcareParty hcp ->
            if (hcp.lastName && hcp.firstName) {
				curDoctors[('' + hcp.lastName + hcp.firstName).toLowerCase().replaceAll("[ \t]", "")] = hcp
            }
            if (hcp.cbe) {
                curDoctors[hcp.cbe] = hcp
            }
            return false
        }

        def startScan = System.currentTimeMillis()

        def drs = [:]
        def pats = [:]
        def patients = [:]
        def upats = [:]

        User mainUser = user ?: users[0] ?: users.size() ? users.values().iterator().next() : null


        print("Scanning doctors... ")
        def prev = [:]
        sql.eachRow("select PT_NOM,PT_PRENOM,PT_CLEUNIQUE,PT_MODE_EXERCE,PT_AD_RUE,PT_AD_CODE_P,PT_AD_VILLE,PT_TEL,PT_NOTES,PT_FAX,PT_E_Mail,PT_TELMOB,PT_Mail_Apicrypt from F_praticien where PT_NOM like '__%' and pt_desactive = 0 order by PT_NOM") { m ->
            if (prev.l_name != m.PT_NOM || prev.l_first != m.PT_PRENOM) {
                prev.l_name = m.PT_NOM
                prev.l_first = m.PT_PRENOM

                HealthcareParty doctor = curDoctors[m.PT_CLEUNIQUE as String] ?: curDoctors[('' + prev.l_name + prev.l_first).toLowerCase().replaceAll("[ \t]", "")]

                if (!doctor) {
                    doctor = new HealthcareParty(
                            id: idg.newGUID().toString(),
                            firstName: t(m.PT_PRENOM),
                            lastName: t(m.PT_NOM),
                            addresses: [new Address(addressType: AddressType.work, street: t(m.PT_AD_RUE), city: t(m.PT_AD_VILLE), postalCode: m.PT_AD_CODE_P,
                                    telecoms: [new Telecom(telecomType: TelecomType.phone, telecomNumber: m.PT_TEL),
                                               new Telecom(telecomType: TelecomType.fax, telecomNumber: m.PT_FAX),
                                               new Telecom(telecomType: TelecomType.mobile, telecomNumber: m.PT_TELMOB),
                                               new Telecom(telecomType: TelecomType.apicrypt, telecomNumber: m.PT_Mail_Apicrypt),
                                               new Telecom(telecomType: TelecomType.email, telecomNumber: m.PT_E_Mail)],
                            )],
                            speciality: m.PT_MODE_EXERCE,
                            notes: t(m.PT_NOTES),
                            cbe: m.PT_CLEUNIQUE)
					healthcarePartyLogic.createEntities([doctor],[])
                } else {
					if (doctor.speciality != m.PT_MODE_EXERCE) {
						doctor.speciality = m.PT_MODE_EXERCE
						healthcarePartyLogic.updateEntities([doctor])
					}
				}
                drs[m.PT_CLEUNIQUE] = doctor
            }
        }
        println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")

        startScan = System.currentTimeMillis()
        print("Scanning patients... ")

        sql.eachRow("select P_NIP,P_CleUnique,P_DESACTIVE,P_CREATEUR,P_MODIFICAT,P_DATE_CREAT,P_DATE_MODIF,P_PREFIX,P_NOM,P_PRENOM,P_NOM_MARITAL,P_DATE_NAISS,P_AGE,P_RADIO_SEXE,P_AD_RUE,P_AD_CODE_P,P_AD_VILLE,P_TELEPHONE,P_PROFESSION,P_PratLink,P_ANTECED,P_NUM_SECU,P_PARAM_X,P_SEQ,P_Zone_E from F_PATIENT, F_LIENPATPRAT where F_PATIENT.P_CleUnique = F_LIENPATPRAT.L1_PatLink and F_LIENPATPRAT.L1_ChirLink = ? group by P_NIP,P_CleUnique,P_DESACTIVE,P_CREATEUR,P_MODIFICAT,P_DATE_CREAT,P_DATE_MODIF,P_PREFIX,P_NOM,P_PRENOM,P_NOM_MARITAL,P_DATE_NAISS,P_AGE,P_RADIO_SEXE,P_AD_RUE,P_AD_CODE_P,P_AD_VILLE,P_TELEPHONE,P_PROFESSION,P_PratLink,P_ANTECED,P_NUM_SECU,P_PARAM_X,P_SEQ,P_Zone_E", [doc]) { p ->
            upats[p.P_CleUnique] = p.toRowResult()
        }
        println("After 1st phase : ${upats.size()} unique patients have been found")

        sql.eachRow("select P_NIP,P_CleUnique,P_DESACTIVE,P_CREATEUR,P_MODIFICAT,P_DATE_CREAT,P_DATE_MODIF,P_PREFIX,P_NOM,P_PRENOM,P_NOM_MARITAL,P_DATE_NAISS,P_AGE,P_RADIO_SEXE,P_AD_RUE,P_AD_CODE_P,P_AD_VILLE,P_TELEPHONE,P_PROFESSION,P_PratLink,P_ANTECED,P_NUM_SECU,P_PARAM_X,P_SEQ,P_Zone_E from F_PATIENT, F_DOSSIER where F_PATIENT.P_CleUnique = F_DOSSIER.D_PatLink and F_DOSSIER.D_ChirLink = ? group by P_NIP,P_CleUnique,P_DESACTIVE,P_CREATEUR,P_MODIFICAT,P_DATE_CREAT,P_DATE_MODIF,P_PREFIX,P_NOM,P_PRENOM,P_NOM_MARITAL,P_DATE_NAISS,P_AGE,P_RADIO_SEXE,P_AD_RUE,P_AD_CODE_P,P_AD_VILLE,P_TELEPHONE,P_PROFESSION,P_PratLink,P_ANTECED,P_NUM_SECU,P_PARAM_X,P_SEQ,P_Zone_E", [doc]) { p ->
            upats[p.P_CleUnique] = p.toRowResult()
        }
        println("After 2nd phase : ${upats.size()} unique patients have been found")

        sql.eachRow("select P_NIP,P_CleUnique,P_DESACTIVE,P_CREATEUR,P_MODIFICAT,P_DATE_CREAT,P_DATE_MODIF,P_PREFIX,P_NOM,P_PRENOM,P_NOM_MARITAL,P_DATE_NAISS,P_AGE,P_RADIO_SEXE,P_AD_RUE,P_AD_CODE_P,P_AD_VILLE,P_TELEPHONE,P_PROFESSION,P_PratLink,P_ANTECED,P_NUM_SECU,P_PARAM_X,P_SEQ,P_Zone_E from F_PATIENT where P_CREATEUR in (?,?) order by P_NOM,P_PRENOM", extraLogs) { p ->
            upats[p.P_CleUnique] = p.toRowResult()
        }
        println("After 3rd phase : ${upats.size()} unique patients have been found")



        println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")
		/*
        sql.eachRow("select P_NIP,P_CleUnique,P_DESACTIVE,P_CREATEUR,P_MODIFICAT,P_DATE_CREAT,P_DATE_MODIF,P_PREFIX,P_NOM,P_PRENOM,P_NOM_MARITAL,P_DATE_NAISS,P_AGE,P_RADIO_SEXE,P_AD_RUE,P_AD_CODE_P,P_AD_VILLE,P_TELEPHONE,P_PROFESSION,P_PratLink,P_ANTECED,P_NUM_SECU,P_PARAM_X,P_SEQ,P_Zone_E from F_PATIENT where P_NOM like 'LORIERO'") { p ->
            upats[p.P_CleUnique] = p.toRowResult()
        }
		*/

        upats.values().sort {
            it?.P_NOM ?: '' + '_' + it?.P_PRENOM ?: ''
        }
        .each { p ->
            String oname = t(p.P_NOM)
            String ofirst = t(p.P_PRENOM)
            def oid = p.P_CleUnique as String
            String bdate = p.P_DATE_NAISS?.format("yyyyMMdd")

            def p_contacts
            def p_healthElements
            def p_forms

            Map<String, Service> p_services = [:]
            Map<String, Document> p_documents = [:]
            Patient pat = curPatients[p.P_CleUnique as String] ?: curPatients[('' + oname + ofirst + bdate).toLowerCase().replaceAll("[ \t]", "")]

            def created = pat?.created ?: System.currentTimeMillis()
            def heModified = [:]

            def standardHierarchy = [new HealthElement(id: idg.newGUID().toString(), healthElementId: idg.newGUID().toString(), created: created, responsible: mainUser.healthcarePartyId, author: mainUser.id,
                    openingDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(created), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                    descr: 'Etat général/Vaccination/Médication', codes: [new Code(type: "CD-ICD", code: "A44")], plansOfAction:
                    [
                            new PlanOfAction(id: idg.newGUID().toString(), descr: "Historique", created: created, openingDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(created), ZoneId.systemDefault()), ChronoUnit.SECONDS), responsible: mainUser.healthcarePartyId, author: mainUser.id),
                            new PlanOfAction(id: idg.newGUID().toString(), descr: "Suivi général", created: created, openingDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(created), ZoneId.systemDefault()), ChronoUnit.SECONDS), responsible: mainUser.healthcarePartyId, author: mainUser.id)
                    ]), new HealthElement(id: idg.newGUID().toString(), healthElementId: idg.newGUID().toString(), created: created, responsible: mainUser.healthcarePartyId, author: mainUser.id,
                    openingDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(created), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                    descr: 'INBOX', plansOfAction:
                    [
                            new PlanOfAction(id: idg.newGUID().toString(), descr: "Lab results", created: created, openingDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(created), ZoneId.systemDefault()), ChronoUnit.SECONDS), responsible: mainUser.healthcarePartyId, author: mainUser.id),
                            new PlanOfAction(id: idg.newGUID().toString(), descr: "Protocols", created: created, openingDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(created), ZoneId.systemDefault()), ChronoUnit.SECONDS), responsible: mainUser.healthcarePartyId, author: mainUser.id)
                    ]), new HealthElement(id: idg.newGUID().toString(), healthElementId: idg.newGUID().toString(), created: created, responsible: mainUser.healthcarePartyId, author: mainUser.id,
                    openingDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(created), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                    descr: 'Historique', plansOfAction: [])
            ]

            if (!pat) {
                pat = new Patient(
                        id: idg.newGUID().toString(), lastName: oname, firstName: ofirst, partnerName: p.P_NOM_MARITAL, dateOfBirth: bdate ? Integer.parseInt(bdate) : null,
                        gender: Gender.fromCode(p.P_RADIO_SEXE == 1 ? 'M' : 'F'), created: created,
                        ssin: p.P_NUM_SECU,
                        note: t(p.P_ANTECED),
                        addresses: [new Address(addressType: AddressType.work, street: t(p.P_AD_RUE), city: t(p.P_AD_VILLE), postalCode: p.P_AD_CODE_P,
                                telecoms: [new Telecom(telecomType: TelecomType.phone, telecomNumber: p.P_TELEPHONE)],
                        )]
                )

                users.findAll { it.healthcarePartyId }.each {
                    def delegateId = it.healthcarePartyId
                    pat = this.appendObjectDelegations(pat, null, dbOwnerId, delegateId, this.cachedDocSFKs[pat.id], null)
                }

                p_healthElements = standardHierarchy
                p_forms = [new Form(id: idg.newGUID().toString(), descr: "Historique médical", formTemplateId: formTemplates['FFFFFFFF-FFFF-FFFF-FFFF-DOSSMED00000'].id, planOfActionId: p_healthElements.find { it.descr == 'Etat général/Vaccination/Médication' }.plansOfAction.find {
                    it.descr == 'Historique'
                }.id, created: created, responsible: mainUser.healthcarePartyId, author: mainUser.id)]
                couchdbPatient.update(pat);
            } else {
                created = pat?.created ?: System.currentTimeMillis()
                def sks = []
                pat.delegations.each {
                    sks.addAll(this.getSecureKeys(pat.delegations, dbOwnerId, cachedKeyPairs[dbOwnerId].private))
                }

                p_contacts = contactLogic.findByHCPartyPatient(dbOwnerId, sks)
                p_contacts.each { Contact c ->
                    c.services.each { s ->
                        if (!p_services[s.id] || p_services[s.id].modified < s.modified) {
                            p_services[s.id] = s
                        }
                    }
                }
                p_services.each { k, s ->
                    s.content.each { kc, c ->
                        if (c.documentId) {
							try {
								p_documents[c.documentId] = documentLogic.get(c.documentId)
							} catch (Exception ignored) {
								c.documentId = null
							}
                        }
                    }
                }
                p_healthElements = healthElementLogic.findByHCPartySecretPatientKeys(dbOwnerId, sks)
                p_forms = formLogic.findByHCPartyPatient(dbOwnerId, sks, null, null, formTemplates['FFFFFFFF-FFFF-FFFF-FFFF-DOSSMED00000'].id)

                standardHierarchy.each { s ->
                    HealthElement hist = p_healthElements.find { it.descr == s.descr }
                    if (!hist) {
                        p_healthElements << s
                    } else {
                        s.plansOfAction.each { ss ->
                            if (!hist.plansOfAction.find { it.descr == ss.descr }) {
                                hist.plansOfAction << ss
                                heModified[hist.id] = true
                            }
                        }
                    }
                }
            }

            pats[oid] = pat
            patients[pat.id] = pat

            def now = Instant.now().toEpochMilli()
            def ctc = new Contact(
                    id: idg.newGUID().toString(),
                    created: now,
                    openingDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(now), ZoneId.systemDefault()), ChronoUnit.DAYS),
                    services: [],
                    responsible: dbOwnerId,
                    author: userId
            )

            users.findAll { it.healthcarePartyId }.each {
                def delegateId = it.healthcarePartyId
                ctc = this.appendObjectDelegations(ctc, pat, dbOwnerId, delegateId, this.cachedDocSFKs[ctc.id], this.cachedDocSFKs[ctc.id]) as Contact
            }

            startScan = System.currentTimeMillis()

            def allServices = []
            Map<String, Document> allDocuments = [:]
            sql.eachRow("select * from f_ag_rv_patient where rp_patlink = ?", [oid]) { rv ->
                for (int i = 1; i <= 20; i++) {
                    if (rv["RP_RV${i}"]) {
                        String[] texts = (rv["RP_RV${i}"] as String).split(/$/)
                        if (texts.length == 11 && texts[3] == doc && texts[10] != 'OUI') {
                            try {
                                Date date = Date.parse("dd MM yyyy hh:mm", texts[0] + " " + texts[1])
                                def sid = idg.newGUID().toString()

                                allServices << (p_services[sid] = new Service(
                                        id: sid, label: "Actes planifiés", index: 100,
                                        valueDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(date.time), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                                        content: [fr: new Content(stringValue: texts[4])],
                                        tags: [new Code("CD-LIFECYCLE", "planned", "1.0")],
                                        created: date.time, modified: date.time, responsible: dbOwnerId, author: userId))
                            } catch (ParseException ignored) {
                            }
                        }
                    }
                }
            }

            Map<String, SubContact> p_ssContacts = [:]

            def hist = p_healthElements.find { it.descr == 'Historique' }
            def eg = p_healthElements.find { it.descr == 'Etat général/Vaccination/Médication' }
            def histForm = p_forms.find { it.descr == 'Dossier médical' || it.descr == 'Historique médical' }
            if (!histForm) {
                p_forms << (histForm = new Form(id: idg.newGUID().toString(), descr: "Historique médical", formTemplateId: formTemplates['FFFFFFFF-FFFF-FFFF-FFFF-DOSSMED00000'].id, planOfActionId: eg.plansOfAction.find {
                    it.descr == 'Historique'
                }.id, created: created, responsible: mainUser.healthcarePartyId, author: mainUser.id))
            }
            def histSsContact = new SubContact(id: idg.newGUID().toString(), descr:"Historique médical", formId: histForm.id, planOfActionId: eg.plansOfAction.find {
                it.descr == 'Historique'
            }.id, healthElementId: hist.id, created: hist.created, modified: hist.created)

            sql.eachRow("select * from f_pat_sig_2 where sig2_patlink = ? and SIG2_Sig_Groupe not in ('Chirurgicaux','Allergiques','Facteur Risques')", [oid]) { rv ->
                try {
                    Date date = rv.SIG2_Date
                    def sid = idg.newGUID().toString()

                    allServices << (new Service(
                            id: sid, label: "Ant\u00e9c\u00e9dents m\u00e9dicaux", index: 100,
                            valueDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(date.time), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                            content: [fr: new Content(stringValue: ("" + rv.SIG2_Sig_Groupe + ": " + rv.SIG2_Texte))],
                            created: date.time, modified: date.time, responsible: dbOwnerId, author: userId))
                    p_ssContacts[sid] = histSsContact
                } catch (Exception ignored) {
                }
            }

            sql.eachRow("select * from f_pat_sig_2 where sig2_patlink = ? and SIG2_Sig_Groupe ='Allergiques'", [oid]) { rv ->
                try {
                    Date date = rv.SIG2_Date
                    def sid = idg.newGUID().toString()

                    allServices << (new Service(
                            id: sid, label: "Allergies", index: 100,
                            valueDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(date.time), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                            content: [fr: new Content(stringValue: ("" + rv.SIG2_Texte))],
                            created: date.time, modified: date.time, responsible: dbOwnerId, author: userId))
                    p_ssContacts[sid] = histSsContact
                } catch (Exception ignored) {
                }
            }

            sql.eachRow("select * from f_pat_sig_2 where sig2_patlink = ? and SIG2_Sig_Groupe ='Chirurgicaux'", [oid]) { rv ->
                try {
                    Date date = rv.SIG2_Date
                    def sid = idg.newGUID().toString()

                    allServices << (new Service(
                            id: sid, label: "Antécédents chirurgicaux", index: 100,
                            valueDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(date.time), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                            content: [fr: new Content(stringValue: ("" + rv.SIG2_Texte))],
                            created: date.time, modified: date.time, responsible: dbOwnerId, author: userId))
                    p_ssContacts[sid] = histSsContact
                } catch (Exception ignored) {
                }
            }

            sql.eachRow("select * from f_pat_sig_2 where sig2_patlink = ? and SIG2_Sig_Groupe ='Facteur Risques'", [oid]) { rv ->
                try {
                    Date date = rv.SIG2_Date
                    def sid = idg.newGUID().toString()

                    allServices << (new Service(
                            id: sid, label: "Facteurs de risque", index: 100,
                            valueDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(date.time), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                            content: [fr: new Content(stringValue: ("" + rv.SIG2_Texte))],
                            created: date.time, modified: date.time, responsible: dbOwnerId, author: userId))
                    p_ssContacts[sid] = histSsContact
                } catch (Exception ignored) {
                }
            }

            sql.eachRow("select * from F_Lienpatprat where L1_PatLink = ?", [oid]) { mp ->
                def dr = drs[mp.L1_PratLink]?.id
                dr && pat.patientHealthCareParties.add(new PatientHealthCareParty(healthcarePartyId: dr))
            }

            def pkeys = []
            def pdocs = [:]
            sql.eachRow("select D_CleUnique,D_ACTE_TYPE, D_NOM, D_ACTE_DATE, D_Sejour_Link, D_TXT_BIN from F_DOSSIER where D_PatLink=? and D_ChirLink=? and D_DESACTIVE = 0 order by D_ACTE_TYPE, D_ACTE_DATE", [oid, doc]) {
                pdoc ->
                    if (pkeys.size() == 0 || pkeys[-1] != pdoc.D_ACTE_TYPE) {
                        pkeys << pdoc.D_ACTE_TYPE
                        pdocs[pdoc.D_ACTE_TYPE] = []
                    }
                    pdocs[pdoc.D_ACTE_TYPE] << pdoc.toRowResult()
            }

            Set<String> newPoas = new HashSet<>()
            pkeys.each { String k ->
                PlanOfAction poa = hist.plansOfAction?.find { it.descr == t(k) }
                def date = pdocs[k][0]?.D_ACTE_DATE?.time ?: created
                if (!poa) {
                    heModified[hist.id] = true
                    if (!hist.plansOfAction) {
                        hist.plansOfAction = []
                    }
                    hist.plansOfAction << (poa = new PlanOfAction(id: idg.newGUID().toString(), descr: t(k), created: created, openingDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.systemDefault()), ChronoUnit.SECONDS), responsible: mainUser.healthcarePartyId, author: mainUser.id))
                    newPoas << poa.id
                }

                pdocs[k].each { pdoc ->
                    def sTk = docTypesMap[t(k)] ?: t(k).toLowerCase().capitalize()
                    def body = new String(getOsoftBody((byte[]) pdoc.D_TXT_BIN), "MacRoman").trim().getBytes("UTF-8")
                    def sid = idg.newGUID().toString()

                    def d = new Document(
                            id: idg.newGUID().toString(),
                            documentType: DocumentType.note,
                            created: date,
                            modified: date,
                            responsible: dbOwnerId,
                            author: userId,
                            name: pdoc.D_ACTE_DATE ? "${sTk}: ${t(pdoc.D_NOM)} [${pdoc.D_ACTE_DATE.format('dd/MM/yyyy')}]" : "${sTk}: ${t(pdoc.D_NOM)}",
                            mainUti: "public.plain-text",
                            otherUtis: [],
                            attachment: body
                    )

                    allServices << (new Service(
                            id: sid, label: d.name, index: 100,
                            valueDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                            content: [fr: new Content(stringValue: d.name, documentId: d.id)],
                            created: date, modified: date, responsible: dbOwnerId, author: userId))

                    allDocuments[d.id] = d
                    p_ssContacts[sid] = new SubContact(id: idg.newGUID().toString(), planOfActionId: poa.id, healthElementId: hist.id, created: date, modified: date)
                }
            }

            allServices.each { Service s ->
                def similar = p_services.values().find {
                    (it.created == s.created && it.label == s.label && it.content.any { k1, v1 ->
                        !s.content.any { k2, v2 ->
                            (k1 == k2) && (
                                    (v2.stringValue?.length() && (v1.stringValue != v2.stringValue))
                            )
                        }
                    })
                }
                if (null == similar || newPoas.contains(p_ssContacts[s.id]?.planOfActionId)) {
                    ctc.services << (p_services[s.id] = s)
                    if (p_ssContacts[s.id]) {
                        if (!ctc.getSubContacts()) {
                            ctc.setSubContacts(new HashSet<SubContact>())
                        }
                        if (!ctc.getSubContacts().contains(p_ssContacts[s.id])) {
                            ctc.getSubContacts() << p_ssContacts[s.id]
                        }
                        p_ssContacts[s.id].getServices().add(new ServiceLink(s.id))
                    }
                } else if (similar.content.any { k1, v1 ->
                    s.content.any { k2, v2 ->
                        (k1 == k2) && (
                                (v2.documentId?.length() && allDocuments[v2.documentId] && (v1.documentId?.length() == 0 || !p_documents[v1.documentId] || p_documents[v1.documentId].attachment != allDocuments[v2.documentId].attachment))
                        )
                    }
                }) {
                    similar.content.each { k1, v1 ->
                        s.content.each { k2, v2 ->
                            if (k1 == k2) {
                                if (v2.documentId?.length()>0 && allDocuments[v2.documentId]) {
                                    if (v1.documentId?.length() == 0 || !p_documents[v1.documentId]) {
                                        //There was no document... Fix service
                                        def sid = s.id
                                        s.id = similar.id
                                        s.modified = System.currentTimeMillis()
                                        ctc.services << s
                                        if (p_ssContacts[sid]) {
                                            if (!ctc.getSubContacts()) {
                                                ctc.setSubContacts(new HashSet<SubContact>())
                                            }
                                            if (!ctc.getSubContacts().contains(p_ssContacts[sid])) {
                                                ctc.getSubContacts() << p_ssContacts[sid]
                                            }
                                            p_ssContacts[sid].getServices().add(new ServiceLink(s.id))
                                        }
                                    } else {
                                        p_documents[v1.documentId].attachment = allDocuments[v2.documentId].attachment
                                        documentLogic.modifyDocument(p_documents[v1.documentId])
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (ctc.services.size()) {
                couchdbContact.update(ctc)
                ctc.services.each { svc ->
                    if (svc.content.any { k, v -> v.documentId }) {
                        svc.content.each { k, v ->
                            if (v.documentId) {
                                documentLogic.modifyDocument(allDocuments[v.documentId])
                            }
                        }
                    }
                }
            }

            p_healthElements.each { he ->
                if (!he.rev || heModified[he.id]) {
                    users.findAll { it.healthcarePartyId }.each {
                        def delegateId = it.healthcarePartyId
                        he = this.appendObjectDelegations(he, pat, dbOwnerId, delegateId, this.cachedDocSFKs[he.id], this.cachedDocSFKs[he.id]) as HealthElement
                    }
					try {
						couchdbContact.update(he)
					} catch (Exception e) {
						println("Error: ${e}")
					}
                }
            }

            p_forms.each { f ->
                if (!f.rev) {
                    users.findAll { it.healthcarePartyId }.each {
                        def delegateId = it.healthcarePartyId
                        f = this.appendObjectDelegations(f, pat, dbOwnerId, delegateId, this.cachedDocSFKs[f.id], this.cachedDocSFKs[f.id]) as Form
                    }

					try {
						couchdbContact.update(f)
					} catch (Exception e) {
						println("Error: ${e}")
					}
                }
            }
        }
    }

    OsoftSyncImporter() {
        this.keyRoot = new File(System.getProperty("user.home"), "Library/icure-cloud/keys")
        this.language = 'fr'
        this.blobsBase = new File(System.getProperty("user.home"), "Library/Application Support/iCure/blob")

    }

    static void main(String... args) {
        new OsoftSyncImporter().sync()
    }

    void sync(doc = 'BAOT237569', extraLogs = ['BACROTM', 'SECBACROT'], String cdbOwner = "bacrot", my_db_connection_url = "jdbc:mysql://172.20.1.3/osoft", MY_USER = "FBACBBB", MY_PASS = "ABCAGFB") {
        def start = System.currentTimeMillis()
        ((Logger) LoggerFactory.getLogger("org.apache.http")).setLevel(Level.ERROR)

        ApplicationContext context = new ClassPathXmlApplicationContext(["ctx/barebone.xml"] as String[])

        insuranceLogic = context.getBean("insuranceLogic")
        formLogic = context.getBean("formLogic")
        userLogic = context.getBean("userLogic")
        contactLogic = context.getBean("contactLogic")
        documentLogic = context.getBean("documentLogic")
		healthcarePartyLogic = context.getBean("healthcarePartyLogic")
        healthElementLogic = context.getBean("healthElementLogic")
        formTemplateLogic = context.getBean("formTemplateLogic")
        couchdbBase = context.getBean("couchdbBase")
        couchdbConfig = context.getBean("couchdbConfig")
        couchdbContact = context.getBean("couchdbContact")
        couchdbPatient = context.getBean("couchdbPatient")
		sessionLogic = context.getBean("sessionLogic")

        def file = new File(this.keyRoot)
        if (!file.exists()) {
            file.mkdirs()
        }
        if (!file.exists() || !file.isDirectory()) {
            println "Invalid keyroot directory"
            return
        }
        ((Logger) LoggerFactory.getLogger("org.apache.http.wire")).setLevel(Level.ERROR)
        ((Logger) LoggerFactory.getLogger("org.apache.http.headers")).setLevel(Level.ERROR)
        ((Logger) LoggerFactory.getLogger("org.apache.http")).setLevel(Level.ERROR)
        ((Logger) LoggerFactory.getLogger("org.ektorp.impl")).setLevel(Level.ERROR)

        long startImport = System.currentTimeMillis()
        print("Importing Insurances... ")

        def insurances = [:]
        def formTemplates = [:]

        insuranceLogic.getAllEntities().each { i -> insurances[i.id] = i }
        formTemplateLogic.getAllEntities().each { ft -> formTemplates[ft.guid] = ft }
        def parties = couchdbBase.queryView(new ViewQuery(includeDocs: true).dbPath(couchdbBase.path()).designDocId("_design/HealthcareParty").viewName("all"), HealthcareParty.class)
        List<User> users = couchdbBase.queryView(new ViewQuery(includeDocs: true).dbPath(couchdbBase.path()).designDocId("_design/User").viewName("all"), User.class)

        def user = users.find { cdbOwner == it.login }
        if (!user?.healthcarePartyId) {
            throw new IllegalArgumentException("Owner is invalid")
        }

		sessionLogic.setCurrentSessionContext(new SessionLogic.SessionContext() {
			@Override
			Authentication getAuthentication() {
				return null
			}

			@Override
			UserDetails getUserDetails() {
				return null
			}

			@Override
			PermissionLogic.PermissionSetLogic getPermissionSetLogic() {
				return null
			}

			@Override
			boolean isAuthenticated() {
				return false
			}

			@Override
			boolean isAnonymous() {
				return false
			}

			@Override
			User getUser() {
				return user
			}

			@Override
			String getLocale() {
				return null
			}

			@Override
			void setLocale(String locale) {

			}

			@Override
			String[] getLocaleIdentifiers() {
				return new String[0]
			}

			@Override
			String getLdapAttribute(String name) {
				return null
			}
		})

        parties.each { dr ->
            cachedDoctors[dr.id] = dr

            if (users*.healthcarePartyId.contains(dr.id)) {
                /**** Cryptography *****/
                def keyPair = loadKeyPair(dr.id)
                if (!keyPair) {
                    throw new IllegalStateException("Cannot find key for hcp ${dr.id}")
                }
                cachedKeyPairs[dr.id] = keyPair
            }
        }
        println("" + (System.currentTimeMillis() - startImport) / 1000 + " s.")

        def src = Sql.newInstance(my_db_connection_url, MY_USER, MY_PASS, my_database_driver)
        doScan(src, user, doc, extraLogs, users, parties, formTemplates)

        println "Process completed in ${(System.currentTimeMillis() - start) / 1000.0} seconds"
        System.exit(0)
    }

}

