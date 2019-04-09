package org.taktik.icure.db.be.icure

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory

class TarificationCodeUpdater {
    /**
     * Program arguments:
     * file_storing_db db_host [specificCode1,specificCode2,...] dir_containing_inami_xml
     */
    static void main(String... args) {
        ((Logger) LoggerFactory.getLogger("org.apache.http")).setLevel(Level.ERROR)
        ((Logger) LoggerFactory.getLogger("org.apache.http.wire")).setLevel(Level.ERROR)
        ((Logger) LoggerFactory.getLogger("org.apache.http.headers")).setLevel(Level.ERROR)
        ((Logger) LoggerFactory.getLogger("org.apache.http")).setLevel(Level.ERROR)
        ((Logger) LoggerFactory.getLogger("org.ektorp.impl")).setLevel(Level.ERROR)

        long start = System.currentTimeMillis()

        String db_protocol = 'https'
        String db_host = args[1]
        String db_port = "443"

        String src_file = args[-1]
        File root = new File(src_file)
        assert root?.exists()

        def codes = null
        def subset = (args.length == 4) ? Arrays.asList(args[2].split(',')) : null


        new File(args[0]).withReader { it.eachLine {
            String[] fields = it.split("\\s")
            def db_group_name = fields[0]
            def password = fields.length > 1 ? fields[1] : null
            def lang = fields.length > 2 ? fields[2] : null
            def importer = fields.length == 3 ?
                    new TarificationCodeImporter(db_protocol, db_host, db_port, "icure-"+db_group_name, db_group_name, password, lang) :
                    new TarificationCodeImporter(db_protocol, db_host, db_port, db_group_name, null,null,null, "template","804e5824-8d79-4074-89be-def87278b51f", db_group_name.replaceAll(".+-",""))
            def type = 'INAMI-RIZIV'
            println "Importing ${db_group_name}"

            codes = importer.doScan(root, type, codes, subset)
        }}
        println "Process completed in ${(System.currentTimeMillis() - start) / 1000.0} seconds"
    }
}
