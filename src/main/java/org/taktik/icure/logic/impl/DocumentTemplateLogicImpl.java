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

package org.taktik.icure.logic.impl;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.taktik.icure.dao.DocumentTemplateDAO;
import org.taktik.icure.entities.DocumentTemplate;
import org.taktik.icure.entities.embed.DocumentType;
import org.taktik.icure.logic.DocumentTemplateLogic;
import org.taktik.icure.logic.ICureSessionLogic;

@Service
public class DocumentTemplateLogicImpl extends GenericLogicImpl<DocumentTemplate, DocumentTemplateDAO> implements DocumentTemplateLogic {
	private static Logger logger = LoggerFactory.getLogger(DocumentTemplateLogicImpl.class);

	private DocumentTemplateDAO documentTemplateDAO;
	private ICureSessionLogic sessionLogic;

	@Autowired
	public void setDocumentTemplateDAO(DocumentTemplateDAO documentTemplateDAO) {
		this.documentTemplateDAO = documentTemplateDAO;
	}

	@Override
	public boolean createEntities(Collection<DocumentTemplate> entities, Collection<DocumentTemplate> createdEntities) throws Exception {
		entities.stream().forEach((e) -> {
			if (e.getOwner() == null) {
				e.setOwner(sessionLogic.getCurrentUserId());
			}
		});
		return super.createEntities(entities, createdEntities);
	}


	@Override
	public DocumentTemplate createDocumentTemplate(DocumentTemplate entity) {
		if (entity.getOwner() == null) {
			entity.setOwner(sessionLogic.getCurrentUserId());
		}

		return documentTemplateDAO.createDocumentTemplate(entity);
	}

	@Override
	public DocumentTemplate getDocumentTemplateById(String documentTemplateId) {
		return documentTemplateDAO.get(documentTemplateId);
	}

	@Override
	public List<DocumentTemplate> getDocumentTemplatesBySpecialty(String specialityCode) {
		return documentTemplateDAO.findBySpecialtyGuid(specialityCode, null);
	}

	@Override
	public List<DocumentTemplate> getDocumentTemplatesByDocumentType(String documentTypeCode) {
		return documentTemplateDAO.findByTypeUserGuid(documentTypeCode, null, null);
	}

	@Override
	public List<DocumentTemplate> getDocumentTemplatesByDocumentTypeAndUser(String documentTypeCode,String userId) {
		return documentTemplateDAO.findByTypeUserGuid(documentTypeCode, userId, null);
	}

	@Override
	public List<DocumentTemplate> getDocumentTemplatesByUser(String userId) {
		return documentTemplateDAO.findByUserGuid(userId, null);
	}

	@Override
	public DocumentTemplate modifyDocumentTemplate(DocumentTemplate documentTemplate) {
		if (documentTemplate.getOwner() == null) {
			documentTemplate.setOwner(sessionLogic.getCurrentUserId());
		}

		return documentTemplateDAO.save(documentTemplate);
	}

	@Override
	protected DocumentTemplateDAO getGenericDAO() {
		return documentTemplateDAO;
	}

	@Autowired
	public void setSessionLogic(ICureSessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}
}
