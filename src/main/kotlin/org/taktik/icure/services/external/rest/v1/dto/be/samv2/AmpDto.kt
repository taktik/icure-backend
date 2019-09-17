package org.taktik.icure.services.external.rest.v1.dto.be.samv2

import org.taktik.icure.entities.samv2.stub.VmpStubDto
import org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed.*

class AmpDto(
        id: String? = null,
        from: Long? = null,
        to: Long? = null,
        var code: String? = null,
        var vmp: VmpStubDto? = null,
        var officialName: String? = null,
        var status: AmpStatusDto? = null,
        var name: SamTextDto? = null,
        var blackTriangle: Boolean = false,
        var medicineType: MedicineTypeDto? = null,
        var company: CompanyDto? = null,
        var abbreviatedName: SamTextDto? = null,
        var proprietarySuffix: SamTextDto? = null,
        var prescriptionName: SamTextDto? = null,
        var ampps : List<AmppDto> = listOf(),
        var components: List<AmpComponentDto> = listOf()
) : StoredDocumentWithPeriodDto(id, from, to) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as AmpDto

        if (code != other.code) return false
        if (vmp != other.vmp) return false
        if (officialName != other.officialName) return false
        if (status != other.status) return false
        if (name != other.name) return false
        if (blackTriangle != other.blackTriangle) return false
        if (medicineType != other.medicineType) return false
        if (company != other.company) return false
        if (abbreviatedName != other.abbreviatedName) return false
        if (proprietarySuffix != other.proprietarySuffix) return false
        if (prescriptionName != other.prescriptionName) return false
        if (ampps != other.ampps) return false
        if (components != other.components) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (code?.hashCode() ?: 0)
        result = 31 * result + (vmp?.hashCode() ?: 0)
        result = 31 * result + (officialName?.hashCode() ?: 0)
        result = 31 * result + (status?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + blackTriangle.hashCode()
        result = 31 * result + (medicineType?.hashCode() ?: 0)
        result = 31 * result + (company?.hashCode() ?: 0)
        result = 31 * result + (abbreviatedName?.hashCode() ?: 0)
        result = 31 * result + (proprietarySuffix?.hashCode() ?: 0)
        result = 31 * result + (prescriptionName?.hashCode() ?: 0)
        result = 31 * result + ampps.hashCode()
        result = 31 * result + components.hashCode()
        return result
    }
}

