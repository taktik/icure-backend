package org.taktik.icure.dto.result

import org.taktik.icure.entities.*
import java.util.LinkedList

class ImportResult(
    var patient :Patient? = null,
    val hes:LinkedList<HealthElement> = LinkedList(),
    val ctcs:LinkedList<Contact> = LinkedList(),
    val warnings:LinkedList<String> = LinkedList(),
    val errors:LinkedList<String> = LinkedList(),
    val forms:LinkedList<Form> = LinkedList(),
    val hcps:LinkedList<HealthcareParty> = LinkedList(),
    val documents:LinkedList<Document> = LinkedList()
                  ) {
    fun warning(w:String): ImportResult {
        warnings.add(w)
        return this
    }

    fun error(e:String): ImportResult {
        errors.add(e)
        return this
    }

    fun notNull(value: String?, message: String): ImportResult {
        if (value == null) { warnings.add(message) }
        return this
    }
}