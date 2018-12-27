package org.taktik.icure.db.be.icure

import com.google.common.collect.Sets
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.ektorp.CouchDbInstance
import org.ektorp.DbAccessException
import org.ektorp.ViewQuery
import org.ektorp.http.HttpClient
import org.ektorp.http.StdHttpClient
import org.ektorp.impl.StdCouchDbInstance
import org.taktik.icure.db.Importer
import org.taktik.icure.entities.base.Code

import java.security.Security

class ProcedureImporter extends Importer{

    def language = 'fr'

    ProcedureImporter() {
        HttpClient httpClient = new StdHttpClient.Builder().socketTimeout(120000).connectionTimeout(120000).url("http://127.0.0.1:5984")/*.username("template").password("804e5824-8d79-4074-89be-def87278b51f")*/.build()
        CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
        // if the second parameter is true, the database will be created if it doesn't exists
        couchdbBase = dbInstance.createConnector('icure-base', false);

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

        def importer = new ProcedureImporter()

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
            root.Procedure.each {
                def label = [:]
                label.fr = it.Label_FR.text();
                label.nl = it.Label_NL.text();

                def code = new Code(Sets.newHashSet('be', 'fr'), type, it.CISP.text(), version, label)

                def links = [];

                for (String k in ['IBUI','IBUI_Not_Exact']) {
                    if (it[k].text().length()) links << "BE-THESAURUS|${it[k].text()}|3.1.0".toString()
                }

                for (String k in ['SNOMED']) {
                    if (it[k].text().length()) links << "SNOMED|${it[k].text().replaceAll(/ ?\|.+/,'')}|1".toString()
                }

                for (String k in ['CD-VACCINEINDICATION']) {
                    if (it[k].text().length()) it[k].text().split(',').each { links << "CD-VACCINEINDICATION|${it}|1".toString() }
                }

                for (String k in ['NIHII_Nurse_WK_HB_SEM_DOM','NIHII_Nurse_WK_CZ_SEM_MM','NIHII_Nurse_WE_HB_WE_DOM']) {
                    if (it[k].text().length()) links << "INAMI-RIZIV|${it[k].text().replaceAll(/ ?\|.+/,'')}|1".toString()
                }

                for (String k in ['Physician','Physiotherapist','Nurse','Social_Worker','Psychologist','Administrative','Dietician','Logopedist','Dentist','Occupational_Therapist','Midwife','Caregiver']) {
                    if (it[k].text().length()) links << "CD-HCPARTY|pers${k.toLowerCase().replaceAll(/\_/,'').replaceAll(/ ?\|.+/,'')}|1".toString()
                }

                if (links.size()) {
                    code.links = links
                }

                code.searchTerms = [:];
                code.searchTerms.fr = new HashSet(Arrays.asList(it.Syn_Fr.text().split(/ ?; ?/)).findAll { s -> s && s.length()})
                code.searchTerms.nl = new HashSet(Arrays.asList(it.Syn_Nl.text().split(/ ?; ?/)).findAll { s -> s && s.length()})

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
            } catch (DbAccessException e) {
                retry = true;
            }
        }

        codes.findAll {!current.contains(it.id)}.collate(100).each {
            if (it.size()>0) { couchdbBase.executeBulk(it) }
        }
    }
}
