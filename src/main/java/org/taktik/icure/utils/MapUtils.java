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

package org.taktik.icure.utils;

import java.security.Key;
import java.util.HashMap;

/**
 * Created by aduchate on 02/10/11, 16:34
 */
public class MapUtils {
    public static<K,V> HashMap<K,V> hashMap(K key, V value, Object... others) {
        HashMap<K,V> result = new HashMap<K, V>(others.length/2+1);
        result.put(key,value);
        for (int i=0;i<others.length/2;i++) {
            result.put((K)others[i*2],(V)others[i*2+1]);
        }
        return result;
    }
}
