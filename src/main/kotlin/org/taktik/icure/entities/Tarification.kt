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
import org.taktik.icure.entities.embed.Valorisation
import java.util.Objects

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Tarification : Code {
    var valorisations: Set<Valorisation>? = null
    var category: Map<String, String>? = null
    var consultationCode: Boolean? = null
    var hasRelatedCode: Boolean? = null
    var needsPrescriber: Boolean? = null
    var relatedCodes: Set<String>? = null
    var nGroup: String? = null
    var letterValues: List<LetterValue>? = null

    constructor() {}
    constructor(typeAndCodeAndVersion: String?) : super(typeAndCodeAndVersion!!) {}
    constructor(type: String?, code: String?, version: String?) : super(type!!, code!!, version!!) {}
    constructor(regions: Set<String?>?, type: String?, code: String?, version: String?) : super(regions, type, code, version) {}
    constructor(region: String?, type: String?, code: String?, version: String?) : super(region!!, type, code, version) {}
    constructor(regions: Set<String>?, type: String?, code: String?, version: String?, label: Map<String, String>?) : super(regions, type!!, code!!, version!!, label) {}

    fun getnGroup(): String? {
        return nGroup
    }

    fun setnGroup(nGroup: String?) {
        this.nGroup = nGroup
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        if (!super.equals(o)) return false
        val that = o as Tarification
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
