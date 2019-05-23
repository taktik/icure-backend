package org.taktik.icure.be.samlv2.logic.impl

import org.springframework.stereotype.Service
import org.taktik.icure.be.samlv2.logic.SamV2Logic
import org.taktik.icure.dao.samv2.AmpDAO
import org.taktik.icure.dao.samv2.VmpDAO
import org.taktik.icure.dao.samv2.VmpGroupDAO
import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.samv2.Amp
import org.taktik.icure.entities.samv2.Vmp
import org.taktik.icure.entities.samv2.VmpGroup

@Service
class SamV2LogicImpl(val ampDAO: AmpDAO, val vmpDAO: VmpDAO, val vmpGroupDAO: VmpGroupDAO) : SamV2Logic {
    override fun findAmpsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<*>): PaginatedList<Amp> {
        return ampDAO.findAmpsByLabel(language, label, paginationOffset)
    }

    override fun listAmpIdsByLabel(language: String?, label: String?): List<String> {
        return ampDAO.listAmpIdsByLabel(language, label)
    }

    override fun findVmpsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<*>): PaginatedList<Vmp> {
        return vmpDAO.findVmpsByLabel(language, label, paginationOffset)
    }

    override fun listVmpIdsByLabel(language: String?, label: String?): List<String> {
        return vmpDAO.listVmpIdsByLabel(language, label)
    }

    override fun findVmpGroupsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<*>): PaginatedList<VmpGroup> {
        return vmpGroupDAO.findVmpGroupsByLabel(language, label, paginationOffset)
    }

    override fun listVmpGroupIdsByLabel(language: String?, label: String?): List<String> {
        return vmpGroupDAO.listVmpGroupIdsByLabel(language, label)
    }
}
