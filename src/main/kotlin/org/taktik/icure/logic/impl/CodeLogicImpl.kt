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
import org.apache.commons.logging.LogFactory
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
import org.taktik.icure.entities.base.LinkQualification
import org.taktik.icure.exceptions.BulkUpdateConflictException
import org.taktik.icure.logic.CodeLogic
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.io.InputStream
import java.lang.reflect.InvocationTargetException
import java.util.*
import javax.xml.parsers.SAXParserFactory
import kotlin.collections.HashMap

@Service
class CodeLogicImpl(val codeDAO: CodeDAO, val filters: org.taktik.icure.logic.impl.filter.Filters) : GenericLogicImpl<Code, CodeDAO>(), CodeLogic {
    val log = LogFactory.getLog(this.javaClass)

    override fun getTagTypeCandidates(): List<String> {
        return Arrays.asList("CD-ITEM", "CD-PARAMETER", "CD-CAREPATH", "CD-SEVERITY", "CD-URGENCY", "CD-GYNECOLOGY")
    }

    override fun getRegions(): List<String> {
        return Arrays.asList("fr", "be")
    }

    override fun get(id: String): Code? {
        return codeDAO[id]
    }

    override fun get(@NotNull type: String, @NotNull code: String, @NotNull version: String): Code? {
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

        return this.get(code.id)!!
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

    override fun findCodesByQualifiedLinkId(linkType: String, linkedId: String?, pagination: PaginationOffset<*>?): PaginatedList<Code> =
            codeDAO.findCodesByQualifiedLinkId(linkType, linkedId, pagination)

    override fun listCodeIdsByQualifiedLinkId(linkType: String, linkedId: String?): List<String> =
            codeDAO.listCodeIdsByQualifiedLinkId(linkType, linkedId)


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

    override fun importCodesFromXml(md5: String, type: String, stream: InputStream) {
        val check = get(listOf(Code("ICURE-SYSTEM", md5, "1").id))

        if (check.isEmpty()) {
            val factory = SAXParserFactory.newInstance()
            val saxParser = factory.newSAXParser()

            val stack = LinkedList<Code>()

            val batchSave : (Code?, Boolean?) -> Unit = { c, flush ->
                c?.let { stack.add(it) }
                if (stack.size == 100 || flush == true) {
                    val existings = get(stack.mapNotNull { it.id }).fold(HashMap<String, Code>()) { map, c -> map[c.id] = c; map }
                    try {
                        codeDAO.save(stack.map { c ->
                            val prev = existings[c.id]
                            prev?.let { c.rev = it.rev }
                            c
                        })
                    } catch (e:BulkUpdateConflictException) {
                        log.error("${e.conflicts.size} conflicts for type $type")
                    }
                    stack.clear()
                }
            }

            val handler = object : DefaultHandler() {
                var initialized = false
                var version: String? = null
                var charsHandler: ((chars: String) -> Unit)? = null
                var code: Code? = null
                var characters: String = ""

                override fun characters(ch: CharArray?, start: Int, length: Int) {
                    ch?.let { characters += String(it, start, length) }
                }

                override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
                    if (!initialized && qName != "kmehr-cd") {
                        throw IllegalArgumentException("Not supported")
                    }
                    initialized = true
                    characters = ""
                    qName?.let {
                        when (it.toUpperCase()) {
                            "VERSION" -> charsHandler = {
                                version = it
                            }
                            "VALUE" -> {
                                code = Code(type, null, version).apply { label = HashMap() }
                            }
                            "CODE" -> charsHandler = { code?.code = it }
                            "PARENT" -> charsHandler = { code?.qualifiedLinks = mapOf(pair = Pair(LinkQualification.parent, listOf("$type|$it|$version"))) }
                            "DESCRIPTION" -> charsHandler = { code?.label?.put(attributes?.getValue("L"), it) }
                            else -> {
                                charsHandler = null
                            }
                        }
                    }
                }

                override fun endElement(uri: String?, localName: String?, qName: String?) {
                    charsHandler?.let { it(characters) }
                    qName?.let {
                        when (it.toUpperCase()) {
                            "VALUE" -> {
                                batchSave(code, false)
                            }
                            else -> null
                        }
                    }
                }
            }

            val beThesaurusProcHandler = object : DefaultHandler() {
                var initialized = false
                var version: String? = null
                var charsHandler: ((chars: String) -> Unit)? = null
                var code: Code? = null
                var characters: String = ""

                override fun characters(ch: CharArray?, start: Int, length: Int) {
                    ch?.let { characters += String(it, start, length) }
                }

                override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
                    if (!initialized && qName != "Root") {
                        throw IllegalArgumentException("XML not supported : $type")
                    }
                    if (!initialized) {
                        version = attributes?.getValue("version")?:
                            throw IllegalArgumentException("Unknown version in : $type")
                    }

                    initialized = true
                    characters = ""
                    qName?.let {
                        when (it.toUpperCase()) {
                            "PROCEDURE" -> {
                                code = Code(type, null, version).apply {
                                    label = HashMap()
                                    searchTerms = HashMap()
                                }
                            }
                            "CISP" -> charsHandler = { ch -> code?.code = ch }
                            "IBUI" -> charsHandler = { ch ->
                                if(ch.isNotBlank()) code?.links = listOf("BE-THESAURUS|$ch|$version")
                            }
                            "IBUI_NOT_EXACT" -> charsHandler = { ch ->
                                if(ch.isNotBlank() && code?.links.isNullOrEmpty())
                                    code?.links = listOf("BE-THESAURUS|$ch|$version")
                            }
                            "LABEL_FR" -> charsHandler = { ch -> if(ch.isNotBlank()) code?.label?.put("fr", ch) }
                            "LABEL_NL" -> charsHandler = { ch -> if(ch.isNotBlank()) code?.label?.put("nl", ch) }
                            "SYN_FR" -> charsHandler = { ch ->
                                if(ch.isNotBlank()) {
                                    code?.searchTerms?.put("fr", ch.split(";").map { it.trim() }.toSet())
                                }
                            }
                            "SYN_NL" -> charsHandler = { ch ->
                                if(ch.isNotBlank()) {
                                    code?.searchTerms?.put("nl", ch.split(";").map { it.trim() }.toSet())
                                }
                            }
                            else -> charsHandler = null
                        }
                    }
                }

                override fun endElement(uri: String?, localName: String?, qName: String?) {
                    charsHandler?.let { it(characters) }
                    qName?.let {
                        when (it.toUpperCase()) {
                            "PROCEDURE" -> {
                                batchSave(code, false)
                            }
                            else -> null
                        }
                    }
                }
            }

            val beThesaurusHandler = object : DefaultHandler() {
                var initialized = false
                var version: String? = null
                var charsHandler: ((chars: String) -> Unit)? = null
                var code: Code? = null
                var characters: String = ""

                override fun characters(ch: CharArray?, start: Int, length: Int) {
                    ch?.let { characters += String(it, start, length) }
                }

                override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
                    if (!initialized && qName != "Root") {
                        throw IllegalArgumentException("XML not supported : $type")
                    }
                    if (!initialized) {
                        version = attributes?.getValue("version")?:
                                throw IllegalArgumentException("Unknown version in : $type")
                    }

                    initialized = true
                    characters = ""
                    qName?.let {
                        when (it.toUpperCase()) {
                            "CLINICAL_LABEL" -> {
                                code = Code(type, null, version).apply {
                                    label = HashMap()
                                    searchTerms = HashMap()
                                    links = mutableListOf()
                                }
                            }
                            "IBUI" -> charsHandler = { ch -> code?.code = ch }
                            "ICPC_2_CODE_1", "ICPC_2_CODE_1X", "ICPC_2_CODE_1Y",
                            "ICPC_2_CODE_2", "ICPC_2_CODE_2X", "ICPC_2_CODE_2Y" -> charsHandler = { ch ->
                                if(ch.isNotBlank()) code?.links?.add("ICPC|$ch|2")
                            }
                            "ICD_10_CODE_1", "ICD_10_CODE_1X", "ICD_10_CODE_1Y",
                            "ICD_10_CODE_2", "ICD_10_CODE_2X", "ICD_10_CODE_2Y" -> charsHandler = { ch ->
                                if(ch.isNotBlank()) code?.links?.add("ICD|$ch|10")
                            }
                            "FR_CLINICAL_LABEL" -> charsHandler = { ch ->
                                if(ch.isNotBlank()) code?.label?.put("fr", ch.replace("&apos;","'")) }
                            "NL_CLINICAL_LABEL" -> charsHandler = { ch -> if(ch.isNotBlank()) code?.label?.put("nl", ch) }
                            "CLEFS_RECHERCHE_FR" -> charsHandler = { ch ->
                                if(ch.isNotBlank()) {
                                    code?.searchTerms?.put("fr", ch.split(" ").map { it.trim() }.toSet())
                                }
                            }
                            "ZOEKTERMEN_NL" -> charsHandler = { ch ->
                                if(ch.isNotBlank()) {
                                    code?.searchTerms?.put("nl", ch.split(" ").map { it.trim() }.toSet())
                                }
                            }
                            else -> charsHandler = null
                        }
                    }
                }

                override fun endElement(uri: String?, localName: String?, qName: String?) {
                    charsHandler?.let { it(characters) }
                    qName?.let {
                        when (it.toUpperCase()) {
                            "CLINICAL_LABEL" -> {
                                batchSave(code, false)
                            }
                            else -> null
                        }
                    }
                }
            }

            try {
                when (type.toUpperCase()) {
                    "BE-THESAURUS-PROCEDURES" -> saxParser.parse(stream, beThesaurusProcHandler)
                    "BE-THESAURUS" -> saxParser.parse(stream, beThesaurusHandler)
                    else -> saxParser.parse(stream, handler)
                }
                batchSave(null, true)
                create(Code("ICURE-SYSTEM", md5, "1"))
            } catch(e:IllegalArgumentException) {
                //Skip
            } finally {
                stream.close()
            }
        } else {
            stream.close()
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


    override fun getOrCreateCode(type: String, code: String, version: String): Code {
        val codes = findCodesBy(type, code, null)

        if (codes.isNotEmpty()) {
            return codes.stream().sorted { a, b -> b.version.compareTo(a.version) }.findFirst().get()
        }

        return this.create(Code(type, code, version))
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
