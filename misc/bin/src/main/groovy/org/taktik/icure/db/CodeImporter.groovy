package org.taktik.icure.db

import com.google.common.collect.Sets
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.ektorp.CouchDbInstance
import org.ektorp.ViewQuery
import org.ektorp.http.HttpClient
import org.ektorp.http.StdHttpClient
import org.ektorp.impl.StdCouchDbInstance
import org.taktik.icure.entities.base.Code

import java.security.Security

class CodeImporter extends Importer{

    def language = 'fr'

    CodeImporter() {
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
        def src_file = new File(args[-1])
        def type = src_file.name.replaceAll('.xml','');
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

        def importer = new CodeImporter()

        importer.language = language;
        importer.keyRoot = keyRoot ?: importer.DEFAULT_KEY_DIR;

        importer.doScan(src_file,type);

        println "Process completed in ${(System.currentTimeMillis() - start) / 1000.0} seconds"
    }

    def doScan(File file, String type) {
        def codes = []
        file.withReader('UTF8') { r ->
            def kmehr = new XmlSlurper().parse(r)
            def version = kmehr.VERSION.text()
            kmehr.VALUE.each {
                def label = [:]
                it.DESCRIPTION.each {label[it.'@L'.text()]=it.text()}
                codes << new Code(Sets.newHashSet('be','fr'), type, it.CODE.text(), version, label)
            }
        }
        def current = []

        couchdbBase.queryView(new ViewQuery(includeDocs: false).dbPath(couchdbBase.path()).designDocId("_design/Code").viewName("all"), Code.class).each {
            current<<it.id
        }

        codes.findAll {!current.contains(it.id)}.collate(1000).each {
            couchdbBase.executeBulk(it);
        }
    }
}
