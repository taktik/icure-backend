package org.taktik.icure.entities.embed

class Suspension {
    var beginMoment: Long? = null
    var endMoment: Long? = null
    var suspensionReason: String? = null

    override fun toString(): String {
        return "Suspension{" +
                "beginMoment=" + beginMoment +
                ", endMoment=" + endMoment +
                ", suspensionReason='" + suspensionReason + '\'' +
                '}'
    }
}
