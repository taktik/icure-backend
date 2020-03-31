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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.base.ReportVersion
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.DocumentGroup
import org.taktik.icure.entities.embed.DocumentType
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class DocumentTemplate : StoredDocument(), Serializable {
    @NotNull(autoFix = AutoFix.NOW)
    var modified: Long? = null

    @NotNull(autoFix = AutoFix.NOW)
    var created: Long? = null
    var version: ReportVersion? = null
    var owner: String? = null
    var guid: String? = null
    var attachmentId: String? = null

    @JsonIgnore
    var attachment: ByteArray? = null

    @JsonIgnore
    var isAttachmentDirty = false
    var documentType: DocumentType? = null
    var mainUti: String? = null
    var group: DocumentGroup? = null
    var name: String? = null
    var descr: String? = null
    var disabled: String? = null
    var specialty: Code? = null
}
