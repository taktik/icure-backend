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

import org.springframework.cache.support.SimpleValueWrapper

class MapCache<K, V>(private val name: String, private val map: HashMap<K, V>) : Cache<K, V> {

	override suspend fun getWrapper(key: K?): org.springframework.cache.Cache.ValueWrapper? {
		return key?.let {
			val value: V? = get(key)
			value?.let { SimpleValueWrapper(value) }
		}
	}

	override suspend fun get(key: K): V? = map[key]

	override fun clear() {
		map.clear()
	}

	override fun invalidate(): Boolean {
		clear()
		return false
	}

	override suspend fun evict(key: K?) {
		map.remove(key)
	}

	override suspend fun put(key: K, value: V) {
		map[key] = value
	}

	override fun getNativeCache(): Any? {
		return map
	}

	override fun getName(): String {
		return name
	}
}
