/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.asyncdao.impl

import com.google.common.io.ByteStreams
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import ma.glasnost.orika.MapperFacade
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.output.ByteArrayOutputStream
import org.ektorp.ComplexKey
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.commons.uti.UTI
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.Document
import org.taktik.icure.utils.createQuery
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.IOException
import java.net.URI
import java.nio.ByteBuffer
import java.nio.charset.CharsetEncoder
import java.nio.charset.StandardCharsets
import java.util.*


@Repository("documentDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Document' && !doc.deleted) emit( null, doc._id )}")
class DocumentDAOImpl(@Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator, mapper: MapperFacade) : GenericDAOImpl<Document>(Document::class.java, couchDbDispatcher, idGenerator, mapper), DocumentDAO {

    override suspend fun beforeSave(dbInstanceUrl: URI, groupId: String, entity: Document) {
        super.beforeSave(dbInstanceUrl, groupId, entity)

        if (entity.attachment != null) {
            val newAttachmentId = DigestUtils.sha256Hex(entity.attachment)

            if (newAttachmentId != entity.attachmentId) {
                if (entity.attachments.containsKey(entity.attachmentId)) {
                    entity.rev = deleteAttachment(dbInstanceUrl, groupId, entity.id, entity.rev, entity.attachmentId)
                    entity.attachments.remove(entity.attachmentId)
                }
                entity.attachmentId = newAttachmentId
                entity.isAttachmentDirty = true
            }
        } else {
            if (entity.attachmentId != null) {
                entity.rev = deleteAttachment(dbInstanceUrl, groupId, entity.id, entity.rev, entity.attachmentId)
                entity.attachmentId = null
                entity.isAttachmentDirty = false
            }
        }
    }

    override suspend fun afterSave(dbInstanceUrl: URI, groupId: String, entity: Document) {
        super.afterSave(dbInstanceUrl, groupId, entity)

        if (entity.isAttachmentDirty) {
            if (entity.attachment != null && entity.attachmentId != null) {
                val uti = UTI.get(entity.mainUti)
                var mimeType = "application/xml"
                if (uti != null && uti.mimeTypes != null && uti.mimeTypes.size > 0) {
                    mimeType = uti.mimeTypes[0]
                }
                entity.rev = createAttachment(dbInstanceUrl, groupId, entity.id, entity.attachmentId, entity.rev, mimeType, flowOf(ByteBuffer.wrap(entity.attachment)))
                entity.isAttachmentDirty = false
            }
        }
    }

    override suspend fun postLoad(dbInstanceUrl: URI, groupId: String, entity: Document?) {
        super.postLoad(dbInstanceUrl, groupId, entity)
        val encoder: CharsetEncoder = StandardCharsets.UTF_8.newEncoder()

        entity?.let {
            if (entity.attachmentId != null) {
                try {
                    if (entity.attachmentId.contains("|")) {
                        val attachmentIs = BufferedInputStream(FileInputStream(entity.attachmentId.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]))
                        entity.attachment = ByteStreams.toByteArray(attachmentIs)
                    } else {
                        val attachmentIs = getAttachment(dbInstanceUrl, groupId, entity.id, entity.attachmentId, entity.rev)
                        ByteArrayOutputStream().use {attachment ->
                            attachmentIs.collect { attachment.write(it.array()) }
                            entity.attachment = attachment.toByteArray()
                        }
                    }
                } catch (e: IOException) {
                    //Could not load
                }
            }
        }
    }

    @View(name = "conflicts", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Document' && !doc.deleted && doc._conflicts) emit(doc._id )}")
    override fun listConflicts(dbInstanceUrl: URI, groupId: String): Flow<Document> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val viewQuery = createQuery<Document>("conflicts")
                .limit(200)
                .includeDocs(true)

        return client.queryViewIncludeDocsNoValue<String, Document>(viewQuery).map { it.doc }
    }

    @View(name = "by_hcparty_message", map = "classpath:js/document/By_hcparty_message_map.js")
    override fun findDocumentsByHCPartySecretMessageKeys(dbInstanceUrl: URI, groupId: String, hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<Document> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val keys = secretForeignKeys.map { fk -> ComplexKey.of(hcPartyId, fk) }

        val viewQuery = createQuery<Document>("by_hcparty_message")
                .keys(keys)
                .includeDocs(true)

        return client.queryViewIncludeDocs<Array<String>, String, Document>(viewQuery).map { it.doc }
    }

    @View(name = "without_delegations", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Document' && !doc.deleted && (!doc.delegations || Object.keys(doc.delegations).length === 0)) emit(doc._id )}")
    override fun findDocumentsWithNoDelegations(dbInstanceUrl: URI, groupId: String, limit: Int): Flow<Document> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val viewQuery = createQuery<Document>("without_delegations")
                .limit(limit)
                .includeDocs(true)

        return client.queryViewIncludeDocsNoValue<String, Document>(viewQuery).map { it.doc }
    }

    @View(name = "by_type_hcparty_message", map = "classpath:js/document/By_document_type_hcparty_message_map.js")
    override fun findDocumentsByDocumentTypeHCPartySecretMessageKeys(dbInstanceUrl: URI, groupId: String, documentTypeCode: String, hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<Document> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val keys = secretForeignKeys.map { fk -> ComplexKey.of(documentTypeCode, hcPartyId, fk) }

        val viewQuery = createQuery<Document>("by_type_hcparty_message")
                .keys(keys)
                .includeDocs(true)

        return client.queryViewIncludeDocs<Array<String>, String, Document>(viewQuery).map { it.doc }
    }

    override fun readAttachment(dbInstanceUrl: URI, groupId: String, documentId: String, attachmentId: String, rev: String?): Flow<ByteBuffer> {
        return getAttachment(dbInstanceUrl, groupId, documentId, attachmentId, rev)
    }

}
