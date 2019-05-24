package org.taktik.icure.services.external.rest.v1.dto.be.samv2

import org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed.*
import java.io.Serializable

class VmpGroupDto(
        from: Long? = null,
        to: Long? = null,
        var code: String? = null,
        var name: SamTextDto? = null,
        var noGenericPrescriptionReason: NoGenericPrescriptionReasonDto? = null,
        var noSwitchReason: NoSwitchReasonDto? = null
) : StoredDocumentWithPeriodDto(from, to), Serializable
