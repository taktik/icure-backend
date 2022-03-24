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
package org.taktik.icure.services.external.rest.v2.dto.gui

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.taktik.icure.handlers.JacksonEditorDeserializer
import org.taktik.icure.handlers.JsonDiscriminator
import org.taktik.icure.services.external.rest.v2.dto.gui.type.Data
import java.io.Serializable

/**
 * Created by aduchate on 19/11/13, 15:28
 */
@JsonDiscriminator("key")
@JsonDeserialize(using = JacksonEditorDeserializer::class)
abstract class Editor(val left: Double? = null, val top: Double? = null, val width: Double? = null, val height: Double? = null, val isMultiline: Boolean = false, val labelPosition: LabelPosition? = null, val isReadOnly: Boolean = false, val defaultValue: Data? = null) : Serializable {

    @JsonProperty("key")
    private fun includeDiscriminator(): String {
        return this.javaClass.simpleName
    }
}
