/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.taktik.icure.domain.filter.impl.contact

import com.github.pozo.KotlinBuilder
import com.google.common.base.Objects
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.Contact

@KotlinBuilder
data class ContactByHcPartyPatientTagCodeDateFilter(
        override val desc: String? = null,
        override val healthcarePartyId: String? = null,
        @Deprecated("Use patientSecretForeignKeys instead")
        override val patientSecretForeignKey: String? = null,
        override val patientSecretForeignKeys: List<String>? = null,
        override val tagType: String? = null,
        override val tagCode: String? = null,
        override val codeType: String? = null,
        override val codeCode: String? = null,
        override val startServiceValueDate: Long? = null,
        override val endServiceValueDate: Long? = null
) : AbstractFilter<Contact>, org.taktik.icure.domain.filter.contact.ContactByHcPartyPatientTagCodeDateFilter {
    override fun hashCode(): Int {
        return Objects.hashCode(healthcarePartyId, tagType, tagCode, codeType, codeCode, startServiceValueDate, endServiceValueDate)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val filter = other as ContactByHcPartyPatientTagCodeDateFilter
        return Objects.equal(healthcarePartyId, filter.healthcarePartyId) && Objects.equal(patientSecretForeignKeys, filter.patientSecretForeignKeys) && Objects.equal(tagType, filter.tagType) && Objects.equal(tagCode, filter.tagCode) && Objects.equal(codeType, filter.codeType) && Objects.equal(codeCode, filter.codeCode) && Objects.equal(startServiceValueDate, filter.startServiceValueDate) && Objects.equal(endServiceValueDate, filter.endServiceValueDate)
    }

    override fun matches(item: Contact): Boolean {
        return ((healthcarePartyId == null || item.delegations.keys.contains(healthcarePartyId!!))
                && (patientSecretForeignKeys == null || item.secretForeignKeys.any { o: String? -> patientSecretForeignKeys!!.contains(o) })
                && (tagType == null || item.services.any { svc ->
            (svc.tags.any { t -> tagType == t.type && (tagCode == null || tagCode == t.code) }
                    && (codeType == null || svc.codes.any { cs -> codeType == cs.type && (codeCode == null || codeCode == cs.code) })
                    && (startServiceValueDate == null || svc.valueDate != null && svc.valueDate > startServiceValueDate!! || svc.openingDate != null && svc.openingDate > startServiceValueDate!!)
                    && (endServiceValueDate == null || svc.valueDate != null && svc.valueDate < endServiceValueDate!! || svc.openingDate != null && svc.openingDate < endServiceValueDate!!))
        }))
    }
}
