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
 * Time: 12:06:22
 * To change this template use File | Settings | File Templates.
 */
class Person : KmehrElement() {
    var firstname: String? = null
    var familyname: String? = null
    var birthdate: Date? = null
    var birthlocation: Address? = null
    var deathdate: Date? = null
    var deathlocation: Address? = null
    var sex: String? = null
    var nationality: String? = null
    var addresses: MutableList<Address?> = ArrayList()
    var telecoms: MutableList<Telecom?> = ArrayList()
    var usuallanguage: String? = null
    var profession: String? = null
    var recorddatetime: Date? = null
    var comments: List<String> = ArrayList()
    fun addAddress(a: Address?) {
        addresses.add(a)
    }

    fun addTelecom(t: Telecom?) {
        telecoms.add(t)
    }

}
