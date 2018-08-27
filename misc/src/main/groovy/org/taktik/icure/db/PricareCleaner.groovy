package org.taktik.icure.db

import org.ektorp.ViewQuery
import org.taktik.icure.entities.User

class PricareCleaner extends Importer {
    static void main(String... args) {
        def imp = new PricareCleaner()
        imp.keyRoot = "c:\\topaz\\keys"
        imp.clean()
    }

    void clean() {
        couchdbBase.queryView(new ViewQuery(includeDocs: false).dbPath(couchdbBase.path()).designDocId("_design/User").viewName("by_username"), User.class).each { User u ->
            //println(u.login)
            if (u.login == "044") {
                println("login to delete:"  + u.login)
                if (u.login == "044") {
                    couchdbBase.delete(u)
                    println("deleted:"  + u.login)
                }
            }
        }
    }
}
