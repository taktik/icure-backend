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
package org.taktik.icure.services.external.rest.v1.dto

/**
 * Created by aduchate on 09/07/13, 16:27
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.v1.dto.base.CodeStubDto
import org.taktik.icure.services.external.rest.v1.dto.base.StoredDocumentDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DocumentGroupDto
import org.taktik.icure.services.external.rest.v1.dto.gui.layout.FormLayout

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class FormTemplateDto(
        override val id: String,
        override val rev: String? = null,
        override val deletionDate: Long? = null,

        val layout: FormLayout? = null,
        val name: String? = null,
        //Globally unique and consistent accross all DBs that get their formTemplate from a icure cloud library
        //The id is not guaranteed to be consistent accross dbs
        val guid: String? = null,
        val group: DocumentGroupDto? = null,
        val descr: String? = null,
        val disabled: String? = null,
        val specialty: CodeStubDto? = null,
        val author: String? = null,
        //Location in the form of a gpath/xpath like location with an optional action
        //ex: healthElements[codes[type == 'ICD' and code == 'I80']].plansOfAction[descr='Follow-up'] : add inside the follow-up plan of action of a specific healthElement
        //ex: healthElements[codes[type == 'ICD' and code == 'I80']].plansOfAction += [descr:'Follow-up'] : create a new planOfAction and add inside it
        val formInstancePreferredLocation: String? = null,
        val keyboardShortcut: String? = null,
        val shortReport: String? = null,
        val mediumReport: String? = null,
        val longReport: String? = null,
        val reports: Set<String> = setOf(),
        val tags: Set<CodeStubDto> = setOf(),
        val layoutAttachmentId: String? = null
        ) : StoredDocumentDto {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}
