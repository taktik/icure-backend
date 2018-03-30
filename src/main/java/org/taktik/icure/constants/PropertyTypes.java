/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.constants;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.taktik.icure.entities.PropertyType;

public interface PropertyTypes {
	String ENVIRONMENT_PROPERTY_PREFIX = "icure.";

    interface Category {
		String System = "org.taktik.icure.system.";
		String Node = "org.taktik.icure.node.";
		String Style = "org.taktik.icure.style.";
		String Preference = "org.taktik.icure.preference.";
		String User = "org.taktik.icure.";
	    String Plugins_be = "be.plugins.";
    }

	enum System {
		ENVIRONMENT(Category.System + "environment", PropertyTypeScope.SYSTEM, TypedValuesType.STRING),
		NAME(Category.System + "name", PropertyTypeScope.SYSTEM, TypedValuesType.STRING),
		URL(Category.System + "url", PropertyTypeScope.SYSTEM, TypedValuesType.STRING),
		VERSION(Category.System + "version", PropertyTypeScope.SYSTEM, TypedValuesType.STRING),
		VERSION_DATE(Category.System + "version.date", PropertyTypeScope.SYSTEM, TypedValuesType.STRING),
		BUILD_DATE(Category.System + "build.date", PropertyTypeScope.SYSTEM, TypedValuesType.STRING),
		INSTANCE_NAME(Category.System + "instance.name", PropertyTypeScope.SYSTEM, TypedValuesType.STRING),

		INDEX_TIMESTAMP(Category.System + "indexTimestamp", PropertyTypeScope.SYSTEM, TypedValuesType.STRING),
		INDEX_IMMEDIATE_UPDATE(Category.System + "indexation.immediate", PropertyTypeScope.SYSTEM, TypedValuesType.STRING),
		SEARCH_DEFAULT_SORT_ORDERS(Category.System + "search.default.sort.orders", PropertyTypeScope.SYSTEM, TypedValuesType.STRING),

		USER_LOST_PASSWORD_ENABLED(Category.System + "user.lost.password.enabled", PropertyTypeScope.SYSTEM, TypedValuesType.STRING),
		USER_REGISTRATION_ENABLED(Category.System + "user.registration.enabled", PropertyTypeScope.SYSTEM, TypedValuesType.STRING),
		USER_LOGIN_REGEXP(Category.System + "user.login.regexp", PropertyTypeScope.SYSTEM, TypedValuesType.STRING),
		USER_PASSWORD_REGEXP(Category.System + "user.password.regexp", PropertyTypeScope.SYSTEM, TypedValuesType.STRING),

		AUTH_FORCE_HTTPS(Category.System + "auth.force.https", PropertyTypeScope.SYSTEM, TypedValuesType.STRING),
		AUTH_URL_LOGIN(Category.System + "auth.url.login", PropertyTypeScope.SYSTEM, TypedValuesType.STRING),
		AUTH_URL_LOGOUT(Category.System + "auth.url.logout", PropertyTypeScope.SYSTEM, TypedValuesType.STRING),
		AUTH_URL_SUCCESS(Category.System + "auth.url.success", PropertyTypeScope.SYSTEM, TypedValuesType.STRING),
		AUTH_URL_FAILURE(Category.System + "auth.url.failure", PropertyTypeScope.SYSTEM, TypedValuesType.STRING),
		AUTH_USERPWD_URL_FILTER(Category.System + "auth.userpwd.url.filter", PropertyTypeScope.SYSTEM, TypedValuesType.STRING),
		AUTH_TOKEN_URL_FILTER(Category.System + "auth.token.url.filter", PropertyTypeScope.SYSTEM, TypedValuesType.STRING),

		LOCALE_DEFAULT(Category.System + "locale.default", PropertyTypeScope.SYSTEM, TypedValuesType.STRING),
		LOCALE_SELECTABLE(Category.System + "locale.selectable", PropertyTypeScope.SYSTEM, TypedValuesType.STRING),

		MIKRONO_SYNC(Category.System + "mikrono.sync", PropertyTypeScope.SYSTEM, TypedValuesType.BOOLEAN),
		PROGENDA_SYNC(Category.System + "progenda.sync", PropertyTypeScope.SYSTEM, TypedValuesType.BOOLEAN),

		LOCALE(Category.System + "locale",PropertyTypeScope.SYSTEM, TypedValuesType.STRING);


		private final PropertyType propertyType;

		System(String identifier, PropertyTypeScope scope, TypedValuesType type) {
			this.propertyType = new PropertyType(type, scope, identifier);
		}

		public String getIdentifier() {
			return propertyType.getIdentifier();
		}

		public PropertyType getPropertyType() {
			return propertyType;
		}

		public static List<PropertyType> propertyTypes() {
			return Arrays.asList(values()).stream().map(System::getPropertyType).collect(Collectors.toList());
		}

		public static List<String> identifiers() {
			return Arrays.asList(values()).stream().map(System::getIdentifier).collect(Collectors.toList());
		}
	}

	enum Preference {
		LOCALE(Category.Preference + "locale",PropertyTypeScope.ROLE, TypedValuesType.STRING);

		private final PropertyType propertyType;

		Preference(String identifier, PropertyTypeScope scope, TypedValuesType type) {
			this.propertyType = new PropertyType(type, scope, identifier);
		}

		public String getIdentifier() {
			return propertyType.getIdentifier();
		}

		public PropertyType getPropertyType() {
			return propertyType;
		}

		public static List<PropertyType> propertyTypes() {
			return Arrays.asList(values()).stream().map(Preference::getPropertyType).collect(Collectors.toList());
		}

		public static List<String> identifiers() {
			return Arrays.asList(values()).stream().map(Preference::getIdentifier).collect(Collectors.toList());
		}
	}

	enum User {
		DATA_FILTERS(Category.User + "datafilters", PropertyTypeScope.USER, TypedValuesType.JSON),
		PREFERRED_FORMS(Category.User + "preferred.forms", PropertyTypeScope.USER, TypedValuesType.JSON),
		TARIFICATION_FAVORITES(Category.User + "tarification.favorites", PropertyTypeScope.USER, TypedValuesType.JSON),
		COUNTRY_CODE(Category.User + "countryCode", PropertyTypeScope.USER, TypedValuesType.STRING),

		MIKRONO_URL(Category.User + Category.Plugins_be + "mikrono.url", PropertyTypeScope.USER, TypedValuesType.STRING),
		MIKRONO_USER(Category.User + Category.Plugins_be + "mikrono.user", PropertyTypeScope.USER, TypedValuesType.STRING),
		MIKRONO_PASSWORD(Category.User + Category.Plugins_be + "mikrono.password", PropertyTypeScope.USER, TypedValuesType.STRING),
		MIKRONO_LASTACCESS(Category.User + Category.Plugins_be + "mikrono.lastAccess", PropertyTypeScope.USER, TypedValuesType.STRING),

		PROGENDA_URL(Category.User + Category.Plugins_be + "progenda.url", PropertyTypeScope.USER, TypedValuesType.STRING),
		PROGENDA_USER(Category.User + Category.Plugins_be + "progenda.user", PropertyTypeScope.USER, TypedValuesType.STRING),
		PROGENDA_TOKEN(Category.User + Category.Plugins_be + "progenda.token", PropertyTypeScope.USER, TypedValuesType.STRING),
		PROGENDA_CENTERID(Category.User + Category.Plugins_be + "progenda.centerId", PropertyTypeScope.USER, TypedValuesType.STRING),
		PROGENDA_LASTACCESS(Category.User + Category.Plugins_be + "progenda.lastAccess", PropertyTypeScope.USER, TypedValuesType.STRING),
		PROGENDA_LASTACCESS_ICURE(Category.User + Category.Plugins_be + "progenda.lastAccess.icure", PropertyTypeScope.USER, TypedValuesType.STRING),
		PROGENDA_LASTACCESS_PROGENDA(Category.User + Category.Plugins_be + "progenda.lastAccess.progenda", PropertyTypeScope.USER, TypedValuesType.STRING),

		LBS_USER(Category.User + Category.Plugins_be + "lbs.user", PropertyTypeScope.USER, TypedValuesType.STRING),
		LBS_PASSWORD(Category.User + Category.Plugins_be + "lbs.password", PropertyTypeScope.USER, TypedValuesType.STRING),

		AML_USER(Category.User + Category.Plugins_be + "aml.user", PropertyTypeScope.USER, TypedValuesType.STRING),
		AML_PASSWORD(Category.User + Category.Plugins_be + "aml.password", PropertyTypeScope.USER, TypedValuesType.STRING);

		private final PropertyType propertyType;

		User(String identifier, PropertyTypeScope scope, TypedValuesType type) {
			this.propertyType = new PropertyType(type, scope, identifier);
		}
		public String getIdentifier() {
			return propertyType.getIdentifier();
		}

		public PropertyType getPropertyType() {
			return propertyType;
		}

		public static List<PropertyType> propertyTypes() {
			return Arrays.asList(values()).stream().map(User::getPropertyType).collect(Collectors.toList());
		}

		public static List<String> identifiers() {
			return Arrays.asList(values()).stream().map(User::getIdentifier).collect(Collectors.toList());
		}
	}

}