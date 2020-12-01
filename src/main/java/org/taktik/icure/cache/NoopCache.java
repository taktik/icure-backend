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

package org.taktik.icure.cache;

import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

public class NoopCache implements Cache {
	@Override
	public void clear() {
	}

	@Override
	public void evict(Object key) {
	}

	@Override
	public Cache.ValueWrapper get(Object key) {
		return null;
	}

	@Override
	public <T> T get(Object key, Class<T> type) {
		return null;
	}

	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		return null;
	}

	@Override
	public String getName() {
		return "noop";
	}

	@Override
	public Object getNativeCache() {
		return null;
	}

	@Override
	public void put(Object key, Object value) {
	}

	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		return null;
	}
}
