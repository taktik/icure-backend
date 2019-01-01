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

package org.taktik.icure.dao.impl

import org.ektorp.ComplexKey
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Repository
import org.taktik.icure.dao.CodeDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector
import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.db.StringUtils
import org.taktik.icure.entities.base.Code
import java.util.stream.Collectors

@Repository("codeDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.base.Code' && !doc.deleted) emit( null, doc._id )}")
class CodeDAOImpl @Autowired
constructor(@Qualifier("couchdbBase") couchdb: CouchDbICureConnector, idGenerator: IDGenerator, @Qualifier("entitiesCacheManager") cacheManager: CacheManager) : CachedDAOImpl<Code>(Code::class.java, couchdb, idGenerator, cacheManager), CodeDAO {
    init {
        initStandardDesignDocument()
    }

    @View(name = "by_type_code_version", map = "classpath:js/code/By_type_code_version.js", reduce = "function(keys, values, rereduce) {if (rereduce) {return sum(values);} else {return values.length;}}")
    override fun findCodes(type: String?, code: String?, version: String?): List<Code> {
        val result = queryResults(
                createQuery("by_type_code_version")
                        .includeDocs(true)
                        .reduce(false)
                        .startKey(ComplexKey.of(
                                type,
                                code,
                                version
                        ))
                        .endKey(ComplexKey.of(
                                type ?: ComplexKey.emptyObject(),
                                code ?: ComplexKey.emptyObject(),
                                version ?: ComplexKey.emptyObject()
                        )))

        return result
    }

    override fun findCodeTypes(type: String?): List<String> {
        val result = (db as CouchDbICureConnector).queryViewWithKeys(
                createQuery("by_type_code_version")
                        .includeDocs(false)
                        .group(true)
                        .groupLevel(2)
                        .startKey(ComplexKey.of(type, null, null))
                        .endKey(ComplexKey.of(if (type == null) null else type + "\ufff0", null, null)),
                String::class.java)

        return result.stream().map { ckv -> ckv.key.toJson().get(1).asText() }.collect(Collectors.toList<String>())
    }

    @View(name = "by_region_type_code_version", map = "classpath:js/code/By_region_type_code_version.js", reduce = "function(keys, values, rereduce) {if (rereduce) {return sum(values);} else {return values.length;}}")
    override fun findCodes(region: String?, type: String?, code: String?, version: String?): List<Code> {
        val result = queryResults(
                createQuery("by_region_type_code_version")
                        .includeDocs(true)
                        .reduce(false)
                        .startKey(ComplexKey.of(
                                region ?: "\u0000",
                                type ?: "\u0000",
                                code ?: "\u0000",
                                version ?: "\u0000"
                        ))
                        .endKey(ComplexKey.of(
                                region ?: ComplexKey.emptyObject(),
                                type ?: ComplexKey.emptyObject(),
                                code ?: ComplexKey.emptyObject(),
                                version ?: ComplexKey.emptyObject()
                        )))

        return result
    }

    override fun findCodeTypes(region: String?, type: String?): List<String> {
        //Not transactional aware
        val result = (db as CouchDbICureConnector).queryViewWithKeys(
                createQuery("by_region_type_code_version")
                        .includeDocs(false)
                        .group(true)
                        .groupLevel(2)
                        .startKey(ComplexKey.of(region, type ?: "", null, null))
                        .endKey(ComplexKey.of(region, if (type == null) ComplexKey.emptyObject() else type + "\ufff0", null, null)),
                String::class.java)

        return result.stream().map { ckv -> ckv.key.toJson().get(1).asText() }.collect(Collectors.toList<String>())
    }

    override fun findCodes(region: String?, type: String?, code: String?, version: String?, paginationOffset: PaginationOffset<*>): PaginatedList<Code> {
        val from = if (paginationOffset.startKey == null)
            ComplexKey.of(region, type, code, version)
        else
            ComplexKey.of(*(paginationOffset.startKey as List<*>).toTypedArray())
        val to = ComplexKey.of(
                region ?: ComplexKey.emptyObject(),
                type ?: ComplexKey.emptyObject(),
                if (code == null) ComplexKey.emptyObject() else code + "\ufff0",
                if (version == null) ComplexKey.emptyObject() else version + "\ufff0"
        )

        return pagedQueryView(
            "by_region_type_code_version",
            from,
            to,
            paginationOffset,
            false
                             )
    }

    @View(name = "by_language_label", map = "classpath:js/code/By_language_label.js")
    override fun findCodesByLabel(region: String?, language: String?, label: String?, pagination: PaginationOffset<*>?): PaginatedList<Code> {
        var label1 = label
        label1 = if (label1 != null) StringUtils.sanitizeString(label1) else null
        val startKey = if (pagination == null) null else pagination.startKey as MutableList<Any?>
        if (startKey != null && startKey.size > 2 && startKey[2] != null) {
			startKey[2] = StringUtils.sanitizeString(startKey[2] as String)
        }
        val from = if (startKey == null)
            ComplexKey.of(
                    region ?: "\u0000",
                    language ?: "\u0000",
                    if (label1 == null) "\u0000" else label1
            )
        else
            ComplexKey.of(*startKey.toTypedArray())
        val to = ComplexKey.of(
                if (region == null) ComplexKey.emptyObject() else if (language == null) region + "\ufff0" else region,
                if (language == null) ComplexKey.emptyObject() else if (label1 == null) language + "\ufff0" else language,
                if (label1 == null) ComplexKey.emptyObject() else label1 + "\ufff0"
        )

        return pagedQueryView(
            "by_language_label",
            from,
            to,
            pagination,
            false
                             )
    }

    @View(name = "by_language_type_label", map = "classpath:js/code/By_language_type_label.js")
    override fun findCodesByLabel(region: String?, language: String?, type: String?, label: String?, pagination: PaginationOffset<*>?): PaginatedList<Code> {
        var label1 = label
        label1 = if (label1 != null) StringUtils.sanitizeString(label1) else null
        val startKey = if (pagination == null || pagination.startKey == null) null else pagination.startKey as MutableList<Any?>
        if (startKey != null && startKey.size > 3 && startKey[3] != null) {
			startKey[3] = StringUtils.sanitizeString(startKey[3] as String)
        }
        val from = if (startKey == null)
            ComplexKey.of(
                    region ?: "\u0000",
                    language ?: "\u0000",
                    type ?: "\u0000",
                    if (label1 == null) "\u0000" else label1
            )
        else
            ComplexKey.of(*startKey.toTypedArray())
        val to = ComplexKey.of(
			if (region == null) ComplexKey.emptyObject() else if (language == null) region + "\ufff0" else region,
			language ?: ComplexKey.emptyObject(),
			type ?: ComplexKey.emptyObject(),
			if (label1 == null) ComplexKey.emptyObject() else label1 + "\ufff0"
		)

        return pagedQueryView(
            "by_language_type_label",
            from,
            to,
            pagination,
            false
                             )
    }

	override fun ensureValid(code : Code, ofType : String?, orDefault : Code?) : Code {
		if (ofType != null && code.type != ofType) {
			return orDefault ?: throw IllegalArgumentException("code ($code) has not the expected type $ofType")
		}
		if (!isValid(code, ofType)) {
			return orDefault ?: throw IllegalArgumentException("code ($code) is invalid")
		}
		return code
	}

	override fun isValid(code: Code, ofType: String?) = findCodes(ofType ?: code.type, code.code, code.version).isNotEmpty()

	override fun getCodeByLabel(label: String, ofType: String, labelLang : List<String>) : Code {
		val cleanLabel = label.toLowerCase().replace("^\\s+".toRegex(), "").replace("\\s+$".toRegex(), "")
		for (lang in labelLang) {
			val langLabelMap : Map<String?, Code> = findCodesByLabel("be", lang, ofType, label, PaginationOffset<Code>()).rows.associateBy({ it.label[lang]?.toLowerCase() }, { it })
			if (langLabelMap.containsKey(cleanLabel)) {
				return langLabelMap[cleanLabel]!!
			}
		}
		throw IllegalArgumentException("code of type $ofType not found for label $label in languages $labelLang")
	}
}
