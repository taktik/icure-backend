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
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.entities.embed.SubContact
import org.taktik.icure.entities.utils.MergeUtil.mergeSets
import org.taktik.icure.utils.FuzzyValues
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.HashSet
import java.util.TreeSet
import java.util.function.BiFunction
import javax.validation.Valid

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Contact : StoredICureDocument {
    @NotNull(autoFix = AutoFix.UUID)
    var groupId // Several contacts can be combined in a logical contact if they share the same groupId
            : String? = null

    @NotNull(autoFix = AutoFix.FUZZYNOW)
    var openingDate // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
            : Long? = null
    var closingDate // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
            : Long? = null
    var descr: String? = null
    var location: String? = null

    //Redundant... Should be responsible
    var healthcarePartyId: String? = null
    var externalId: String? = null
    var modifiedContactId: String? = null
    var encounterType: Code? = null
    protected var subContacts: @Valid MutableSet<SubContact>? = HashSet()
    protected var services: @Valid MutableSet<Service>? = TreeSet()
    fun solveConflictWith(other: Contact): Contact {
        super.solveConflictsWith(other)
        encryptedSelf = if (encryptedSelf == null) other.encryptedSelf else encryptedSelf
        openingDate = if (other.openingDate == null) openingDate else if (openingDate == null) other.openingDate else java.lang.Long.valueOf(Math.min(openingDate!!, other.openingDate!!))
        closingDate = if (other.closingDate == null) closingDate else if (closingDate == null) other.closingDate else java.lang.Long.valueOf(Math.max(closingDate!!, other.closingDate!!))
        descr = if (descr == null) other.descr else descr
        location = if (location == null) other.location else location
        encounterType = if (encounterType == null) other.encounterType else encounterType
        subContacts = mergeSets<SubContact, Set<SubContact>?>(subContacts, other.subContacts, HashSet(),
                BiFunction { a: SubContact?, b: SubContact? -> a == null && b == null || a != null && b != null && a.id == b.id },
                BiFunction { a: SubContact, b: SubContact? ->
                    a.solveConflictWith(b!!)
                    a
                })?.toMutableSet()
        services = mergeSets<Service, Set<Service>?>(services, other.services, TreeSet(),
                BiFunction { a: Service?, b: Service? -> a == null && b == null || a != null && b != null && a.id == b.id }, BiFunction { obj: Service, other: Service? -> obj.solveConflictWith(other!!) })?.toMutableSet()
        return this
    }

    constructor() {}
    constructor(healthcarePartyId: String) {
        this.healthcarePartyId = healthcarePartyId
        openingDate = FuzzyValues.getFuzzyDateTime(LocalDateTime.now(), ChronoUnit.SECONDS)
        responsible = healthcarePartyId
    }

}
