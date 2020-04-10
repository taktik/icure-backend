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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.FormGroup
import org.taktik.icure.entities.embed.RevisionInfo

/**
 * Created by aduchate on 09/07/13, 16:27
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class FormTemplate(id: String,
                   rev: String? = null,
                   revisionsInfo: Array<RevisionInfo> = arrayOf(),
                   conflicts: Array<String> = arrayOf(),
                   revHistory: Map<String, String> = mapOf()) : StoredDocument(id, rev, revisionsInfo, conflicts, revHistory) {
    @JsonIgnore
    var layout: ByteArray? = null
    var layoutAttachmentId: String? = null
    var group: FormGroup? = null
    var name: String? = null
    var descr: String? = null
    var disabled: String? = null
    var specialty //Always CD-HCPARTY
            : Code? = null

    //Globally unique and consistent accross all DBs that get their formTemplate from a icure cloud library
    //The id is not guaranteed to be consistent accross dbs
    var guid: String? = null
    var author //userId
            : String? = null

    //Location in the form of a gpath/xpath like location with an optional action
    //ex: healthElements[codes[type == 'ICD' and code == 'I80']].plansOfAction[descr='Follow-up'] : add inside the follow-up plan of action of a specific healthElement
    //ex: healthElements[codes[type == 'ICD' and code == 'I80']].plansOfAction += [descr:'Follow-up'] : create a new planOfAction and add inside it
    var formInstancePreferredLocation: String? = null
    var keyboardShortcut: String? = null
    var shortReport: String? = null
    var mediumReport: String? = null
    var longReport: String? = null
    var reports: List<String>? = null

    @get:JsonIgnore
    @set:JsonIgnore
    @JsonIgnore
    @Transient
    var isAttachmentDirty = false

}
