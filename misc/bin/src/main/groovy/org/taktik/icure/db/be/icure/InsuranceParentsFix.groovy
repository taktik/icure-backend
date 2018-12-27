package org.taktik.icure.db.be.icure

import com.google.common.collect.Sets
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.ektorp.CouchDbInstance
import org.ektorp.DbAccessException
import org.ektorp.ViewQuery
import org.ektorp.http.HttpClient
import org.ektorp.http.StdHttpClient
import org.ektorp.impl.StdCouchDbInstance
import org.taktik.icure.dao.migration.MigrationStub
import org.taktik.icure.db.Importer
import org.taktik.icure.entities.Insurance
import org.taktik.icure.entities.base.Code

import java.security.Security
import java.util.stream.Collectors
import java.util.stream.Stream

class InsuranceParentsFix extends Importer{

    def language = 'fr'

    InsuranceParentsFix() {
        HttpClient httpClient = new StdHttpClient.Builder().socketTimeout(120000).connectionTimeout(120000).url("https://couch.icure.cloud").username("template").password("804e5824-8d79-4074-89be-def87278b51f").build()
        CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
        // if the second parameter is true, the database will be created if it doesn't exists
        couchdbBase = dbInstance.createConnector('icure-_template_-persphysician-fr', false);

        Security.addProvider(new BouncyCastleProvider())
    }

    static public void main(String... args) {
        def options = args

        def language = 'fr'
        def keyRoot = null

        options.each {
            if (it.startsWith("lang=")) {
                language = it.substring(5);
            } else if (it.startsWith("keyroot=")) {
                keyRoot = it.substring(8);
            } else if (it.startsWith("type=")) {
                type = it.substring(5);
            }
        }


        def start = System.currentTimeMillis()

        def importer = new InsuranceParentsFix()

        importer.language = language;
        importer.keyRoot = keyRoot ?: importer.DEFAULT_KEY_DIR;

        importer.doScan()

        println "Process completed in ${(System.currentTimeMillis() - start) / 1000.0} seconds"
    }

    def doScan() {
        def insurances = couchdbBase.queryView(new ViewQuery(includeDocs: true).dbPath(couchdbBase.path()).designDocId("_design/Insurance").viewName("all"), Insurance.class)

        insurances.forEach {i ->
            if (!i.parent) {
                i.parent = i.code == '306' ? i.id : insurances.find {ii -> ii.code == i.code.substring(0,1)+'00'}?.id
            }
            println("${i.code}: ${i.id} -> ${i.parent}")
        }
        couchdbBase.executeBulk(insurances)
    }
}
