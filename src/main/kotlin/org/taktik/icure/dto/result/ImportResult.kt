package org.taktik.icure.dto.result

import java.util.LinkedList

class ImportResult(val warnings:LinkedList<String> = LinkedList(), val errors:LinkedList<String> = LinkedList()) {
    fun warning(w:String): ImportResult {
        warnings.add(w)
        return this
    }
    fun error(e:String): ImportResult {
        errors.add(e)
        return this
    }

    fun notNull(value: String?, message: String): ImportResult {
        if (value == null) { warnings.add(message) }
        return this
    }
}