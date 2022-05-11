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

package org.taktik.icure.validation.aspect

import javax.validation.Validation
import javax.validation.ValidatorFactory
import kotlin.reflect.KFunction
import kotlin.reflect.full.findParameterByName
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.validation.AutoFix

class Fixer<E : Any>(private val sessionLogic: AsyncSessionLogic, private val factory: ValidatorFactory = Validation.buildDefaultValidatorFactory()) {
	data class Fix(val fixPath: List<FixPointSelector>, val value: Any?) {
		fun behead() = this.copy(fixPath = fixPath.drop(1))
	}

	data class FixPointSelector(val name: String, val leaf: Boolean, val iterable: Boolean, val beanInIteration: Any?)

	fun <K : Any> applyFixes(doc: K, fixes: List<Fix>): K {
		val docFixes = fixes.groupBy { f -> f.fixPath.first().let { Triple(it.name, it.leaf, it.iterable) } }.map { (sel, groupedFixes) ->
			val (name, leaf, iterable) = sel
			if (iterable) {
				val collection = doc::class.memberProperties.find { it.name == name }?.getter?.call(doc)
				Pair(
					name,
					(collection as? MutableSet<*>)?.let { applyFixOnCollection(it, groupedFixes).toMutableSet() }
						?: (collection as? MutableList<*>)?.let { applyFixOnCollection(it, groupedFixes).toMutableList() }
						?: (collection as? Set<*>)?.let { applyFixOnCollection(it, groupedFixes).toSet() }
						?: (collection as? Collection<*>)?.let { applyFixOnCollection(it, groupedFixes) }
				)
			} else if (!leaf) {
				val item = doc::class.memberProperties.find { it.name == name }?.getter?.call(doc)
				Pair(name, item?.let { applyFixes(it, groupedFixes.filter { f -> f.fixPath.first().beanInIteration === it }.map { f -> f.behead() }) })
			} else Pair(name, groupedFixes.first().value)
		}
		return doc::class.memberFunctions.find { it.name == "copy" }?.let { copy ->
			val args = (listOf(copy.instanceParameter!! to doc) + docFixes.mapNotNull { it: Pair<String, Any?> -> copy.findParameterByName(it.first)?.let { p -> p to it.second } }).toMap()
			(copy as? KFunction<K>)?.callBy(args) ?: doc
		} ?: doc
	}

	private fun applyFixOnCollection(items: Collection<*>, groupedFixes: List<Fix>): List<Any?> {
		return items.map {
			it?.let {
				if (groupedFixes.any { f -> f.fixPath.first().beanInIteration === it })
					applyFixes(it, groupedFixes.filter { f -> f.fixPath.first().beanInIteration === it }.map { f -> f.behead() })
				else it
			}
		}
	}

	suspend fun fix(doc: E): E {
		val violations = factory.validator.validate(doc)

		return violations.fold(listOf<Fix>()) { fixes, cv ->
			val annotation = cv.constraintDescriptor.annotation
			try {
				val autoFixMethod = annotation.annotationClass.members.find { it.name == "autoFix" }
				autoFixMethod?.let {
					autoFixMethod.call(annotation) as? AutoFix
				}?.let { autoFix ->
					if (autoFix != AutoFix.NOFIX) {
						try {
							val pp = cv.propertyPath.toList()
							fixes + Fix(
								pp.mapIndexed { idx, it ->
									val isLeaf = pp.size == idx + 1
									FixPointSelector(it.name, isLeaf, !isLeaf && pp[idx + 1].isInIterable, (it as org.hibernate.validator.internal.engine.path.NodeImpl).value)
								},
								autoFix.fix(cv.leafBean, cv.invalidValue, sessionLogic)
							)
						} catch (e: Exception) {
							fixes
						}
					} else fixes
				} ?: fixes
			} catch (e: NoSuchMethodException) { //Skip
				fixes
			}
		}.let { fixes ->
			applyFixes(doc, fixes)
		}
	}
}
