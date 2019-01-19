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
import org.apache.commons.beanutils.PropertyUtilsBean
import org.apache.commons.lang3.ObjectUtils
import org.jetbrains.annotations.NotNull
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.icure.dao.CodeDAO
import org.taktik.icure.db.PaginatedDocumentKeyIdPair
import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.dto.filter.chain.FilterChain
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.base.EnumVersion
import org.taktik.icure.logic.CodeLogic
import java.lang.reflect.InvocationTargetException
import java.util.*
import java.util.stream.Collectors
import javax.security.auth.login.LoginException

@Service
class CodeLogicImpl(val codeDAO: CodeDAO, val filters: org.taktik.icure.logic.impl.filter.Filters) : GenericLogicImpl<Code, CodeDAO>(), CodeLogic {
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

    override fun listCodeIdsByLabel(region: String?, language: String?, type: String?, label: String?): List<String> {
        return codeDAO.listCodeIdsByLabel(region, language, type, label)
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

    override fun listCodes(paginationOffset: PaginationOffset<*>?, filterChain: FilterChain<Patient>, sort: String?, desc: Boolean?): PaginatedList<Code> {
        var ids: SortedSet<String> = TreeSet<String>(filters.resolve(filterChain.filter))
        if (filterChain.predicate != null || sort != null && sort != "id") {
            var codes = this.get(ArrayList(ids))
            if (filterChain.predicate != null) {
                codes = codes.filter { it -> filterChain.predicate.apply(it) }
            }
            val pub = PropertyUtilsBean()

            codes.sortedBy { it -> try { pub.getProperty(it, sort ?: "id") as? String } catch(e:Exception) {""} ?: "" }

            var firstIndex = if (paginationOffset != null && paginationOffset.startDocumentId != null) codes.map { it -> it.id }.indexOf(paginationOffset.startDocumentId) else 0
            if (firstIndex == -1) {
                return PaginatedList(0, ids.size, ArrayList(), null)
            } else {
                firstIndex += if (paginationOffset != null && paginationOffset.offset != null) paginationOffset.offset else 0
                val hasNextPage = paginationOffset != null && paginationOffset.limit != null && firstIndex + paginationOffset.limit!! < codes.size
                return if (hasNextPage)
                    PaginatedList(paginationOffset!!.limit!!, codes.size, codes.subList(firstIndex, firstIndex + paginationOffset.limit!!),
                                  PaginatedDocumentKeyIdPair(null, codes[firstIndex + paginationOffset.limit!!].id))
                else
                    PaginatedList(codes.size - firstIndex, codes.size, codes.subList(firstIndex, codes.size), null)
            }
        } else {
            if (desc != null && desc) {
                ids = (ids as TreeSet<String>).descendingSet()
            }
            if (paginationOffset != null && paginationOffset.startDocumentId != null) {
                ids = ids.subSet(paginationOffset.startDocumentId, (ids as TreeSet<*>).last().toString() + "\u0000")
            }
            var idsList: List<String> = ArrayList(ids)
            if (paginationOffset != null && paginationOffset.offset != null) {
                idsList = idsList.subList(paginationOffset.offset!!, idsList.size)
            }
            val hasNextPage = paginationOffset != null && paginationOffset.limit != null && paginationOffset.limit < idsList.size
            if (hasNextPage) {
                idsList = idsList.subList(0, paginationOffset!!.limit!! + 1)
            }
            val codes = this.get(idsList)
            return PaginatedList(if (hasNextPage) paginationOffset!!.limit else codes.size, ids.size, if (hasNextPage) codes.subList(0, paginationOffset!!.limit!!) else codes, if (hasNextPage) PaginatedDocumentKeyIdPair(null, codes[codes.size - 1].id) else null)
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
