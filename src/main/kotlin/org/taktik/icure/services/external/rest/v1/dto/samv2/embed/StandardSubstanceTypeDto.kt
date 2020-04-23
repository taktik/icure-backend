package org.taktik.icure.services.external.rest.v1.dto.samv2.embed

enum class StandardSubstanceTypeDto(val value: String) {
    CAS("CAS"),
    DM_D("DM+D"),
    EDQM("EDQM"),
    SNOMED_CT("SNOMED_CT");

    companion object Factory {
        fun withValue(value: String): StandardSubstanceTypeDto = if (value == "DM+D") DM_D else valueOf(value)
    }
}
