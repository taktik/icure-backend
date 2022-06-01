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
package org.taktik.icure.dto.gui.layout

import java.io.Serializable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.dto.gui.*
import org.taktik.icure.entities.embed.Content

/**
 * Created by aduchate on 19/11/13, 10:50
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
open class FormLayoutData : Serializable {
	var isSubForm: Boolean? = null
	var isIrrelevant: Boolean? = null
	var isDeterminesSscontactName: Boolean? = null
	var type: String? = null
	var name: String? = null
	var sortOrder: Double? = null
	var options: Map<String, FormDataOption>? = null
	var descr: String? = null
	var label: String? = null
	var editor: Editor? = null
	var defaultValue: List<Content>? = null
	var defaultStatus: Int? = null

	//Suggestions
	var codeTypes: List<CodeType>? = null

	//More versatile way
	//<Suggest class="org.taktik.icure.domain.base.Code" filterKey="type" filterValue="CD-ITEM"/>
	//<Suggest class="org.taktik.icure.domain.HealthcareParty" filterKey="speciality" filterValue="gp"/>
	var suggest: List<Suggest>? = null
	var plannings: List<FormPlanning>? = null
	var tags: List<Code>? = null
	var codes: List<Code>? = null
	var formulas: List<Formula>? = null

	companion object {
		const val OPTION_ALWAYS_INITIALIZED_IN_FORM = "AlwaysInitialized"
	}
}
