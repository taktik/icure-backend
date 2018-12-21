package org.taktik.icure.db.be.icure

import com.google.gson.reflect.TypeToken
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.ektorp.CouchDbInstance
import org.ektorp.ViewQuery
import org.ektorp.http.HttpClient
import org.ektorp.http.StdHttpClient
import org.ektorp.impl.StdCouchDbInstance
import org.taktik.icure.db.Importer
import org.taktik.icure.entities.Insurance
import org.taktik.icure.logic.impl.GsonSerializerFactory

import java.security.Security

class InsuranceImporter extends Importer{

    def language = 'fr'

    InsuranceImporter() {
        DB_NAME = System.getProperty("dbuser") ? "icure-${System.getProperty("dbuser")}-base" : "icure-base";
		HttpClient httpClient = new StdHttpClient.Builder().socketTimeout(120000).connectionTimeout(120000).url("${DB_PROTOCOL?:"http"}://${ DB_HOST ?: "127.0.0.1"}:" + DB_PORT).username(System.getProperty("dbuser")?:"icure").password(System.getProperty("dbpass")?:"S3clud3dM@x1m@").build()
        CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
        // if the second parameter is true, the database will be created if it doesn't exists
        couchdbBase = dbInstance.createConnector(DB_NAME, true);

        Security.addProvider(new BouncyCastleProvider())
    }

    static public void main(String... args) {
        def options = args.size() > 1 ? args[0..-2] : []

        def language = 'fr'
        def keyRoot = null

        options.each {
            if (it.startsWith("lang=")) {
                language = it.substring(5);
            } else if (it.startsWith("keyroot=")) {
                keyRoot = it.substring(8);
            }
        }

        def src_file = new File(args[-1])

        def start = System.currentTimeMillis()

        def importer = new InsuranceImporter()

        importer.language = language;
        importer.keyRoot = keyRoot ?: importer.DEFAULT_KEY_DIR;

        src_file.withReader('UTF8') { r ->
            importer.doScan(r);
        }

        println "Process completed in ${(System.currentTimeMillis() - start) / 1000.0} seconds"
    }

    def doScan(Reader r) {
        def insurances
            insurances = new GsonSerializerFactory().gsonSerializer.fromJson(r, new TypeToken<ArrayList<Insurance>>() {
            }.getType())
        def current = []

            couchdbBase.queryView(new ViewQuery(includeDocs: true).dbPath(couchdbBase.path()).designDocId("_design/Insurance").viewName("all_by_code"), Insurance.class).each {
                if (it.code) {
                    current << it.code
                }
            }

        def imported = []
        insurances.findAll {!current.contains(it.code)}.collate(1000).each {
            imported.addAll(it)
            couchdbBase.executeBulk(it);
        }

        return imported
    }
}
