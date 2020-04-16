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
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.*
import org.taktik.icure.entities.utils.MergeUtil.mergeListsDistinct
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import java.util.ArrayList
import javax.validation.Valid

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class HealthElement(id: String,
                    val healthElementId: String, //The Unique UUID common to a group of HealthElements that forms an history
                    rev: String? = null,
                    revisionsInfo: Array<RevisionInfo> = arrayOf(),
                    conflicts: Array<String> = arrayOf(),
                    revHistory: Map<String, String> = mapOf()) : StoredICureDocument(id, rev, revisionsInfo, conflicts, revHistory) {

    //Usually one of the following is used (either valueDate or openingDate and closingDate)
    @NotNull(autoFix = AutoFix.FUZZYNOW)
    var valueDate // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
            : Long? = null

    @NotNull(autoFix = AutoFix.FUZZYNOW)
    var openingDate // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
            : Long? = null
    var closingDate // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
            : Long? = null
    var descr: String? = null
    var note: String? = null
    var isRelevant = true
    var idOpeningContact: String? = null
    var idClosingContact: String? = null
    var idService //When a service is used to create the healthElement
            : String? = null
    var status //bit 0: active/inactive, bit 1: relevant/irrelevant, bit 2 : present/absent, ex: 0 = active,relevant and present
            : Int = 0
    var laterality: Laterality? = null
    private var plansOfAction: @Valid MutableList<PlanOfAction> = ArrayList()
    private var episodes: @Valid MutableList<Episode> = ArrayList()
    var careTeam: List<CareTeamMember> = ArrayList()

    fun solveConflictsWith(other: HealthElement): HealthElement {
        super.solveConflictsWith(other)
        openingDate = if (other.openingDate == null) openingDate else if (openingDate == null) other.openingDate else openingDate!!.coerceAtMost(other.openingDate!!)
        closingDate = if (other.closingDate == null) closingDate else if (closingDate == null) other.closingDate else closingDate!!.coerceAtLeast(other.closingDate!!)
        valueDate = if (other.valueDate == null) valueDate else if (valueDate == null) other.valueDate else valueDate!!.coerceAtMost(other.valueDate!!)
        descr = if (descr == null) other.descr else descr
        note = if (note == null) other.note else note
        idOpeningContact = if (idOpeningContact == null) other.idOpeningContact else idOpeningContact
        idClosingContact = if (idClosingContact == null) other.idClosingContact else idClosingContact
        idService = if (idService == null) other.idService else idService
        plansOfAction = mergeListsDistinct(plansOfAction, other.plansOfAction,
                { a: PlanOfAction?, b: PlanOfAction? -> a == null && b == null || a != null && b != null && a.id == b.id }, { obj: PlanOfAction, other: PlanOfAction? -> obj.solveConflictsWith(other!!) }).toMutableList()
        careTeam = mergeListsDistinct(careTeam, other.careTeam, { a: CareTeamMember?, b: CareTeamMember? -> a == b }, { a: CareTeamMember, b: CareTeamMember? -> a })
        episodes = mergeListsDistinct(episodes, other.episodes,
                { a: Episode?, b: Episode? -> a == null && b == null || a != null && b != null && a.id == b.id }, { obj: Episode, other: Episode? -> obj.solveConflictsWith(other!!) }).toMutableList()
        return this
    }
}
