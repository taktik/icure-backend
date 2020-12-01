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
package org.taktik.icure.db

import java.io.Serializable

/**
 * Created by aduchate on 3/11/13, 14:38
 */
class PaginationOffset<K> : Serializable {
    val startKey: K?
    val startDocumentId: String?
    val offset: Int? // should be scarcely used
    val limit: Int

    constructor(limit: Int): this(null, null, null, limit)

    constructor(limit: Int, startDocumentId: String?): this(null, startDocumentId, null, limit)

    constructor(paginatedList: PaginatedList<*>): this(
            paginatedList.nextKeyPair?.startKey as K?,
            paginatedList.nextKeyPair?.startKeyDocId,
            null,
            paginatedList.pageSize
    )

    constructor(startKey: K?, startDocumentId: String?, offset: Int?, limit: Int) {
        this.startKey = startKey
        this.startDocumentId = startDocumentId
        this.offset = offset
        this.limit = limit
    }

    fun <L>toPaginationOffset(startKeyConverter: (k:K) -> L) = PaginationOffset(this.startKey?.let { startKeyConverter(it) }, this.startDocumentId, this.offset, this.limit)
}
