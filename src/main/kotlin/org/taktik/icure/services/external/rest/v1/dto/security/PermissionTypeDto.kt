package org.taktik.icure.services.external.rest.v1.dto.security

enum class PermissionTypeDto {
    AUTHENTICATE,
    ADMIN,
    PATIENT_VIEW,
    PATIENT_CREATE,
    PATIENT_CHANGE_DELETE,
    MEDICAL_DATA_VIEW,
    MEDICAL_DATA_CREATE,
    MEDICAL_CHANGE_DELETE,
    FINANCIAL_DATA_VIEW,
    FINANCIAL_DATA_CREATE,
    FINANCIAL_CHANGE_DELETE
}
