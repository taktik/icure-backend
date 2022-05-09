/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.be.mikrono.dto.kmehr

import com.fasterxml.jackson.annotation.JsonIgnore
import org.taktik.icure.be.ehealth.logic.kmehr.KmehrUtils

/**
 * Created by IntelliJ IDEA.
 * User: aduchate
 * Date: 15 sept. 2010
 * Time: 15:33:58
 * To change this template use File | Settings | File Templates.
 */
class Address : KmehrElement {
	var countryCode: String? = null
	var zip: String? = null
	var nis: String? = null
	var city: String? = null
	var district: String? = null
	var street: String? = null
	var houseNumber: String? = null
	var postboxNumber: String? = null
	var text: String? = null

	constructor() {}
	constructor(type: String?, street: String?, houseNumber: String?, postboxNumber: String?, zip: String?, city: String?, country: String?) {
		addType(type)
		this.street = street
		this.houseNumber = houseNumber
		this.postboxNumber = postboxNumber
		this.zip = zip
		this.city = city
		countryCode = country
	}

	constructor(street: String?, houseNumber: String?, postboxNumber: String?, zip: String?, city: String?, country: String?) : this("CD-ADDRESS:home", street, houseNumber, postboxNumber, zip, city, country) {}
	constructor(text: String?) {
		this.text = text
	}

	@get:JsonIgnore
	val fullStreetAddress: String
		get() = ((if (text != null) text else "") + (if (street != null) " $street" else "") + (if (houseNumber != null) ", $houseNumber" else "") + if (postboxNumber != null) " b$postboxNumber" else "").trim { it <= ' ' }.replace("  ".toRegex(), " ")

	@get:JsonIgnore
	val fullLocality: String
		get() = ((if (zip != null) zip else "") + (if (city != null) " $city" else "") + if (district != null) " ($district) " else "").trim { it <= ' ' }.replace("  ".toRegex(), " ")

	@get:JsonIgnore
	val fullAddress: String
		get() = (fullStreetAddress + " " + fullLocality + " " + if (countryCode != null) KmehrUtils.getValue(countryCode) else "").trim { it <= ' ' }
}
