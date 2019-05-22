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
 * User: gpiroux
 * Date: 22/11/18
 * Time: 11:13
 * To change this template use File | Settings | File Templates.
 */
enum class InvoicingDerogationMaxNumberCode(val code: Int) {
   /*
    01 Dérogation au nombre maximal.
    02 Autre séance/prescription.
    03 2e prestation identique de la journée.
    04 3e ou suivante prestation identique de la journée.
    00 Dans les autres cas.
   */

    Other(0),
    DerogationMaxNumber(1),
    OtherPrescription(2),
    SecondPrestationOfDay(3),
    ThirdAndNextPrestationOfDay(4);

    companion object {

        fun withCode(derogationMaxNumberCode: Int): InvoicingDerogationMaxNumberCode? {
            for (idc in InvoicingDerogationMaxNumberCode.values()) {
                if (idc.code == derogationMaxNumberCode) {
                    return idc
                }
            }
            return null
        }
    }
}
