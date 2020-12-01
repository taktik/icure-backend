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

package org.taktik.icure.be.ehealth.logic.kmehr

import javax.xml.datatype.XMLGregorianCalendar

data class Config(var _kmehrId: String? = null, var date: XMLGregorianCalendar? = null, var time: XMLGregorianCalendar? = null, var soft: Software? = null, var clinicalSummaryType: String? = null, var defaultLanguage: String? = null, var format:Format? = null) {
    data class Software(val name : String, val version : String)
    enum class Format {
        KMEHR,
        SUMEHR,
        SMF,
        PMF,
        MEDEX
    }
}

