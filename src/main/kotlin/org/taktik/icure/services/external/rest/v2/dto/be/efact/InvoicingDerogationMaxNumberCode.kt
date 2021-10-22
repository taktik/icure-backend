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
