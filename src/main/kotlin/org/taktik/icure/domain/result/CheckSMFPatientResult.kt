package org.taktik.icure.domain.result

class CheckSMFPatientResult {
    var firstName: String = ""
    var lastName: String = ""
    var ssin: String = ""
    var dateOfBirth: Int? = null
    var exists: Boolean = false
    var existingPatientId: String? = null
}
