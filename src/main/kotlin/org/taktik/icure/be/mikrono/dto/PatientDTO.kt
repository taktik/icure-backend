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
package org.taktik.icure.be.mikrono.dto

import org.taktik.icure.be.mikrono.dto.kmehr.Person
import java.io.Serializable

/**
 * Created by aduchate on 16/12/11, 13:57
 */
class PatientDTO : Serializable {
    var doctorId: Long? = null
    var patient: Person? = null
    var externalId: String? = null

    companion object {
        private const val serialVersionUID = 1L
    }
}
