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


package org.taktik.commons.beanutils;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BeanPropertyValueWildcardComparePredicate<T> extends AbstractBeanPropertyValuePredicate<T> {
	private final Log log = LogFactory.getLog(getClass());

	private int flags = Pattern.CASE_INSENSITIVE;

	/**
	 * Instantiates a new bean property value wildcard compare predicate.
	 *
	 * @param propertyName  the property name
	 * @param propertyValue the property value
	 */
	public BeanPropertyValueWildcardComparePredicate(String propertyName, Object propertyValue) {
		super(propertyName, propertyValue);
	}

	/**
	 * Wildcard to regex.
	 *
	 * @param wildcard the wildcard
	 * @return the string
	 */
	private String wildcardToRegex(String wildcard) {
		StringBuilder s = new StringBuilder(wildcard.length());
		s.append('^');
		for (int i = 0, is = wildcard.length(); i < is; i++) {
			char c = wildcard.charAt(i);
			switch (c) {
				case '*':
					s.append(".*");
					break;
				case '?':
					s.append('.');
					break;
				// escape special regexp-characters
				case '(':
				case ')':
				case '[':
				case ']':
				case '$':
				case '^':
				case '.':
				case '{':
				case '}':
				case '|':
				case '\\':
					s.append('\\');
					s.append(c);
					break;
				default:
					s.append(c);
					break;
			}
		}
		s.append('$');
		return (s.toString());
	}

	@Override
	public boolean evaluate(Object object) {
		Object propertyValue = getPropertyValue();
		if ((getPropertyValue() instanceof String) && (((String) propertyValue).contains("*") || ((String) propertyValue).contains("?"))) {
			try {
				Object objectValue = PropertyUtils.getProperty(object, getPropertyName());
				if (objectValue instanceof String) {
					Pattern pattern = Pattern.compile(wildcardToRegex(((String) propertyValue)), flags);
					Matcher matcher = pattern.matcher((String) objectValue);
					return matcher.matches();
				}
				return false;
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
		} else {
			if (propertyValue instanceof String) {
				Object objectValue;
				try {
					objectValue = PropertyUtils.getProperty(object, getPropertyName());
					if (objectValue instanceof String) {
						return Objects.equals(((String) propertyValue).toUpperCase(), ((String) objectValue).toUpperCase());
					}
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
		}

		return super.evaluate(object);
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		flags = flags;
	}
}