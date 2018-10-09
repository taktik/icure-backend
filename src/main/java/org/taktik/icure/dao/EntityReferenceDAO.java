package org.taktik.icure.dao;

import org.taktik.icure.entities.EntityReference;

public interface EntityReferenceDAO extends GenericDAO<EntityReference> {
	EntityReference getLatest(String prefix);
}
