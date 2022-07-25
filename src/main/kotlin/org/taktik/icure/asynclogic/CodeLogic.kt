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

package org.taktik.icure.asynclogic

import java.io.InputStream
import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.base.CodeStub

interface CodeLogic : EntityPersister<Code, String> {
	fun getTagTypeCandidates(): List<String>
	fun getRegions(): List<String>
	suspend fun get(id: String): Code?

	suspend fun get(type: String, code: String, version: String): Code?
	fun getCodes(ids: List<String>): Flow<Code>
	suspend fun create(code: Code): Code?

	suspend fun create(batch: List<Code>): List<Code>?

	@Throws(Exception::class)
	suspend fun modify(code: Code): Code?

	fun modify(batch: List<Code>): Flow<Code>

	fun findCodeTypes(type: String?): Flow<String>
	fun findCodeTypes(region: String?, type: String?): Flow<String>
	fun findCodesBy(type: String?, code: String?, version: String?): Flow<Code>
	fun findCodesBy(region: String?, type: String?, code: String?, version: String?): Flow<Code>
	fun findCodesBy(region: String?, type: String?, code: String?, version: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent>
	fun findCodesByLabel(region: String?, language: String?, label: String?, version: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent>
	fun findCodesByLabel(region: String?, language: String?, type: String?, label: String?, version: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent>
	fun listCodeIdsByLabel(region: String?, language: String?, type: String?, label: String?): Flow<String>
	fun listCodeIdsByTypeCodeVersionInterval(startType: String?, startCode: String?, startVersion: String?, endType: String?, endCode: String?, endVersion: String?): Flow<String>
	fun findCodesByQualifiedLinkId(region: String?, linkType: String, linkedId: String, pagination: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent>
	fun listCodeIdsByQualifiedLinkId(linkType: String, linkedId: String?): Flow<String>
	suspend fun <T : Enum<*>> importCodesFromEnum(e: Class<T>)

	suspend fun importCodesFromXml(md5: String, type: String, stream: InputStream)

	suspend fun importCodesFromJSON(stream: InputStream)
	fun listCodes(paginationOffset: PaginationOffset<*>?, filterChain: FilterChain<Code>, sort: String?, desc: Boolean?): Flow<ViewQueryResultEvent>

	suspend fun getOrCreateCode(type: String, code: String, version: String): Code?

	suspend fun isValid(code: Code, ofType: String? = null): Boolean
	suspend fun isValid(code: CodeStub, ofType: String? = null): Boolean

	suspend fun getCodeByLabel(region: String, label: String, ofType: String, labelLang: List<String> = listOf("fr", "nl")): Code?
}
