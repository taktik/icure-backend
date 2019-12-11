///*
// * Copyright (C) 2018 Taktik SA
// *
// * This file is part of iCureBackend.
// *
// * iCureBackend is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License version 2 as published by
// * the Free Software Foundation.
// *
// * iCureBackend is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
// */
//package org.taktik.icure.asynclogic.impl.filter
//
//import kotlinx.coroutines.flow.Flow
//import org.springframework.beans.factory.config.AutowireCapableBeanFactory
//import org.springframework.context.ApplicationContext
//import org.springframework.context.ApplicationContextAware
//import org.taktik.icure.entities.base.Identifiable
//import org.taktik.icure.exceptions.NoSuperSetException
//import java.io.Serializable
//import java.util.*
//
//class Filters : ApplicationContextAware {
//    private var applicationContext: ApplicationContext? = null
//    private val filters: MutableMap<String, Filter<*, *, *>> = HashMap()
//
//    override fun setApplicationContext(applicationContext: ApplicationContext) {
//        this.applicationContext = applicationContext
//    }
//
//    fun <T : Serializable, O: Identifiable<T>> resolve(filter: org.taktik.icure.dto.filter.Filter<T, O>): Set<T> {
//        val truncatedFullClassName = filter.javaClass.name.replace(".+?filter\\.".toRegex(), "")
//        val f = (filters[truncatedFullClassName] ?: try {
//            (applicationContext!!.autowireCapableBeanFactory.createBean(
//                    Class.forName("org.taktik.icure.logic.impl.filter.$truncatedFullClassName"),
//                    AutowireCapableBeanFactory.AUTOWIRE_BY_NAME,
//                    false
//            )).also { filters[truncatedFullClassName] = it }
//        } catch (e: ClassNotFoundException) {
//            throw IllegalArgumentException(e)
//        } ) as Filter<T, O, *>
//        val ret = f.resolve(filter, this)
//        return ret
//    }
//
//    class ConstantFilter<T : Serializable, O : Identifiable<T>> : Filter<T, O, org.taktik.icure.dto.filter.Filters.ConstantFilter<T, O>> {
//        override fun resolve(filter: org.taktik.icure.dto.filter.Filters.ConstantFilter<T, O>, context: Filters): Flow<T> {
//            return filter.constant
//        }
//    }
//
//    class UnionFilter<T : Serializable?, O : Identifiable<T>?> : Filter<T, O, org.taktik.icure.dto.filter.Filters.UnionFilter<T, O>> {
//        override fun resolve(filter: org.taktik.icure.dto.filter.Filters.UnionFilter<T, O>, context: Filters): Set<T>? {
//            val filters = filter.filters
//            if (filters.size == 0) {
//                return HashSet()
//            }
//            val result: MutableSet<T> = HashSet()
//            for (f in filters) {
//                result.addAll(context.resolve(f))
//            }
//            return result
//        }
//    }
//
//    class IntersectionFilter<T : Serializable?, O : Identifiable<T>?> : Filter<T, O, org.taktik.icure.dto.filter.Filters.IntersectionFilter<T, O>> {
//        override fun resolve(filter: org.taktik.icure.dto.filter.Filters.IntersectionFilter<T, O>, context: Filters): Set<T>? {
//            val filters = filter.filters
//            if (filters.size == 0) {
//                return HashSet()
//            }
//            val result: MutableSet<T> = HashSet()
//            for (i in filters.indices) {
//                if (i == 0) {
//                    result.addAll(context.resolve(filters[i]))
//                } else {
//                    result.retainAll(context.resolve(filters[i]))
//                }
//            }
//            return result
//        }
//    }
//
//    class ComplementFilter<T : Serializable?, O : Identifiable<T>?> : Filter<T, O, org.taktik.icure.dto.filter.Filters.ComplementFilter<T, O>> {
//        override fun resolve(filter: org.taktik.icure.dto.filter.Filters.ComplementFilter<T, O>, context: Filters): Set<T>? {
//            if (filter.superSet == null) throw NoSuperSetException()
//            val result: Set<T>? = context.resolve(filter.superSet)
//            result.removeAll(context.resolve(filter.subSet))
//            return result
//        }
//    }
//}
