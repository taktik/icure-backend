package org.taktik.icure.asynclogic.samv2

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.samv2.Amp
import org.taktik.icure.entities.samv2.Vmp
import org.taktik.icure.entities.samv2.VmpGroup

interface SamV2Logic {
    fun findAmpsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent>
    fun findVmpsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent>
    fun findVmpGroupsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent>
    fun findVmpsByGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>
    fun findVmpsByGroupId(vmpgId: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>
    fun findAmpsByVmpGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>
    fun findAmpsByVmpGroupId(vmpgId: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>
    fun findAmpsByVmpCode(vmpCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>
    fun findAmpsByVmpId(vmpId: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>


    fun listAmpIdsByLabel(language: String?, label: String?): Flow<String>
    fun listVmpIdsByLabel(language: String?, label: String?): Flow<String>
    fun listVmpGroupIdsByLabel(language: String?, label: String?): Flow<String>
    fun listVmpIdsByGroupCode(vmpgCode: String): Flow<String>
    fun listVmpIdsByGroupId(vmpgId: String): Flow<String>
    fun listAmpIdsByVmpGroupCode(vmpgCode: String): Flow<String>
    fun listAmpIdsByVmpGroupId(vmpgId: String): Flow<String>
    fun listAmpIdsByVmpCode(vmpCode: String): Flow<String>
    fun listAmpIdsByVmpId(vmpId: String): Flow<String>
}
