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
package org.taktik.icure.services.external.rest.v1.dto.base

interface ICureDocumentDto<T> : IdentifiableDto<T>, HasTagsDto, HasCodesDto {
    val created: Long?
    val modified: Long?
    val endOfLife: Long?
    val author: String?
    val responsible: String?
    val medicalLocationId: String?

    fun solveConflictsWith(other: ICureDocumentDto<T>): Map<String, Any?> {
        return mapOf(
                "id" to this.id,
                "created" to (this.created?.coerceAtMost(other.created ?: Long.MAX_VALUE) ?: other.created),
                "modified" to (this.modified?.coerceAtLeast(other.modified ?: 0L) ?: other.modified),
                "endOfLife" to (this.endOfLife?.coerceAtMost(other.endOfLife ?: Long.MAX_VALUE) ?: other.endOfLife),
                "author" to (this.author ?: other.author),
                "responsible" to (this.responsible ?: other.responsible),
                "medicalLocationId" to (this.medicalLocationId ?: other.medicalLocationId),
                "tags" to (other.tags + this.tags),
                "codes" to (other.codes + this.codes)
        )
    }

}
