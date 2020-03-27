package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.validation.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
class Classification : ClassificationTemplate() {
    @NotNull
    var templateId: String? = null

}
