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

import java.io.Serializable

/**
 * Created by emad7105 on 11/07/2014.
 */
class PaginatedDocumentKeyIdPair<K> : Serializable {
    var startKey: K? = null
        private set
    var startKeyDocId: String? = null

    constructor() {}
    constructor(startKey: K, startKeyDocId: String?) {
        this.startKey = startKey
        this.startKeyDocId = startKeyDocId
    }

    fun setStartKey(startKey: K) {
        this.startKey = startKey
    }

}
