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
/*
 * Copyright (c) 2010. Taktik SA.
 *
 * This file is part of JoepieViewer.
 *
 * JoepieViewer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JoepieViewer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JoepieViewer.  If not, see <http://www.gnu.org/licenses/>.
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
