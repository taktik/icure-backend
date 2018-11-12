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
import org.ektorp.DocumentNotFoundException;
import org.ektorp.support.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.FormTemplateDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator;
import org.taktik.icure.entities.FormTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by aduchate on 02/02/13, 15:24
 */

@Repository("formTemplateDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.FormTemplate' && !doc.deleted) emit(doc._id, null )}")
class FormTemplateDAOImpl extends CachedDAOImpl<FormTemplate> implements FormTemplateDAO {
	private static final Logger log = LoggerFactory.getLogger(FormTemplateDAOImpl.class);

	private UUIDGenerator uuidGenerator;

	@Autowired
	public void setUuidGenerator(UUIDGenerator uuidGenerator) {
		this.uuidGenerator = uuidGenerator;
	}

	@Autowired
    public FormTemplateDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbBase") CouchDbICureConnector couchdb, IDGenerator idGenerator, @Qualifier("entitiesCacheManager") CacheManager cacheManager) {
        super(FormTemplate.class, couchdb, idGenerator, cacheManager);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "by_userId_and_guid", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.FormTemplate' && !doc.deleted && doc.author) emit([doc.author,doc.guid], null )}")
    public List<FormTemplate> findByUserGuid(String userId, String guid, boolean loadLayout) {
        ComplexKey from = ComplexKey.of(userId, guid != null ? guid : "");
		ComplexKey to = ComplexKey.of(userId, guid != null ? guid : "\ufff0");
		List<FormTemplate> formTemplates = queryView("by_userId_and_guid", from, to);

		// invoke postLoad()
	    if (loadLayout) {
		    formTemplates.forEach(this::postLoad);
	    }

		return formTemplates;
	}

    @Override
    @View(name = "by_specialty_code_and_guid", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.FormTemplate' && !doc.deleted && doc.specialty) emit([doc.specialty.code,doc.guid], null )}")
    public List<FormTemplate> findBySpecialtyGuid(String specialityCode, String guid, boolean loadLayout) {

		List<FormTemplate> formTemplates;
		if (guid !=null) {
			ComplexKey key = ComplexKey.of(specialityCode, guid);
			formTemplates = queryView("by_specialty_code_and_guid", key);
		} else {
			ComplexKey from = ComplexKey.of(specialityCode, null);
			ComplexKey to = ComplexKey.of(specialityCode, ComplexKey.emptyObject());
			formTemplates = queryView("by_specialty_code_and_guid", from, to);
		}

		// invoke postLoad()
	    if (loadLayout) {
		    formTemplates.forEach(this::postLoad);
	    }

		return formTemplates;
	}


	public void evictFromCache(FormTemplate entity) {
		super.evictFromCache(entity);
	}

	public FormTemplate createFormTemplate(FormTemplate entity) {
		super.save(true, entity);
		return entity;
	}


	@Override
	protected void beforeSave(FormTemplate entity) {
		super.beforeSave(entity);

		if (entity.getLayout() != null) {
			String newLayoutAttachmentId = DigestUtils.sha256Hex(entity.getLayout());

			if (!newLayoutAttachmentId.equals(entity.getLayoutAttachmentId())) {
				entity.setLayoutAttachmentId(newLayoutAttachmentId);
				entity.setAttachmentDirty(true);
			}
		}
	}

	@Override
	protected void afterSave(FormTemplate entity) {
		super.afterSave(entity);

		if (entity.isAttachmentDirty() && entity.getLayout() != null && entity.getLayoutAttachmentId() != null) {
			AttachmentInputStream a = new AttachmentInputStream(entity.getLayoutAttachmentId(), new ByteArrayInputStream(entity.getLayout()), "application/json");
			entity.setRev(createAttachment(entity.getId(), entity.getRev(), a));
			entity.setAttachmentDirty(false);
		}
	}

	@Override
	public void postLoad(FormTemplate entity) {
		super.postLoad(entity);

		if (entity != null && entity.getLayoutAttachmentId() != null) {
			try {
				AttachmentInputStream attachmentIs = getAttachmentInputStream(entity.getId(), entity.getLayoutAttachmentId());
				byte[] layout = ByteStreams.toByteArray(attachmentIs);
				entity.setLayout(layout);
			} catch (IOException|DocumentNotFoundException e) {
				log.warn("Failed to obtain attachment(" + entity.getId() + ") for the doc id (" + entity.getLayoutAttachmentId() + ").");
			}
		}
	}
}
