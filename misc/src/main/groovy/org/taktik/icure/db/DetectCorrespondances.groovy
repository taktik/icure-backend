package org.taktik.icure.db

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.ektorp.CouchDbConnector
import org.ektorp.CouchDbInstance
import org.ektorp.ViewQuery
import org.ektorp.http.HttpClient
import org.ektorp.http.StdHttpClient
import org.ektorp.impl.StdCouchDbInstance
import org.taktik.icure.entities.AccessLog
import org.taktik.icure.entities.Contact

import java.security.Security
import java.util.Map.Entry

/* a timestamp based attack on anonimity */
class DetectCorrespondances {
    CouchDbConnector couchdbPatient
    CouchDbConnector couchdbContact

    DetectCorrespondances() {
        HttpClient httpClient = new StdHttpClient.Builder().socketTimeout(120000).connectionTimeout(120000).url("https://couch.icure.cloud").username("gs-825e6d76-8303-4db7-a197-1d7539361e3e").password("****").build()
        CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
        // if the second parameter is true, the database will be created if it doesn't exists
        couchdbPatient = dbInstance.createConnector('icure-gs-825e6d76-8303-4db7-a197-1d7539361e3e-patient', false);
        couchdbContact = dbInstance.createConnector('icure-gs-825e6d76-8303-4db7-a197-1d7539361e3e-healthdata', false);

        Security.addProvider(new BouncyCastleProvider())
    }

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

        Map<String,Map<String,Integer>> corr = [:]

        couchdbContact.queryView(new ViewQuery(includeDocs: true).dbPath(couchdbPatient.path()).designDocId("_design/Contact").viewName("all"), Contact.class).each {
            def l = Math.floor(it.created / 10000) as Long

            String patId = al[l] ?: al[l-1]  ?: al[l-2] ?: al[l-3]
            if (patId && it.secretForeignKeys.size()) {
                def co = (corr[patId] ?: new HashMap<>())
                def sfk = it.secretForeignKeys[0]

                if (co.containsKey(sfk)) {
                   co[sfk] = co[sfk] + 1
                } else {
                    co[sfk] = 1
                }

                corr[patId] = co
            }
        }

        corr.each {k,v ->
            def a = new ArrayList<>(v.entrySet()).sort {Entry a, Entry b -> b.value <=> a.value}.collect {Entry e -> "${e.key}:${e.value}"}
            println("${k} -> ${a.join(',')}")
        }
    }

    static public void main(args) {
        new DetectCorrespondances().scan()
    }
}
