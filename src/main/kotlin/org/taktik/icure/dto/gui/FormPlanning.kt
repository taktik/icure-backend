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
package org.taktik.icure.dto.gui

import java.io.Serializable

/**
 * Created by aduchate on 03/12/13, 16:27
 */
class FormPlanning : Serializable {
	var planninfForAnyDoctor: Boolean? = null
	var planningForDelegate: Boolean? = null
	var planningForPatientDoctor: Boolean? = null
	var planningForMe: Boolean? = null
	var codedDelayInDays: Int? = null
	var repetitions: Int? = null
	var repetitionsUnit: Int? = null
	var descr: String? = null
}
