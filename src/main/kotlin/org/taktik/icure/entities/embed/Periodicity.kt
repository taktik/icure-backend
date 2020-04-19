package org.taktik.icure.entities.embed

import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.ValidCode
import java.io.Serializable

data class Periodicity(
    @ValidCode(autoFix = AutoFix.NORMALIZECODE) val relatedCode: CodeStub? = null,
    @ValidCode(autoFix = AutoFix.NORMALIZECODE) val relatedPeriodicity: CodeStub? = null
) : Serializable
