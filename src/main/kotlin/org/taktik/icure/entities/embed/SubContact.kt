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
import com.fasterxml.jackson.annotation.JsonProperty
import com.squareup.moshi.Json
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.base.ICureDocument
import org.taktik.icure.entities.utils.MergeUtil
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import org.taktik.icure.validation.ValidCode
import java.io.Serializable
import java.util.ArrayList
import java.util.HashSet

/**
 * Created by aduchate on 06/07/13, 10:09
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class SubContact : ICureDocument, Serializable {
    @NotNull
    @JsonProperty("_id")
    @Json(name = "_id")
    override var id: String? = null
    var descr: String? = null
    var protocol: String? = null
    var status //To be refactored
            : Int? = null

    @NotNull(autoFix = AutoFix.NOW)
    override var created: Long? = null

    @NotNull(autoFix = AutoFix.NOW)
    override var modified: Long? = null
    override var endOfLife: Long? = null

    @NotNull(autoFix = AutoFix.CURRENTUSERID)
    override var author //userId
            : String? = null

    @NotNull(autoFix = AutoFix.CURRENTHCPID)
    override var responsible //healthcarePartyId
            : String? = null

    @ValidCode(autoFix = AutoFix.NORMALIZECODE)
    override var codes: MutableSet<CodeStub> = HashSet()

    @ValidCode(autoFix = AutoFix.NORMALIZECODE)
    override var tags: MutableSet<CodeStub> = HashSet()
    var formId // form or subform unique ID. Several subcontacts with the same form ID can coexist as long as they are in different contacts or they relate to a different planOfActionID
            : String? = null
    var planOfActionId: String? = null
    var healthElementId: String? = null
    var classificationId: String? = null
    var services: List<ServiceLink?>? = ArrayList()
    fun solveConflictWith(other: SubContact): SubContact {
        created = if (other.created == null) created else if (created == null) other.created else java.lang.Long.valueOf(Math.min(created!!, other.created!!))
        modified = if (other.modified == null) modified else if (modified == null) other.modified else java.lang.Long.valueOf(Math.max(modified!!, other.modified!!))
        codes.addAll(other.codes)
        tags.addAll(other.tags)
        formId = if (formId == null) other.formId else formId
        planOfActionId = if (planOfActionId == null) other.planOfActionId else planOfActionId
        healthElementId = if (healthElementId == null) other.healthElementId else healthElementId
        classificationId = if (classificationId == null) other.classificationId else classificationId
        services = MergeUtil.mergeListsDistinct(services, other.services,
                { a: ServiceLink?, b: ServiceLink? -> a == null && b == null || a != null && b != null && a.serviceId == b.serviceId }
        ) { a: ServiceLink?, b: ServiceLink? -> a }
        return this
    }

    override var encryptedSelf: String? = null

    companion object {
        private const val serialVersionUID = 1L
        const val STATUS_LABO_RESULT = 1
        const val STATUS_UNREAD = 2
        const val STATUS_ALWAYS_DISPLAY = 4
        const val RESET_TO_DEFAULT_VALUES = 8
        const val STATUS_COMPLETE = 16
        const val STATUS_PROTOCOL_RESULT = 32
        const val STATUS_UPLOADED_FILES = 64
    }
}
