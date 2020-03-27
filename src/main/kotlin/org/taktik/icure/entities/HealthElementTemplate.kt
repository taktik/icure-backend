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
package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.PlanOfActionTemplate
import java.util.ArrayList
import javax.validation.Valid

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class HealthElementTemplate : StoredICureDocument() {
    var descr: String? = null
    var isRelevant = true
    var status //bit 0: active/inactive, bit 1: relevant/irrelevant, bit2 : present/absent, ex: 0 = active,relevant and present
            : Int? = null
    protected var plansOfAction: @Valid MutableList<PlanOfActionTemplate>? = ArrayList()

    fun getPlansOfAction(): List<PlanOfActionTemplate>? {
        return plansOfAction
    }

    fun setPlansOfAction(plansOfAction: List<PlanOfActionTemplate>?) {
        this.plansOfAction = plansOfAction
    }

}
