/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.commons.collections.maps;

import java.util.concurrent.ConcurrentMap;

public interface ConcurrentMapFactory {
	interface ConcurrentMapListener<K, V> {
		void entryUpdated(K key, V oldValue);

		void entryRemoved(K key, V oldValue);

		void entryEvicted(K key, V oldValue);
	}

	<K, V> ConcurrentMap<K, V> getMap(ConcurrentMapListener<K, V> listener);
}