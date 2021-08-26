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

package org.taktik.icure.asynclogic.impl


import com.google.common.collect.ImmutableMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.apache.commons.beanutils.PropertyUtilsBean
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Service
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.ViewRowWithDoc
import org.taktik.icure.asyncdao.CodeDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.CodeLogic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.base.EnumVersion
import org.taktik.icure.entities.base.LinkQualification
import org.taktik.icure.exceptions.BulkUpdateConflictException
import org.taktik.icure.utils.invoke
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.io.InputStream
import java.lang.reflect.InvocationTargetException
import java.util.*
import javax.xml.parsers.SAXParserFactory
import kotlin.collections.HashMap

@ExperimentalCoroutinesApi
@Service
class CodeLogicImpl(private val sessionLogic: AsyncSessionLogic, val codeDAO: CodeDAO, val filters: org.taktik.icure.asynclogic.impl.filter.Filters) : GenericLogicImpl<Code, CodeDAO>(sessionLogic), CodeLogic {
    companion object {
        private val log = LogFactory.getLog(this.javaClass)
    }

    override fun getTagTypeCandidates(): List<String> {
        return listOf("CD-ITEM", "CD-PARAMETER", "CD-CAREPATH", "CD-SEVERITY", "CD-URGENCY", "CD-GYNECOLOGY")
    }

    override fun getRegions(): List<String> {
        return listOf("fr", "be")
    }

    override suspend fun get(id: String): Code? {
        return getEntity(id)
    }

    override suspend fun get(type: String, code: String, version: String): Code? {
        return codeDAO.get("$type|$code|$version")
    }

    override fun get(ids: List<String>) = flow<Code> {
        emitAll(codeDAO.getList(ids))
    }

    override suspend fun create(code: Code) = fix(code) { code: Code ->
        code.code ?: error("Code field is null")
        code.type ?: error("Type field is null")
        code.version ?: error("Version field is null")

        codeDAO.create(code.copy(id = code.type + "|" + code.code + "|" + code.version))
    }

    @Throws(Exception::class)
    override suspend fun modify(code: Code) = fix(code) { code ->
        updateEntities(setOf(code)).firstOrNull()
    }

    override fun findCodeTypes(type: String?) = flow<String> {
        emitAll(codeDAO.findCodeTypes(type))
    }

    override fun findCodeTypes(region: String?, type: String?) = flow<String> {
        emitAll(codeDAO.findCodeTypes(region, type))
    }

    override fun findCodesBy(type: String?, code: String?, version: String?) = flow<Code> {
        emitAll(codeDAO.findCodes(type, code, version))
    }

    override fun findCodesBy(region: String?, type: String?, code: String?, version: String?) = flow<Code> {
        emitAll(codeDAO.findCodes(region, type, code, version))
    }

    override fun findCodesBy(region: String?, type: String?, code: String?, version: String?, paginationOffset: PaginationOffset<List<String?>>) = flow<ViewQueryResultEvent> {
        emitAll(codeDAO.findCodes(region, type, code, version, paginationOffset))
    }

    override fun findCodesByLabel(region: String?, language: String?, label: String?, paginationOffset: PaginationOffset<List<String?>>) = flow<ViewQueryResultEvent> {
        emitAll(codeDAO.findCodesByLabel(region, language, label, paginationOffset))
    }

    override fun findCodesByLabel(region: String?, language: String?, type: String?, label: String?, paginationOffset: PaginationOffset<List<String?>>) = flow<ViewQueryResultEvent> {
        emitAll(codeDAO.findCodesByLabel(region, language, type, label, paginationOffset))
    }

    override fun listCodeIdsByLabel(region: String?, language: String?, type: String?, label: String?) = flow<String> {
        emitAll(codeDAO.listCodeIdsByLabel(region, language, type, label))
    }

    override fun findCodesByQualifiedLinkId(region: String?, linkType: String, linkedId: String, pagination: PaginationOffset<List<String>>) = flow<ViewQueryResultEvent> {
        emitAll(codeDAO.findCodesByQualifiedLinkId(region, linkType, linkedId, pagination))
    }

    override fun listCodeIdsByQualifiedLinkId(linkType: String, linkedId: String?) = flow<String> {
        emitAll(codeDAO.listCodeIdsByQualifiedLinkId(linkType, linkedId))
    }


    override suspend fun <T : Enum<*>> importCodesFromEnum(e: Class<T>) {
        /* TODO: rewrite this */
        val version = "" + e.getAnnotation(EnumVersion::class.java).value

        val regions = getRegions().toSet()
        val codes = HashMap<String, Code>()
        findCodesBy(e.name, null, null).filter { c -> c.version == version }.onEach { c -> codes[c.id] = c }.collect()

        try {
            for (t in e.getMethod("values").invoke(null) as Array<T>) {
                val newCode = Code.from(e.name, t.name, version).copy(regions = regions, label = ImmutableMap.of("en", t.name.replace("_".toRegex(), " ")))
                if (!codes.values.contains(newCode)) {
                    if (!codes.containsKey(newCode.id)) {
                        create(newCode)
                    } else {
                        val modCode = newCode.copy(rev = codes[newCode.id]!!.rev)
                        if (codes[newCode.id] != modCode) {
                            try {
                                modify(modCode)
                            } catch (ex2: Exception) {
                                log.info("Could not create code " + e.name, ex2)
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

    override suspend fun importCodesFromXml(md5: String, type: String, stream: InputStream) {
        val check = get(listOf(Code.from("ICURE-SYSTEM", md5, version = "1").id)).toList()

        if (check.isEmpty()) {
            val factory = SAXParserFactory.newInstance();
            val saxParser = factory.newSAXParser();

            val stack = LinkedList<Code>()

            val batchSave: suspend (Code?, Boolean?) -> Unit = { c, flush ->
                c?.let { stack.add(it) }
                if (stack.size == 100 || flush == true) {
                    val existings = get(stack.map { it.id }).fold(HashMap<String, Code>()) { map, c -> map[c.id] = c; map }
                    try {
                        codeDAO.save(stack.map { xc ->
                            existings[xc.id]?.let { xc.copy(rev = it.rev) } ?: xc
                        })
                    } catch (e: BulkUpdateConflictException) {
                        log.error("${e.conflicts.size} conflicts for type $type")
                    }
                    stack.clear()
                }
            }

            val handler = object : DefaultHandler() {
                var initialized = false
                var version: String = "1.0"
                var charsHandler: ((chars: String) -> Unit)? = null
                var code: MutableMap<String, Any> = mutableMapOf()
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
                                code = mutableMapOf("type" to type, "version" to version, "label" to mapOf<String,String>())
                            }
                            "CODE" -> charsHandler = { code["code"] = it }
                            "PARENT" -> charsHandler = { code["qualifiedLinks"] = mapOf(LinkQualification.parent to listOf("$type|$it|$version")) }
                            "DESCRIPTION" -> charsHandler = { attributes?.getValue("L")?.let { attributesValue -> code["label"] = (code["label"] as Map<String,String>) + (attributesValue to it) } }
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
                                runBlocking {
                                    code["id"] = "${code["type"] as String}|${code["code"] as String}|${code["version"] as String}"
                                    batchSave(Code(args = code), false)
                                }
                            }
                            else -> null
                        }
                    }

                }
            }
            try {
                saxParser.parse(stream, handler)
                batchSave(null, true)
                create(Code.from("ICURE-SYSTEM", md5, "1"))
            } catch (e: IllegalArgumentException) {
                //Skip
            } finally {
                stream.close()
            }
        } else {
            stream.close()
        }
    }

    override fun listCodes(paginationOffset: PaginationOffset<*>?, filterChain: FilterChain<Code>, sort: String?, desc: Boolean?) = flow<ViewQueryResultEvent> {
        var ids = filters.resolve(filterChain.filter).toList().sorted()
        var codes = codeDAO.getForPagination(ids)
        if (filterChain.predicate != null || sort != null && sort != "id") {
            filterChain.predicate?.let {
                codes.filter {
                    if (it is ViewRowWithDoc<*, *, *>) {
                        val code = it.doc as Code
                        filterChain.predicate.apply(code)
                    }else{
                        true
                    }
                }
            }

            sort?.let { sortProperty ->
                val pub = PropertyUtilsBean()
                var codesList = codes.toList()
                codesList = codesList.mapNotNull {
                    if(it is ViewRowWithDoc<*, *, *>){
                        it
                    }else{
                        emit(it)
                        null
                    }
                }.toList().sortedBy { it ->
                    val itCode = it.doc as Code
                    try { pub.getProperty(itCode, sort) as? String } catch(e:Exception) {""} ?: ""
                }
                emitAll(codesList.asFlow())
            }?: emitAll(codes)
        } else {
            emitAll(codes)
        }
    }


    override suspend fun getOrCreateCode(type: String, code: String, version: String): Code? {
        val codes = findCodesBy(type, code, null).toList()
        if (codes.isNotEmpty()) {
            return codes.stream().sorted { a, b -> a.version?.let { b.version?.compareTo(it) }!! }.findFirst().get()
        }

        return this.create(Code.from(type, code, version))
    }

    override suspend fun isValid(code: Code, ofType: String?): Boolean {
        val codeType = ofType ?: code.type
        return if (codeType != null && code.code != null) codeDAO.isValid(codeType, code.code, code.version) else false
    }

    override suspend fun isValid(code: CodeStub, ofType: String?): Boolean {
        val codeType = ofType ?: code.type
        return if (codeType != null && code.code != null) codeDAO.isValid(codeType, code.code, code.version) else false
    }

    override suspend fun getCodeByLabel(region: String, label: String, ofType: String, labelLang: List<String>): Code? {
        return codeDAO.getCodeByLabel(region, label, ofType, labelLang)
    }

    override fun getGenericDAO(): CodeDAO {
        return codeDAO
    }

}
