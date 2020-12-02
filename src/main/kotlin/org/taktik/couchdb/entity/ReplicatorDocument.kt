/*
 *  iCure Data Stack. Copyright (c) 2020  aduchate
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

package org.taktik.couchdb.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.couchdb.CouchDbDocument

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ReplicatorDocument(
        override val id: String,
        override val rev: String?,
        val source: String? = null,
        val target: String? = null,
        val create_target: Boolean = false,
        val continuous: Boolean = false,
        override val revHistory: Map<String, String>? = null
) : CouchDbDocument {
    override fun withIdRev(id: String?, rev: String) = id?.let { this.copy(id = it, rev = rev) } ?: this.copy(rev = rev)
}
