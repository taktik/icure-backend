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
package org.taktik.icure.services.external.rest.v2.dto.gui.layout

import java.io.Serializable

/**
 * Created by aduchate on 07/02/13, 17:10
 */
class FormColumn : Serializable {
    private var formDataList: MutableList<FormLayoutData> = ArrayList()

    /**
     * Determines the columns span of the object
     *
     * @param columns: 1=column 1, 1-2=column 1 and 2. Null means all columns.
     */
    var columns: String? = null
    var shouldDisplay: Boolean? = null
    fun getFormDataList(): List<FormLayoutData> {
        return formDataList
    }

    fun setFormDataList(formDataList: MutableList<FormLayoutData>) {
        this.formDataList = formDataList
    }

    fun addFormData(fd: FormLayoutData) {
        formDataList.add(fd)
    }
}
