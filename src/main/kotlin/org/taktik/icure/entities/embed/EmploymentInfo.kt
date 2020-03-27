package org.taktik.icure.entities.embed

import org.taktik.icure.entities.base.CodeStub
import java.io.Serializable
import java.util.Objects

class EmploymentInfo : Serializable {
    var startDate: Long? = null
    var endDate: Long? = null
    var professionType: CodeStub? = null
    var employer: Employer? = null

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as EmploymentInfo
        return startDate == that.startDate &&
                endDate == that.endDate &&
                professionType == that.professionType &&
                employer == that.employer
    }

    override fun hashCode(): Int {
        return Objects.hash(startDate, endDate, professionType, employer)
    }
}
