package org.taktik.icure.be.samv2.logic

import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.samv2.Amp
import org.taktik.icure.entities.samv2.Vmp
import org.taktik.icure.entities.samv2.VmpGroup
import org.taktik.icure.samv2.SamVersion

interface SamV2Logic {
    fun findAmpsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<*>): PaginatedList<Amp>
    fun findVmpsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<*>): PaginatedList<Vmp>
    fun findVmpGroupsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<*>): PaginatedList<VmpGroup>
    fun findVmpsByGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<*>): PaginatedList<Vmp>
    fun findVmpsByGroupId(vmpgId: String, paginationOffset: PaginationOffset<*>): PaginatedList<Vmp>
    fun findAmpsByVmpGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<*>): PaginatedList<Amp>
    fun findAmpsByVmpGroupId(vmpgId: String, paginationOffset: PaginationOffset<*>): PaginatedList<Amp>
    fun findAmpsByVmpCode(vmpCode: String, paginationOffset: PaginationOffset<*>): PaginatedList<Amp>
    fun findAmpsByVmpId(vmpId: String, paginationOffset: PaginationOffset<*>): PaginatedList<Amp>
    fun findAmpsByDmppCode(dmppCode: String): List<Amp>


    fun listAmpIdsByLabel(language: String?, label: String?): List<String>
    fun listVmpIdsByLabel(language: String?, label: String?): List<String>
    fun listVmpGroupIdsByLabel(language: String?, label: String?): List<String>
    fun listVmpIdsByGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<*>): List<String>
    fun listVmpIdsByGroupId(vmpgId: String, paginationOffset: PaginationOffset<*>): List<String>
    fun listAmpIdsByVmpGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<*>): List<String>
    fun listAmpIdsByVmpGroupId(vmpgId: String, paginationOffset: PaginationOffset<*>): List<String>
    fun listAmpIdsByVmpCode(vmpCode: String, paginationOffset: PaginationOffset<*>): List<String>
    fun listAmpIdsByVmpId(vmpId: String, paginationOffset: PaginationOffset<*>): List<String>
    fun getVersion(): SamVersion?
}
