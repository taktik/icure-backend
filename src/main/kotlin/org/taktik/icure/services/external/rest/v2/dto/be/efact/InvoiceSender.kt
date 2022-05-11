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

package org.taktik.icure.services.external.rest.v2.dto.be.efact

import io.swagger.v3.oas.annotations.media.Schema

class InvoiceSender {
	var nihii: Long? = null
	var bic: String? = null
	var iban: String? = null
	@Schema(defaultValue = "999999922L")
	var bce: Long? = 999999922L

	var ssin: String? = null
	var lastName: String? = null
	var firstName: String? = null
	var phoneNumber: Long? = null
	var conventionCode: Int? = null

	@Schema(defaultValue = "false")
	var isSpecialist: Boolean = false
		get() = nihii!! % 1000L >= 10
}
