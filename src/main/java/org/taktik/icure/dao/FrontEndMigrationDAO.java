package org.taktik.icure.dao;

import org.taktik.icure.entities.FrontEndMigration;


public interface FrontEndMigrationDAO extends GenericDAO<FrontEndMigration> {
    FrontEndMigration getByUserIdName(String userId, String name);
}
