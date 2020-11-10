package org.taktik.icure.dao.samv2.impl

import org.ektorp.ComplexKey
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.icure.dao.impl.GenericDAOImpl
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.dao.samv2.NmpDAO
import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.db.StringUtils
import org.taktik.icure.entities.samv2.Amp
import org.taktik.icure.entities.samv2.Nmp

@Repository("nmpDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.samv2.Nmp' && !doc.deleted) emit( null, doc._id )}")
class NmpDAOImpl @Autowired
constructor(@Qualifier("couchdbDrugs") couchdb: CouchDbICureConnector, idGenerator: IDGenerator) : GenericDAOImpl<Nmp>(Nmp::class.java, couchdb, idGenerator), NmpDAO {
    @View(name = "by_language_label", map = "classpath:js/nmp/By_language_label.js")
    override fun findNmpsByLabel(language: String?, label: String?, pagination: PaginationOffset<*>?): PaginatedList<Nmp> {
        val sanitizedLabel = label?.let { StringUtils.sanitizeString(it)}
        @Suppress("UNCHECKED_CAST") val startKey = if (pagination?.startKey == null) null else pagination.startKey as MutableList<Any?>
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
        )    }

    override fun listNmpIdsByLabel(language: String?, label: String?): List<String> {
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

    @View(name = "by_cnk", map = "classpath:js/nmp/By_cnk.js")
    override fun listNmpsByCnks(cnks: List<String>): List<Nmp> {
        return db.queryView(createQuery(
                "by_cnk")
                .includeDocs(true)
                .keys(cnks), Nmp::class.java)
    }

    init {
        initStandardDesignDocument()
    }
}
