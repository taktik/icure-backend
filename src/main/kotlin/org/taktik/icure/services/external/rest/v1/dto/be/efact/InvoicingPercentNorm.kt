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
 * Time: 11:13
 * To change this template use File | Settings | File Templates.
 */
enum class InvoicingPercentNorm(val code: Int) {
    None(0),
    SurgicalAid1(1),
    SurgicalAid2(2),
    ReducedFee(3),
    Ah1n1(4),
    HalfPriceSecondAct(5),
    InvoiceException(6),
    ForInformation(7);


    companion object {

        fun withCode(prescriberCode: Int): InvoicingPercentNorm? {
            for (ipc in InvoicingPercentNorm.values()) {
                if (ipc.code == prescriberCode) {
                    return ipc
                }
            }
            return null
        }
    }
}
