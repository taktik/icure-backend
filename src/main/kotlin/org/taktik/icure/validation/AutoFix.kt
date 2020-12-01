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
package org.taktik.icure.validation

import com.fasterxml.uuid.Generators
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.entities.base.CodeIdentification
import org.taktik.icure.security.CryptoUtils
import org.taktik.icure.utils.FuzzyValues
import java.time.Instant

enum class AutoFix(private val fixer: suspend (b:Any?,v:Any?,sl:AsyncSessionLogic?) -> Any?) {
    FUZZYNOW({ _: Any?, _: Any?, _: AsyncSessionLogic? -> FuzzyValues.getCurrentFuzzyDateTime() }),
    NOW({ _: Any?, _: Any?, _: AsyncSessionLogic? -> Instant.now().toEpochMilli() }),
    UUID({ _: Any?, _: Any?, _: AsyncSessionLogic? -> Generators.randomBasedGenerator(CryptoUtils.getRandom()).generate().toString() }),
    CURRENTUSERID({ _: Any?, _: Any?, sl: AsyncSessionLogic? -> sl?.getCurrentUserId() }),
    CURRENTHCPID({ _: Any?, _: Any?, sl: AsyncSessionLogic? -> sl?.getCurrentHealthcarePartyId() }),
    NOFIX({ _: Any?, v: Any?, _: AsyncSessionLogic? -> v }),
    NORMALIZECODE({ _: Any?, v: Any?, _: AsyncSessionLogic? -> (v as? CodeIdentification)?.normalizeIdentification() ?: v });

    suspend fun fix(bean: Any?, value: Any?, sessionLogic: AsyncSessionLogic?): Any? {
        return (value as? MutableSet<*>)?.let { it.map { v: Any? -> fixer(bean, v, sessionLogic) }.toMutableSet() }
                ?: (value as? MutableList<*>)?.let { it.map { v: Any? -> fixer(bean, v, sessionLogic) }.toMutableList() }
                ?: (value as? Set<*>)?.let { it.map { v: Any? -> fixer(bean, v, sessionLogic) }.toSet() }
                ?: (value as? Collection<*>)?.let { it.map { v: Any? -> fixer(bean, v, sessionLogic) } }
                ?: fixer(bean, value, sessionLogic)
    }
}
