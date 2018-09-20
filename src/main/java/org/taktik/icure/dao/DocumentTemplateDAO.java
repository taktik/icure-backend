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

package org.taktik.icure.dao;

import org.ektorp.support.View;
import org.taktik.icure.entities.DocumentTemplate;
import org.taktik.icure.entities.embed.DocumentType;

import java.util.List;

public interface DocumentTemplateDAO  extends GenericDAO<DocumentTemplate> {
	DocumentTemplate createDocumentTemplate(DocumentTemplate entity);
	List<DocumentTemplate> findByUserGuid(String userId, String guid);
	List<DocumentTemplate> findBySpecialtyGuid(String healthcarePartyId, String guid);
	List<DocumentTemplate> findByTypeUserGuid(String documentTypeCode, String userId, String guid);
	void evictFromCache(DocumentTemplate entity);

}
