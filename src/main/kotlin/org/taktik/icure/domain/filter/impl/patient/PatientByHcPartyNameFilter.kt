package org.taktik.icure.domain.filter.impl.patient

import com.github.pozo.KotlinBuilder
import com.google.common.base.Objects
import org.taktik.icure.db.StringUtils.sanitizeString
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.Patient
import java.util.*

@KotlinBuilder
data class PatientByHcPartyNameFilter(
        override val desc: String? = null,
        override val name: String? = null,
        override val healthcarePartyId: String? = null
) : AbstractFilter<Patient>, org.taktik.icure.domain.filter.patient.PatientByHcPartyNameFilter {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val filter = other as PatientByHcPartyNameFilter
        return Objects.equal(healthcarePartyId, filter.healthcarePartyId) &&
                Objects.equal(name, filter.name)
    }

    override fun hashCode(): Int {
        return Objects.hashCode(healthcarePartyId, name)
    }

    override fun matches(item: Patient): Boolean {
        val ss = sanitizeString(name)
        return ((healthcarePartyId == null || item.delegations.keys.contains(healthcarePartyId))
                && (sanitizeString(Optional.of<String?>(item.lastName!!).orElse("") + Optional.of<String?>(item.firstName!!).orElse(""))!!.contains(ss!!) ||
                sanitizeString(Optional.of<String?>(item.maidenName!!).orElse(""))!!.contains(ss) ||
                sanitizeString(Optional.of<String?>(item.partnerName!!).orElse(""))!!.contains(ss)))
    }
}
