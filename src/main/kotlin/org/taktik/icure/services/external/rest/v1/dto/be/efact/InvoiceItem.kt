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

package org.taktik.icure.services.external.rest.v1.dto.be.efact

import io.swagger.v3.oas.annotations.media.Schema

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 19/08/15
 * Time: 11:12
 * To change this template use File | Settings | File Templates.
 */
class InvoiceItem {
	var dateCode: Long? = null
	@Schema(defaultValue = "0")
	var codeNomenclature: Long = 0
	var relatedCode: Long? = null

	var eidItem: EIDItem? = null
	var insuranceRef: String? = null
	var insuranceRefDate: Long? = null

	@Schema(defaultValue = "0")
	var units: Int = 0

	@Schema(defaultValue = "0")
	var reimbursedAmount: Long = 0
	@Schema(defaultValue = "0")
	var patientFee: Long = 0
	@Schema(defaultValue = "0")
	var doctorSupplement: Long = 0

	var sideCode: InvoicingSideCode? = null
	var timeOfDay: InvoicingTimeOfDay? = null

	var override3rdPayerCode: Int? = null
	var gnotionNihii: String? = null

	var derogationMaxNumber: InvoicingDerogationMaxNumberCode? = null
	var prescriberNorm: InvoicingPrescriberCode? = null
	var prescriberNihii: String? = null
	var prescriptionDate: Long? = null

	var personalInterventionCoveredByThirdPartyCode: Int? = null

	var doctorIdentificationNumber: String? = null
	var invoiceRef: String? = null
	var percentNorm: InvoicingPercentNorm? = null
}
