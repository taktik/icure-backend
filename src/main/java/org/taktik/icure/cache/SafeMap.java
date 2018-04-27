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

package org.taktik.icure.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SafeMap<K, V> implements Serializable {
	private static final long serialVersionUID = 1L;

	public interface ValueProvider<K, V> {
		V getValue(K key);
	}

	private Map<K, V> map;

	public SafeMap() {
		map = new HashMap<K, V>();
	}

	public SafeMap(Map<K, V> map) {
		this.map = map;
	}

	public V get(K key, ValueProvider<K, V> valueProvider) {
		V value = map.get(key);

		if (value == null) {
			synchronized (this) {
				value = map.get(key);
				if (value == null) {
					value = valueProvider.getValue(key);
					if (value != null) {
						map.put(key, value);
					}
				}
			}
		}

		return value;
	}

	public V getIfPresent(K key) {
		return map.get(key);
	}

	public synchronized void put(K key, V value) {
		map.put(key, value);
	}

	public Map<K, V> getMap() {
		return map;
	}

	public synchronized void setMap(Map<K, V> map) {
		this.map = map;
	}
}