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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.taktik.icure.dto.gui.type.Data
import org.taktik.icure.handlers.JacksonEditorDeserializer
import org.taktik.icure.handlers.JsonDiscriminator
import java.io.Serializable

/**
 * Created by aduchate on 19/11/13, 15:28
 */
@JsonDeserialize(using = JacksonEditorDeserializer::class)
@JsonDiscriminator("key")
abstract class Editor : Serializable {
    var left: Double? = null
    var top: Double? = null
    var width: Double? = null
    var height: Double? = null
    var isMultiline = false
    var labelPosition: LabelPosition? = null
    var isReadOnly = false
    var defaultValue: Data? = null
}
