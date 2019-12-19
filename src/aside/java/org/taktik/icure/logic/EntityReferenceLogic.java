package org.taktik.icure.logic;

import org.taktik.icure.entities.EntityReference;

public interface EntityReferenceLogic extends EntityPersister<EntityReference, String> {

	EntityReference getLatest(String prefix);
}
