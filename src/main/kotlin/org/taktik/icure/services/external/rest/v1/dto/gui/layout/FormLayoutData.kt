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
package org.taktik.icure.services.external.rest.v1.dto.gui.layout

import java.io.Serializable
import org.taktik.icure.services.external.rest.v1.dto.embed.ContentDto
import org.taktik.icure.services.external.rest.v1.dto.gui.*

/**
 * Created by aduchate on 19/11/13, 10:50
 */
open class FormLayoutData(
	val isSubForm: Boolean? = null,
	val isIrrelevant: Boolean? = null,
	val isDeterminesSscontactName: Boolean? = null,
	val type: String? = null,
	val name: String? = null,
	val sortOrder: Double? = null,
	val options: Map<String, FormDataOption>? = null,
	val descr: String? = null,
	val label: String? = null,
	val editor: Editor? = null,
	val defaultValue: List<ContentDto>? = null,
	val defaultStatus: Int? = null,
	val suggest: List<Suggest>? = null,
	val plannings: List<FormPlanning>? = null,
	val tags: List<GuiCode>? = null,
	val codes: List<GuiCode>? = null,
	val codeTypes: List<GuiCodeType>? = null,
	val formulas: List<Formula>? = null,
) : Serializable
