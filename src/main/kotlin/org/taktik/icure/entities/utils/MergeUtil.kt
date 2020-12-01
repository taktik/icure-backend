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
@file:Suppress("unused")

package org.taktik.icure.entities.utils

import java.util.*

/**
 * Created by aduchate on 07/06/2017.
 */
object MergeUtil {
    private fun <K> getLongestCommonSubSeq(x: List<K>, y: List<K>, comparator: (K, K) -> Boolean = { aa, bb -> aa == bb }): List<ItemWithIndices<K>> {
        val m = x.size + 1
        val n = y.size + 1
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
                when {
                    comparator(x[i - 1], y[j - 1]) -> {
                        c[i][j] = c[i - 1][j - 1] + 1
                        b[i][j] = "NW"
                    }
                    c[i - 1][j] >= c[i][j - 1] -> {
                        c[i][j] = c[i - 1][j]
                        b[i][j] = "N"
                    }
                    else -> {
                        c[i][j] = c[i][j - 1]
                        b[i][j] = "W"
                    }
                }
            }
        }
        return getLCS(m - 1, n - 1, b, x)
    }

    private fun <K> getLCS(i: Int, j: Int, b: Array<Array<String?>>, x: List<K>?): List<ItemWithIndices<K>> {
        var ret: List<ItemWithIndices<K>> = ArrayList()
        if (i == 0 || j == 0) return ret
        when (b[i][j]) {
            "NW" -> {
                ret = getLCS(i - 1, j - 1, b, x)
                ret = ret + ItemWithIndices(x!![i - 1], i - 1, j - 1)
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
    fun <K> mergeLists(a: List<K>, b: List<K>, comparator: (K, K) -> Boolean = { aa, bb -> aa == bb }, merger: (K, K) -> K = { a, _ -> a }): List<K> {
        val pivot = getLongestCommonSubSeq(b, a, comparator)
        if (pivot.isEmpty()) {
            val ks = ArrayList(b)
            ks.addAll(a)
            return ks
        }
        val middle: MutableList<K> = ArrayList()
        for (i in pivot.indices) {
            middle.add(pivot[i].x)
            if (i < pivot.size - 1) {
                for (j in pivot[i].xi + 1 until pivot[i + 1].xi) {
                    middle.add(b[j])
                }
                for (j in pivot[i].yi + 1 until pivot[i + 1].yi) {
                    middle.add(a[j])
                }
            }
        }
        val ks: MutableList<K> = ArrayList(mergeLists(b.subList(0, pivot[0].xi), a.subList(0, pivot[0].yi), comparator, merger))
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
     * @param <V> the class of the elements in the list
     * @return the merged list without duplicates
    </V> */
    fun <V> mergeListsDistinct(a: List<V>, b: List<V>, comparator: (V, V) -> Boolean = { aa, bb -> aa == bb }, merger: (V, V) -> V = { a, _ -> a }): List<V> {
        val ks = mergeLists(a, b, comparator, merger)
        val result: MutableList<V> = ArrayList()
        OUTER@ for (k in ks) {
            for (j in result.indices) {
                if (comparator(k, result[j])) {
                    result[j] = merger(result[j], k)
                    continue@OUTER
                }
            }
            result.add(k)
        }
        return result
    }

    inline fun <reified K> mergeArraysDistinct(a: Array<K>, b: Array<K>, noinline comparator: (K, K) -> Boolean = { aa, bb -> aa == bb }, noinline merger: (K, K) -> K = { a, _ -> a }): Array<K> {
        val ks = mergeLists(listOf(*a), listOf(*b), comparator, merger)
        val result: MutableList<K> = ArrayList()
        OUTER@ for (k in ks) {
            for (j in result.indices) {
                if (comparator(k, result[j])) {
                    result[j] = merger(result[j], k)
                    continue@OUTER
                }
            }
            result.add(k)
        }
        return result.toTypedArray()
    }

    fun <K> mergeSets(a: Set<K>, b: Set<K>, comparator: (K, K) -> Boolean = { aa, bb -> aa == bb }, merger: (K, K) -> K = { a, _ -> a }): Set<K> {
        val leftOverAvs: MutableSet<K> = HashSet(a)
        val mutableSet = mutableSetOf<K>()
        OUTER@ for (bi in b) {
            for (ai in a) {
                if (comparator(ai, bi)) {
                    mutableSet.add(merger(ai, bi))
                    leftOverAvs.remove(ai)
                    continue@OUTER
                }
            }
            mutableSet.add(bi)
        }
        mutableSet.addAll(leftOverAvs)
        return mutableSet.toSet()
    }

    fun <K, V> mergeMapsOfListsDistinct(a: Map<K, List<V>>, b: Map<K, List<V>>, comparator: (V, V) -> Boolean = { aa, bb -> aa == bb }, merger: (V, V) -> V = { a, _ -> a }): Map<K, List<V>> {
        val result: MutableMap<K, List<V>> = HashMap()
        val leftOverAKeys: MutableSet<K> = HashSet(a.keys)
        b.forEach { (key: K, bvs: List<V>) ->
            val listForKey = a[key]
            if (listForKey != null) {
                result[key] = mergeListsDistinct(listForKey, bvs, comparator, merger)
                leftOverAKeys.remove(key)
            } else {
                result[key] = bvs
            }
        }
        leftOverAKeys.forEach { k: K -> a[k]?.let { result[k] = it } }
        return result
    }

    fun <K, V> mergeMapsOfSetsDistinct(a: Map<K, Set<V>>, b: Map<K, Set<V>>, comparator: (V, V) -> Boolean = { aa, bb -> aa == bb }, merger: (V, V) -> V = { a, _ -> a }): Map<K, Set<V>> {
        val result: MutableMap<K, Set<V>> = HashMap()
        val leftOverAKeys: MutableSet<K> = HashSet(a.keys)
        b.forEach { (key: K, bvs: Set<V>) ->
            val listForKey = a[key]
            if (listForKey != null) {
                result[key] = mergeSets(listForKey, bvs, comparator, merger)
                leftOverAKeys.remove(key)
            } else {
                result[key] = bvs
            }
        }
        leftOverAKeys.forEach { k: K -> a[k]?.let { result[k] = it } }
        return result
    }

    inline fun <K, reified V> mergeMapsOfArraysDistinct(a: Map<K, Array<V>>, b: Map<K, Array<V>>, noinline comparator: (V, V) -> Boolean = { aa, bb -> aa == bb }, noinline merger: (V, V) -> V = { a, _ -> a }): Map<K, Array<V>> {
        val result: MutableMap<K, Array<V>> = HashMap()
        val leftOverAKeys: MutableSet<K> = HashSet(a.keys)
        b.forEach { (key: K, bvs: Array<V>) ->
            val arrayForKey = a[key]
            if (arrayForKey != null) {
                result[key] = mergeArraysDistinct(arrayForKey, bvs, comparator, merger)
                leftOverAKeys.remove(key)
            } else {
                result[key] = bvs
            }
        }
        leftOverAKeys.forEach { k: K -> a[k]?.let { result[k] = it } }
        return result
    }

    fun <K, V> mergeMapsOfSets(a: Map<K, Set<V>>, b: Map<K, Set<V>>, comparator: (V, V) -> Boolean = { aa, bb -> aa == bb }, merger: (V, V) -> V = { a, _ -> a }): Map<K, Set<V>> {
        val result: MutableMap<K, Set<V>> = HashMap()
        val leftOverAKeys: MutableSet<K> = HashSet(a.keys)
        b.forEach { (key: K, bvs: Set<V>) ->
            val setForKey = a[key]
            if (setForKey != null) {
                result[key] = mergeSets(setForKey, bvs, comparator, merger)
                leftOverAKeys.remove(key)
            } else {
                result[key] = bvs
            }
        }
        leftOverAKeys.forEach { k: K -> a[k]?.let { result[k] = it } }
        return result
    }

    class ItemWithIndices<K>(var x: K, var xi: Int, var yi: Int)
}
