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
package org.taktik.icure.services.external.rest.v1.dto.embed

/**
 * Created by aduchate on 21/01/13, 14:47
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class TelecomDto(
        val telecomType: TelecomTypeDto? = null,
        val telecomNumber: String? = null,
        val telecomDescription: String? = null,
        override val encryptedSelf: String? = null
) : EncryptedDto, Serializable, Comparable<TelecomDto> {
    companion object : DynamicInitializer<TelecomDto>

    fun merge(other: TelecomDto) = TelecomDto(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: TelecomDto) = super.solveConflictsWith(other) + mapOf(
            "telecomType" to (this.telecomType ?: other.telecomType),
            "telecomNumber" to (this.telecomNumber ?: other.telecomNumber),
            "telecomDescription" to (this.telecomDescription ?: other.telecomDescription)
    )

    override fun compareTo(other: TelecomDto): Int {
        return telecomType?.compareTo(other.telecomType ?: TelecomTypeDto.other) ?: 0
    }
}
