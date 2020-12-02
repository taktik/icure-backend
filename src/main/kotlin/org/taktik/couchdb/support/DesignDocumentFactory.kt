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

package org.taktik.couchdb.support

import org.taktik.couchdb.entity.DesignDocument

/**
 *
 * @author henrik lundgren
 */
interface DesignDocumentFactory {
    /**
     * Generates a design document with views, lists, shows and filters generated and loaded
     * according to the annotations found in the metaDataSource object.
     *
     * @param metaDataSource
     * @return
     */
    fun generateFrom(id: String, metaDataSource: Any): DesignDocument

}
