package org.taktik.icure.db

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.ektorp.CouchDbInstance
import org.ektorp.ViewQuery
import org.ektorp.http.HttpClient
import org.ektorp.http.StdHttpClient
import org.ektorp.impl.StdCouchDbInstance
import org.taktik.icure.entities.Document

import java.security.Security

class DocsImporter extends Importer{

    def language = 'fr'

    DocsImporter() {
        HttpClient httpClient = new StdHttpClient.Builder().socketTimeout(120000).connectionTimeout(120000).url("http://127.0.0.1:" + DB_PORT)/*.username("admin").password("S3clud3sM@x1m@")*/.build()
        CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
        // if the second parameter is true, the database will be created if it doesn't exists
        couchdbContact = dbInstance.createConnector(DB_NAME + '-healthdata', true);

        Security.addProvider(new BouncyCastleProvider())
    }

    static public void main(String... args) {
        String[] options = args ?: []
        def language = 'fr'
        def keyRoot = null
        options.each {
            if (it.startsWith("lang=")) {
                language = it.substring(5);
            } else if (it.startsWith("keyroot=")) {
                keyRoot = it.substring(8);
            }
        }

        def start = System.currentTimeMillis()

        def importer = new DocsImporter()

        importer.language = language;
        importer.keyRoot = keyRoot ?: importer.DEFAULT_KEY_DIR;

        importer.doScan();

        println "Process completed in ${(System.currentTimeMillis() - start) / 1000.0} seconds"
    }

    def doScan() {
        couchdbContact.queryView(new ViewQuery(includeDocs: true).dbPath(couchdbBase.path()).designDocId("_design/Document").viewName("all"), Document.class).each {
            if (it.attachmentId?.contains('|')) {
                File f = new File(it.attachmentId.split(/\|/)[1])
                createAttachment(f, it)
            }
        }
    }
}
