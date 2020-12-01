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


package org.taktik.commons.beanutils;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;

public class BeanPropertyValueComparableEqualsPredicate<T> extends BeanPropertyValueEqualsPredicate implements Predicate<T> {
    private final Log log = LogFactory.getLog(getClass());

    public BeanPropertyValueComparableEqualsPredicate(String propertyName, Object propertyValue) {
        super(propertyName, propertyValue);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean evaluate(Object object) {
        if (getPropertyValue() instanceof Comparable) {
            try {
                Object propertyValue = getPropertyValue();
                Object objectValue = PropertyUtils.getProperty(object, getPropertyName());
                return objectValue.equals(propertyValue);
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
