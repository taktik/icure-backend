/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.cache;

import org.springframework.cache.Cache;

public class SafeCache<K, V> {
	public interface ValueProvider<K, V> {
		V getValue(K key);
	}

	private Cache cache;

	public SafeCache() {
	}

	public SafeCache(Cache cache) {
		this.cache = cache;
	}

	public String getName() {
		return cache.getName();
	}

	public synchronized void clear() {
		cache.clear();
	}

	public synchronized void evict(K key) {
		cache.evict(key);
	}

	public V get(K key, ValueProvider<K, V> valueProvider) {
		V value;

		Cache.ValueWrapper valueWrapper = cache.get(key);
		if (valueWrapper == null) {
			synchronized (this) {
				valueWrapper = cache.get(key);
				if (valueWrapper == null) {
					value = valueProvider.getValue(key);
					cache.put(key, value);
				} else {
					value = (V) valueWrapper.get();
				}
			}
		} else {
			value = (V) valueWrapper.get();
		}

		return value;
	}

	public V getIfPresent(K key) {
		Cache.ValueWrapper valueWrapper = cache.get(key);
		return (valueWrapper != null) ? (V) valueWrapper.get() : null;
	}

	public synchronized void put(K key, V value) {
		cache.put(key, value);
	}

	public Cache getCache() {
		return cache;
	}

	public synchronized void setCache(Cache cache) {
		this.cache = cache;
	}
}