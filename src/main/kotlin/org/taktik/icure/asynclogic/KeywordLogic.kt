package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.KeywordDAO
import org.taktik.icure.entities.Keyword

interface KeywordLogic : EntityPersister<Keyword, String> {
    fun getGenericDAO(): KeywordDAO

    suspend fun createKeyword(keyword: Keyword): Keyword?

    suspend fun getKeyword(keywordId: String): Keyword?
    fun deleteKeywords(ids: Set<String>): Flow<DocIdentifier>

    suspend fun modifyKeyword(keyword: Keyword): Keyword?
    fun getKeywordsByUser(userId: String): Flow<Keyword>
}
