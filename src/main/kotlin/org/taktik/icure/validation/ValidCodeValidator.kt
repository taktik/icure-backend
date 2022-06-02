/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */
package org.taktik.icure.validation

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import org.taktik.icure.entities.base.CodeIdentification

class ValidCodeValidator : ConstraintValidator<ValidCode?, Any?> {
	override fun initialize(parameters: ValidCode?) {}
	override fun isValid(`object`: Any?, constraintValidatorContext: ConstraintValidatorContext): Boolean {
		return if (`object` is Collection<*>) {
			val c = `object` as Collection<Any>
			c.size == 0 || c.stream().allMatch { `object`: Any? -> isValidItem(`object`) }
		} else {
			isValidItem(`object`)
		}
	}

	private fun isValidItem(`object`: Any?): Boolean {
		return (
			`object` == null ||
				(
					`object` is CodeIdentification &&
						`object`.id != null && `object`.code != null && `object`.type != null && `object`.id.startsWith(`object`.type + "|" + `object`.code)
					)
			)
	}
}
