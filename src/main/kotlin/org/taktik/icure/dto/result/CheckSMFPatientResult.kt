package org.taktik.icure.dto.result

class CheckSMFPatientResult {
    var firstName: String = ""
    var lastName: String = ""
    var ssin: String = ""
    var dateOfBirth: Int? = null
    var exists: Boolean = false
    var existingPatientId: String? = null
}
