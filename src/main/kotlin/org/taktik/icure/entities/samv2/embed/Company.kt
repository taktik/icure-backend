package org.taktik.icure.entities.samv2.embed

import java.io.Serializable
class Company(
        from: Long? = null,
        to: Long? = null,
        var authorisationNr: String? = null,
        var vatNr: Map<String, String>? = null,
        var europeanNr: String? = null,
        var denomination: String? = null,
        var legalForm: String? = null,
        var building: String? = null,
        var streetName: String? = null,
        var streetNum: String? = null,
        var postbox: String? = null,
        var postcode: String? = null,
        var city: String? = null,
        var countryCode: String? = null,
        var phone: String? = null,
        var language: String? = null,
        var website: String? = null
) : DataPeriod(from, to), Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Company) return false
        if (!super.equals(other)) return false

        if (authorisationNr != other.authorisationNr) return false
        if (vatNr != other.vatNr) return false
        if (europeanNr != other.europeanNr) return false
        if (denomination != other.denomination) return false
        if (legalForm != other.legalForm) return false
        if (building != other.building) return false
        if (streetName != other.streetName) return false
        if (streetNum != other.streetNum) return false
        if (postbox != other.postbox) return false
        if (postcode != other.postcode) return false
        if (city != other.city) return false
        if (countryCode != other.countryCode) return false
        if (phone != other.phone) return false
        if (language != other.language) return false
        if (website != other.website) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (authorisationNr?.hashCode() ?: 0)
        result = 31 * result + (vatNr?.hashCode() ?: 0)
        result = 31 * result + (europeanNr?.hashCode() ?: 0)
        result = 31 * result + (denomination?.hashCode() ?: 0)
        result = 31 * result + (legalForm?.hashCode() ?: 0)
        result = 31 * result + (building?.hashCode() ?: 0)
        result = 31 * result + (streetName?.hashCode() ?: 0)
        result = 31 * result + (streetNum?.hashCode() ?: 0)
        result = 31 * result + (postbox?.hashCode() ?: 0)
        result = 31 * result + (postcode?.hashCode() ?: 0)
        result = 31 * result + (city?.hashCode() ?: 0)
        result = 31 * result + (countryCode?.hashCode() ?: 0)
        result = 31 * result + (phone?.hashCode() ?: 0)
        result = 31 * result + (language?.hashCode() ?: 0)
        result = 31 * result + (website?.hashCode() ?: 0)
        return result
    }

}
