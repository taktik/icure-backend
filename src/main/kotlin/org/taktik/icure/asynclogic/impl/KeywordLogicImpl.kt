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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.KeywordDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.KeywordLogic
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.entities.Keyword
import org.taktik.icure.utils.firstOrNull

@ExperimentalCoroutinesApi
@Service
class KeywordLogicImpl(private val keywordDAO: KeywordDAO,
                       private val uuidGenerator: UUIDGenerator,
                       private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<Keyword, KeywordDAO>(sessionLogic), KeywordLogic {

    override fun getGenericDAO(): KeywordDAO {
        return keywordDAO
    }

    override suspend fun createKeyword(keyword: Keyword) = fix(keyword) { keyword ->
        val createdKeywords = try { // Setting Keyword attributes
            createEntities(setOf(keyword))
        } catch (e: Exception) {
            log.error("createKeyword: " + e.message)
            throw IllegalArgumentException("Invalid Keyword", e)
        }
        createdKeywords.firstOrNull()
    }

    override suspend fun getKeyword(keywordId: String): Keyword? {
        return keywordDAO.getKeyword(keywordId)
    }

    override fun deleteKeywords(ids: Set<String>): Flow<DocIdentifier> {
        return try {
            deleteByIds(ids)
        } catch (e: Exception) {
            log.error(e.message, e)
            flowOf()
        }
    }

    override suspend fun modifyKeyword(keyword: Keyword): Keyword? {
        return try {
            updateEntities(setOf(keyword)).firstOrNull()
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid Keyword", e)
        }
    }

    override fun getKeywordsByUser(userId: String): Flow<Keyword> = flow {
        emitAll(keywordDAO.getByUserId(userId))
    }

    companion object {
        private val log = LoggerFactory.getLogger(KeywordLogicImpl::class.java)
    }
}
