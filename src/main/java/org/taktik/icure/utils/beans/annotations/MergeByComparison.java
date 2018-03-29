/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.utils.beans.annotations;

import org.taktik.icure.utils.beans.MergeByComparisonLogic;

import java.lang.annotation.*;

/**
 * This annotations allows to establish the field value retained for the merge according to the result of the
 * {@link java.lang.Comparable#compareTo(Object)} method call. The annotation can also be used on primitive types.
 */
@Documented
@ImplementedBy(MergeByComparisonLogic.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MergeByComparison {

	enum Retains {
		GREATER, LESSER
	}

	Retains retains() default Retains.GREATER;
}
