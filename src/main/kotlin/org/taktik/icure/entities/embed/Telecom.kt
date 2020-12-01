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
package org.taktik.icure.entities.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke
import java.io.Serializable

/**
 * Created by aduchate on 21/01/13, 14:47
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Telecom(
        val telecomType: TelecomType? = null,
        val telecomNumber: String? = null,
        val telecomDescription: String? = null,
        override val encryptedSelf: String? = null
) : Encrypted, Serializable, Comparable<Telecom> {
    companion object : DynamicInitializer<Telecom>

    fun merge(other: Telecom) = Telecom(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: Telecom) = super.solveConflictsWith(other) + mapOf(
            "telecomType" to (this.telecomType ?: other.telecomType),
            "telecomNumber" to (this.telecomNumber ?: other.telecomNumber),
            "telecomDescription" to (this.telecomDescription ?: other.telecomDescription)
    )

    override fun compareTo(other: Telecom): Int {
        return telecomType?.compareTo(other.telecomType ?: TelecomType.other) ?: 0
    }
}
