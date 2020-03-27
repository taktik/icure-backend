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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Partnership : Serializable {
    @get:JsonIgnore
    @set:JsonIgnore
    @JsonIgnore
    var partnershipDescription: String? = null
    var type //codes are from CD-CONTACT-PERSON
            : PartnershipType? = null
    var status: PartnershipStatus? = null
    var partnerId //Person: can either be a patient or a hcp
            : String? = null

    @Deprecated("")
    var meToOtherRelationshipDescription //son if partnerId is my son - codes are from CD-CONTACT-PERSON
            : String? = null

    @Deprecated("")
    var otherToMeRelationshipDescription //father/mother if partnerId is my son
            : String? = null

}
