package org.taktik.icure.db.be.icure

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.google.common.collect.Sets
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.ektorp.CouchDbInstance
import org.ektorp.DbAccessException
import org.ektorp.DocumentNotFoundException
import org.ektorp.ViewQuery
import org.ektorp.http.HttpClient
import org.ektorp.http.StdHttpClient
import org.ektorp.impl.StdCouchDbInstance
import org.slf4j.LoggerFactory
import org.taktik.icure.db.Importer
import org.taktik.icure.entities.Tarification
import org.taktik.icure.entities.embed.Valorisation

import java.security.Security

class TarificationCodeImporter extends Importer {
    def language = 'fr'

	TarificationCodeImporter(dbprotocol, dbhost, dbport, couchdbBase, couchdbPatient, couchdbContact, couchdbConfig, username, password, lang) {
		this.DB_PROTOCOL = dbprotocol
		this.DB_HOST = dbhost
		this.DB_PORT = dbport
		this.DB_NAME = null
		this.language = lang

		HttpClient httpClient = new StdHttpClient.Builder().socketTimeout(120000).connectionTimeout(120000).url("${DB_PROTOCOL?:"http"}://${ DB_HOST ?: "127.0.0.1"}:" + DB_PORT).username(username?:System.getProperty("dbuser")?:"icure").password(password?:System.getProperty("dbpass")?:"S3clud3dM@x1m@").build()
		CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient)
		// if the second parameter is true, the database will be created if it doesn't exists
		this.couchdbBase = couchdbBase ? dbInstance.createConnector(couchdbBase, false): null
		this.couchdbPatient = couchdbPatient ? dbInstance.createConnector(couchdbPatient, false): null
		this.couchdbContact = couchdbContact ? dbInstance.createConnector(couchdbContact, false): null
		this.couchdbConfig = couchdbConfig ? dbInstance.createConnector(couchdbConfig, false): null
		Security.addProvider(new BouncyCastleProvider())
	}

	TarificationCodeImporter(dbprotocol, dbhost, dbport, dbname, username, password, lang) {
		this.DB_PROTOCOL = dbprotocol
		this.DB_HOST = dbhost
		this.DB_PORT = dbport
		this.DB_NAME = dbname
		this.language = lang

		HttpClient httpClient = new StdHttpClient.Builder().socketTimeout(120000).connectionTimeout(120000).url("${DB_PROTOCOL?:"http"}://${ DB_HOST ?: "127.0.0.1"}:" + DB_PORT).username(username?:System.getProperty("dbuser")?:"icure").password(password?:System.getProperty("dbpass")?:"S3clud3dM@x1m@").build()
		CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient)
		// if the second parameter is true, the database will be created if it doesn't exists
		couchdbBase = dbInstance.createConnector(DB_NAME + '-base', false);
		couchdbPatient = dbInstance.createConnector(DB_NAME + '-patient', false);
		couchdbContact = dbInstance.createConnector(DB_NAME + '-healthdata', false);
		couchdbConfig = dbInstance.createConnector(DB_NAME + '-config', false);
		Security.addProvider(new BouncyCastleProvider())
	}


	TarificationCodeImporter() {
		HttpClient httpClient = new StdHttpClient.Builder().socketTimeout(120000).connectionTimeout(120000).url("${DB_PROTOCOL?:"http"}://${ DB_HOST ?: "127.0.0.1"}:" + DB_PORT).username(System.getProperty("dbuser")?:"icure").password(System.getProperty("dbpass")?:"S3clud3dM@x1m@").build()
		CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient)
		// if the second parameter is true, the database will be created if it doesn't exists
		couchdbBase = dbInstance.createConnector(DB_NAME + '-base', false);
		couchdbPatient = dbInstance.createConnector(DB_NAME + '-patient', false);
		couchdbContact = dbInstance.createConnector(DB_NAME + '-healthdata', false);
		couchdbConfig = dbInstance.createConnector(DB_NAME + '-config', false);
		Security.addProvider(new BouncyCastleProvider())
    }

    static void main(String... args) {
        def options = args
        ((Logger) LoggerFactory.getLogger("org.apache.http")).setLevel(Level.ERROR);

        def language = 'fr'
        def keyRoot = null
        def src_files = options.findAll {
            !it.matches(/^(lang=|keyroot=|type=).+/)
        }.collect { new File(it)}
        def type = 'INAMI-RIZIV'

        options.each {
            if (it.startsWith("lang=")) {
                language = it.substring(5)
            } else if (it.startsWith("keyroot=")) {
                keyRoot = it.substring(8)
            } else if (it.startsWith("type=")) {
                type = it.substring(5)
            }
        }

        def start = System.currentTimeMillis()

        def importer = new TarificationCodeImporter()

        ((Logger) LoggerFactory.getLogger("org.apache.http.wire")).setLevel(Level.ERROR);
        ((Logger) LoggerFactory.getLogger("org.apache.http.headers")).setLevel(Level.ERROR);
        ((Logger) LoggerFactory.getLogger("org.apache.http")).setLevel(Level.ERROR);
        ((Logger) LoggerFactory.getLogger("org.ektorp.impl")).setLevel(Level.ERROR);

        importer.language = language;
        importer.keyRoot = keyRoot ?: importer.DEFAULT_KEY_DIR;

        src_files.each {
            it.withReader('UTF8') { r ->
                importer.doScan(r, type);
            }
        }

        println "Process completed in ${(System.currentTimeMillis() - start) / 1000.0} seconds"
    }

    def doScan(Reader r, String type) {
        def codes = []

        def root = new XmlSlurper().parse(r)
        def version = root.'@version'.text().length()>0 ? root.'@version'.text() : '1.0'

        def conditions = [:]
        root.conditiondef.each { cd ->
            def map = [:]
            def cnd = cd.anyfilter.size() ? (cd.anyfilter.collect {
                '( ' + conditions[it.text()] + ' )' ?: "<<${it.text()}>>"
            }).join('||') : (map = [preferentialstatus: cd.filter.'@preferentialstatus'.text(),
                                    trainee           : cd.filter.'@trainee'.text(),
                                    child             : cd.filter.'@child'.text(),
                                    old               : cd.filter.'@old'.text(),
                                    dmg               : cd.filter.'@dmg'.text(),
                                    chronical         : cd.filter.'@chronical'.text(),
                                    convention        : cd.filter.'@convention'.text()]).keySet().sort().collect { k ->
                def v = map[k]
                def ref = (k == 'convention' || k == 'trainee') ? 'hcp' : 'patient'
                v == 'any' ? 'true' :
                        (k == 'old' && v == 'yes') ? "${ref}.age>=75" :
                                (k == 'old' && v == 'no') ? "${ref}.age<75" :
                                        (k == 'child' && v == 'no') ? "${ref}.age>=10" :
                                                (k == 'child') ? "${ref}.age < ${v.replaceAll('yes','10').replaceAll('([0-9]+)m', '$1/12')}" :
                                                        v=='yes' ? "${ref}.${k}" : v=='no' ? "!${ref}.${k}" : "${ref}.${k} == '${v}'"
            }.findAll { it != 'true' }.join('&&')
            conditions[cd.'@name'.text()] = cnd.length() ? cnd : 'true'
        }

        conditions.each {k,String vv -> if (vv.contains('<<')) { conditions[k] = vv.replaceAll(/<<(.+)>>/) {_,ref -> '( '+conditions[ref]+' )'}}}

        root.code.each {
            def label = [:]
            label.fr = it.description.find { it.'@language'.text() == 'fr' }.text()
            label.nl = it.description.find { it.'@language'.text() == 'nl' }.text()

            def code = new Tarification(Sets.newHashSet('be', 'fr'), type, it.'@id'.text(), version, label)
            code.category = [fr: it.category.'@fr'.text(), nl: it.category.'@nl'.text()] as Map<String, String>

            code.valorisations = it.tarif.collect { v ->
                new Valorisation(
                        startOfValidity: Long.valueOf((v.@from.text().length() ? v.@from.text() : root.updateperiod.@from.text())?.replaceAll(/(....)-(..)-(..) (..):(..):(..).*/, '$1$2$3') as String),
                        endOfValidity: Long.valueOf((v.@to.text().length() ? v.@to.text() : root.updateperiod.@to.text())?.replaceAll(/(....)-(..)-(..) (..):(..):(..).*/, '$1$2$3') as String),
                        label: ([fr: v.label.'@fr'.text(), nl: v.label.'@nl'.text()] as Map<String, String>),
                        predicate: conditions[v.valcode.text()]?:"false&&'${v.valcode.text()}'",
                        patientIntervention: v.patientIntervention.text().length() ? Double.valueOf(v.patientIntervention.text() as String) : 0,
                        reimbursement: v.reimbursement.text().length() ? Double.valueOf(v.reimbursement.text() as String) : 0,
                        totalAmount: v.fee.text().length() ? Double.valueOf(v.fee.text() as String) : 0,
                        vat: v.vat.text().length() ? Double.valueOf(v.vat.text() as String) : 0)
            }

            Set<Valorisation> compacted = new HashSet<>()

            //First compact valorisations
            code.valorisations.each { v ->
                def eq = compacted.find { e -> e != v &&  e.predicate == v.predicate && e.startOfValidity == v.startOfValidity && e.endOfValidity == v.endOfValidity}
                if (eq) {
                    if (eq.totalAmount?.doubleValue()>0.0 && v.totalAmount?.doubleValue()>0.0) {
                        //println("Invalid predicates for code ${code.id} : ${eq.label.fr} <-> ${v.label.fr}")
                        compacted<<v
                    } else {
                        eq.patientIntervention += v.patientIntervention
                        eq.reimbursement += v.reimbursement

                        eq.totalAmount += v.totalAmount
                        eq.vat += v.vat
                    }
                } else {
                    compacted<<v
                }
            }

            def trueCode = compacted.find { it.predicate == 'true'}

            if (trueCode) {
                compacted.remove(trueCode)
                compacted.removeIf {v -> v.totalAmount == trueCode.totalAmount && v.reimbursement == 0 as Double & v.patientIntervention == 0 as Double }
                List<String> preds = []
                compacted.each { v ->
                    if (v.totalAmount == 0.0 as Double) {
                        v.totalAmount = trueCode.totalAmount
                        preds << '( ' + v.predicate + ' )'
                    }
                }
                if (preds.size()) {
                    trueCode.predicate = "!(${preds.join('||')})"
                }
            }
            code.valorisations = compacted
            if (trueCode) { code.valorisations << trueCode }

            codes << code
        }

        def current = [:]

        boolean retry = true;
        while (retry) {
            retry = false;
            try {
                couchdbBase.queryView(new ViewQuery(includeDocs: true).dbPath(couchdbBase.path()).designDocId("_design/Tarification").viewName("all"), Tarification.class).each { Tarification t ->
                    current[t.id] = t
                }
            } catch (DbAccessException e) {
				if (e instanceof DocumentNotFoundException) {
					println "Bailing because of $e"
					return [:]
				} else {
					println "Retrying because of $e"
					retry = true
				}
            }
        }

        Map<String, Tarification> res = [:]

        codes.each { newCode ->
            res[newCode.code] = newCode
            if (current.containsKey(newCode.id)) {
				newCode.rev = current[newCode.id].rev

				def keptCodes = current[newCode.id].valorisations.findAll{ v -> v != null }.collect { Valorisation v ->
					for (Valorisation nv in newCode.valorisations) {
						if (v.predicate == nv.predicate) {
							if (nv.startOfValidity <= v.startOfValidity && (nv.endOfValidity ?: 29991231) > v.startOfValidity) {
								v.startOfValidity = Math.min(v.endOfValidity, nv.endOfValidity)
							}
							if ((nv.endOfValidity ?: 29991231) >= (v.endOfValidity ?: 29991231) && nv.startOfValidity < (v.endOfValidity ?: 29991231)) {
								v.endOfValidity = Math.max(v.startOfValidity, nv.startOfValidity)
							}
						}
					}
					return v
				}.findAll { vv -> vv.startOfValidity < vv.endOfValidity }
				newCode.valorisations.addAll(keptCodes)
			}
        }

        codes.collate(1000).each {
            couchdbBase.executeBulk(it)
        }

        return res
    }
}
