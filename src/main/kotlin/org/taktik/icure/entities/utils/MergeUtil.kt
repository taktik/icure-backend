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
package org.taktik.icure.entities.utils

import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap
import java.util.HashSet
import java.util.function.BiFunction
import java.util.function.Consumer

/**
 * Created by aduchate on 07/06/2017.
 */
object MergeUtil {
    fun <K> getLongestCommonSubSeq(x: List<K>?, y: List<K>?, comparator: BiFunction<in K, in K, Boolean>): List<ItemWithIndices<K>> {
        val m = x!!.size + 1
        val n = y!!.size + 1
        val b = Array(m) { arrayOfNulls<String>(n) }
        val c = Array(m) { IntArray(n) }
        for (i in 1 until m) {
            c[i][0] = 0
        }
        for (j in 0 until n) {
            c[0][j] = 0
        }
        for (i in 1 until m) {
            for (j in 1 until n) {
                if (comparator.apply(x[i - 1], y[j - 1])) {
                    c[i][j] = c[i - 1][j - 1] + 1
                    b[i][j] = "NW"
                } else if (c[i - 1][j] >= c[i][j - 1]) {
                    c[i][j] = c[i - 1][j]
                    b[i][j] = "N"
                } else {
                    c[i][j] = c[i][j - 1]
                    b[i][j] = "W"
                }
            }
        }
        return getLCS(m - 1, n - 1, b, x)
    }

    fun <K> getLCS(i: Int, j: Int, b: Array<Array<String?>>, x: List<K>?): MutableList<ItemWithIndices<K>> {
        var ret: MutableList<ItemWithIndices<K>> = ArrayList()
        if (i == 0 || j == 0) return ret
        when (b[i][j]) {
            "NW" -> {
                ret = getLCS(i - 1, j - 1, b, x)
                ret.add(ItemWithIndices(x!![i - 1], i - 1, j - 1))
            }
            "N" -> ret = getLCS(i - 1, j, b, x)
            "W" -> ret = getLCS(i, j - 1, b, x)
        }
        return ret
    }

    /**
     *
     * @param a The first list
     * @param b The second list
     * @param comparator A comparator that tells if two values are considered equals (String::equals for example)
     * @param merger A merger that combines two equals values in one. Must be commutative ((a,b)->a in case of above for example)
     * @param <K> the class of the elements in the list
     * @return the merged list with duplicates (a first)
    </K> */
    fun <K> mergeLists(a: List<K>?, b: List<K>?, comparator: BiFunction<in K, in K, Boolean>, merger: BiFunction<in K, in K, out K>?): List<K> {
        val pivot = getLongestCommonSubSeq(b, a, comparator)
        if (pivot.size == 0) {
            val ks = ArrayList(b)
            ks.addAll(a!!)
            return ks
        }
        val middle: MutableList<K> = ArrayList()
        for (i in pivot.indices) {
            middle.add(pivot[i].x)
            if (i < pivot.size - 1) {
                for (j in pivot[i].xi + 1 until pivot[i + 1].xi) {
                    middle.add(b!![j])
                }
                for (j in pivot[i].yi + 1 until pivot[i + 1].yi) {
                    middle.add(a!![j])
                }
            }
        }
        val ks: MutableList<K> = ArrayList(mergeLists(b!!.subList(0, pivot[0].xi), a!!.subList(0, pivot[0].yi), comparator, merger))
        ks.addAll(middle)
        ks.addAll(mergeLists(b.subList(pivot[pivot.size - 1].xi + 1, b.size), a.subList(pivot[pivot.size - 1].yi + 1, a.size), comparator, merger))
        return ks
    }

    /**
     *
     * @param a The first list, which will be mostly preserved
     * @param b The second list
     * @param comparator A comparator that tells if two values are considered equals (String::equals for example)
     * @param merger A merger that combines two equals values in one. Must be commutative ((a,b)->a in case of above for example)
     * @param <K> the class of the elements in the list
     * @return the merged list without duplicates
    </K> */
    fun <K> mergeListsDistinct(a: List<K>?, b: List<K>?, comparator: BiFunction<in K, in K, Boolean>, merger: BiFunction<in K, in K, out K>): List<K> {
        val ks = mergeLists(a, b, comparator, merger)
        val result: MutableList<K> = ArrayList()
        OUTER@ for (k in ks) {
            for (j in result.indices) {
                if (comparator.apply(k, result[j])) {
                    result[j] = merger.apply(result[j], k)
                    continue@OUTER
                }
            }
            result.add(k)
        }
        return result
    }

    fun <K> mergeArraysDistinct(a: Array<K>, b: Array<K>, comparator: BiFunction<in K, in K, Boolean>, merger: BiFunction<in K, in K, out K>): Array<K> {
        val ks = mergeLists(Arrays.asList(*a), Arrays.asList(*b), comparator, merger)
        val result: MutableList<K> = ArrayList()
        OUTER@ for (k in ks) {
            for (j in result.indices) {
                if (comparator.apply(k, result[j])) {
                    result[j] = merger.apply(result[j], k)
                    continue@OUTER
                }
            }
            result.add(k)
        }
        return result.toTypedArray()
    }

    fun <V, S : Set<V>?> mergeSets(a: S, b: S, mergedSet: S, comparator: BiFunction<in V, in V, Boolean>, merger: BiFunction<in V, in V, out V>): S {
        val leftOverAvs: MutableSet<V> = HashSet(a)
        OUTER@ for (bi in b!!) {
            for (ai in a!!) {
                if (comparator.apply(ai, bi)) {
                    mergedSet.add(merger.apply(ai, bi))
                    leftOverAvs.remove(ai)
                    continue@OUTER
                }
            }
            mergedSet.add(bi)
        }
        mergedSet.addAll(leftOverAvs)
        return mergedSet
    }

    fun <K, V> mergeMapsOfListsDistinct(a: Map<K, List<V>>, b: Map<K, List<V>>, comparator: BiFunction<in V, in V, Boolean>, merger: BiFunction<in V, in V, out V>): Map<K, List<V>> {
        val result: MutableMap<K, List<V>> = HashMap()
        val leftOverAKeys: MutableSet<K> = HashSet(a.keys)
        b.forEach { (key: K, bvs: List<V>) ->
            if (a.containsKey(key)) {
                result[key] = mergeListsDistinct(a[key], bvs, comparator, merger)
                leftOverAKeys.remove(key)
            } else {
                result[key] = bvs
            }
        }
        leftOverAKeys.forEach(Consumer { k: K -> result[k] = a[k]!! })
        return result
    }

    fun <K, V> mergeMapsOfArraysDistinct(a: Map<K, Array<V>>, b: Map<K, Array<V>>, comparator: BiFunction<in V, in V, Boolean>, merger: BiFunction<in V, in V, out V>): Map<K, Array<V>> {
        val result: MutableMap<K, Array<V>> = HashMap()
        val leftOverAKeys: MutableSet<K> = HashSet(a.keys)
        b.forEach { (key: K, bvs: Array<V>) ->
            if (a.containsKey(key)) {
                result[key] = mergeArraysDistinct(a[key], bvs, comparator, merger)
                leftOverAKeys.remove(key)
            } else {
                result[key] = bvs
            }
        }
        leftOverAKeys.forEach(Consumer { k: K -> result[k] = a[k]!! })
        return result
    }

    fun <K, V> mergeMapsOfSets(a: Map<K, Set<V>>, b: Map<K, Set<V>>, comparator: BiFunction<in V, in V, Boolean>, merger: BiFunction<in V, in V, out V>): MutableMap<K, MutableSet<V>> {
        val result: MutableMap<K, Mut<V>> = HashMap()
        val leftOverAKeys: MutableSet<K> = HashSet(a.keys)
        b.forEach { (key: K, bvs: Set<V>) ->
            if (a.containsKey(key)) {
                result[key] = mergeSets(a[key], bvs, HashSet<V>(), comparator, merger)!!
                leftOverAKeys.remove(key)
            } else {
                result[key] = bvs
            }
        }
        leftOverAKeys.forEach(Consumer { k: K -> result[k] = a[k]!! })
        return result
    }

    protected class ItemWithIndices<K>(var x: K, var xi: Int, var yi: Int)
}
