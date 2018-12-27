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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import com.google.common.io.ByteStreams;
import org.apache.commons.codec.digest.DigestUtils;
import org.ektorp.AttachmentInputStream;
import org.ektorp.ComplexKey;
import org.ektorp.support.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.DocumentTemplateDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.entities.DocumentTemplate;
import org.taktik.commons.uti.UTI;

/**
 * Created by aduchate on 02/02/13, 15:24
 */

@Repository("documentTemplateDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.DocumentTemplate' && !doc.deleted) emit(doc._id, null )}")
class DocumentTemplateDAOImpl extends CachedDAOImpl<DocumentTemplate> implements DocumentTemplateDAO {
	private static final Logger log = LoggerFactory.getLogger(DocumentTemplateDAOImpl.class);

	@Autowired
	public DocumentTemplateDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbBase") CouchDbICureConnector couchdb, IDGenerator idGenerator, @Qualifier("entitiesCacheManager") CacheManager cacheManager) {
		super(DocumentTemplate.class, couchdb, idGenerator, cacheManager);
		initStandardDesignDocument();
	}

	@Override
	@View(name = "by_userId_and_guid", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.DocumentTemplate' && !doc.deleted && doc.owner) emit([doc.owner,doc.guid], null )}")
	public List<DocumentTemplate> findByUserGuid(String userId, String guid) {
		ComplexKey from = ComplexKey.of(userId, "");
		ComplexKey to = ComplexKey.of(userId, "\ufff0");
		List<DocumentTemplate> documentTemplates = queryView("by_userId_and_guid", from, to);

		// invoke postLoad()
		documentTemplates.forEach(this::postLoad);

		return documentTemplates;
	}

	@Override
	@View(name = "by_specialty_code_and_guid", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.DocumentTemplate' && !doc.deleted && doc.specialty) emit([doc.specialty.code,doc.guid], null )}")
	public List<DocumentTemplate> findBySpecialtyGuid(String healthcarePartyId, String guid) {

		List<DocumentTemplate> documentTemplates;
		if (guid !=null) {
			ComplexKey key = ComplexKey.of(healthcarePartyId, guid);
			documentTemplates = queryView("by_specialty_code_and_guid", key);
		} else {
			ComplexKey from = ComplexKey.of(healthcarePartyId, "");
			ComplexKey to = ComplexKey.of(healthcarePartyId, "\ufff0");
			documentTemplates = queryView("by_specialty_code_and_guid", from, to);
		}

		// invoke postLoad()
		documentTemplates.forEach(this::postLoad);

		return documentTemplates;
	}

	@Override
	@View(name = "by_document_type_code_and_user_id_and_guid", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.DocumentTemplate' && !doc.deleted && doc.documentType ) emit([doc.documentType,doc.owner,doc.guid], null )}")
	public List<DocumentTemplate> findByTypeUserGuid(String documentTypeCode, String userId, String guid) {

		List<DocumentTemplate> documentTemplates;
		if (userId != null && guid !=null) {
			ComplexKey key = ComplexKey.of(documentTypeCode,userId, guid);
			documentTemplates = queryView("by_document_type_code_and_user_id_and_guid", key);
		} else if (userId != null) {
			ComplexKey from = ComplexKey.of(documentTypeCode, userId, "");
			ComplexKey to = ComplexKey.of(documentTypeCode, userId, "\ufff0");
			documentTemplates = queryView("by_document_type_code_and_user_id_and_guid", from, to);

		} else{
			ComplexKey from = ComplexKey.of(documentTypeCode, "","");
			ComplexKey to = ComplexKey.of(documentTypeCode, "\ufff0","\ufff0");
			documentTemplates = queryView("by_document_type_code_and_user_id_and_guid", from, to);
		}

		// invoke postLoad()
		documentTemplates.forEach(this::postLoad);

		return documentTemplates;
	}

	public void evictFromCache(DocumentTemplate entity) {
		evictFromCache(entity);
	}

	public DocumentTemplate createDocumentTemplate(DocumentTemplate entity) {
		super.save(true, entity);
		return entity;
	}


	@Override
	protected void beforeSave(DocumentTemplate entity) {
		super.beforeSave(entity);

		if (entity.getAttachment() != null) {
			String newLayoutAttachmentId = DigestUtils.sha256Hex(entity.getAttachment());

			if (!newLayoutAttachmentId.equals(entity.getAttachmentId())) {
				entity.setAttachmentId(newLayoutAttachmentId);
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
	protected void afterSave(DocumentTemplate entity) {
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
	public void postLoad(DocumentTemplate entity) {
		super.postLoad(entity);

		if (entity != null && entity.getAttachmentId() != null) {
			AttachmentInputStream attachmentIs = getAttachmentInputStream(entity.getId(), entity.getAttachmentId());
			try {
				byte[] layout = ByteStreams.toByteArray(attachmentIs);
				entity.setAttachment(layout);
			} catch (IOException e) {
				//Could not load
			}
		}
	}
}
