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
package org.taktik.icure.validation.aspect

import org.apache.commons.beanutils.PropertyUtilsBean
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.validation.AutoFix
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*
import javax.validation.ConstraintViolation
import javax.validation.ConstraintViolationException
import javax.validation.Path
import javax.validation.Validator

@Aspect
class ValidateAspect(val validator: Validator, val sessionLogic: AsyncSessionLogic) {

    @Before("execution(* *(@org.taktik.icure.validation.aspect.Check (*))) || execution(* *(@org.taktik.icure.validation.aspect.Check (*), ..))")
    @Throws(NoSuchMethodException::class)
    suspend fun valid(jp: JoinPoint) { // ConstraintViolations to return
        val violations: MutableSet<ConstraintViolation<*>> = HashSet()
        // Get the target method
        val interfaceMethod = (jp.signature as MethodSignature).method
        val implementationMethod = jp.target.javaClass.getMethod(interfaceMethod.name, *interfaceMethod.parameterTypes)
        // Get the annotated parameters and validate those with the @Check annotation
        val annotationParameters = implementationMethod.parameterAnnotations
        for (i in annotationParameters.indices) {
            val annotations = annotationParameters[i]
            for (annotation in annotations) {
                if (annotation.annotationClass == Check::class) {
                    val check = annotation as Check
                    val arg = jp.args[i]
                    val paramViolations: MutableSet<ConstraintViolation<Any>> = HashSet()
                    paramViolations.addAll(getConstraintViolations(arg, check))
                    violations.addAll(paramViolations)
                }
            }
        }
        // Throw an exception if ConstraintViolations are found
        if (!violations.isEmpty()) {
            throw ConstraintViolationException(violations)
        }
    }

    private suspend fun getConstraintViolations(arg: Any, check: Check): Set<ConstraintViolation<Any>> {
        var paramViolations = validator!!.validate(arg, *check.groups.map { it.java }.toTypedArray())
        var pub: PropertyUtilsBean? = null
        var shouldRecheck = false
        for (cv in paramViolations) {
            val annot = cv.constraintDescriptor.annotation
            try {
                val autoFixMethod: Method = annot.annotationClass.java.getMethod("autoFix")
                if (autoFixMethod != null) {
                    var autoFix: AutoFix
                    try {
                        autoFix = autoFixMethod.invoke(annot) as AutoFix
                        if (autoFix != AutoFix.NOFIX) {
                            if (pub == null) {
                                pub = PropertyUtilsBean()
                            }
                            try {
                                val it: Iterator<Path.Node> = cv.propertyPath.iterator()
                                var element: Path.Node? = null
                                while (it.hasNext()) {
                                    element = it.next()
                                }
                                if (element != null) {
                                    pub.setProperty(cv.leafBean, element.name, autoFix.fix(cv.leafBean, cv.invalidValue, sessionLogic))
                                    shouldRecheck = true
                                }
                            } catch (e: IllegalAccessException) { //Skip
                            } catch (e: InvocationTargetException) {
                            }
                        }
                    } catch (e: IllegalAccessException) { //Skip
                    } catch (e: InvocationTargetException) {
                    }
                }
            } catch (e: NoSuchMethodException) { //Skip
            }
        }
        if (shouldRecheck) {
            paramViolations = validator.validate(arg, *check.groups.map { it.java }.toTypedArray())
        }
        return paramViolations
    }
}
