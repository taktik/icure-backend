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
package org.taktik.icure.constants

import java.util.*
import java.util.stream.Collectors
import org.taktik.icure.entities.PropertyType
import org.taktik.icure.entities.PropertyType.Companion.with

interface PropertyTypes {
	interface Category {
		companion object {
			const val System = "org.taktik.icure.system."
			const val Node = "org.taktik.icure.node."
			const val Style = "org.taktik.icure.style."
			const val Preference = "org.taktik.icure.preference."
			const val User = "org.taktik.icure."
			const val Plugins_be = "be.plugins."
		}
	}

	enum class System(identifier: String, scope: PropertyTypeScope, type: TypedValuesType) {
		ENVIRONMENT(
			Category.System + "environment",
			PropertyTypeScope.SYSTEM,
			TypedValuesType.STRING
		),
		NAME(Category.System + "name", PropertyTypeScope.SYSTEM, TypedValuesType.STRING), URL(
			Category.System + "url", PropertyTypeScope.SYSTEM, TypedValuesType.STRING
		),
		VERSION(Category.System + "version", PropertyTypeScope.SYSTEM, TypedValuesType.STRING), VERSION_DATE(
			Category.System + "version.date", PropertyTypeScope.SYSTEM, TypedValuesType.STRING
		),
		BUILD_DATE(Category.System + "build.date", PropertyTypeScope.SYSTEM, TypedValuesType.STRING), INSTANCE_NAME(
			Category.System + "instance.name", PropertyTypeScope.SYSTEM, TypedValuesType.STRING
		),
		ICURE_PATH_ROOT(
			Category.System + "path.root", PropertyTypeScope.SYSTEM, TypedValuesType.STRING
		),
		ICURE_PATH_TEMP(
			Category.System + "path.temp", PropertyTypeScope.SYSTEM, TypedValuesType.STRING
		),
		INDEX_TIMESTAMP(
			Category.System + "indexTimestamp", PropertyTypeScope.SYSTEM, TypedValuesType.STRING
		),
		INDEX_IMMEDIATE_UPDATE(
			Category.System + "indexation.immediate", PropertyTypeScope.SYSTEM, TypedValuesType.STRING
		),
		SEARCH_DEFAULT_SORT_ORDERS(
			Category.System + "search.default.sort.orders", PropertyTypeScope.SYSTEM, TypedValuesType.STRING
		),
		USER_LOST_PASSWORD_ENABLED(
			Category.System + "user.lost.password.enabled", PropertyTypeScope.SYSTEM, TypedValuesType.STRING
		),
		USER_REGISTRATION_ENABLED(
			Category.System + "user.registration.enabled", PropertyTypeScope.SYSTEM, TypedValuesType.STRING
		),
		USER_LOGIN_REGEXP(
			Category.System + "user.login.regexp", PropertyTypeScope.SYSTEM, TypedValuesType.STRING
		),
		USER_PASSWORD_REGEXP(
			Category.System + "user.password.regexp", PropertyTypeScope.SYSTEM, TypedValuesType.STRING
		),
		AUTH_FORCE_HTTPS(
			Category.System + "auth.force.https", PropertyTypeScope.SYSTEM, TypedValuesType.STRING
		),
		AUTH_URL_LOGIN(
			Category.System + "auth.url.login", PropertyTypeScope.SYSTEM, TypedValuesType.STRING
		),
		AUTH_URL_LOGOUT(
			Category.System + "auth.url.logout", PropertyTypeScope.SYSTEM, TypedValuesType.STRING
		),
		AUTH_URL_SUCCESS(
			Category.System + "auth.url.success", PropertyTypeScope.SYSTEM, TypedValuesType.STRING
		),
		AUTH_URL_FAILURE(
			Category.System + "auth.url.failure", PropertyTypeScope.SYSTEM, TypedValuesType.STRING
		),
		AUTH_USERPWD_URL_FILTER(
			Category.System + "auth.userpwd.url.filter", PropertyTypeScope.SYSTEM, TypedValuesType.STRING
		),
		AUTH_TOKEN_URL_FILTER(
			Category.System + "auth.token.url.filter", PropertyTypeScope.SYSTEM, TypedValuesType.STRING
		),
		LOCALE_DEFAULT(
			Category.System + "locale.default", PropertyTypeScope.SYSTEM, TypedValuesType.STRING
		),
		LOCALE_SELECTABLE(
			Category.System + "locale.selectable", PropertyTypeScope.SYSTEM, TypedValuesType.STRING
		),
		PROGENDA_SYNC(
			Category.System + "progenda.sync", PropertyTypeScope.SYSTEM, TypedValuesType.BOOLEAN
		),
		LOCALE(Category.System + "locale", PropertyTypeScope.SYSTEM, TypedValuesType.STRING);

		val propertyType: PropertyType
		val identifier: String
			get() = propertyType.identifier

		companion object {
			fun propertyTypes(): List<PropertyType> {
				return Arrays.stream(values()).map { obj: System -> obj.propertyType }
					.collect(Collectors.toList())
			}

			fun identifiers(): List<String> {
				return Arrays.stream(values()).map { obj: System -> obj.identifier }
					.collect(Collectors.toList())
			}
		}

		init {
			propertyType = with(type, scope, identifier)
		}
	}

	enum class Preference(identifier: String, scope: PropertyTypeScope, type: TypedValuesType) {
		LOCALE(Category.Preference + "locale", PropertyTypeScope.ROLE, TypedValuesType.STRING);

		val propertyType: PropertyType
		val identifier: String?
			get() = propertyType.identifier

		companion object {
			fun propertyTypes(): List<PropertyType> {
				return Arrays.stream(values()).map { obj: Preference -> obj.propertyType }
					.collect(Collectors.toList())
			}

			fun identifiers(): List<String?> {
				return Arrays.stream(values()).map { obj: Preference -> obj.identifier }
					.collect(Collectors.toList())
			}
		}

		init {
			propertyType = with(type, scope, identifier)
		}
	}

	enum class User(identifier: String, scope: PropertyTypeScope, type: TypedValuesType) {
		DATA_FILTERS(Category.User + "datafilters", PropertyTypeScope.USER, TypedValuesType.JSON), PREFERRED_FORMS(
			Category.User + "preferred.forms", PropertyTypeScope.USER, TypedValuesType.JSON
		),
		TARIFICATION_FAVORITES(
			Category.User + "tarification.favorites", PropertyTypeScope.USER, TypedValuesType.JSON
		),
		COUNTRY_CODE(
			Category.User + "countryCode", PropertyTypeScope.USER, TypedValuesType.STRING
		);

		val propertyType: PropertyType
		val identifier: String?
			get() = propertyType.identifier

		companion object {
			fun propertyTypes(): List<PropertyType> {
				return Arrays.stream(values()).map { obj: User -> obj.propertyType }
					.collect(Collectors.toList())
			}

			fun identifiers(): List<String> {
				return values().mapNotNull { obj: User -> obj.identifier }
			}
		}

		init {
			propertyType = with(type, scope, identifier)
		}
	}

	companion object {
		const val ENVIRONMENT_PROPERTY_PREFIX = "icure."
	}
}
