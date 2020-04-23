package org.taktik.icure.services.external.rest.v1.dto.samv2.embed

data class DmppDto(
        override val from: Long? = null,
        override val to: Long? = null,
        val deliveryEnvironment: DeliveryEnvironmentDto? = null,
        val code: String? = null,
        val codeType: DmppCodeTypeDto? = null,
        val price: String? = null,
        val cheap: Boolean? = null,
        val cheapest: Boolean? = null,
        val reimbursable: Boolean? = null,
        val reimbursements: List<ReimbursementDto>? = null
) : DataPeriodDto
