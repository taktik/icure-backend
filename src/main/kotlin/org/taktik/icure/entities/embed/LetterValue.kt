package org.taktik.icure.entities.embed

import java.util.Objects

class LetterValue {
    var letter: String? = null
    var index: String? = null
    var coefficient: Double? = null
    var value: Double? = null

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as LetterValue
        return letter == that.letter &&
                index == that.index &&
                coefficient == that.coefficient &&
                value == that.value
    }

    override fun hashCode(): Int {
        return Objects.hash(letter, index, coefficient, value)
    }
}
