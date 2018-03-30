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

package org.taktik.commons.collections.maps;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;

public class GuavaConcurrentMapFactory implements ConcurrentMapFactory {
	private Long expireDuration;
	private TimeUnit expireUnit;

	@Override
	public <K, V> ConcurrentMap<K, V> getMap(final ConcurrentMapListener<K, V> listener) {
		// Create cache builder
		CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder();

		// Set expireAfterWrite
		if (expireDuration != null && expireUnit != null) {
			cacheBuilder = cacheBuilder.expireAfterWrite(expireDuration, expireUnit);
		}

		// Configure listener
		if (listener != null) {
			cacheBuilder.removalListener((RemovalListener<K, V>) notification -> {
				K key = notification.getKey();
				V value = notification.getValue();
				switch (notification.getCause()) {
					case REPLACED:
						listener.entryUpdated(key, value);
						break;
					case EXPLICIT:
						listener.entryRemoved(key, value);
						break;
					case COLLECTED:
					case EXPIRED:
					case SIZE:
						listener.entryEvicted(key, value);
						break;
				}
			});
		}

		// Build cache
		Cache<K, V> cache = cacheBuilder.build();

		return cache.asMap();
	}

	public void setExpireAfterWrite(Long duration, TimeUnit unit) {
		this.expireDuration = duration;
		this.expireUnit = unit;
	}
}