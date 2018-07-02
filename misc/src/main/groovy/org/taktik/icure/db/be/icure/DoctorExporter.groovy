package org.taktik.icure.db.be.icure

import groovy.sql.Sql
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.ektorp.ComplexKey
import org.ektorp.CouchDbInstance
import org.ektorp.ViewQuery
import org.ektorp.http.HttpClient
import org.ektorp.http.StdHttpClient
import org.ektorp.impl.StdCouchDbInstance
import org.taktik.icure.db.Importer
import org.taktik.icure.entities.HealthcareParty

import java.security.Security

class DoctorExporter extends Importer{

    def language = 'fr'

    DoctorExporter() {
        HttpClient httpClient = new StdHttpClient.Builder().socketTimeout(120000).connectionTimeout(120000).url("http://127.0.0.1:" + DB_PORT)/*.username("admin").password("S3clud3sM@x1m@")*/.build()
        CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
        // if the second parameter is true, the database will be created if it doesn't exists
        couchdbBase = dbInstance.createConnector(DB_NAME + '-base', true);

        Security.addProvider(new BouncyCastleProvider())
    }

    static public void main(String... args) {
        def options = args.size() > 1 ? args[0..-2] : []

        def language = 'fr'
        def keyRoot = null
        def pcs = null
        def scs = null

        options.each {
            if (it.startsWith("lang=")) {
                language = it.substring(5);
            } else if (it.startsWith("keyroot=")) {
                keyRoot = it.substring(8);
            } else if (it.startsWith("sc=")) {
                scs = it.substring(3).split(',');
            } else if (it.startsWith("pc=")) {
                pcs = keyRoot = it.substring(3).split(',');
            }
        }

        def src_host = args[-1]

        def src = Sql.newInstance("jdbc:postgresql://${src_host}/iCureHibernate", 'iCure', '');

        def start = System.currentTimeMillis()

        def exporter = new DoctorExporter()

        exporter.language = language;
        exporter.keyRoot = keyRoot ?: exporter.DEFAULT_KEY_DIR;


        exporter.doScan(src, scs, pcs);

        println "Process completed in ${(System.currentTimeMillis() - start) / 1000.0} seconds"
    }

    def doScan(Sql sql, def scs, def pcs) {
        for (String sc in scs) {
            for (String pc in pcs) {
                couchdbBase.queryView(new ViewQuery(includeDocs: true).dbPath(couchdbBase.path()).designDocId("_design/HealthcareParty").viewName("by_speciality_postcode").startKey(ComplexKey.of('persphysician', sc, pc.split(':')[0])).endKey(ComplexKey.of('persphysician', sc, pc.split(':')[1])), HealthcareParty.class).each { hcp->
                    sql.executeInsert("insert into mdc (mdc_id, mdc_nom, mdc_prenom, mdc_inami, mdc_adresse, mdc_adresseno, mdc_ville, mdc_postalcode) values (${Long.valueOf(hcp.nihii)*100+99},${hcp.lastName},${hcp.firstName},${hcp.nihii+hcp.nihiiSpecCode},${hcp.addresses[0].street},${hcp.addresses[0].houseNumber},${hcp.addresses[0].city},${hcp.addresses[0].postalCode})")
                }
            }
        }
    }
}
