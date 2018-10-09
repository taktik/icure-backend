package org.taktik.icure.logic.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.dao.EntityReferenceDAO;
import org.taktik.icure.entities.Contact;
import org.taktik.icure.entities.EntityReference;
import org.taktik.icure.logic.EntityPersister;
import org.taktik.icure.logic.EntityReferenceLogic;

@org.springframework.stereotype.Service
public class EntityReferenceLogicImpl  extends GenericLogicImpl<EntityReference, EntityReferenceDAO> implements EntityReferenceLogic {
	private EntityReferenceDAO entitReferenceDAO;

	@Override
	public EntityReference getLatest(String prefix) {
		return entitReferenceDAO.getLatest(prefix);
	}

	@Override
	protected EntityReferenceDAO getGenericDAO() {
		return entitReferenceDAO;
	}

	@Autowired
	public void setEntitReferenceDAO(EntityReferenceDAO entitReferenceDAO) {
		this.entitReferenceDAO = entitReferenceDAO;
	}
}
