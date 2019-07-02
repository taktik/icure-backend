#!/usr/local/util/groovy/bin/groovy
// Use with find src -name 'kmehr_elements-*.xsd'  | perl -pe 's#(.+)/v([0-9]{8})/(.+)#groovy misc/src/main/groovy/prepareJaxbBindings.groovy $2 src misc/src/main/resources/jxb/v$2 && xjc -extension -b misc/src/main/resources/jxb/v$2/jxb_ehealth_bindings.xjb -d src/main/java $1/v$2/$3 && xjc -extension -b misc/src/main/resources/jxb/v$2/jxb_ehealth_bindings.dtos.xjb -d src/main/java $1/v$2/$3#' | grep 2016 | bash
def dir = args[0]
def src = args[1]
def dest = args[2]

def goback = dest.replaceAll('[^/]+','..')

def dirFile = new File(dest)
dirFile.mkdirs()

new File(dirFile,'jxb_ehealth_bindings.dtos.xjb').withWriter { w1 ->
    new File(dirFile, 'jxb_ehealth_bindings.xjb').withWriter { w2 ->

        def header = """<jxb:bindings version="2.1"
               xmlns:jxb="http://java.sun.com/xml/ns/jaxb"
               xmlns:xs="http://www.w3.org/2001/XMLSchema"
               xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc"
               jxb:extensionBindingPrefixes="xjc">
  <jxb:globalBindings>
    <xjc:simple/>
   <xjc:serializable uid="${dir}"/>
  </jxb:globalBindings>
"""
        w1.println header
        w2.println header

        def treated = []
        ['find', "$src", '-name', '*.xsd'].execute().inputStream.withReader { r ->
            r.eachLine { f ->
                if (f.contains("/v${dir}/")) {
                    def ns = new XmlSlurper().parse(new File(f)).'@targetNamespace'.text()

                    if (!treated.contains(ns)) {
                        treated << ns

                        def pkg = ns
                        def host = pkg.replaceAll('http://(?:www.)?(.+?)/.+', '$1')
                        def path = pkg.replaceAll('http://.+?/(.+)', '$1')
                        pkg = ("org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v$dir." + host.split(/\./).reverse().join('.') + "."
                                + path.replaceAll('#', '').replaceAll('/', '.')).replaceAll(/\.[0-9]+/, '')

                        w1.println """
  <jxb:bindings schemaLocation="$goback/$f">
     <jxb:schemaBindings>
        <jxb:package name="$pkg"/>
     </jxb:schemaBindings>
  </jxb:bindings>
""";

                        pkg = ("org.taktik.icure.be.ehealth.dto.kmehr.v$dir." + host.split(/\./).reverse().join('.') + "."
                                + path.replaceAll('#', '').replaceAll('/', '.')).replaceAll(/\.[0-9]+/, '')

                        w2.println """
  <jxb:bindings schemaLocation="$goback/$f">
     <jxb:schemaBindings>
        <jxb:package name="$pkg"/>
     </jxb:schemaBindings>
  </jxb:bindings>
""";

                    }
                }
            }
        }
        w1.println "</jxb:bindings>"
        w2.println "</jxb:bindings>"
    }
}
