package org.taktik.icure.entities.samv2

import org.taktik.icure.entities.samv2.embed.*
import org.taktik.icure.entities.samv2.stub.VmpStub
import java.util.*

class Amp(
        id: String? = null,
        from: Long? = null,
        to: Long? = null,
        var code: String? = null,
        var vmp: VmpStub? = null,
        var officialName: String? = null,
        var status: AmpStatus? = null,
        var name: SamText? = null,
        var blackTriangle: Boolean = false,
        var medicineType: MedicineType? = null,
        var company: Company? = null,
        var abbreviatedName: SamText? = null,
        var proprietarySuffix: SamText? = null,
        var prescriptionName: SamText? = null,
        var ampps : SortedSet<Ampp> = sortedSetOf(),
        var components: SortedSet<AmpComponent> = sortedSetOf()
) : StoredDocumentWithPeriod(id, from, to) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Amp

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

