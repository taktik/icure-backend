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

package org.taktik.icure.asynclogic.samv2.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Service
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.ViewRowWithDoc
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.icure.asyncdao.samv2.AmpDAO
import org.taktik.icure.asyncdao.samv2.NmpDAO
import org.taktik.icure.asyncdao.samv2.ParagraphDAO
import org.taktik.icure.asyncdao.samv2.PharmaceuticalFormDAO
import org.taktik.icure.asyncdao.samv2.ProductIdDAO
import org.taktik.icure.asyncdao.samv2.SubstanceDAO
import org.taktik.icure.asyncdao.samv2.VerseDAO
import org.taktik.icure.asyncdao.samv2.VmpDAO
import org.taktik.icure.asyncdao.samv2.VmpGroupDAO
import org.taktik.icure.asynclogic.samv2.SamV2Logic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.samv2.Amp
import org.taktik.icure.entities.samv2.Nmp
import org.taktik.icure.entities.samv2.Paragraph
import org.taktik.icure.entities.samv2.PharmaceuticalForm
import org.taktik.icure.entities.samv2.ProductId
import org.taktik.icure.entities.samv2.SamVersion
import org.taktik.icure.entities.samv2.Substance
import org.taktik.icure.entities.samv2.Verse
import org.taktik.icure.entities.samv2.Vmp
import org.taktik.icure.entities.samv2.VmpGroup
import org.taktik.icure.utils.bufferedChunks
import org.taktik.icure.utils.distinct

@Service
class SamV2LogicImpl(
	val ampDAO: AmpDAO,
	val vmpDAO: VmpDAO,
	val vmpGroupDAO: VmpGroupDAO,
	val productIdDAO: ProductIdDAO,
	val nmpDAO: NmpDAO,
	val substanceDAO: SubstanceDAO,
	val pharmaceuticalFormDAO: PharmaceuticalFormDAO,
	val paragraphDAO: ParagraphDAO,
	val verseDAO: VerseDAO
) : SamV2Logic {
	private val mutex = Mutex()

	private var ampProductIds: Map<String, String>? = null
	private var nmpProductIds: Map<String, String>? = null
	private var vmpProductIds: Map<String, String>? = null

	override fun findVmpsByGroupId(vmpgId: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
		return vmpDAO.findVmpsByGroupId(vmpgId, paginationOffset)
	}

	override fun findAmpsByVmpGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
		return ampDAO.findAmpsByVmpGroupCode(vmpgCode, paginationOffset)
	}

	override fun findAmpsByVmpGroupId(vmpgId: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
		return ampDAO.findAmpsByVmpGroupId(vmpgId, paginationOffset)
	}

	override fun findAmpsByVmpCode(vmpCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
		return ampDAO.findAmpsByVmpCode(vmpCode, paginationOffset)
	}

	override fun findAmpsByVmpId(vmpId: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
		return ampDAO.findAmpsByVmpId(vmpId, paginationOffset)
	}

	override fun findAmpsByDmppCode(dmppCode: String): Flow<ViewQueryResultEvent> {
		return ampDAO.findAmpsByDmppCode(dmppCode)
	}

	override fun listVmpIdsByGroupCode(vmpgCode: String): Flow<String> {
		return vmpDAO.listVmpIdsByGroupCode(vmpgCode)
	}

	override fun listVmpIdsByGroupId(vmpgId: String): Flow<String> {
		return vmpDAO.listVmpIdsByGroupId(vmpgId)
	}

	override fun listAmpIdsByVmpGroupCode(vmpgCode: String): Flow<String> {
		return ampDAO.listAmpIdsByVmpGroupCode(vmpgCode)
	}

	override fun listAmpIdsByVmpGroupId(vmpgId: String): Flow<String> {
		return ampDAO.listAmpIdsByVmpGroupId(vmpgId)
	}

	override fun listAmpIdsByVmpCode(vmpCode: String): Flow<String> {
		return ampDAO.listAmpIdsByVmpCode(vmpCode)
	}

	override fun listAmpIdsByVmpId(vmpId: String): Flow<String> {
		return ampDAO.listAmpIdsByVmpId(vmpId)
	}

	override suspend fun getVersion(): SamVersion? {
		return ampDAO.getVersion()
	}

	override suspend fun listAmpProductIds(ids: Collection<String>): List<ProductId?> {
		mutex.withLock {
			if (this.ampProductIds == null) {
				this.ampProductIds = ampDAO.getProductIdsFromSignature("amp")
				return ids.map { id -> this.ampProductIds?.get(id)?.let { ProductId(id = id, productId = it) } }
			}
		}
		return ids.map { id -> this.ampProductIds?.get(id)?.let { ProductId(id = id, productId = it) } }
	}

	override suspend fun listVmpgProductIds(ids: Collection<String>): List<ProductId?> {
		mutex.withLock {
			if (this.vmpProductIds == null) {
				this.vmpProductIds = ampDAO.getProductIdsFromSignature("vmp")
				return ids.map { id -> this.vmpProductIds?.get(id)?.let { ProductId(id = id, productId = it) } }
			}
		}
		return ids.map { id -> this.vmpProductIds?.get(id)?.let { ProductId(id = id, productId = it) } }
	}

	override suspend fun listNmpProductIds(ids: Collection<String>): List<ProductId?> {
		mutex.withLock {
			if (this.nmpProductIds == null) {
				this.nmpProductIds = ampDAO.getProductIdsFromSignature("nmp")
				return ids.map { id -> this.nmpProductIds?.get(id)?.let { ProductId(id = id, productId = it) } }
			}
		}
		return ids.map { id -> this.nmpProductIds?.get(id)?.let { ProductId(id = id, productId = it) } }
	}

	override fun findVmpsByGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
		return vmpDAO.findVmpsByGroupCode(vmpgCode, paginationOffset)
	}

	override fun findAmpsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent> {
		return ampDAO.findAmpsByLabel(language, label, paginationOffset)
	}

	override fun listAmpIdsByLabel(language: String?, label: String?): Flow<String> {
		return ampDAO.listAmpIdsByLabel(language, label)
	}

	override fun findVmpsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent> {
		return vmpDAO.findVmpsByLabel(language, label, paginationOffset)
	}

	override fun listVmpIdsByLabel(language: String?, label: String?): Flow<String> {
		return vmpDAO.listVmpIdsByLabel(language, label)
	}

	override fun findVmpGroupsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent> {
		return vmpGroupDAO.findVmpGroupsByLabel(language, label, paginationOffset)
	}

	override fun findVmpGroupsByVmpGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
		return vmpGroupDAO.findVmpGroupsByVmpGroupCode(vmpgCode, paginationOffset)
	}

	override fun findVmpGroups(paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
		return vmpGroupDAO.findVmpGroups(paginationOffset)
	}

	override fun findVmpsByVmpCode(vmpCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
		return vmpDAO.findVmpsByVmpCode(vmpCode, paginationOffset)
	}

	override fun listVmpGroupIdsByLabel(language: String?, label: String?): Flow<String> {
		return vmpGroupDAO.listVmpGroupIdsByLabel(language, label)
	}

	override fun findNmpsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent> {
		return nmpDAO.findNmpsByLabel(language, label, paginationOffset)
	}

	override fun listNmpIdsByLabel(language: String?, label: String?): Flow<String> {
		return nmpDAO.listNmpIdsByLabel(language, label)
	}

	override fun listSubstances(): Flow<Substance> {
		return substanceDAO.getEntities()
	}

	override fun listPharmaceuticalForms(): Flow<PharmaceuticalForm> {
		return pharmaceuticalFormDAO.getEntities()
	}

	override fun listVmpsByVmpCodes(vmpCodes: List<String>): Flow<Vmp> {
		return vmpDAO.listVmpsByVmpCodes(vmpCodes)
	}

	override fun findAmpsByAtcCode(atcCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
		return ampDAO.findAmpsByAtc(atcCode, paginationOffset)
	}

	override fun listVmpsByGroupIds(vmpgIds: List<String>): Flow<Vmp> {
		return vmpDAO.listVmpsByGroupIds(vmpgIds)
	}

	override fun listAmpsByGroupCodes(vmpgCodes: List<String>): Flow<Amp> {
		return ampDAO.listAmpsByVmpGroupCodes(vmpgCodes)
	}

	override fun listAmpsByDmppCodes(dmppCodes: List<String>): Flow<Amp> {
		return ampDAO.listAmpsByDmppCodes(dmppCodes)
	}

	override fun listAmpsByGroupIds(groupIds: List<String>): Flow<Amp> {
		return ampDAO.listAmpsByVmpGroupIds(groupIds)
	}

	override fun listAmpsByVmpCodes(vmpgCodes: List<String>): Flow<Amp> {
		return ampDAO.listAmpsByVmpCodes(vmpgCodes)
	}

	override fun listAmpsByVmpIds(vmpIds: List<String>): Flow<Amp> {
		return ampDAO.listAmpsByVmpIds(vmpIds)
	}

	override fun listVmpGroupsByVmpGroupCodes(vmpgCodes: List<String>): Flow<VmpGroup> {
		return vmpGroupDAO.listVmpGroupsByVmpGroupCodes(vmpgCodes)
	}

	override fun listNmpsByCnks(cnks: List<String>): Flow<Nmp> {
		return nmpDAO.listNmpsByCnks(cnks)
	}

	override fun findParagraphs(searchString: String, language: String): Flow<Paragraph> {
		return paragraphDAO.findParagraphs(searchString, language, PaginationOffset(1000))
			.filterIsInstance<ViewRowWithDoc<ComplexKey, Int, Paragraph>>()
			.map { it.doc }
	}

	override fun findParagraphsWithCnk(cnk: Long, language: String): Flow<Paragraph> {
		return paragraphDAO.findParagraphsWithCnk(cnk, language)
	}

	override suspend fun getParagraphInfos(chapterName: String, paragraphName: String): Paragraph? {
		return paragraphDAO.getParagraph(chapterName, paragraphName)
	}

	override suspend fun getVersesHierarchy(chapterName: String, paragraphName: String): Verse {
		val allVerses: List<Verse> = listVerses(chapterName, paragraphName).toList()

		fun fillChildren(v: Verse): Verse = v.copy(children = allVerses.filter { it.verseSeqParent == v.verseSeq }.map { fillChildren(it) })

		return fillChildren(allVerses.first())
	}

	override fun listVerses(
		chapterName: String,
		paragraphName: String
	) = verseDAO.listVerses(chapterName, paragraphName)

	override fun getAmpsForParagraph(chapterName: String, paragraphName: String): Flow<Amp> {
		return ampDAO.findAmpsByChapterParagraph(chapterName, paragraphName, PaginationOffset(1000))
			.filterIsInstance<ViewRowWithDoc<ComplexKey, Int, Amp>>()
			.map { it.doc }
	}

	@ExperimentalCoroutinesApi
	@FlowPreview
	override fun getVtmNamesForParagraph(chapterName: String, paragraphName: String, language: String): Flow<String> {
		return getAmpsForParagraph(chapterName, paragraphName).bufferedChunks(100, 200).flatMapConcat {
			vmpDAO.getEntities(it.mapNotNull { it.vmp?.id }).mapNotNull {
				it.vtm?.name?.let { t ->
					when (language) {
						"fr" -> t.fr
						"en" -> t.en
						"de" -> t.de
						"nl" -> t.nl
						else -> null
					}
				}
			}
		}.distinct()
	}
}
