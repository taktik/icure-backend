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
import java.util.HashSet

/**
 * Created by aduchate on 09/07/13, 16:30
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
open class PlanOfAction : ICureDocument, Serializable {
    @NotNull
    @JsonProperty("_id")
    @Json(name = "_id")
    override var id: String? = null
    var name: String? = null
    var descr: String? = null

    //Usually one of the following is used
    @NotNull(autoFix = AutoFix.FUZZYNOW)
    var valueDate // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20140101235960.
            : Long? = null

    @NotNull(autoFix = AutoFix.FUZZYNOW)
    var openingDate // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20140101235960.
            : Long? = null
    var closingDate // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20140101235960.
            : Long? = null
    var deadlineDate // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20140101235960.
            : Long? = null
    var idOpeningContact: String? = null
    var idClosingContact: String? = null

    @NotNull(autoFix = AutoFix.CURRENTUSERID)
    override var author //userId
            : String? = null

    @NotNull(autoFix = AutoFix.CURRENTHCPID)
    override var responsible //healthcarePartyId
            : String? = null

    @NotNull(autoFix = AutoFix.NOW)
    override var created: Long? = null

    @NotNull(autoFix = AutoFix.NOW)
    override var modified: Long? = null
    override var endOfLife: Long? = null

    @ValidCode(autoFix = AutoFix.NORMALIZECODE)
    protected var codes: MutableSet<CodeStub> = HashSet()

    @ValidCode(autoFix = AutoFix.NORMALIZECODE)
    protected var tags: MutableSet<CodeStub> = HashSet()
    var documentIds: List<String>? = null
    var prescriberId //healthcarePartyId
            : String? = null
    var numberOfCares: Int? = null
    var status: Int? = null
    var careTeamMemberships: List<CareTeamMembership?>? = null
    fun solveConflictWith(other: PlanOfAction): PlanOfAction {
        created = if (other.created == null) created else if (created == null) other.created else java.lang.Long.valueOf(Math.min(created!!, other.created!!))
        modified = if (other.modified == null) modified else if (modified == null) other.modified else java.lang.Long.valueOf(Math.max(modified!!, other.modified!!))
        codes.addAll(other.codes)
        tags.addAll(other.tags)
        openingDate = if (other.openingDate == null) openingDate else if (openingDate == null) other.openingDate else java.lang.Long.valueOf(Math.min(openingDate!!, other.openingDate!!))
        closingDate = if (other.closingDate == null) closingDate else if (closingDate == null) other.closingDate else java.lang.Long.valueOf(Math.max(closingDate!!, other.closingDate!!))
        valueDate = if (other.valueDate == null) valueDate else if (valueDate == null) other.valueDate else java.lang.Long.valueOf(Math.min(valueDate!!, other.valueDate!!))
        name = if (name == null) other.name else name
        descr = if (descr == null) other.descr else descr
        idOpeningContact = if (idOpeningContact == null) other.idOpeningContact else idOpeningContact
        idClosingContact = if (idClosingContact == null) other.idClosingContact else idClosingContact
        careTeamMemberships = MergeUtil.mergeListsDistinct(careTeamMemberships, other.careTeamMemberships, { a: CareTeamMembership?, b: CareTeamMembership? -> a == b }) { a: CareTeamMembership?, b: CareTeamMembership? -> a }
        return this
    }

    override var encryptedSelf: String? = null

    override fun getCodes(): Set<CodeStub> {
        return codes
    }

    override fun setCodes(codes: MutableSet<CodeStub>) {
        this.codes = codes
    }

    override fun getTags(): Set<CodeStub> {
        return tags
    }

    override fun setTags(tags: MutableSet<CodeStub>) {
        this.tags = tags
    }

    companion object {
        private const val serialVersionUID = 1L
        const val STATUS_PLANNED = 1 shl 0
        const val STATUS_ONGOING = 1 shl 1
        const val STATUS_FINISHED = 1 shl 2
        const val STATUS_PROLONGED = 1 shl 3
        const val STATUS_CANCELED = 1 shl 4
    }
}
