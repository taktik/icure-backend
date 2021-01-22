package org.taktik.icure.dao.samv2.impl

import org.ektorp.ComplexKey
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.icure.dao.impl.GenericDAOImpl
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.dao.samv2.VmpDAO
import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.db.StringUtils
import org.taktik.icure.entities.samv2.Vmp

@Repository("vmpDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.samv2.Vmp' && !doc.deleted) emit( null, doc._id )}")
class VmpDAOImpl @Autowired
constructor(@Qualifier("couchdbDrugs") couchdb: CouchDbICureConnector, idGenerator: IDGenerator) : GenericDAOImpl<Vmp>(Vmp::class.java, couchdb, idGenerator), VmpDAO {
    init {
        initStandardDesignDocument()
    }

    @View(name = "by_groupcode", map = "classpath:js/vmp/By_groupcode.js")
    override fun findVmpsByGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<*>?): PaginatedList<Vmp> {
        val from = vmpgCode
        val to = vmpgCode

        return pagedQueryView(
                "by_groupcode",
                from,
                to,
                paginationOffset,
                false
        )
    }

    @View(name = "by_vmpcode", map = "classpath:js/vmp/By_vmpcode.js")
    override fun findVmpsByVmpCode(vmpgCode: String, paginationOffset: PaginationOffset<*>?): PaginatedList<Vmp> {
        val from = vmpgCode
        val to = vmpgCode

        return pagedQueryView(
                "by_vmpcode",
                from,
                to,
                paginationOffset,
                false
        )
    }

    @View(name = "by_language_label", map = "classpath:js/vmp/By_language_label.js")
    override fun findVmpsByLabel(language: String?, label: String?, pagination: PaginationOffset<*>?): PaginatedList<Vmp> {
        val sanitizedLabel = label?.let { StringUtils.sanitizeString(it)}
        val startKey = if (pagination?.startKey == null) null else pagination.startKey as MutableList<Any?>
        if (startKey != null && startKey.size > 2 && startKey[2] != null) {
            startKey[2] = StringUtils.sanitizeString(startKey[2] as String)
        }
        val from = if (startKey == null)
            ComplexKey.of(
                    language ?: "\u0000",
                    if (sanitizedLabel == null) "\u0000" else sanitizedLabel
            )
        else
            ComplexKey.of(*startKey.toTypedArray())
        val to = ComplexKey.of(
                if (language == null) ComplexKey.emptyObject() else if (sanitizedLabel == null) language + "\ufff0" else language,
                if (sanitizedLabel == null) ComplexKey.emptyObject() else sanitizedLabel + "\ufff0"
        )

        return pagedQueryView(
                "by_language_label",
                from,
                to,
                pagination,
                false
        )
    }

    @View(name = "by_groupid", map = "classpath:js/vmp/By_groupid.js")
    override fun findVmpsByGroupId(vmpgId: String, paginationOffset: PaginationOffset<*>): PaginatedList<Vmp> {
        val from = vmpgId
        val to = vmpgId

        return pagedQueryView(
                "by_groupid",
                from,
                to,
                paginationOffset,
                false
        )
    }

    override fun listVmpIdsByGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<*>): List<String> {
        val from = vmpgCode
        val to = vmpgCode

        return db.queryView(createQuery(
                "by_groupcode")
                .includeDocs(false)
                .startKey(from)
                .endKey(to), String::class.java)
    }

    override fun listVmpIdsByGroupId(vmpgId: String, paginationOffset: PaginationOffset<*>): List<String> {
        val from = vmpgId
        val to = vmpgId

        return db.queryView(createQuery(
                "by_groupid")
                .includeDocs(false)
                .startKey(from)
                .endKey(to), String::class.java)
    }

    override fun listVmpIdsByLabel(language: String?, label: String?): List<String> {
        val sanitizedLabel= label?.let { StringUtils.sanitizeString(it) }
        val from =
                ComplexKey.of(
                        language ?: "\u0000",
                        if (sanitizedLabel == null) "\u0000" else sanitizedLabel
                )

        val to = ComplexKey.of(
                if (language == null) ComplexKey.emptyObject() else if (sanitizedLabel == null) language + "\ufff0" else language,
                if (sanitizedLabel == null) ComplexKey.emptyObject() else sanitizedLabel + "\ufff0"
        )

        return db.queryView(createQuery("by_language_label")
                .includeDocs(false)
                .startKey(from)
                .endKey(to), String::class.java)
    }


    override fun listVmpsByVmpCodes(vmpCodes: List<String>): List<Vmp> {
        return db.queryView(createQuery(
                "by_vmpcode")
                .includeDocs(true)
                .keys(vmpCodes), Vmp::class.java)
    }

    override fun listVmpsByGroupIds(vmpgIds: List<String>): List<Vmp> {
        return db.queryView(createQuery(
                "by_groupid")
                .includeDocs(true)
                .keys(vmpgIds), Vmp::class.java)
    }

}
