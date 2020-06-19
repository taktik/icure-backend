package org.taktik.icure.dto.result

import org.taktik.icure.dto.be.mikrono.Attachment
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.Form
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import java.util.HashMap

class ImportResult(
        var patient: Patient? = null,
        var hes: MutableList<HealthElement> = mutableListOf(),
        var ctcs: MutableList<Contact> = mutableListOf(),
        var warnings: MutableList<String> = mutableListOf(),
        var errors: MutableList<String> = mutableListOf(),
        var forms: MutableList<Form> = mutableListOf(),
        var hcps: MutableList<HealthcareParty> = mutableListOf(),
        var documents: MutableList<Document> = mutableListOf(),

        var attachments: HashMap<String, Attachment>? = null
) {
    fun warning(w: String): ImportResult {
        warnings.add(w)
        return this
    }

    fun error(e: String): ImportResult {
        errors.add(e)
        return this
    }

    fun notNull(value: String?, message: String): ImportResult {
        if (value == null) {
            warnings.add(message)
        }
        return this
    }
}
