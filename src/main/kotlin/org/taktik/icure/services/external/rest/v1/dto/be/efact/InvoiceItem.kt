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

package org.taktik.icure.services.external.rest.v1.dto.be.efact

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 19/08/15
 * Time: 11:12
 * To change this template use File | Settings | File Templates.
 */
class InvoiceItem {
    var dateCode: Long? = null
    var codeNomenclature: Long = 0
    var relatedCode: Long? = null

    var eidItem: EIDItem? = null
    var insuranceRef: String? = null
    var insuranceRefDate: Long? = null

    var units: Int = 0

    var reimbursedAmount: Long = 0
    var patientFee: Long = 0
    var doctorSupplement: Long = 0

    var sideCode: InvoicingSideCode? = null
    var timeOfDay: InvoicingTimeOfDay? = null

    var override3rdPayerCode: Int? = null
    var gnotionNihii: String? = null

    var derogationMaxNumber: InvoicingDerogationMaxNumberCode? = null
    var prescriberNorm: InvoicingPrescriberCode? = null
    var prescriberNihii: String? = null

    var personalInterventionCoveredByThirdPartyCode: Int? = null

    var doctorIdentificationNumber: String? = null
    var invoiceRef: String? = null
    var percentNorm: InvoicingPercentNorm? = null
}
