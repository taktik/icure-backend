package org.taktik.icure.entities.embed

import org.taktik.icure.entities.base.CodeStub
import java.util.Objects

class SchoolingInfo {
    var startDate: Long? = null
    var endDate: Long? = null
    var school: String? = null
    var typeOfEducation: CodeStub? = null

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as SchoolingInfo
        return startDate == that.startDate &&
                endDate == that.endDate &&
                school == that.school &&
                typeOfEducation == that.typeOfEducation
    }

    override fun hashCode(): Int {
        return Objects.hash(startDate, endDate, school, typeOfEducation)
    }
}
