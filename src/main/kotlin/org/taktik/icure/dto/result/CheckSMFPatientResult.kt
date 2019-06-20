package org.taktik.icure.dto.result

import org.taktik.icure.entities.Patient

public class CheckSMFPatientResult {
    var firstName: String = ""
    var lastName: String = ""
    var ssin: String = ""
    var dateOfBirth: Int? = null
    var exists: Boolean = false
    var existingPatientId: String? = null
}

