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

import org.taktik.icure.services.external.rest.v1.dto.PatientDto
import java.util.*

class EfactInvoice {
    var patient: PatientDto? = null
    var ioCode: String? = null
    var items: MutableList<InvoiceItem> = LinkedList()
    var reason: InvoicingTreatmentReasonCode? = null
    var invoiceRef: String? = null
    var invoiceNumber: Long? = null
    var ignorePrescriptionDate: Boolean = false
    var hospitalisedPatient: Boolean = false
    var creditNote: Boolean = false

    var relatedInvoiceIoCode: String? = null
    var relatedInvoiceNumber: Long? = null
    var relatedBatchSendNumber: Long? = null
    var relatedBatchYearMonth: Long? = null
}
