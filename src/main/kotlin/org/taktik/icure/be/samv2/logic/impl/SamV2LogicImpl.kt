package org.taktik.icure.be.samv2.logic.impl

import org.springframework.stereotype.Service
import org.taktik.icure.be.samv2.logic.SamV2Logic
import org.taktik.icure.dao.samv2.*
import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.samv2.*
import org.taktik.icure.entities.samv2.embed.PharmaceuticalForm
import org.taktik.icure.entities.samv2.embed.Substance
import org.taktik.icure.samv2.SamVersion
import java.util.zip.GZIPInputStream

@Service
class SamV2LogicImpl(val ampDAO: AmpDAO, val nmpDAO: NmpDAO, val vmpDAO: VmpDAO, val vmpGroupDAO: VmpGroupDAO, val productIdDAO: ProductIdDAO, val pharmaceuticalFormDAO: PharmaceuticalFormDAO, val substanceDAO: SubstanceDAO) : SamV2Logic {
    private var ampProductIds: HashMap<String, String>? = null
    private var nmpProductIds: HashMap<String, String>? = null
    private var vmpProductIds: HashMap<String, String>? = null

    override fun findAmpsByDmppCode(dmppCode: String): List<Amp> {
        return ampDAO.findAmpsByDmppCode(dmppCode)
    }

    override fun findVmpsByGroupId(vmpgId: String, paginationOffset: PaginationOffset<*>): PaginatedList<Vmp> {
        return vmpDAO.findVmpsByGroupId(vmpgId, paginationOffset)
    }

    override fun findAmpsByVmpGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<*>): PaginatedList<Amp> {
        return ampDAO.findAmpsByVmpGroupCode(vmpgCode, paginationOffset)
    }

    override fun findAmpsByVmpGroupId(vmpgId: String, paginationOffset: PaginationOffset<*>): PaginatedList<Amp> {
        return ampDAO.findAmpsByVmpGroupId(vmpgId, paginationOffset)
    }

    override fun findAmpsByVmpCode(vmpCode: String, paginationOffset: PaginationOffset<*>): PaginatedList<Amp> {
        return ampDAO.findAmpsByVmpCode(vmpCode, paginationOffset)
    }

    override fun findAmpsByVmpId(vmpId: String, paginationOffset: PaginationOffset<*>): PaginatedList<Amp> {
        return ampDAO.findAmpsByVmpId(vmpId, paginationOffset)
    }

    override fun listVmpIdsByGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<*>): List<String> {
        return vmpDAO.listVmpIdsByGroupCode(vmpgCode, paginationOffset)
    }

    override fun listVmpIdsByGroupId(vmpgId: String, paginationOffset: PaginationOffset<*>): List<String> {
        return vmpDAO.listVmpIdsByGroupId(vmpgId, paginationOffset)
    }

    override fun listAmpIdsByVmpGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<*>): List<String> {
        return ampDAO.listAmpIdsByVmpGroupCode(vmpgCode, paginationOffset)
    }

    override fun listAmpIdsByVmpGroupId(vmpgId: String, paginationOffset: PaginationOffset<*>): List<String> {
        return ampDAO.listAmpIdsByVmpGroupId(vmpgId, paginationOffset)
    }

    override fun listAmpIdsByVmpCode(vmpCode: String, paginationOffset: PaginationOffset<*>): List<String> {
        return ampDAO.listAmpIdsByVmpCode(vmpCode, paginationOffset)
    }

    override fun listAmpIdsByVmpId(vmpId: String, paginationOffset: PaginationOffset<*>): List<String> {
        return ampDAO.listAmpIdsByVmpId(vmpId, paginationOffset)
    }

    override fun getVersion(): SamVersion? {
        return ampDAO.getVersion()
    }

    override fun findVmpsByVmpCode(vmpCode: String, paginationOffset: PaginationOffset<*>?): PaginatedList<Vmp> {
        return vmpDAO.findVmpsByVmpCode(vmpCode, paginationOffset)
    }

    override fun findVmpsByGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<*>): PaginatedList<Vmp> {
        return vmpDAO.findVmpsByGroupCode(vmpgCode, paginationOffset)
    }

    override fun findAmpsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<*>): PaginatedList<Amp> {
        return ampDAO.findAmpsByLabel(language, label, paginationOffset)
    }

    override fun findNmpsByLabel(language: String?, label: String?, paginationOffset: PaginationOffset<*>): PaginatedList<Nmp> {
        return nmpDAO.findNmpsByLabel(language, label, paginationOffset)
    }

    override fun listAmpIdsByLabel(language: String?, label: String?): List<String> {
        return ampDAO.listAmpIdsByLabel(language, label)
    }

    override fun listNmpIdsByLabel(language: String?, label: String?): List<String> {
        return nmpDAO.listNmpIdsByLabel(language, label)
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

    override fun findVmpGroupsByVmpGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<*>): PaginatedList<VmpGroup> {
        return vmpGroupDAO.findVmpGroupsByVmpGroupCode(vmpgCode, paginationOffset)
    }

    override fun findVmpGroups(paginationOffset: PaginationOffset<*>): PaginatedList<VmpGroup> {
        return vmpGroupDAO.findVmpGroups(paginationOffset)
    }

    override fun listVmpGroupIdsByLabel(language: String?, label: String?): List<String> {
        return vmpGroupDAO.listVmpGroupIdsByLabel(language, label)
    }

    override fun listProductIds(ids: Collection<String>): List<ProductId> {
        return productIdDAO.getList(ids)
    }

    @Synchronized
    override fun listAmpProductIds(ids: Collection<String>): List<ProductId?> {
        if (this.ampProductIds == null) {
            this.ampProductIds = ampDAO.getSignature("amp")?.let {
                GZIPInputStream(ampDAO.getAttachmentInputStream(it.id, "signatures", it.rev)).reader(Charsets.UTF_8).useLines { it.fold(HashMap<String, String>()) { acc, l -> acc.also { l.split('|').let { parts -> acc[parts[0]] = parts[1] } } } }
            }
        }
        return ids.map { id -> this.ampProductIds?.get(id)?.let { ProductId(id, it)} }
    }

    override fun listVmpgProductIds(ids: Collection<String>): List<ProductId?> {
        if (this.vmpProductIds == null) {
            this.vmpProductIds = ampDAO.getSignature("vmp")?.let {
                GZIPInputStream(ampDAO.getAttachmentInputStream(it.id, "signatures", it.rev)).reader(Charsets.UTF_8).useLines { it.fold(HashMap<String, String>()) { acc, l -> acc.also { l.split('|').let { parts -> acc[parts[0]] = parts[1] } } } }
            }
        }
        return ids.map { id -> this.vmpProductIds?.get(id)?.let { ProductId(id, it)} }
    }

    override fun listNmpProductIds(ids: Collection<String>): List<ProductId?> {
        if (this.nmpProductIds == null) {
            this.nmpProductIds = ampDAO.getSignature("nmp")?.let {
                GZIPInputStream(ampDAO.getAttachmentInputStream(it.id, "signatures", it.rev)).reader(Charsets.UTF_8).useLines { it.fold(HashMap<String, String>()) { acc, l -> acc.also { l.split('|').let { parts -> acc[parts[0]] = parts[1] } } } }
            }
        }
        return ids.map { id -> this.nmpProductIds?.get(id)?.let { ProductId(id, it)} }
    }

    override fun listSubstances(): List<Substance> {
        return substanceDAO.all
    }

    override fun listPharmaceuticalForms(): List<PharmaceuticalForm> {
        return pharmaceuticalFormDAO.all
    }

    override fun listVmpsByVmpCodes(vmpCodes: List<String>): List<Vmp> {
        return vmpDAO.listVmpsByVmpCodes(vmpCodes)
    }

    override fun findAmpsByAtcCode(atcCode: String, paginationOffset: PaginationOffset<*>): PaginatedList<Amp> {
        return ampDAO.findAmpsByAtc(atcCode, paginationOffset)
    }

    override fun listVmpsByGroupIds(vmpgIds: List<String>): List<Vmp> {
        return vmpDAO.listVmpsByGroupIds(vmpgIds)
    }

    override fun listAmpsByGroupCodes(vmpgCodes: List<String>): List<Amp> {
        return ampDAO.listAmpsByVmpGroupCodes(vmpgCodes)
    }

    override fun listAmpsByDmppCodes(dmppCodes: List<String>): List<Amp> {
        return ampDAO.listAmpsByDmppCodes(dmppCodes)
    }

    override fun listAmpsByGroupIds(groupIds: List<String>): List<Amp> {
        return ampDAO.listAmpsByVmpGroupIds(groupIds)
    }

    override fun listAmpsByVmpCodes(vmpgCodes: List<String>): List<Amp> {
        return ampDAO.listAmpsByVmpCodes(vmpgCodes)
    }

    override fun listAmpsByVmpIds(vmpIds: List<String>): List<Amp> {
        return ampDAO.listAmpsByVmpIds(vmpIds)
    }

    override fun listVmpGroupsByVmpGroupCodes(vmpgCodes: List<String>): List<VmpGroup> {
        return vmpGroupDAO.listVmpGroupsByVmpGroupCodes(vmpgCodes)
    }

    override fun listNmpsByCnks(cnks: List<String>): List<Nmp> {
        return nmpDAO.listNmpsByCnks(cnks)
    }
}
