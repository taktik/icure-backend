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

import kotlinx.coroutines.flow.*
import org.apache.commons.codec.digest.DigestUtils
import org.ektorp.ComplexKey
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Repository
import org.taktik.commons.uti.UTI
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.DocumentTemplateDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.DocumentTemplate
import org.taktik.icure.utils.createQuery
import java.io.IOException
import java.net.URI
import java.nio.ByteBuffer

/**
 * Created by aduchate on 02/02/13, 15:24
 */

@Repository("documentTemplateDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.DocumentTemplate' && !doc.deleted) emit(doc._id, null )}")
internal class DocumentTemplateDAOImpl(@Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator, @Qualifier("entitiesCacheManager") cacheManager: CacheManager) : CachedDAOImpl<DocumentTemplate>(DocumentTemplate::class.java, couchDbDispatcher, idGenerator, cacheManager), DocumentTemplateDAO {

    @View(name = "by_userId_and_guid", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.DocumentTemplate' && !doc.deleted && doc.owner) emit([doc.owner,doc.guid], null )}")
    override fun findByUserGuid(dbInstanceUrl: URI, groupId: String, userId: String, guid: String?): Flow<DocumentTemplate> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val from = ComplexKey.of(userId, "")
        val to = ComplexKey.of(userId, "\ufff0")
        val viewQuery = createQuery<DocumentTemplate>("by_userId_and_guid").startKey(from).endKey(to).includeDocs(true)
        val documentTemplates = client.queryViewIncludeDocsNoValue<Array<String>, DocumentTemplate>(viewQuery).map { it.doc }

        // invoke postLoad()
        documentTemplates.onEach {
            this.postLoad(dbInstanceUrl, groupId, it)
        }

        return documentTemplates
    }

    @View(name = "by_specialty_code_and_guid", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.DocumentTemplate' && !doc.deleted && doc.specialty) emit([doc.specialty.code,doc.guid], null )}")
    override fun findBySpecialtyGuid(dbInstanceUrl: URI, groupId: String, healthcarePartyId: String, guid: String?): Flow<DocumentTemplate> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val documentTemplates = if (guid != null) {
            val key = ComplexKey.of(healthcarePartyId, guid)
            val viewQuery = createQuery<DocumentTemplate>("by_specialty_code_and_guid").key(key).includeDocs(true)
            client.queryViewIncludeDocsNoValue<Array<String>, DocumentTemplate>(viewQuery).map { it.doc }
        } else {
            val from = ComplexKey.of(healthcarePartyId, "")
            val to = ComplexKey.of(healthcarePartyId, "\ufff0")
            val viewQuery = createQuery<DocumentTemplate>("by_specialty_code_and_guid").startKey(from).endKey(to).includeDocs(true)
            client.queryViewIncludeDocsNoValue<Array<String>, DocumentTemplate>(viewQuery).map { it.doc }
        }

        // invoke postLoad()
        documentTemplates.onEach {
            this.postLoad(dbInstanceUrl, groupId, it)
        }

        return documentTemplates
    }

    @View(name = "by_document_type_code_and_user_id_and_guid", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.DocumentTemplate' && !doc.deleted && doc.documentType ) emit([doc.documentType,doc.owner,doc.guid], null )}")
    override fun findByTypeUserGuid(dbInstanceUrl: URI, groupId: String, documentTypeCode: String, userId: String?, guid: String?): Flow<DocumentTemplate> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val viewQuery = if (userId != null && guid != null) {
            val key = ComplexKey.of(documentTypeCode, userId, guid)
            createQuery<DocumentTemplate>("by_document_type_code_and_user_id_and_guid").key(key).includeDocs(true)
        } else if (userId != null) {
            val from = ComplexKey.of(documentTypeCode, userId, "")
            val to = ComplexKey.of(documentTypeCode, userId, "\ufff0")
            createQuery<DocumentTemplate>("by_document_type_code_and_user_id_and_guid").startKey(from).endKey(to).includeDocs(true)
        } else {
            val from = ComplexKey.of(documentTypeCode, "", "")
            val to = ComplexKey.of(documentTypeCode, "\ufff0", "\ufff0")
            createQuery<DocumentTemplate>("by_document_type_code_and_user_id_and_guid").startKey(from).endKey(to).includeDocs(true)
        }
        val documentTemplates = client.queryViewIncludeDocsNoValue<Array<String>, DocumentTemplate>(viewQuery).map { it.doc }

        // invoke postLoad()
        documentTemplates.onEach {
            this.postLoad(dbInstanceUrl, groupId, it)
        }

        return documentTemplates
    }

    override fun evictFromCache(entity: DocumentTemplate) {
        evictFromCache(entity)
    }

    override suspend fun createDocumentTemplate(dbInstanceUrl: URI, groupId: String, entity: DocumentTemplate): DocumentTemplate {
        super.save(dbInstanceUrl, groupId, true, entity)
        return entity
    }


    override suspend fun beforeSave(dbInstanceUrl: URI, groupId: String, entity: DocumentTemplate) {
        super.beforeSave(dbInstanceUrl, groupId, entity)

        if (entity.attachment != null) {
            val newLayoutAttachmentId = DigestUtils.sha256Hex(entity.attachment)

            if (newLayoutAttachmentId != entity.attachmentId) {
                entity.attachmentId = newLayoutAttachmentId
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

    override suspend  fun afterSave(dbInstanceUrl: URI, groupId: String, entity: DocumentTemplate) {
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

    override suspend fun postLoad(dbInstanceUrl: URI, groupId: String, entity: DocumentTemplate?) {
        super.postLoad(dbInstanceUrl, groupId, entity)

        if (entity != null && entity.attachmentId != null) {
            val attachmentIs = getAttachment(dbInstanceUrl, groupId, entity.id, entity.attachmentId, entity.rev)
            try {
                entity.attachment = attachmentIs.reduce { acc, value -> acc.put(value) }.array()
            } catch (e: IOException) {
                //Could not load
            }

        }
    }
}
