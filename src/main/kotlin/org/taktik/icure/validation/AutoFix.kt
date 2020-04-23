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
    FUZZYNOW({ b: Any?, v: Any?, sl: AsyncSessionLogic? -> FuzzyValues.getCurrentFuzzyDateTime() }),
    NOW({ b: Any?, v: Any?, sl: AsyncSessionLogic? -> Instant.now().toEpochMilli() }),
    UUID({ b: Any?, v: Any?, sl: AsyncSessionLogic? -> Generators.randomBasedGenerator(CryptoUtils.getRandom()).generate() }),
    CURRENTUSERID({ b: Any?, v: Any?, sl: AsyncSessionLogic? -> sl?.getCurrentUserId() }),
    CURRENTHCPID({ b: Any?, v: Any?, sl: AsyncSessionLogic? -> sl?.getCurrentHealthcarePartyId() }),
    NOFIX({ b: Any?, v: Any?, sl: AsyncSessionLogic? -> v }),
    NORMALIZECODE({ b: Any?, v: Any?, sl: AsyncSessionLogic? -> (v as? CodeIdentification)?.normalizeIdentification() ?: v });

    suspend fun fix(bean: Any?, value: Any?, sessionLogic: AsyncSessionLogic?): Any? {
        if (value is Collection<*>) {
            val c = value as Collection<Any>
            return c.map { v: Any? -> fixer(bean, v, sessionLogic) }
        }
        return fixer(bean, value, sessionLogic)
    }
}
