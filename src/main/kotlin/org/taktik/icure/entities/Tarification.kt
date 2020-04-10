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
package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.embed.LetterValue
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.entities.embed.Valorisation
import java.util.Objects

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Tarification(id: String,
                   rev: String? = null,
                   revisionsInfo: Array<RevisionInfo> = arrayOf(),
                   conflicts: Array<String> = arrayOf(),
                   revHistory: Map<String, String> = mapOf()) : Code(id, rev, revisionsInfo, conflicts, revHistory) {
    var valorisations: Set<Valorisation>? = null
    var category: Map<String, String>? = null
    var consultationCode: Boolean? = null
    var hasRelatedCode: Boolean? = null
    var needsPrescriber: Boolean? = null
    var relatedCodes: Set<String>? = null
    var nGroup: String? = null
    var letterValues: List<LetterValue>? = null

    fun getnGroup(): String? {
        return nGroup
    }

    fun setnGroup(nGroup: String?) {
        this.nGroup = nGroup
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        if (!super.equals(other)) return false
        val that = other as Tarification
        return valorisations == that.valorisations &&
                category == that.category &&
                consultationCode == that.consultationCode &&
                hasRelatedCode == that.hasRelatedCode &&
                needsPrescriber == that.needsPrescriber &&
                relatedCodes == that.relatedCodes &&
                nGroup == that.nGroup &&
                letterValues == that.letterValues
    }

    override fun hashCode(): Int {
        return Objects.hash(super.hashCode(), valorisations, category, consultationCode, hasRelatedCode, needsPrescriber, relatedCodes, nGroup, letterValues)
    }
}
