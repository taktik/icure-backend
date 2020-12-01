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
package org.taktik.icure.services.external.rest.v1.dto.embed

/**
 * Created by aduchate on 21/01/13, 14:43
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class AddressDto(
        val addressType: AddressTypeDto? = null,
        val descr: String? = null,
        val street: String? = null,
        val houseNumber: String? = null,
        val postboxNumber: String? = null,
        val postalCode: String? = null,
        val city: String? = null,
        val state: String? = null,
        val country: String? = null,
        val note: String? = null,
        val telecoms: List<TelecomDto> = listOf(),
        override val encryptedSelf: String? = null
) : EncryptedDto, Serializable, Comparable<AddressDto> {
    override fun compareTo(other: AddressDto): Int {
        return addressType?.compareTo(other.addressType ?: AddressTypeDto.other) ?: 0
    }

}
