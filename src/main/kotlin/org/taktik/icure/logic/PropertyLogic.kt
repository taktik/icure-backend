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

package org.taktik.icure.logic

import org.taktik.icure.entities.Property

interface PropertyLogic {
    /**
     * Return the system properties, those that are not linked to any roles.
     *
     * @return
     */
    fun getSystemProperties(includeEnvironmentProperties: Boolean): Set<Property>

    /**
     * Return the system property with the given identifier
     *
     * @param propertyIdentifier
     * @return
     */
    fun getSystemProperty(propertyIdentifier: String): Property?

    /**
     * Return the system property value with the given identifier
     *
     * @param propertyIdentifier
     * @return
     */
    fun <T> getSystemPropertyValue(propertyIdentifier: String): T?

    /**
     * Updates the system property value with the given identifier
     *
     * @param propertyIdentifier
     * @return
     */
}