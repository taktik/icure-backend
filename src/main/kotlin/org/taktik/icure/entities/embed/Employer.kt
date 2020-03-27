package org.taktik.icure.entities.embed

import java.io.Serializable
import java.util.Objects

class Employer : Serializable {
    var name: String? = null
    var addresse: Address? = null

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val employer = o as Employer
        return name == employer.name &&
                addresse == employer.addresse
    }

    override fun hashCode(): Int {
        return Objects.hash(name, addresse)
    }
}
