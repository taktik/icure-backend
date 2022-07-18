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

package org.taktik.icure.asyncdao.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.transform
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.Offset
import org.taktik.couchdb.TotalCount
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.ViewRowWithDoc
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.queryView
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.CodeDAO
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.db.StringUtils
import org.taktik.icure.entities.base.Code
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.spring.asynccache.AsyncCacheManager

data class QueryResultAccumulator(
	val seenElements: Int = 0,
	val sentElements: Int = 0,
	val elementsFound: Int? = null,
	val offset: Int? = null,
	val toEmit: ViewQueryResultEvent? = null,
	val lastVisited: ViewRowWithDoc<*, *, *>? = null
)

@Repository("codeDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.base.Code' && !doc.deleted) emit( null, doc._id )}")
class CodeDAOImpl(
	couchDbProperties: CouchDbProperties,
	@Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher,
	idGenerator: IDGenerator,
	@Qualifier("asyncCacheManager") asyncCacheManager: AsyncCacheManager
) : CachedDAOImpl<Code>(Code::class.java, couchDbProperties, couchDbDispatcher, idGenerator, asyncCacheManager), CodeDAO {

	@View(name = "by_type_code_version", map = "classpath:js/code/By_type_code_version.js", reduce = "_count")
	override fun listCodesBy(type: String?, code: String?, version: String?): Flow<Code> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		emitAll(
			client.queryViewIncludeDocsNoValue<Array<String>, Code>(
				createQuery(client, "by_type_code_version")
					.includeDocs(true)
					.reduce(false)
					.startKey(
						ComplexKey.of(
							type,
							code,
							version
						)
					)
					.endKey(
						ComplexKey.of(
							type ?: ComplexKey.emptyObject(),
							code ?: ComplexKey.emptyObject(),
							version ?: ComplexKey.emptyObject()
						)
					)
			).map { it.doc }

		)
	}

	override fun listCodesByType(type: String?): Flow<String> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		emitAll(
			client.queryView<String, String>(
				createQuery(client, "by_type_code_version")
					.includeDocs(false)
					.group(true)
					.groupLevel(2)
					.startKey(ComplexKey.of(type, null, null))
					.endKey(ComplexKey.of(if (type == null) null else type + "\ufff0", null, null))
			).mapNotNull { it.key }
		)
	}

	@View(name = "by_region_type_code_version", map = "classpath:js/code/By_region_type_code_version.js", reduce = "_count")
	override fun listCodesBy(region: String?, type: String?, code: String?, version: String?): Flow<Code> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		emitAll(
			client.queryViewIncludeDocsNoValue<Array<String>, Code>(
				createQuery(client, "by_region_type_code_version")
					.includeDocs(true)
					.reduce(false)
					.startKey(
						ComplexKey.of(
							region ?: "\u0000",
							type ?: "\u0000",
							code ?: "\u0000",
							version ?: "\u0000"
						)
					)
					.endKey(
						ComplexKey.of(
							region ?: ComplexKey.emptyObject(),
							type ?: ComplexKey.emptyObject(),
							code ?: ComplexKey.emptyObject(),
							version ?: ComplexKey.emptyObject()
						)
					)
			).map { it.doc }

		)
	}

	override fun listCodesByRegionAndType(region: String?, type: String?): Flow<String> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		emitAll(
			client.queryView<List<String>, String>(
				createQuery(client, "by_region_type_code_version")
					.includeDocs(false)
					.group(true)
					.groupLevel(2)
					.startKey(ComplexKey.of(region, type ?: "", null, null))
					.endKey(ComplexKey.of(region, if (type == null) ComplexKey.emptyObject() else type + "\ufff0", null, null))
			).mapNotNull { it.key?.get(1) }
		)
	}

	@ExperimentalCoroutinesApi
	@FlowPreview
	override fun findCodesBy(region: String?, type: String?, code: String?, version: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val from = ComplexKey.of(region, type, code, version)
		val to = ComplexKey.of(
			region ?: ComplexKey.emptyObject(),
			type ?: ComplexKey.emptyObject(),
			if (code == null) ComplexKey.emptyObject() else code + "\ufff0",
			if (version == null) ComplexKey.emptyObject() else version + "\ufff0"
		)

		val viewQuery = pagedViewQuery<Code, ComplexKey>(
			client,
			"by_region_type_code_version",
			from,
			to,
			paginationOffset.toPaginationOffset { ComplexKey.of(*it.toTypedArray()) },
			false
		)
		emitAll(client.queryView(viewQuery, Array<String>::class.java, String::class.java, Code::class.java))
	}


	fun accumulateLatestVersionOrNull(acc: QueryResultAccumulator, row: ViewRowWithDoc<*, *, *>, limit: Int): QueryResultAccumulator {
		return if (acc.lastVisited != null &&                                            // If I have something to emit
			acc.sentElements < limit &&                        // And I still have space on the page
			((acc.lastVisited.doc as Code).code != (row.doc as Code).code ||    // The codes are sorted, If this one is different for something
				(acc.lastVisited.doc as Code).type != (row.doc as Code).type)
		)
			QueryResultAccumulator(acc.seenElements + 1, acc.sentElements + 1, acc.elementsFound, acc.offset, acc.lastVisited, row)
		else QueryResultAccumulator(acc.seenElements + 1, acc.sentElements, acc.elementsFound, acc.offset,null, row)
	}

	fun accumulateVersionOrNull(acc: QueryResultAccumulator, row: ViewRowWithDoc<*, *, *>, limit: Int, version: String, skip: Boolean): QueryResultAccumulator {
		return if ((acc.lastVisited != null || !skip) &&                            // If it is the second or later call, I have to skip the first result (otherwise is repeated)
			(row.doc as Code).version == version &&                           // And the version is correct
			acc.sentElements < limit										// And I still have space on the page
		)
			QueryResultAccumulator(acc.seenElements + 1, acc.sentElements + 1, acc.elementsFound, acc.offset, row, row)
		else QueryResultAccumulator(acc.seenElements + 1, acc.sentElements, acc.elementsFound, acc.offset,null, row)
	}

	// Recursive function to filter results by version
	// If the filtered results are not enough to fill a page, it does the recusive step
	fun findCodesByLabel(from: ComplexKey, to: ComplexKey, version: String?, viewName: String, mapIndex: Int, paginationOffset: PaginationOffset<List<String?>>, extensionFactor: Float, prevTotalCount: Int, isContinue: Boolean): Flow<ViewQueryResultEvent> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		val extendedLimit = (paginationOffset.limit * extensionFactor).toInt()
		val viewQuery = pagedViewQuery<Code, ComplexKey>(
			client,
			viewName,
			from,
			to,
			paginationOffset.copy(limit = extendedLimit).toPaginationOffset { sk -> ComplexKey.of(*sk.mapIndexed { i, s -> if (i == mapIndex) s?.let { StringUtils.sanitizeString(it) } else s }.toTypedArray()) },
			false
		)

		emitAll(
			client.queryView(viewQuery, Array<String>::class.java, String::class.java, Code::class.java).let { flw ->
				if (version == null) flw
				else flw.scan(QueryResultAccumulator()) { acc, it ->
					when (it) {
						is ViewRowWithDoc<*, *, *> -> {
							if (version == "latest") accumulateLatestVersionOrNull(acc, it, paginationOffset.limit)
							else accumulateVersionOrNull(acc, it, paginationOffset.limit, version, isContinue)
							}
						is TotalCount -> QueryResultAccumulator(acc.seenElements, acc.sentElements, it.total, acc.offset, null, acc.lastVisited)
						is Offset -> QueryResultAccumulator(acc.seenElements, acc.sentElements, acc.elementsFound, it.offset, null, acc.lastVisited)
						else -> QueryResultAccumulator(acc.seenElements, acc.sentElements, acc.elementsFound, acc.offset,null, acc.lastVisited)
						}
					}
					.transform {
						if (it.toEmit != null){
							emit(it.toEmit)
						} // If I have something to emit, I emit it

						// Condition to check if I arrived at the end of the page
						if (it.seenElements >= extendedLimit || (it.elementsFound != null && it.offset != null && it.seenElements >= (it.elementsFound - it.offset))) {

							// If it viewed all the elements there can be more
							// AND it did not fill the page
							// it does the recursive call
							if(it.seenElements >= extendedLimit && it.sentElements < paginationOffset.limit)
								emitAll(
									findCodesByLabel(
										from,
										to,
										version,
										viewName,
										mapIndex,
										paginationOffset.copy(startKey = (it.lastVisited?.key as? Array<String>)?.toList() , startDocumentId = it.lastVisited?.id, limit = paginationOffset.limit - it.sentElements),
										(if (it.seenElements == 0) extensionFactor * 2 else (it.seenElements.toFloat() / it.sentElements)).coerceAtMost(100f),
										it.sentElements + prevTotalCount,
										true
									)
								)
							else{
								// If the version filter is latest and there are no more elements to visit and the page is not full, I emit the last element
								if (version == "latest" && it.lastVisited != null && it.sentElements < paginationOffset.limit)
									emit(it.lastVisited)    //If the version filter is "latest" then the last code must be always emitted
								emit(TotalCount(it.elementsFound ?: 0))
							}
						}
					}
			}
		)
	}

	@ExperimentalCoroutinesApi
	@FlowPreview
	@View(name = "by_language_label", map = "classpath:js/code/By_language_label.js")
	override fun findCodesByLabel(region: String?, language: String?, label: String?, version: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent> {
		val sanitizedLabel = label?.let { StringUtils.sanitizeString(it) }
		val from = ComplexKey.of(
			region ?: "\u0000",
			language ?: "\u0000",
			sanitizedLabel ?: "\u0000"
		)

		val to = ComplexKey.of(
			if (region == null) ComplexKey.emptyObject() else if (language == null) region + "\ufff0" else region,
			if (language == null) ComplexKey.emptyObject() else if (sanitizedLabel == null) language + "\ufff0" else language,
			if (sanitizedLabel == null) ComplexKey.emptyObject() else sanitizedLabel + "\ufff0"
		)
		return findCodesByLabel(from, to, version, "by_language_label", 2, paginationOffset, 1f, 0, false)
	}

	@ExperimentalCoroutinesApi
	@FlowPreview
	@View(name = "by_language_type_label", map = "classpath:js/code/By_language_type_label.js")
	override fun findCodesByLabel(region: String?, language: String?, type: String?, label: String?, version: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent> {
		val sanitizedLabel = label?.let { StringUtils.sanitizeString(it) }
		val from = ComplexKey.of(
			region ?: "\u0000",
			language ?: "\u0000",
			type ?: "\u0000",
			sanitizedLabel ?: "\u0000"
		)

		val to = ComplexKey.of(
			if (region == null) ComplexKey.emptyObject() else if (language == null) region + "\ufff0" else region,
			language ?: ComplexKey.emptyObject(),
			type ?: ComplexKey.emptyObject(),
			if (sanitizedLabel == null) ComplexKey.emptyObject() else sanitizedLabel + "\ufff0"
		)

		return findCodesByLabel(from, to, version, "by_language_type_label", 3, paginationOffset, 1f, 0, false)
	}

	@ExperimentalCoroutinesApi
	@FlowPreview
	@View(name = "by_qualifiedlink_id", map = "classpath:js/code/By_qualifiedlink_id.js")
	override fun findCodesByQualifiedLinkId(region: String?, linkType: String, linkedId: String?, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		val from =
			ComplexKey.of(
				linkType,
				linkedId
			)
		val to = ComplexKey.of(
			linkType,
			linkedId ?: ComplexKey.emptyObject()
		)

		val viewQuery = pagedViewQuery<Code, ComplexKey>(
			client,
			"by_qualifiedlink_id",
			from,
			to,
			paginationOffset.toPaginationOffset { ComplexKey.of(*it.toTypedArray()) },
			false
		)
		emitAll(client.queryView(viewQuery, Array<String>::class.java, String::class.java, Code::class.java))
	}

	override fun listCodeIdsByLabel(region: String?, language: String?, label: String?): Flow<String> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		val sanitizedLabel = label?.let { StringUtils.sanitizeString(it) }
		val from =
			ComplexKey.of(
				region ?: "\u0000",
				language ?: "\u0000",
				sanitizedLabel ?: "\u0000"
			)

		val to = ComplexKey.of(
			if (region == null) ComplexKey.emptyObject() else if (language == null) region + "\ufff0" else region,
			if (language == null) ComplexKey.emptyObject() else if (sanitizedLabel == null) language + "\ufff0" else language,
			if (sanitizedLabel == null) ComplexKey.emptyObject() else sanitizedLabel + "\ufff0"
		)

		emitAll(
			client.queryView<String, String>(
				createQuery(client, "by_language_label")
					.includeDocs(false)
					.startKey(from)
					.endKey(to)
			).mapNotNull { it.key }
		)
	}

	override fun listCodeIdsByLabel(region: String?, language: String?, type: String?, label: String?): Flow<String> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		val sanitizedLabel = label?.let { StringUtils.sanitizeString(it) }
		val from =
			ComplexKey.of(
				region ?: "\u0000",
				language ?: "\u0000",
				type ?: "\u0000",
				sanitizedLabel ?: "\u0000"
			)
		val to = ComplexKey.of(
			if (region == null) ComplexKey.emptyObject() else if (language == null) region + "\ufff0" else region,
			language ?: ComplexKey.emptyObject(),
			type ?: ComplexKey.emptyObject(),
			if (sanitizedLabel == null) ComplexKey.emptyObject() else sanitizedLabel + "\ufff0"
		)

		emitAll(
			client.queryView<String, String>(
				createQuery(client, "by_language_type_label")
					.includeDocs(false)
					.startKey(from)
					.endKey(to)
			).mapNotNull { it.id }
		)
	}

	override fun listCodeIdsByTypeCodeVersionInterval(startType: String?, startCode: String?, startVersion: String?, endType: String?, endCode: String?, endVersion: String?): Flow<String> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		val from = ComplexKey.of(
			startType ?: "\u0000",
			startCode ?: "\u0000",
			startVersion ?: "\u0000",
		)
		val to = ComplexKey.of(
			endType ?: ComplexKey.emptyObject(),
			endCode ?: ComplexKey.emptyObject(),
			endVersion ?: ComplexKey.emptyObject(),
		)
		emitAll(
			client.queryView<Array<String>, String>(
				createQuery(client, "by_type_code_version")
					.includeDocs(false)
					.reduce(false)
					.startKey(from)
					.endKey(to)
			).mapNotNull { it.id }
		)
	}

	override fun listCodeIdsByQualifiedLinkId(linkType: String, linkedId: String?): Flow<String> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		val from = ComplexKey.of(
			linkType,
			linkedId
		)
		val to = ComplexKey.of(
			linkType,
			linkedId ?: ComplexKey.emptyObject()
		)

		emitAll(
			client.queryView<String, String>(
				createQuery(client, "by_qualifiedlink_id")
					.includeDocs(false)
					.startKey(from)
					.endKey(to)
			).mapNotNull { it.id }
		)
	}

	override fun getCodesByIdsForPagination(ids: List<String>): Flow<ViewQueryResultEvent> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		emitAll(client.getForPagination(ids, Code::class.java))
	}

	override suspend fun isValid(codeType: String, codeCode: String, codeVersion: String?) = listCodesBy(codeType, codeCode, codeVersion).firstOrNull() != null

	@InternalCoroutinesApi
	override suspend fun getCodeByLabel(region: String, label: String, ofType: String, labelLang: List<String>): Code? {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		val sanitizedLabel = label.let { StringUtils.sanitizeString(it) }
		for (lang in labelLang) {
			val codeFlow = client.queryViewIncludeDocsNoValue<Array<String>, Code>(
				createQuery(client, "by_region_type_code_version")
					.includeDocs(true)
					.reduce(false)
					.key(
						ComplexKey.of(
							region,
							lang,
							ofType,
							sanitizedLabel
						)
					)
			).map { it.doc }.filter { c -> c.label?.get(lang)?.let { StringUtils.sanitizeString(it) } == sanitizedLabel }
			val code = codeFlow.firstOrNull()
			if (code != null) {
				return code
			}
		}

		//throw IllegalArgumentException("code of type $ofType not found for label $label in languages $labelLang")
		return null
	}
}
