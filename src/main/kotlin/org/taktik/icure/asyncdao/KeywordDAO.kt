package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.entities.Keyword
import java.net.URI

interface KeywordDAO {
    suspend fun getKeyword(dbInstanceUrl: URI, groupId: String, keywordId: String): Keyword?

    fun getByUserId(dbInstanceUrl: URI, groupId: String, userId: String): Flow<Keyword>
}
