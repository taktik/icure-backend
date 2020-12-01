package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.entities.Keyword
import java.net.URI

interface KeywordDAO: GenericDAO<Keyword> {
    suspend fun getKeyword(keywordId: String): Keyword?

    fun getByUserId(userId: String): Flow<Keyword>
}
