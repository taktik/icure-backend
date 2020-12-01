package org.taktik.icure.entities.samv2.embed

enum class StandardSubstanceType(val value: String) {
    CAS("CAS"),
    DM_D("DM+D"),
    EDQM("EDQM"),
    SNOMED_CT("SNOMED_CT");

    companion object Factory {
        fun withValue(value: String): StandardSubstanceType = if (value == "DM+D") DM_D else valueOf(value)
    }
}
