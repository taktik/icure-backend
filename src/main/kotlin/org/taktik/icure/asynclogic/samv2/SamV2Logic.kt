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

package org.taktik.icure.asynclogic.samv2

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.samv2.*

interface SamV2Logic {
    fun findAmpsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent>
    fun findVmpsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent>
    fun findVmpGroupsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent>
    fun findVmpGroupsByVmpGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>
    fun findVmpGroups(paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>
    fun findVmpsByVmpCode(vmpCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>
    fun findVmpsByGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>
    fun findVmpsByGroupId(vmpgId: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>
    fun findAmpsByVmpGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>
    fun findAmpsByVmpGroupId(vmpgId: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>
    fun findAmpsByVmpCode(vmpCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>
    fun findAmpsByVmpId(vmpId: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>
    fun findAmpsByDmppCode(dmppCode: String): Flow<ViewQueryResultEvent>
    fun findAmpsByAtcCode(atcCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>

    fun listAmpIdsByLabel(language: String?, label: String?): Flow<String>
    fun listVmpIdsByLabel(language: String?, label: String?): Flow<String>
    fun listVmpGroupIdsByLabel(language: String?, label: String?): Flow<String>
    fun listVmpIdsByGroupCode(vmpgCode: String): Flow<String>
    fun listVmpIdsByGroupId(vmpgId: String): Flow<String>
    fun listAmpIdsByVmpGroupCode(vmpgCode: String): Flow<String>
    fun listAmpIdsByVmpGroupId(vmpgId: String): Flow<String>
    fun listAmpIdsByVmpCode(vmpCode: String): Flow<String>
    fun listAmpIdsByVmpId(vmpId: String): Flow<String>

    suspend fun getVersion(): SamVersion?
    fun listProductIds(productIds: List<String>): Flow<ProductId>
    fun findNmpsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent>
    fun listNmpIdsByLabel(language: String?, label: String?): Flow<String>
    fun listSubstances(): Flow<Substance>
    fun listPharmaceuticalForms(): Flow<PharmaceuticalForm>

    fun listVmpsByVmpCodes(vmpCodes:  List<String>): Flow<Vmp>
    fun listVmpsByGroupIds(vmpgIds:  List<String>): Flow<Vmp>
    fun listAmpsByGroupCodes(vmpgCodes:  List<String>): Flow<Amp>
    fun listAmpsByDmppCodes(dmppCodes:  List<String>): Flow<Amp>
    fun listAmpsByGroupIds(groupIds:  List<String>): Flow<Amp>
    fun listAmpsByVmpCodes(vmpgCodes:  List<String>): Flow<Amp>
    fun listAmpsByVmpIds(vmpIds:  List<String>): Flow<Amp>
    fun listVmpGroupsByVmpGroupCodes(vmpgCodes: List<String>): Flow<VmpGroup>
    fun listNmpsByCnks(cnks: List<String>): Flow<Nmp>

    fun findParagraphs(searchString: String, language: String): Flow<Paragraph>
    fun findParagraphsWithCnk(cnk: Long, language: String): Flow<Paragraph>

    fun listVerses(chapterName: String, paragraphName: String): Flow<Verse>

    suspend fun getParagraphInfos(chapterName: String, paragraphName: String): Paragraph?
    suspend fun getVersesHierarchy(chapterName: String, paragraphName: String): Verse

    fun getAmpsForParagraph(chapterName: String, paragraphName: String): Flow<Amp>
    fun getVtmNamesForParagraph(chapterName: String, paragraphName: String, language: String): Flow<String>
}
