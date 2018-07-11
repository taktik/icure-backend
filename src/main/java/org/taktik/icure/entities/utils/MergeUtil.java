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

package org.taktik.icure.entities.utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Created by aduchate on 07/06/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MergeUtil {
	protected static class ItemWithIndices<K>{
		K x;
		int xi;
		int yi;

		public ItemWithIndices(K x, int xi, int yi) {
			this.x = x;
			this.xi = xi;
			this.yi = yi;
		}
	}

	public static <K> List<ItemWithIndices<K>> getLongestCommonSubSeq(List<K> x, List<K> y, BiFunction<? super K, ? super K, Boolean> comparator) {
		int m = x.size() +1;
		int n = y.size() +1;

		String[][] b =new String[m][n];
		int[][] c = new int[m][n];
		for(int i=1;i<m;i++) {
			c[i][0] = 0;
		}
		for(int j = 0;j<n;j++) {
			c[0][j]= 0;
		}
		for(int i=1; i<m;i++) {
			for(int j=1;j<n;j++) {
				if(comparator.apply(x.get(i-1),y.get(j-1))) {
					c[i][j] = c[i-1][j-1]+1;
					b[i][j]="NW";
				} else if(c[i-1][j] >= c[i][j-1]) {
					c[i][j]=c[i-1][j];
					b[i][j]="N";
				} else {
					c[i][j] = c[i][j-1];
					b[i][j]= "W";
				}
			}
		}
		return getLCS(m-1, n-1, b, x );
	}

	public static <K> List<ItemWithIndices<K>> getLCS(int i, int j, String[][] b, List<K> x) {
		List<ItemWithIndices<K>> ret = new ArrayList<>();

		if(i==0 || j ==0)
			return ret;

		switch (b[i][j]) {
			case "NW":
				ret = getLCS(i - 1, j - 1, b, x);
				ret.add(new ItemWithIndices<>(x.get(i-1),i-1,j-1));
				break;
			case "N":
				ret = getLCS(i - 1, j, b, x);
				break;
			case "W":
				ret = getLCS(i, j - 1, b, x);
				break;
		}

		return ret;
	}

	/**
	 *
	 * @param a The first list
	 * @param b The second list
	 * @param comparator A comparator that tells if two values are considered equals (String::equals for example)
	 * @param merger A merger that combines two equals values in one. Must be commutative ((a,b)->a in case of above for example)
	 * @param <K> the class of the elements in the list
	 * @return the merged list with duplicates (a first)
	 */

	public static <K> List<K> mergeLists(List<K> a, List<K> b, BiFunction<? super K, ? super K, Boolean> comparator, BiFunction<? super K, ? super K, ? extends K> merger) {

		List<ItemWithIndices<K>> pivot = getLongestCommonSubSeq(b, a, comparator);

		if (pivot.size()==0) {
			ArrayList<K> ks = new ArrayList<>(b);
			ks.addAll(a);
			return ks;
		}

		List<K> middle = new ArrayList<>();
		for (int i=0; i<pivot.size(); i++) {
			middle.add(pivot.get(i).x);
			if (i<pivot.size()-1) {
				for (int j = pivot.get(i).xi + 1; j <pivot.get(i+1).xi; j++) {
					middle.add(b.get(j));
				}
				for (int j = pivot.get(i).yi + 1; j <pivot.get(i+1).yi; j++) {
					middle.add(a.get(j));
				}
			}
		}

		List<K> ks = new ArrayList<>(mergeLists(b.subList(0,pivot.get(0).xi), a.subList(0,pivot.get(0).yi), comparator, merger));
		ks.addAll(middle);
		ks.addAll(mergeLists(b.subList(pivot.get(pivot.size()-1).xi+1, b.size()), a.subList(pivot.get(pivot.size()-1).yi+1, a.size()), comparator, merger));

		return ks;
	}

	/**
	 *
	 * @param a The first list, which will be mostly preserved
	 * @param b The second list
	 * @param comparator A comparator that tells if two values are considered equals (String::equals for example)
	 * @param merger A merger that combines two equals values in one. Must be commutative ((a,b)->a in case of above for example)
	 * @param <K> the class of the elements in the list
	 * @return the merged list without duplicates
	 */

	public static <K> List<K> mergeListsDistinct(List<K> a, List<K> b, BiFunction<? super K, ? super K, Boolean> comparator, BiFunction<? super K, ? super K, ? extends K> merger) {
		List<K> ks = mergeLists(a, b, comparator, merger);
		List<K> result = new ArrayList<>();

		OUTER: for (K k : ks) {
			for (int j = 0; j < result.size(); j++) {
				if (comparator.apply(k, result.get(j))) {
					result.set(j, merger.apply(result.get(j), k));
					continue OUTER;
				}
			}
			result.add(k);
		}

		return result;
	}

	public static <V,S extends Set<V>> S mergeSets(S a, S b, S mergedSet, BiFunction<? super V, ? super V, Boolean> comparator, BiFunction<? super V, ? super V, ? extends V> merger) {
		Set<V> leftOverAvs = new HashSet<>(a);

		OUTER:
		for (V bi : b) {
			for (V ai : a) {
				if (comparator.apply(ai, bi)) {
					mergedSet.add(merger.apply(ai, bi));
					leftOverAvs.remove(ai);
					continue OUTER;
				}
			}
			mergedSet.add(bi);
		}
		mergedSet.addAll(leftOverAvs);

		return mergedSet;
	}

	public static  <K,V> Map<K,List<V>> mergeMapsOfListsDistinct(Map<K,List<V>> a, Map<K,List<V>> b, BiFunction<? super V, ? super V, Boolean> comparator, BiFunction<? super V, ? super V, ? extends V> merger) {
		Map<K,List<V>> result = new HashMap<>();
		Set<K> leftOverAKeys = new HashSet<>(a.keySet());

		b.forEach((key, bvs) -> {
			if (a.containsKey(key)) {
				result.put(key, mergeListsDistinct(a.get(key), bvs, comparator, merger));
				leftOverAKeys.remove(key);
			} else {
				result.put(key, bvs);
			}
		});
		leftOverAKeys.forEach(k->result.put(k,a.get(k)));
		return result;
	}

	public static <K, V> Map<K, Set<V>> mergeMapsOfSets(Map<K, Set<V>> a, Map<K, Set<V>> b, BiFunction<? super V, ? super V, Boolean> comparator, BiFunction<? super V, ? super V, ? extends V> merger) {
		Map<K, Set<V>> result = new HashMap<>();
		Set<K> leftOverAKeys = new HashSet<>(a.keySet());

		b.forEach((key, bvs) -> {
			if (a.containsKey(key)) {
				result.put(key, mergeSets(a.get(key), bvs, new HashSet<>(), comparator, merger));
				leftOverAKeys.remove(key);
			} else {
				result.put(key, bvs);
			}
		});
		leftOverAKeys.forEach(k->result.put(k,a.get(k)));
		return result;
	}
}
