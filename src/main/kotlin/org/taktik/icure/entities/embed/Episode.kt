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
package org.taktik.icure.entities.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.github.pozo.KotlinBuilder
import org.taktik.couchdb.id.Identifiable
import org.taktik.icure.entities.base.Named
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import java.io.Serializable
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Episode(
        @JsonProperty("_id") override val id: String = UUID.randomUUID().toString(), //Autofix if needed when loading from db
        override val name: String? = null,
        val comment: String? = null,
        @field:NotNull(autoFix = AutoFix.FUZZYNOW) var startDate: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20140101235960.
        @field:NotNull(autoFix = AutoFix.FUZZYNOW) var endDate: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20140101235960.
        override val encryptedSelf: String? = null
) : Encrypted, Serializable, Identifiable<String>, Named {
    companion object : DynamicInitializer<Episode>

    fun merge(other: Episode) = Episode(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: Episode) = super.solveConflictsWith(other) + mapOf(
            "id" to (this.id),
            "name" to (this.name ?: other.name),
            "comment" to (this.comment ?: other.comment),
            "startDate" to (startDate?.coerceAtMost(other.startDate ?: Long.MAX_VALUE) ?: other.startDate),
            "endDate" to (endDate?.coerceAtLeast(other.endDate ?: 0L) ?: other.endDate)
    )
}
