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

package org.taktik.icure.validation.aspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.Validator;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.logic.ICureSessionLogic;
import org.taktik.icure.validation.AutoFix;

@Aspect
public class ValidateAspect {
	private Validator validator;

	private ICureSessionLogic sessionLogic;

	@Autowired
	public void setSessionLogic(ICureSessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@Autowired
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	@Before("execution(* *(@org.taktik.icure.validation.aspect.Check (*))) || execution(* *(@org.taktik.icure.validation.aspect.Check (*), ..))")
	public void valid(JoinPoint jp) throws NoSuchMethodException {
		// ConstraintViolations to return
		Set<ConstraintViolation<?>> violations = new HashSet<>();

		// Get the target method
		Method interfaceMethod = ((MethodSignature)jp.getSignature()).getMethod();
		Method implementationMethod = jp.getTarget().getClass().getMethod(interfaceMethod.getName(), interfaceMethod.getParameterTypes());

		// Get the annotated parameters and validate those with the @Check annotation
		Annotation[][] annotationParameters = implementationMethod.getParameterAnnotations();
		for (int i = 0; i < annotationParameters.length; i++) {
			Annotation[] annotations = annotationParameters[i];
			for (Annotation annotation : annotations) {
				if (annotation.annotationType().equals(Check.class)) {
					Check check = (Check)annotation;
					Object arg = jp.getArgs()[i];

					Set<ConstraintViolation<Object>> paramViolations = new HashSet<>();
					paramViolations.addAll(getConstraintViolations(arg, check));
					violations.addAll(paramViolations);
				}
			}
		}

		// Throw an exception if ConstraintViolations are found
		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
	}

	protected Set<ConstraintViolation<Object>> getConstraintViolations(Object arg, Check check) {
		Set<ConstraintViolation<Object>> paramViolations = validator.validate(arg, check.groups());

		PropertyUtilsBean pub = null;
		boolean shouldRecheck = false;

		for (ConstraintViolation<Object> cv : paramViolations) {
			Annotation annot = cv.getConstraintDescriptor().getAnnotation();
			try {
				final Method autoFixMethod = annot.annotationType().getMethod("autoFix");

				if (autoFixMethod != null) {
					AutoFix autoFix;
					try {
						autoFix = (AutoFix) autoFixMethod.invoke(annot);
						if (autoFix != AutoFix.NOFIX) {
							if (pub == null) {
								pub = new PropertyUtilsBean();
							}
							try {
								Iterator<Path.Node> it = cv.getPropertyPath().iterator();
								Path.Node element = null;
								while (it.hasNext()) {
									element =it.next();
								}
								if (element!=null) {
									pub.setProperty(cv.getLeafBean(), element.getName(), autoFix.fix(cv.getLeafBean(), cv.getInvalidValue(), sessionLogic));
									shouldRecheck = true;
								}
							} catch (IllegalAccessException | InvocationTargetException e) {
								//Skip
							}
						}
					} catch (IllegalAccessException | InvocationTargetException e) {
						//Skip
					}
				}
			} catch (NoSuchMethodException e) {
				//Skip
			}
		}

		if (shouldRecheck) {
			paramViolations = validator.validate(arg, check.groups());
		}
		return paramViolations;
	}
}
