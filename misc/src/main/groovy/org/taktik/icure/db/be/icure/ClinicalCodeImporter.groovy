package org.taktik.icure.db.be.icure

import com.google.common.collect.Sets
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.ektorp.CouchDbInstance
import org.ektorp.ViewQuery
import org.ektorp.http.HttpClient
import org.ektorp.http.StdHttpClient
import org.ektorp.impl.StdCouchDbInstance
import org.taktik.icure.db.Importer
import org.taktik.icure.entities.base.Code

import java.security.Security

class ClinicalCodeImporter extends Importer{

    def language = 'fr'

    ClinicalCodeImporter() {
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

        def importer = new ClinicalCodeImporter()

        importer.language = language;
        importer.keyRoot = keyRoot ?: importer.DEFAULT_KEY_DIR;

        src_file.withReader('UTF8') { r ->
            importer.doScan(r, type);
        }

        println "Process completed in ${(System.currentTimeMillis() - start) / 1000.0} seconds"
    }

    def doScan(Reader r, String type) {
        def codes = []

            def root = new XmlSlurper().parse(r)
            def version = root.'@version'.text()
            root.Clinical_Label.each {
                def label = [:]
                label.fr = it.FR_Clinical_Label.text();
                label.nl = it.NL_Clinical_Label.text();

                def code = new Code(Sets.newHashSet('be', 'fr'), type, it.IBUI.text(), version, label)

                def links = [];

                for (String k in ['ICPC_2_Code_1','ICPC_2_Code_2','ICPC_2_Code_1X','ICPC_2_Code_2X','ICPC_2_Code_1Y','ICPC_2_Code_2Y']) {
                    if (it[k].text().length()) links << "ICPC|${it[k].text()}|2".toString();
                }

                for (String k in ['ICD_10_Code_1','ICD_10_Code_2','ICD_10_Code_1X','ICD_10_Code_2X','ICD_10_Code_1Y','ICD_10_Code_2Y']) {
                    if (it[k].text().length()) links << "ICD|${it[k].text()}|10".toString();
                }

                if (links.size()) {
                    code.links = links
                }

                code.searchTerms = [:];
                code.searchTerms.fr = new HashSet(Arrays.asList(it.Clefs_Recherche_FR.text().split(/ /)))
                code.searchTerms.nl = new HashSet(Arrays.asList(it.Clefs_Recherche_NL.text().split(/ /)))

                codes << code
            }
        def current = new HashSet()

        boolean retry = true;
        while (retry) {
            retry = false;
            try {
                couchdbBase.queryView(new ViewQuery(includeDocs: false).dbPath(couchdbBase.path()).designDocId("_design/Code").viewName("all"), Code.class).each {
                    current<<it.id
                }
            } catch (org.ektorp.DbAccessException e) {
                retry = true;
            }
        }

        codes.findAll {!current.contains(it.id)}.collate(100).each {
            if (it.size()>0) { couchdbBase.executeBulk(it) }
        }
    }
}
