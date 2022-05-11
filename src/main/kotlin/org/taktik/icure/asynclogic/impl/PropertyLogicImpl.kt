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

package org.taktik.icure.asynclogic.impl

import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.EnumerablePropertySource
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.PropertyLogic
import org.taktik.icure.constants.PropertyTypes
import org.taktik.icure.entities.base.PropertyStub
import org.taktik.icure.entities.base.PropertyTypeStub
import org.taktik.icure.entities.embed.TypedValue

@Service
class PropertyLogicImpl(private val environment: ConfigurableEnvironment) : PropertyLogic {
	private val environmentProperties: Map<String, PropertyStub> = getEnvironmentProperties().mapValues { e ->
		val propertyTypedValue = TypedValue.withValue(e.value)
		PropertyStub(
			type = PropertyTypeStub(
				identifier = e.key,
				type = propertyTypedValue?.type
			),
			typedValue = propertyTypedValue
		)
	}

	override fun getSystemProperties(includeEnvironmentProperties: Boolean): Set<PropertyStub> {
		return environmentProperties.values.toSet()
	}

	private fun getEnvironmentProperties(): Map<String, *> {
		val propertyNames = environment.propertySources.filterIsInstance(EnumerablePropertySource::class.java).flatMap { eps ->
			eps.propertyNames.map { it.replace('_', '.').toLowerCase() }.filter { it.startsWith(PropertyTypes.ENVIRONMENT_PROPERTY_PREFIX) && !it.endsWith("password") }
		}

		return propertyNames.fold(mutableMapOf<String, Any>()) { acc, propertyName ->
			val propertyKey = PropertyTypes.Category.System + propertyName.substring(PropertyTypes.ENVIRONMENT_PROPERTY_PREFIX.length)
			environment.getProperty(propertyName)?.let { propertyValue: String ->
				if ("null" == propertyValue) {
					acc
				} else if (propertyValue.trim { it <= ' ' }.toLowerCase() == "true" || propertyValue.trim { it <= ' ' }.toLowerCase() == "false") {
					acc[propertyKey] = java.lang.Boolean.valueOf(propertyValue); acc
				} else {
					try {
						acc[propertyKey] = Integer.valueOf(propertyValue)
					} catch (ignored: NumberFormatException) {
						acc[propertyKey] = propertyValue
					}
					acc
				}
			} ?: acc
		}
	}

	override fun getSystemProperty(propertyIdentifier: String): PropertyStub? {
		return environmentProperties[propertyIdentifier]
	}

	override fun <T> getSystemPropertyValue(propertyIdentifier: String): T? {
		return getSystemProperty(propertyIdentifier)?.getValue<T>()
	}
}
