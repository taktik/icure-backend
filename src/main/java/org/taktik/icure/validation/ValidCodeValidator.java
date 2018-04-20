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

package org.taktik.icure.validation;

import java.util.Collection;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.taktik.icure.entities.base.Code;

public class ValidCodeValidator  implements ConstraintValidator<ValidCode, Object> {
	public ValidCodeValidator() {
	}

	public void initialize(org.taktik.icure.validation.ValidCode parameters) {
	}

	public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
		if (object instanceof Collection) {
			Collection<Object> c = (Collection<Object>) object;
			return c.size()==0 || c.stream().allMatch(this::isValidItem);
		} else {
			return isValidItem(object);
		}
	}

	private boolean isValidItem(Object object) {
		return object == null
				|| object instanceof Code
				&& ((Code)object).getId() != null
				&& ((Code)object).getCode() != null
				&& ((Code)object).getType() != null
				&& ((Code)object).getId().startsWith(((Code)object).getType()+"|"+((Code)object).getCode());
	}
}