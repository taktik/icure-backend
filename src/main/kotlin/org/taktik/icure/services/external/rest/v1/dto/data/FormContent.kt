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
package org.taktik.icure.services.external.rest.v1.dto.data

import org.taktik.icure.services.external.rest.v1.dto.CodeDto

/**
 * Created by aduchate on 01/02/13, 12:20
 */
class FormContent(
        override val id: String? = null,
        override val entityClass: String? = null,
        override val entityId: String? = null,
        label: String? = null,
        index: Int? = null,
        guid: String? = null,
        tags: List<CodeDto>? = null,
        val formTemplateGuid: String? = null,
        val dashboardGuid: String? = null,
        val dataJXPath: String? = null,
        val descr: String? = null,
        val isAllowMultiple: Boolean = false,
        val isDeleted: Boolean = false,
        val items: MutableList<FormItem> = ArrayList()
) : FormItem(label, index, guid, tags), DisplayableContent
