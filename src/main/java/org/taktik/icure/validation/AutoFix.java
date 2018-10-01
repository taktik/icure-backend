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

package org.taktik.icure.validation;

import java.time.Instant;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.fasterxml.uuid.Generators;
import org.taktik.icure.entities.base.Code;
import org.taktik.icure.entities.base.CodeIdentification;
import org.taktik.icure.logic.ICureSessionLogic;
import org.taktik.icure.security.CryptoUtils;
import org.taktik.icure.utils.FuzzyValues;

public enum AutoFix {
	FUZZYNOW((b,v,sl)-> FuzzyValues.getCurrentFuzzyDateTime()),
	NOW((b,v,sl)-> Instant.now().toEpochMilli()),
	UUID((b,v,sl)-> Generators.randomBasedGenerator(CryptoUtils.getRandom()).generate()),
	CURRENTUSERID((b,v,sl)-> sl.getCurrentUserId()),
	CURRENTHCPID((b,v,sl)-> sl.getCurrentHealthcarePartyId()),
	NOFIX((b,v,sl)->v),
	NORMALIZECODE((b,v,sl)-> {
		CodeIdentification c = (CodeIdentification) v;
		if (c.getId() != null) {
			String[] parts = c.getId().split("|");
			if (c.getType() == null) {
				c.setType(parts[0]);
			}
			if (c.getCode() == null) {
				c.setCode(parts[1]);
			}
		}
		return c;
	});

	private final Fixer fix;

	AutoFix(Fixer fix) {
		this.fix = fix;
	}

	public Object fix(Object bean, Object value, ICureSessionLogic sessionLogic) {
		if (value instanceof Collection) {
			Collection<Object> c = (Collection<Object>) value;
			Collection<Object> fixedValue = c.stream().map((v) -> this.fix.fix(bean, v, sessionLogic)).collect(Collectors.toCollection((Supplier<Collection<Object>>) () -> {
				try {
					return c.getClass().newInstance();
				} catch (InstantiationException | IllegalAccessException ignored) {
				}
				return null;
			}));

			if (fixedValue==null && value!=null) {
				throw new IllegalArgumentException("Cannot instantiate collection of class "+value.getClass());
			}

			return fixedValue;
		}
		return this.fix.fix(bean,value,sessionLogic);
	}

	@FunctionalInterface
	public interface Fixer {
		Object fix(Object bean, Object value, ICureSessionLogic sessionLogic);
	}
}
