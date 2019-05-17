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

import java.util.LinkedList

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 19/08/15
 * Time: 12:18
 * To change this template use File | Settings | File Templates.
 */
class InvoicesBatch {
    var invoicingYear: Int = 0
    var invoicingMonth: Int = 0
    var fileRef: String? = null //13 alphanumeric internal reference. Typically, we use a base36 representation of the 16 first hex of the UUID id of the Message
    var batchRef: String? = null //25 alphanumeric internal reference. Typically, we use a base36 representation of the UUID id of the Message
    var ioFederationCode: String? = null //3 digits code of the IO federation
    var uniqueSendNumber : Long? = null //3 digits number for batch (typically the number of the day * 2 + 1 if 306)
    var sender:  InvoiceSender? = null
    var numericalRef : Long? = null
    var invoices: MutableList<Invoice> = LinkedList()
}
