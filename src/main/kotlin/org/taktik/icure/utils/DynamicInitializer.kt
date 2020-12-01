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

package org.taktik.icure.utils

import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor

interface DynamicInitializer<T>

inline operator fun <reified C : Any, T : DynamicInitializer<C>> T.invoke(args: Map<String, Any?>): C {
    val constructor = C::class.primaryConstructor!!
    val argmap = HashMap<KParameter, Any?>().apply {
        constructor.parameters.forEach { if (it.name in args) put(it, args[it.name]) }
    }
    return constructor.callBy(argmap)
}
