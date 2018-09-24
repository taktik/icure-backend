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

package org.taktik.icure.dao.impl;

import com.google.common.io.ByteStreams;
import org.apache.commons.codec.digest.DigestUtils;
import org.ektorp.AttachmentInputStream;
import org.ektorp.ComplexKey;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.DocumentDAO;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.entities.Contact;
import org.taktik.icure.entities.Document;
import org.taktik.commons.uti.UTI;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
@Repository("documentDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Document' && !doc.deleted) emit( null, doc._id )}")
public class DocumentDAOImpl extends GenericIcureDAOImpl<Document> implements DocumentDAO {

    @Autowired
    public DocumentDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbHealthdata") CouchDbICureConnector db, IDGenerator idGenerator) {
        super(Document.class, db, idGenerator);
        initStandardDesignDocument();
    }

    @Override
    protected void beforeSave(Document entity) {
        super.beforeSave(entity);

        if (entity.getAttachment() != null) {
            String newAttachmentId = DigestUtils.sha256Hex(entity.getAttachment());

            if (!newAttachmentId.equals(entity.getAttachmentId())) {
                if (entity.getAttachments().containsKey(entity.getAttachmentId())) {
                    entity.setRev(deleteAttachment(entity.getId(), entity.getRev(), entity.getAttachmentId()));
                    entity.getAttachments().remove(entity.getAttachmentId());
                }
                entity.setAttachmentId(newAttachmentId);
                entity.setAttachmentDirty(true);
            }
        } else {
            if (entity.getAttachmentId() != null) {
                entity.setRev(deleteAttachment(entity.getId(), entity.getRev(), entity.getAttachmentId()));
                entity.setAttachmentId(null);
                entity.setAttachmentDirty(false);
            }
        }
    }

    @Override
    protected void afterSave(Document entity) {
        super.afterSave(entity);

        if (entity.isAttachmentDirty()) {
            if (entity.getAttachment() != null && entity.getAttachmentId() != null) {
                UTI uti = UTI.get(entity.getMainUti());
                String mimeType = "application/xml";
                if (uti != null && uti.getMimeTypes() != null && uti.getMimeTypes().size() > 0) {
                    mimeType = uti.getMimeTypes().get(0);
                }
                AttachmentInputStream a = new AttachmentInputStream(entity.getAttachmentId(), new ByteArrayInputStream(entity.getAttachment()), mimeType);
                entity.setRev(createAttachment(entity.getId(), entity.getRev(), a));
                entity.setAttachmentDirty(false);
            }
        }
    }

    @Override
    public void postLoad(Document entity) {
        super.postLoad(entity);

        if (entity.getAttachmentId() != null) {
            try {
                InputStream attachmentIs = entity.getAttachmentId().contains("|") ? new BufferedInputStream(new FileInputStream(entity.getAttachmentId().split("\\|")[1])) :
                        getAttachmentInputStream(entity.getId(), entity.getAttachmentId());
                byte[] layout = ByteStreams.toByteArray(attachmentIs);
                entity.setAttachment(layout);
            } catch (IOException e) {
                //Could not load
            }
        }
    }

    @Override
    @View(name = "conflicts", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Document' && !doc.deleted && doc._conflicts) emit(doc._id )}")
    public List<Document> listConflicts() {
        return queryView("conflicts");
    }

    @Override
    @View(name = "by_hcparty_message", map = "classpath:js/document/By_hcparty_message_map.js")
    public List<Document> findDocumentsByHCPartySecretMessageKeys(String hcPartyId, ArrayList<String> secretForeignKeys) {
        ComplexKey[] keys = secretForeignKeys.stream().map(fk -> ComplexKey.of(hcPartyId, fk)).collect(Collectors.toList()).toArray(new ComplexKey[secretForeignKeys.size()]);
        return queryView("by_hcparty_message", keys);
    }

    @Override
    @View(name = "without_delegations", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Document' && !doc.deleted && (!doc.delegations || Object.keys(doc.delegations).length === 0)) emit(doc._id )}")
    public List<Document> findDocumentsWithNoDelegations(int limit) {
        ViewQuery viewQuery = createQuery("without_delegations")
                .limit(limit)
                .includeDocs(true);

        return db.queryView(viewQuery, Document.class);
    }

    @Override
    @View(name = "by_type_hcparty_message", map = "classpath:js/document/By_document_type_hcparty_message_map.js")
    public List<Document> findDocumentsByDocumentTypeHCPartySecretMessageKeys(String documentTypeCode, String hcPartyId, ArrayList<String> secretForeignKeys) {

        ComplexKey[] keys = secretForeignKeys.stream().map(fk -> ComplexKey.of(documentTypeCode, hcPartyId, fk)).collect(Collectors.toList()).toArray(new ComplexKey[secretForeignKeys.size()]);
        return queryView("by_type_hcparty_message", keys);
    }

    @Override
    public InputStream readAttachment(String documentId, String attachmentId) {
        return getAttachmentInputStream(documentId, attachmentId);
    }

}
