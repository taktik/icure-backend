package org.taktik.icure.db.be.icure

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory

class TarificationCodeUpdater {
    static void main(String... args) {
        ((Logger) LoggerFactory.getLogger("org.apache.http")).setLevel(Level.ERROR);
        ((Logger) LoggerFactory.getLogger("org.apache.http.wire")).setLevel(Level.ERROR);
        ((Logger) LoggerFactory.getLogger("org.apache.http.headers")).setLevel(Level.ERROR);
        ((Logger) LoggerFactory.getLogger("org.apache.http")).setLevel(Level.ERROR);
        ((Logger) LoggerFactory.getLogger("org.ektorp.impl")).setLevel(Level.ERROR);

        def start = System.currentTimeMillis()

        def src_file = args[-1]
        File root = new File(src_file)
        assert root?.exists()

        def codes = null

        new File(args[0]).withReader { it.eachLine {
            def fields = it.split("\\s")
            def importer = fields.length==3?new TarificationCodeImporter("https", args[1],"443","icure-"+fields[0],fields[0],fields[1],fields[2]):new TarificationCodeImporter("https", args[1],"443",fields[0], null,null,null, "template","804e5824-8d79-4074-89be-def87278b51f",fields[0].replaceAll(".+-",""))
            def type = 'INAMI-RIZIV'
            println "Importing ${fields[0]}"

            codes = importer.doScan(root, type, codes)
        }}
        println "Process completed in ${(System.currentTimeMillis() - start) / 1000.0} seconds"
    }
}
