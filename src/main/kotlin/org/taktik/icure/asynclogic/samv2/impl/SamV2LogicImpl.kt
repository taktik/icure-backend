package org.taktik.icure.asynclogic.samv2.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asyncdao.samv2.AmpDAO
import org.taktik.icure.asyncdao.samv2.VmpDAO
import org.taktik.icure.asyncdao.samv2.VmpGroupDAO
import org.taktik.icure.asynclogic.samv2.SamV2Logic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.samv2.Amp

@Service
class SamV2LogicImpl(val ampDAO: AmpDAO, val vmpDAO: VmpDAO, val vmpGroupDAO: VmpGroupDAO) : SamV2Logic {
    override fun findVmpsByGroupId(vmpgId: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
        return vmpDAO.findVmpsByGroupId(vmpgId, paginationOffset)
    }

    override fun findAmpsByVmpGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
        return ampDAO.findAmpsByVmpGroupCode(vmpgCode, paginationOffset)
    }

    override fun findAmpsByVmpGroupId(vmpgId: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
        return ampDAO.findAmpsByVmpGroupId(vmpgId, paginationOffset)
    }

    override fun findAmpsByVmpCode(vmpCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
        return ampDAO.findAmpsByVmpCode(vmpCode, paginationOffset)
    }

    override fun findAmpsByVmpId(vmpId: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
        return ampDAO.findAmpsByVmpId(vmpId, paginationOffset)
    }

    override fun findAmpsByDmppCode(dmppCode: String): Flow<ViewQueryResultEvent> {
        return ampDAO.findAmpsByDmppCode(dmppCode)
    }

    override fun listVmpIdsByGroupCode(vmpgCode: String): Flow<String> {
        return vmpDAO.listVmpIdsByGroupCode(vmpgCode)
    }

    override fun listVmpIdsByGroupId(vmpgId: String): Flow<String> {
        return vmpDAO.listVmpIdsByGroupId(vmpgId)
    }

    override fun listAmpIdsByVmpGroupCode(vmpgCode: String): Flow<String> {
        return ampDAO.listAmpIdsByVmpGroupCode(vmpgCode)
    }

    override fun listAmpIdsByVmpGroupId(vmpgId: String): Flow<String> {
        return ampDAO.listAmpIdsByVmpGroupId(vmpgId)
    }

    override fun listAmpIdsByVmpCode(vmpCode: String): Flow<String> {
        return ampDAO.listAmpIdsByVmpCode(vmpCode)
    }

    override fun listAmpIdsByVmpId(vmpId: String): Flow<String> {
        return ampDAO.listAmpIdsByVmpId(vmpId)
    }

    override fun findVmpsByGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
        return vmpDAO.findVmpsByGroupCode(vmpgCode, paginationOffset)
    }

    override fun findAmpsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent> {
        return ampDAO.findAmpsByLabel(language, label, paginationOffset)
    }

    override fun listAmpIdsByLabel(language: String?, label: String?): Flow<String> {
        return ampDAO.listAmpIdsByLabel(language, label)
    }

    override fun findVmpsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent> {
        return vmpDAO.findVmpsByLabel(language, label, paginationOffset)
    }

    override fun listVmpIdsByLabel(language: String?, label: String?): Flow<String> {
        return vmpDAO.listVmpIdsByLabel(language, label)
    }

    override fun findVmpGroupsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent> {
        return vmpGroupDAO.findVmpGroupsByLabel(language, label, paginationOffset)
    }

    override fun listVmpGroupIdsByLabel(language: String?, label: String?): Flow<String> {
        return vmpGroupDAO.listVmpGroupIdsByLabel(language, label)
    }
}
