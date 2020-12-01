/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.taktik.icure.validation

import org.taktik.icure.entities.base.CodeIdentification
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class ValidCodeValidator : ConstraintValidator<ValidCode?, Any?> {
    override fun initialize(parameters: org.taktik.icure.validation.ValidCode?) {}
    override fun isValid(`object`: Any?, constraintValidatorContext: ConstraintValidatorContext): Boolean {
        return if (`object` is Collection<*>) {
            val c = `object` as Collection<Any>
            c.size == 0 || c.stream().allMatch { `object`: Any? -> isValidItem(`object`) }
        } else {
            isValidItem(`object`)
        }
    }

    private fun isValidItem(`object`: Any?): Boolean {
        return (`object` == null
                || (`object` is CodeIdentification
                && `object`.id != null && `object`.code != null && `object`.type != null && `object`.id.startsWith(`object`.type + "|" + `object`.code)))
    }
}
