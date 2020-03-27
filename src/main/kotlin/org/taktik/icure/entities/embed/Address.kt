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
import org.taktik.icure.entities.base.Encryptable
import java.io.Serializable
import java.util.LinkedList

/**
 * Created by aduchate on 21/01/13, 14:43
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Address : Serializable, Comparable<Address>, Encryptable {
    var addressType: AddressType? = null
    var descr: String? = null
    var street: String? = null
    var houseNumber: String? = null
    var postboxNumber: String? = null
    var postalCode: String? = null
    var city: String? = null
    var country: String? = null
    override var encryptedSelf: String? = null
    var note: String? = null

    var telecoms: MutableList<Telecom> = LinkedList()

    constructor() {}
    constructor(addressType: AddressType?) {
        this.addressType = addressType
    }

    fun mergeFrom(other: Address) {
        if (descr == null && other.descr != null) {
            descr = other.descr
        }
        if (street == null && other.street != null) {
            street = other.street
        }
        if (houseNumber == null && other.houseNumber != null) {
            houseNumber = other.houseNumber
        }
        if (postboxNumber == null && other.postboxNumber != null) {
            postboxNumber = other.postboxNumber
        }
        if (postalCode == null && other.postalCode != null) {
            postalCode = other.postalCode
        }
        if (city == null && other.city != null) {
            city = other.city
        }
        if (country == null && other.country != null) {
            country = other.country
        }
        if (encryptedSelf == null && other.encryptedSelf != null) {
            encryptedSelf = other.encryptedSelf
        }
        for (fromTelecom in other.telecoms) {
            val destTelecom = telecoms.stream().filter { telecom: Telecom -> telecom.getTelecomType() == fromTelecom.getTelecomType() }.findAny()
            if (destTelecom.isPresent) {
                destTelecom.orElseThrow { IllegalStateException() }.mergeFrom(fromTelecom)
            } else {
                telecoms.add(fromTelecom)
            }
        }
    }

    fun forceMergeFrom(other: Address) {
        if (other.descr != null) {
            descr = other.descr
        }
        if (other.street != null) {
            street = other.street
        }
        if (other.houseNumber != null) {
            houseNumber = other.houseNumber
        }
        if (other.postboxNumber != null) {
            postboxNumber = other.postboxNumber
        }
        if (other.postalCode != null) {
            postalCode = other.postalCode
        }
        if (other.city != null) {
            city = other.city
        }
        if (other.country != null) {
            country = other.country
        }
        if (other.encryptedSelf != null) {
            encryptedSelf = other.encryptedSelf
        }
        for (fromTelecom in other.telecoms) {
            val destTelecom = telecoms.stream().filter { telecom: Telecom -> telecom.getTelecomType() == fromTelecom.getTelecomType() }.findAny()
            if (destTelecom.isPresent) {
                destTelecom.orElseThrow { IllegalStateException() }.forceMergeFrom(fromTelecom)
            } else {
                telecoms.add(fromTelecom)
            }
        }
    }

    @JsonIgnore
    fun findMobile(): String? {
        for (t in telecoms) {
            if (TelecomType.mobile == t.getTelecomType()) {
                return t.getTelecomNumber()
            }
        }
        return null
    }

    @JsonIgnore
    fun setMobile(value: String?) {
        for (t in telecoms) {
            if (TelecomType.mobile == t.getTelecomType()) {
                t.setTelecomNumber(value)
            }
        }
        if (value != null) {
            telecoms.add(Telecom(TelecomType.mobile, value))
        }
    }

    override fun compareTo(other: Address): Int {
        return addressType!!.compareTo(other.addressType!!)
    }
}
