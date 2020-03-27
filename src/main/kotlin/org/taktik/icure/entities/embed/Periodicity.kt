package org.taktik.icure.entities.embed

import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.ValidCode
import java.io.Serializable

class Periodicity : Serializable {
    @ValidCode(autoFix = AutoFix.NORMALIZECODE)
    var relatedCode: CodeStub? = null

    @ValidCode(autoFix = AutoFix.NORMALIZECODE)
    var relatedPeriodicity: CodeStub? = null

}
