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
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.RevisionInfo
import java.util.ArrayList

/**
 * Created by aduchate on 18/07/13, 13:06
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Form(id: String,
           rev: String? = null,
           revisionsInfo: Array<RevisionInfo> = arrayOf(),
           conflicts: Array<String> = arrayOf(),
           revHistory: Map<String, String> = mapOf()) : StoredICureDocument(id, rev, revisionsInfo, conflicts, revHistory) {
    var descr: String? = null
    var formTemplateId: String? = null
    var contactId: String? = null
    var healthElementId: String? = null
    var planOfActionId: String? = null

    //if form is not filled in with contact data but with patient data. If this is null, it means that the list of services comes from all sub-contacts with this formId
    var dataJXPath: String? = null
    var dashboardIds: List<String> = ArrayList()
    var parent: String? = null

    @JsonIgnore
    var children: List<Form>? = ArrayList()
    get() {
        if (field == null) field = ArrayList()
        return field
    }
    var isHasBeenInitialized: Boolean? = null
    fun solveConflictWith(other: Form): Form {
        super.solveConflictsWith(other)
        descr = if (descr == null) other.descr else descr
        formTemplateId = if (formTemplateId == null) other.formTemplateId else formTemplateId
        contactId = if (contactId == null) other.contactId else contactId
        planOfActionId = if (planOfActionId == null) other.planOfActionId else planOfActionId
        healthElementId = if (healthElementId == null) other.healthElementId else healthElementId
        isHasBeenInitialized = if (isHasBeenInitialized == null) other.isHasBeenInitialized else if (other.isHasBeenInitialized == null) isHasBeenInitialized else java.lang.Boolean.valueOf(isHasBeenInitialized!! || other.isHasBeenInitialized!!)
        return this
    }
}
