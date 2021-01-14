package org.taktik.icure.entities.samv2.embed

import java.io.Serializable
import java.util.*

class Dmpp(
        var id: String? = null,
        from: Long? = null,
        to: Long? = null,
        var deliveryEnvironment: DeliveryEnvironment? = null,
        var code: String? = null,
        var codeType: DmppCodeType? = null,
        var price: String? =  null,
        var cheap: Boolean? =  null,
        var cheapest: Boolean? =  null,
        var reimbursable: Boolean? =  null,
        var reimbursements: SortedSet<Reimbursement>? = null
) : DataPeriod(from, to), Serializable, Comparable<Dmpp> {
    override fun compareTo(other: Dmpp): Int {
        return if (this == other) {
            0
        } else compareValuesBy(this, other, { it.from }, { it.deliveryEnvironment }, { it.id }, { it.hashCode() }).also { if(it==0) throw IllegalStateException("Invalid compareTo implementation") }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Dmpp) return false
        if (!super.equals(other)) return false

        if (id != other.id) return false
        if (deliveryEnvironment != other.deliveryEnvironment) return false
        if (code != other.code) return false
        if (codeType != other.codeType) return false
        if (price != other.price) return false
        if (cheap != other.cheap) return false
        if (cheapest != other.cheapest) return false
        if (reimbursable != other.reimbursable) return false
        if (reimbursements != other.reimbursements) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + (deliveryEnvironment?.hashCode() ?: 0)
        result = 31 * result + (code?.hashCode() ?: 0)
        result = 31 * result + (codeType?.hashCode() ?: 0)
        result = 31 * result + (price?.hashCode() ?: 0)
        result = 31 * result + (cheap?.hashCode() ?: 0)
        result = 31 * result + (cheapest?.hashCode() ?: 0)
        result = 31 * result + (reimbursable?.hashCode() ?: 0)
        result = 31 * result + (reimbursements?.hashCode() ?: 0)
        return result
    }
}
