package org.taktik.icure.entities.samv2.embed

import com.github.pozo.KotlinBuilder

@KotlinBuilder
data class Dmpp(
        override val from: Long? = null,
        override val to: Long? = null,
        val deliveryEnvironment: DeliveryEnvironment? = null,
        val code: String? = null,
        val codeType: DmppCodeType? = null,
        val price: String? = null,
        val cheap: Boolean? = null,
        val cheapest: Boolean? = null,
        val reimbursable: Boolean? = null,
        val reimbursements: List<Reimbursement>? = null
) : DataPeriod
