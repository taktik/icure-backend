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

package org.taktik.icure.spring.asynccache

import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class AsyncMapCacheManager() : AsyncCacheManager{

    private val caches: ConcurrentMap<String, Cache<Any, Any>> = ConcurrentHashMap()

    override fun <K, V> getCache(name: String): Cache<K, V> {
        var cache = caches[name] as Cache<K, V>?
        if (cache == null) {
            cache = MapCache(name, HashMap<K,V>())
            val currentCache = caches.putIfAbsent(name, (cache as Cache<Any, Any>))
            if (currentCache != null) {
                cache = currentCache as Cache<K, V>
            }
        }
        return cache
    }

}
