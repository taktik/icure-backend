package org.taktik.icure.logic;

import org.taktik.icure.entities.FrontEndMigration;
import org.taktik.icure.exceptions.DeletionException;

import java.util.List;

public interface FrontEndMigrationLogic extends EntityPersister<FrontEndMigration, String>{

    FrontEndMigration createFrontEndMigration(FrontEndMigration frontEndMigration);

    String deleteFrontEndMigration(String frontEndMigrationId) throws DeletionException;

    FrontEndMigration getFrontEndMigration(String frontEndMigrationId);

    List<FrontEndMigration> getFrontEndMigrationByUserIdName(String userId, String name);

    FrontEndMigration modifyFrontEndMigration(FrontEndMigration frontEndMigration);
}
