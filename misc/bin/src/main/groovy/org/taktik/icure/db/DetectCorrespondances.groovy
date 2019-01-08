package org.taktik.icure.db

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.ektorp.ComplexKey
import org.ektorp.CouchDbConnector
import org.ektorp.CouchDbInstance
import org.ektorp.ViewQuery
import org.taktik.icure.entities.AccessLog
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.Form
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.User
import org.taktik.icure.security.CryptoUtils

import javax.crypto.KeyGenerator
import java.security.Key
import java.util.Map.Entry

/* a timestamp based attack on anonimity */

class DetectCorrespondances extends Importer {

    def scan() {
        def al = [:]

        couchdbPatient.queryView(new ViewQuery(includeDocs: true).dbPath(couchdbPatient.path()).designDocId("_design/AccessLog").viewName("all"), AccessLog.class).each {
            def l = Math.floor(it.date.toEpochMilli() / 10000) as Long
            al[l] = it.patientId
        }

        /*couchdbPatient.queryView(new ViewQuery(includeDocs: true).dbPath(couchdbPatient.path()).designDocId("_design/Patient").viewName("all"), Patient.class).each {
            if (it.created) al[Math.floor(it.created / 10000) as Long] = it.id
            if (it.modified) al[Math.floor(it.modified / 10000) as Long] = it.id
        }*/

        Map<String, Map<String, Integer>> corr = [:]

        def contacts = couchdbContact.queryView(new ViewQuery(includeDocs: true).dbPath(couchdbContact.path()).designDocId("_design/Contact").viewName("all"), Contact.class).findAll {
            it.secretForeignKeys?.size()
        }

        contacts.each {
            def sfk = it.secretForeignKeys[0]

            def l = Math.floor(it.created / 10000) as Long

            String patId = al[l] ?: al[l - 1] ?: al[l - 2] ?: al[l - 3]
            if (patId) {
                def co = (corr[sfk] ?: new HashMap<>())

                if (co.containsKey(patId)) {
                    co[patId] = co[patId] + 1
                } else {
                    co[patId] = 1
                }

                corr[sfk] = co
            }
        }

        contacts.each {
            def sfk = it.secretForeignKeys[0]
            if (!corr[sfk]) {
                println("${sfk}: No match with content: ${it.services.collect { it.label + ':' + it.content.values().collect { it.stringValue }.findAll { it }.join(' ') }.join(';').replaceAll(/\s/,' ')}")
            }
        }

        def users = couchdbBase.queryView(new ViewQuery(includeDocs: true).dbPath(couchdbBase.path()).designDocId("_design/User").viewName("all"), User.class)
        def ids = new HashSet<>(users.collect { it -> it.healthcarePartyId })
        def parties = couchdbBase.queryView(new ViewQuery(includeDocs: true).dbPath(couchdbBase.path()).designDocId("_design/HealthcareParty").viewName("all"), HealthcareParty.class).findAll { ids.contains(it.id) }

        parties.each { hcp ->
            def keyPair = createKeyPair(hcp.id)

            KeyGenerator aesKeyGenerator = KeyGenerator.getInstance("AES", "BC")
            aesKeyGenerator.init(256)
            Key encryptKey = aesKeyGenerator.generateKey()

            def crypted = CryptoUtils.encrypt(encryptKey.encoded, keyPair.public).encodeHex()

            hcp.hcPartyKeys = [:]
            hcp.hcPartyKeys[hcp.id] = ([crypted, crypted] as String[])

            hcp.setPublicKey(keyPair.public.encoded.encodeHex().toString())

            cachedKeyPairs[hcp.id] = keyPair

            this.cachedDoctors[hcp.id] = hcp

            couchdbBase.update(hcp)
        }


        def corr2 = [:]

        def fixer = { k, v ->
            def a = new ArrayList<>(v.entrySet()).sort { Entry x, Entry y -> y.value <=> x.value }.collect { Entry e -> [pat: e.key, score: e.value] }

            if (a.size() > 1 && (a[0].score * 1.0) / (a[1].score * 1.0) < 3) {
                def it = contacts.find { it.secretForeignKeys[0] == k }
                println("${k}: No clear match with ${a[0].pat} (${a[0].score} >< ${a[1].score}) content: ${it.services.collect { it.label + ':' + it.content.values().collect { it.stringValue }.findAll { it }.join(' ') }.join(';').replaceAll(/\s/,' ')}")
            } else {
                def pat = couchdbPatient.get(Patient.class, a[0].pat as String)
                def ctcs = couchdbContact.queryView(new ViewQuery(includeDocs: true).dbPath(couchdbContact.path())
                        .designDocId("_design/Contact").viewName("by_hcparty_patientfk").startKey(ComplexKey.of(users[0].healthcarePartyId, k)).endKey(ComplexKey.of(users[0].healthcarePartyId, k)), Contact.class)
                def hes = couchdbContact.queryView(new ViewQuery(includeDocs: true).dbPath(couchdbContact.path())
                        .designDocId("_design/HealthElement").viewName("by_hcparty_patient").startKey(ComplexKey.of(users[0].healthcarePartyId, k)).endKey(ComplexKey.of(users[0].healthcarePartyId, k)), HealthElement.class)
                def forms = couchdbContact.queryView(new ViewQuery(includeDocs: true).dbPath(couchdbContact.path())
                        .designDocId("_design/Form").viewName("by_hcparty_patientfk").startKey(ComplexKey.of(users[0].healthcarePartyId, k)).endKey(ComplexKey.of(users[0].healthcarePartyId, k)), HealthElement.class)

                def formIds = new HashSet(forms.collect {it.id})

                users.each { delegate -> this.appendObjectDelegations(pat, null, users[0].healthcarePartyId, delegate.healthcarePartyId, k, null) }
                ctcs.each {  c ->
                    c.subContacts.each { sc ->
                        if (sc.formId && !formIds.contains(sc.formId)) {
                            def form = couchdbContact.get(Form.class, sc.formId)
                            if (form.secretForeignKeys?.size() >0) {
                                if (!corr[form.secretForeignKeys[0]] && !corr2[form.secretForeignKeys[0]]) {
                                    def vv = (corr2[form.secretForeignKeys[0]] = [:])
                                    vv[a[0].pat] = 1
                                } else {
                                    def vv = corr[form.secretForeignKeys[0]] ?: corr2[form.secretForeignKeys[0]]
                                    def aa = new ArrayList<>(vv.entrySet()).sort { Entry x, Entry y -> y.value <=> x.value }.collect { Entry e -> [pat: e.key, score: e.value] }
                                    if (aa.size() > 1 && (aa[0].score * 1.0) / (aa[1].score * 1.0) < 3) {
                                        if (aa[0].pat!=a[0].pat) {
                                            println("Mismatch for ${a[0].pat}")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    users.each { delegate -> this.appendObjectDelegations(c, pat, users[0].healthcarePartyId, delegate.healthcarePartyId, null, k) }
                }
                hes.each {  c ->
                    users.each { delegate -> this.appendObjectDelegations(c, pat, users[0].healthcarePartyId, delegate.healthcarePartyId, null, k) }
                }
                forms.each {  c ->
                    users.each { delegate -> this.appendObjectDelegations(c, pat, users[0].healthcarePartyId, delegate.healthcarePartyId, null, k) }
                }

                couchdbPatient.update(pat)
                couchdbContact.executeBulk(ctcs)
                couchdbContact.executeBulk(hes)
                couchdbContact.executeBulk(forms)
            }
        }


        corr.each(fixer)
        while (corr2.size()>0) {
            corr = corr2
            corr2 = [:]
            corr.each(fixer)
        }


    }

    static public void main(args) {
        def detector = new DetectCorrespondances()

        detector.keyRoot = "/tmp/keys"
        new File(detector.keyRoot).mkdirs()

        detector.scan()
    }
}
