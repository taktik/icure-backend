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

package org.taktik.icure.domain.result

import java.util.*
import org.taktik.icure.dto.result.MimeAttachment
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.Form
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient

class ImportResult(
	var patient: Patient? = null,
	var hes: MutableList<HealthElement> = mutableListOf(),
	var ctcs: MutableList<Contact> = mutableListOf(),
	var warnings: MutableList<String> = mutableListOf(),
	var errors: MutableList<String> = mutableListOf(),
	var forms: MutableList<Form> = mutableListOf(),
	var hcps: MutableList<HealthcareParty> = mutableListOf(),
	var documents: MutableList<Document> = mutableListOf(),
	var attachments: MutableMap<String, MimeAttachment> = mutableMapOf()
) {
	fun warning(w: String): ImportResult {
		warnings.add(w)
		return this
	}

	fun error(e: String): ImportResult {
		errors.add(e)
		return this
	}

	fun notNull(value: String?, message: String): ImportResult {
		if (value == null) {
			warnings.add(message)
		}
		return this
	}
}
