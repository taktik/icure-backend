package org.taktik.icure.be.samv2.logic

import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.samv2.Amp
import org.taktik.icure.entities.samv2.Vmp
import org.taktik.icure.entities.samv2.VmpGroup

interface SamV2Logic {
    fun findAmpsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<*>): PaginatedList<Amp>
    fun findVmpsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<*>): PaginatedList<Vmp>
    fun findVmpGroupsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<*>): PaginatedList<VmpGroup>
    fun listAmpIdsByLabel(language: String?, label: String?): List<String>
    fun listVmpIdsByLabel(language: String?, label: String?): List<String>
    fun listVmpGroupIdsByLabel(language: String?, label: String?): List<String>
}
