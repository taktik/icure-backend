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

package org.taktik.icure.logic.impl


import com.google.common.base.Preconditions
import com.google.common.collect.ImmutableMap
import org.jetbrains.annotations.NotNull
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.icure.dao.CodeDAO
import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.base.EnumVersion
import org.taktik.icure.logic.CodeLogic
import java.lang.reflect.InvocationTargetException
import java.util.*

@Service
class CodeLogicImpl(val codeDAO: CodeDAO) : GenericLogicImpl<Code, CodeDAO>(), CodeLogic {
    override fun getTagTypeCandidates(): List<String> {
        return Arrays.asList("CD-ITEM", "CD-PARAMETER", "CD-CAREPATH", "CD-SEVERITY", "CD-URGENCY", "CD-GYNECOLOGY")
    }

    override fun getRegions(): List<String> {
        return Arrays.asList("fr", "be")
    }

    override fun get(id: String): Code {
        return codeDAO[id]
    }

    override fun get(@NotNull type: String, @NotNull code: String, @NotNull version: String): Code {
        return codeDAO["$type|$code|$version"]
    }

    override fun get(ids: List<String>): List<Code> {
        return codeDAO.getList(ids)
    }

    override fun create(code: Code): Code {
        Preconditions.checkNotNull(code.code, "Code field is null.")
        Preconditions.checkNotNull(code.type, "Type field is null.")
        Preconditions.checkNotNull(code.version, "Version code field is null.")

        // assinging Code id type|code|version
        code.id = code.type + "|" + code.code + "|" + code.version

        return codeDAO.create(code)
    }

    @Throws(Exception::class)
    override fun modify(code: Code): Code {
        val existingCode = codeDAO[code.id]

        Preconditions.checkState(existingCode.code == code.code, "Modification failed. Code field is immutable.")
        Preconditions.checkState(existingCode.type == code.type, "Modification failed. Type field is immutable.")
        Preconditions.checkState(existingCode.version == code.version, "Modification failed. Version field is immutable.")

        updateEntities(setOf(code))

        return this.get(code.id)
    }

    override fun findCodeTypes(type: String?): List<String> {
        return codeDAO.findCodeTypes(type)
    }

    override fun findCodeTypes(region: String?, type: String?): List<String> {
        return codeDAO.findCodeTypes(region, type)
    }

    override fun findCodesBy(type: String?, code: String?, version: String?): List<Code> {
        return codeDAO.findCodes(type, code, version)
    }

    override fun findCodesBy(region: String?, type: String?, code: String?, version: String?): List<Code> {
        return codeDAO.findCodes(region, type, code, version)
    }

    override fun findCodesBy(region: String?, type: String?, code: String?, version: String?, paginationOffset: PaginationOffset<*>): PaginatedList<Code> {
        return codeDAO.findCodes(region, type, code, version, paginationOffset)
    }

    override fun findCodesByLabel(region: String?, language: String?, label: String?, paginationOffset: PaginationOffset<*>): PaginatedList<Code> {
        return codeDAO.findCodesByLabel(region, language, label, paginationOffset)
    }

    override fun findCodesByLabel(region: String?, language: String?, type: String?, label: String?, paginationOffset: PaginationOffset<*>): PaginatedList<Code> {
        return codeDAO.findCodesByLabel(region, language, type, label, paginationOffset)
    }

    override fun <T : Enum<*>> importCodesFromEnum(e: Class<T>) {
		/* TODO: rewrite this */
        val version = "" + e.getAnnotation(EnumVersion::class.java).value

        val regions = getRegions().toSet()
        val codes = HashMap<String, Code>()
        findCodesBy(e.name, null, null).stream().filter { c -> c.version == version }.forEach { c -> codes.put(c.id, c) }

        try {
            for (t in e.getMethod("values").invoke(null) as Array<T>) {
                val newCode = Code(regions, e.name, t.name, version, ImmutableMap.of("en", t.name.replace("_".toRegex(), " ")))
                if (!codes.values.contains(newCode)) {
                    if (!codes.containsKey(newCode.id)) {
                        create(newCode)
                    } else {
                        newCode.rev = codes[newCode.id]!!.rev
                        if (codes[newCode.id] != newCode) {
                            try {
                                modify(newCode)
                            } catch (ex2: Exception) {
                                logger.info("Could not create code " + e.name, ex2)
                            }
                        }
                    }
                }
            }
        } catch (ex: IllegalAccessException) {
            throw IllegalStateException(ex)
        } catch (ex: InvocationTargetException) {
            throw IllegalStateException(ex)
        } catch (ex: NoSuchMethodException) {
            throw IllegalStateException(ex)
        }

    }

    override fun getOrCreateCode(type: String, code: String): Code {
        val codes = findCodesBy(type, code, null)

        if (codes.isNotEmpty()) {
            return codes.stream().sorted { a, b -> b.version.compareTo(a.version) }.findFirst().get()
        }

        return this.create(Code(type, code, "1.0"))
    }

	override fun ensureValid(code: Code, ofType: String?, orDefault: Code?): Code {
		return codeDAO.ensureValid(code, ofType, orDefault)
	}

	override fun isValid(code: Code, ofType: String?): Boolean {
		return codeDAO.isValid(code, ofType)
	}

	override fun getCodeByLabel(label: String, ofType: String, labelLang: List<String>): Code {
		return codeDAO.getCodeByLabel(label, ofType, labelLang)
	}

    override fun getGenericDAO(): CodeDAO? {
        return codeDAO
    }

    companion object {
        private val logger = LoggerFactory.getLogger(CodeLogicImpl::class.java)
    }
}
