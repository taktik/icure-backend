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
package org.taktik.icure.services.external.rest.v1.dto.embed

import com.github.pozo.KotlinBuilderclass
import org.taktik.icure.utils.DynamicInitializer

EpisodeDto(
        override val id: String,
        override val name: String? = null,
        val comment: String? = null,
        var startDate: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20140101235960.
        var endDate: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20140101235960.
        override val encryptedSelf: String? = null
) : EncryptedDto, Serializable, IdentifiableDto<String>, NamedDto {
    companion object : DynamicInitializer<EpisodeDto>

    fun merge(other: EpisodeDto) = EpisodeDto(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: EpisodeDto) = super.solveConflictsWith(other) + mapOf(
            "id" to (this.id),
            "name" to (this.name ?: other.name),
            "comment" to (this.comment ?: other.comment),
            "startDate" to (startDate?.coerceAtMost(other.startDate ?: Long.MAX_VALUE) ?: other.startDate),
            "endDate" to (endDate?.coerceAtLeast(other.endDate ?: 0L) ?: other.endDate)
    )
}
