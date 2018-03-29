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

package org.taktik.commons.beanutils;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BeanPropertyValueComparableIsDifferentPredicate<T> extends AbstractBeanPropertyValuePredicate<T> {
	private final Log log = LogFactory.getLog(getClass());

	public BeanPropertyValueComparableIsDifferentPredicate(String propertyName, Object propertyValue) {
		super(propertyName, propertyValue);
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public boolean evaluate(Object object) {
		if (getPropertyValue() instanceof Comparable) {
			try {
				Object propertyValue = getPropertyValue();
				Object objectValue = PropertyUtils.getProperty(object, getPropertyName());
				return !objectValue.equals(propertyValue);
			} catch (IllegalArgumentException e) {
				final String errorMsg = "Problem during evaluation. Null value encountered in property path...";
				log.warn("WARNING: " + errorMsg, e);
			} catch (IllegalAccessException e) {
				final String errorMsg = "Unable to access the property provided.";
				log.error(errorMsg, e);
				throw new IllegalArgumentException(errorMsg);
			} catch (InvocationTargetException e) {
				final String errorMsg = "Exception occurred in property's getter";
				log.error(errorMsg, e);
				throw new IllegalArgumentException(errorMsg);
			} catch (NoSuchMethodException e) {
				final String errorMsg = "Property not found.";
				log.error(errorMsg, e);
				throw new IllegalArgumentException(errorMsg);
			}
		}

		return super.evaluate(object);
	}
}