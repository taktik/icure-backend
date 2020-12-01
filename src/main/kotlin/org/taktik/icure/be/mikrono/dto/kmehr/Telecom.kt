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

package org.taktik.icure.be.mikrono.dto.kmehr

import java.util.*

/**
 * Created by IntelliJ IDEA.
 * User: aduchate
 * Date: 15 sept. 2010
 * Time: 15:34:03
 * To change this template use File | Settings | File Templates.
 */
class Telecom : KmehrElement {
    var location: String? = null
    var type: String? = null
    var address: String? = null

    constructor() {}
    constructor(address: String?, location: String?, type: String?) {
        this.location = location
        this.type = type
        this.address = address
    }

    override var types: MutableList<String?>?
        get() {
            var res = super.types
            if (res == null && type == null && location == null) {
                return res
            }
            if (res == null) {
                res = ArrayList()
            }
            if (location != null) {
                res.add("CD-ADDRESS:$location")
            }
            if (type != null) {
                res.add("CD-TELECOM:$type")
            }
            return res
        }
        set(types) {
            super.types = types
        }
}
