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
package org.taktik.icure.asynclogic.impl.filter

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.taktik.icure.entities.base.Identifiable
import java.io.Serializable
import java.util.*

@ExperimentalCoroutinesApi
class Filters : ApplicationContextAware {
    private var applicationContext: ApplicationContext? = null
    private val filters: MutableMap<String, Filter<*, *, *>> = HashMap()

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    fun <T : Serializable, O : Identifiable<T>> resolve(filter: org.taktik.icure.domain.filter.Filter<T, O>) = flow<T> {
        val truncatedFullClassName = filter.javaClass.name.replace(".+?filter\\.impl\\.".toRegex(), "").replace(".+?dto\\.filter\\.".toRegex(), "")
        val filterToBeResolved =
                filters[truncatedFullClassName] as Filter<T, O, org.taktik.icure.domain.filter.Filter<T, O>>?
                        ?: try {
                            ((applicationContext!!.autowireCapableBeanFactory.createBean(
                                    Class.forName("org.taktik.icure.asynclogic.impl.filter.$truncatedFullClassName"),
                                    AutowireCapableBeanFactory.AUTOWIRE_BY_NAME,
                                    false
                            )) as? Filter<T, O, org.taktik.icure.domain.filter.Filter<T, O>>)?.also { filters[truncatedFullClassName] = it }
                        } catch (e: ClassNotFoundException) {
                            throw IllegalStateException(e)
                        }
        val ids = hashSetOf<Serializable>()
        (filterToBeResolved?.resolve(filter, this@Filters)?: throw IllegalStateException("Invalid filter")).collect {
            if (!ids.contains(it)) {
                emit(it)
                ids.add(it)
            }
        }
    }

}
